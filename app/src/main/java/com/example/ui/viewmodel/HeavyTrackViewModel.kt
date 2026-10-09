package com.example.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.ActivityMasterEntity
import com.example.data.local.entity.ActivityReportEntity
import com.example.data.local.entity.OperatorEntity
import com.example.data.local.entity.UnitEntity
import com.example.data.model.Supervisor
import com.example.data.model.SupervisorDirectory
import com.example.data.remote.ActivityPayload
import com.example.data.remote.CompanyServerSimulator
import com.example.data.remote.OperatorPayload
import com.example.data.remote.UnitPayload
import com.example.data.repository.HeavyTrackRepository
import com.example.data.repository.SyncLogItem
import com.example.util.ImageCompressor
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FormUiState(
    val reportDate: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
    val selectedUnit: UnitEntity? = null,
    val selectedActivity: ActivityMasterEntity? = null,
    val selectedOperator: OperatorEntity? = null,
    val workLocation: String = "001A / Mainroad",
    val shift: String = "SIANG",
    val workDurationHours: Double = 7.0,
    val hmAwal: Double = 0.0,
    val hmAkhirInput: String = "",
    val hmDailyRunning: Double = 0.0,
    val isHmValid: Boolean = true,
    val hmErrorMessage: String? = null,
    val workOutputInput: String = "",
    val notes: String = "",
    val unitStatus: String = "OPERASI",  // "OPERASI" | "STANDBY" | "BREAKDOWN"
    val isRefueled: Boolean = false,
    val fuelLitersInput: String = "",
    val standbyReason: String = "Hujan / Slippery",
    val breakdownTrouble: String = "Trouble Engine / Mesin",
    val photoPath: String? = null,
    val photoBase64: String? = null,
    val photoSizeKb: Long? = null,
    val photoDimensions: String? = null,
    val isCompressingPhoto: Boolean = false,
    val isSubmitting: Boolean = false,
    val formError: String? = null
)

sealed interface UiMessage {
    data class Success(val message: String) : UiMessage
    data class Error(val message: String) : UiMessage
    data class Info(val message: String) : UiMessage
}

class HeavyTrackViewModel(
    private val repository: HeavyTrackRepository
) : ViewModel() {

    // Active Supervisor
    private val _activeSupervisor = MutableStateFlow("Budi Santoso")
    val activeSupervisor: StateFlow<String> = _activeSupervisor.asStateFlow()

    // Supervisor Login Session
    private val _currentSupervisorSession = MutableStateFlow<Supervisor?>(null)
    val currentSupervisorSession: StateFlow<Supervisor?> = _currentSupervisorSession.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Base URL configuration for Web Admin
    private val _apiBaseUrl = MutableStateFlow(repository.apiClientManager.baseUrl)
    val apiBaseUrl: StateFlow<String> = _apiBaseUrl.asStateFlow()

    private val _isTestingConnection = MutableStateFlow(false)
    val isTestingConnection: StateFlow<Boolean> = _isTestingConnection.asStateFlow()

    // Offline mode simulation toggle
    private val _isOfflineSimulated = MutableStateFlow(CompanyServerSimulator.isSimulatedOffline)
    val isOfflineSimulated: StateFlow<Boolean> = _isOfflineSimulated.asStateFlow()

    // Sync logs
    val syncLogs: StateFlow<List<SyncLogItem>> = repository.syncLogs

    // Form state
    private val _formState = MutableStateFlow(FormUiState())
    val formState: StateFlow<FormUiState> = _formState.asStateFlow()

    // UI events (snackbars)
    private val _uiEvents = MutableSharedFlow<UiMessage>()
    val uiEvents: SharedFlow<UiMessage> = _uiEvents.asSharedFlow()

    // Syncing state
    private val _isSyncingMaster = MutableStateFlow(false)
    val isSyncingMaster: StateFlow<Boolean> = _isSyncingMaster.asStateFlow()

    private val _isSyncingPending = MutableStateFlow(false)
    val isSyncingPending: StateFlow<Boolean> = _isSyncingPending.asStateFlow()

    // Master Units (filtered by supervisor)
    val units: StateFlow<List<UnitEntity>> = _activeSupervisor
        .flatMapLatest { supervisor -> repository.getUnitsForSupervisor(supervisor) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Master Activities
    val activities: StateFlow<List<ActivityMasterEntity>> = repository.getAllActivities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Master Operators (filtered by supervisor)
    val operators: StateFlow<List<OperatorEntity>> = _activeSupervisor
        .flatMapLatest { supervisor -> repository.getOperatorsForSupervisor(supervisor) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reports list
    val reports: StateFlow<List<ActivityReportEntity>> = _activeSupervisor
        .flatMapLatest { supervisor -> repository.getReportsForSupervisor(supervisor) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Unsynced count
    val unsyncedCount: StateFlow<Int> = repository.getUnsyncedCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty(_activeSupervisor.value)
        }
    }

    fun updateApiBaseUrl(newUrl: String) {
        val trimmed = newUrl.trim()
        repository.apiClientManager.baseUrl = trimmed
        _apiBaseUrl.value = repository.apiClientManager.baseUrl
        viewModelScope.launch {
            _uiEvents.emit(UiMessage.Info("URL Web Admin diperbarui: ${_apiBaseUrl.value}"))
            fetchMasterData()
        }
    }

    fun testApiConnection(targetUrl: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isTestingConnection.value = true
            val (ok, message) = repository.testConnection(targetUrl)
            _isTestingConnection.value = false
            onResult(ok, message)
            if (ok) {
                _uiEvents.emit(UiMessage.Success(message))
            } else {
                _uiEvents.emit(UiMessage.Error(message))
            }
        }
    }

    fun login(supervisor: Supervisor) {
        _currentSupervisorSession.value = supervisor
        _activeSupervisor.value = supervisor.name
        _isLoggedIn.value = true
        _formState.value = _formState.value.copy(
            shift = supervisor.defaultShift,
            workLocation = supervisor.assignedSector,
            selectedUnit = null,
            selectedOperator = null,
            hmAwal = 0.0,
            hmAkhirInput = "",
            hmDailyRunning = 0.0,
            isHmValid = true,
            hmErrorMessage = null
        )
        viewModelScope.launch {
            _uiEvents.emit(UiMessage.Success("Selamat bertugas, Pengawas ${supervisor.name} (${supervisor.nik})"))
            fetchMasterData()
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentSupervisorSession.value = null
        _formState.value = _formState.value.copy(
            selectedUnit = null,
            selectedActivity = null,
            selectedOperator = null,
            hmAwal = 0.0,
            hmAkhirInput = "",
            hmDailyRunning = 0.0
        )
        viewModelScope.launch {
            _uiEvents.emit(UiMessage.Info("Anda telah keluar dari sesi pengawas"))
        }
    }

    fun switchSupervisor(newSupervisor: String) {
        _activeSupervisor.value = newSupervisor
        _formState.value = _formState.value.copy(
            selectedUnit = null,
            selectedOperator = null,
            hmAwal = 0.0,
            hmAkhirInput = "",
            hmDailyRunning = 0.0,
            isHmValid = true,
            hmErrorMessage = null
        )
        fetchMasterData()
    }

    fun toggleOfflineSimulation() {
        val next = !_isOfflineSimulated.value
        CompanyServerSimulator.isSimulatedOffline = next
        _isOfflineSimulated.value = next
        viewModelScope.launch {
            if (next) {
                _uiEvents.emit(UiMessage.Info("Mode Offline Lapangan Aktif (Simulasi sinyal hilang)"))
            } else {
                _uiEvents.emit(UiMessage.Success("Koneksi Pulih! Siap sinkronisasi ke Web Admin"))
                syncPendingReports()
            }
        }
    }

    fun fetchMasterData() {
        viewModelScope.launch {
            _isSyncingMaster.value = true
            val result = repository.fetchMasterData(_activeSupervisor.value)
            _isSyncingMaster.value = false
            result.onSuccess { msg ->
                _uiEvents.emit(UiMessage.Success(msg))
            }.onFailure { err ->
                _uiEvents.emit(UiMessage.Error("Gagal sinkron master: ${err.message ?: "Jaringan bermasalah"}"))
            }
        }
    }

    fun selectUnit(unit: UnitEntity) {
        val lastHm = unit.lastHm
        val currentInput = _formState.value.hmAkhirInput
        val inputVal = currentInput.toDoubleOrNull()

        val (isValid, running, errorMsg) = validateHm(lastHm, inputVal)

        _formState.value = _formState.value.copy(
            selectedUnit = unit,
            hmAwal = lastHm,
            isHmValid = isValid,
            hmDailyRunning = running,
            hmErrorMessage = errorMsg
        )
    }

    fun selectActivity(activity: ActivityMasterEntity) {
        _formState.value = _formState.value.copy(
            selectedActivity = activity
        )
    }

    fun selectOperator(operator: OperatorEntity) {
        _formState.value = _formState.value.copy(
            selectedOperator = operator
        )
    }

    fun setWorkLocation(location: String) {
        _formState.value = _formState.value.copy(workLocation = location)
    }

    fun setShift(shift: String) {
        _formState.value = _formState.value.copy(shift = shift)
    }

    fun setWorkDuration(duration: Double) {
        _formState.value = _formState.value.copy(workDurationHours = duration)
    }

    fun setWorkOutput(output: String) {
        _formState.value = _formState.value.copy(workOutputInput = output)
    }

    fun setNotes(notes: String) {
        _formState.value = _formState.value.copy(notes = notes)
    }

    val standbyOptions = listOf(
        "Hujan / Slippery",
        "Tunggu Blasting / Lokasi",
        "Antre Hauler / Disposal",
        "Shift Change / Istirahat",
        "Tunggu Instruksi Pengawas",
        "Lainnya"
    )

    val breakdownOptions = listOf(
        "Trouble Engine / Mesin",
        "Undercarriage / Track Lepas",
        "Sistem Hidrolik / Hose Bocor",
        "Electrical / Kelistrikan",
        "Preventive Maintenance / Servis",
        "Transmission / Drive Train",
        "Lainnya"
    )

    fun setUnitStatus(status: String) {
        when (status) {
            "STANDBY" -> {
                val reason = _formState.value.standbyReason
                val standbyActivity = ActivityMasterEntity(
                    sapCode = "ACT-STB-01",
                    activityName = "Standby - $reason",
                    unitOfMeasure = "Jam",
                    category = "STANDBY",
                    description = "Standby unit operasional"
                )
                _formState.value = _formState.value.copy(
                    unitStatus = "STANDBY",
                    selectedActivity = standbyActivity,
                    workOutputInput = "0"
                )
            }
            "BREAKDOWN" -> {
                val trouble = _formState.value.breakdownTrouble
                val breakdownActivity = ActivityMasterEntity(
                    sapCode = "ACT-BDN-01",
                    activityName = "Breakdown - $trouble",
                    unitOfMeasure = "Jam",
                    category = "BREAKDOWN",
                    description = "Perbaikan / breakdown unit"
                )
                _formState.value = _formState.value.copy(
                    unitStatus = "BREAKDOWN",
                    selectedActivity = breakdownActivity,
                    workOutputInput = "0"
                )
            }
            else -> {
                _formState.value = _formState.value.copy(
                    unitStatus = "OPERASI",
                    selectedActivity = null,
                    workOutputInput = ""
                )
            }
        }
    }

    fun setStandbyReason(reason: String) {
        val standbyActivity = ActivityMasterEntity(
            sapCode = "ACT-STB-01",
            activityName = "Standby - $reason",
            unitOfMeasure = "Jam",
            category = "STANDBY",
            description = "Standby: $reason"
        )
        _formState.value = _formState.value.copy(
            standbyReason = reason,
            selectedActivity = standbyActivity,
            workOutputInput = "0"
        )
    }

    fun setBreakdownTrouble(trouble: String) {
        val breakdownActivity = ActivityMasterEntity(
            sapCode = "ACT-BDN-01",
            activityName = "Breakdown - $trouble",
            unitOfMeasure = "Jam",
            category = "BREAKDOWN",
            description = "Breakdown: $trouble"
        )
        _formState.value = _formState.value.copy(
            breakdownTrouble = trouble,
            selectedActivity = breakdownActivity,
            workOutputInput = "0"
        )
    }

    fun setRefueled(refueled: Boolean) {
        _formState.value = _formState.value.copy(
            isRefueled = refueled,
            fuelLitersInput = if (!refueled) "" else _formState.value.fuelLitersInput
        )
    }

    fun setFuelLiters(liters: String) {
        _formState.value = _formState.value.copy(
            fuelLitersInput = liters.replace(',', '.')
        )
    }

    fun setHmAkhir(input: String) {
        val hmAwal = _formState.value.hmAwal
        val cleanInput = input.replace(',', '.')
        val inputDouble = cleanInput.toDoubleOrNull()

        val (isValid, running, errorMsg) = validateHm(hmAwal, inputDouble)

        _formState.value = _formState.value.copy(
            hmAkhirInput = cleanInput,
            isHmValid = isValid,
            hmDailyRunning = running,
            hmErrorMessage = errorMsg
        )
    }

    fun resetHm() {
        val hmAwal = _formState.value.hmAwal
        _formState.value = _formState.value.copy(
            hmAkhirInput = "",
            isHmValid = true,
            hmDailyRunning = 0.0,
            hmErrorMessage = null
        )
    }

    fun quickAddHours(hours: Double) {
        val hmAwal = _formState.value.hmAwal
        val newHmAkhir = hmAwal + hours
        setHmAkhir(String.format(Locale.US, "%.1f", newHmAkhir))
    }

    private fun validateHm(hmAwal: Double, hmAkhir: Double?): Triple<Boolean, Double, String?> {
        if (hmAkhir == null) {
            return Triple(true, 0.0, null)
        }
        return if (hmAkhir < hmAwal) {
            val diff = hmAwal - hmAkhir
            Triple(
                false,
                0.0,
                "HM Akhir (${String.format(Locale.US, "%.1f", hmAkhir)}) lebih kecil dari HM Awal (${String.format(Locale.US, "%.1f", hmAwal)}) selisih -${String.format(Locale.US, "%.1f", diff)}"
            )
        } else {
            val running = hmAkhir - hmAwal
            Triple(true, running, null)
        }
    }

    fun submitReport() {
        val state = _formState.value
        val unit = state.selectedUnit
        val activity = state.selectedActivity
        val operator = state.selectedOperator

        if (unit == null) {
            viewModelScope.launch { _uiEvents.emit(UiMessage.Error("Harap pilih Kode Unit Alat Berat terlebih dahulu")) }
            return
        }
        if (activity == null) {
            viewModelScope.launch { _uiEvents.emit(UiMessage.Error("Harap pilih Aktivitas Unit Kerja terlebih dahulu")) }
            return
        }
        if (operator == null) {
            viewModelScope.launch { _uiEvents.emit(UiMessage.Error("Harap pilih Operator Pelaksana terlebih dahulu")) }
            return
        }
        if (state.workLocation.isBlank()) {
            viewModelScope.launch { _uiEvents.emit(UiMessage.Error("Harap isi Lokasi Kerja Lapangan")) }
            return
        }

        val hmAkhir = state.hmAkhirInput.toDoubleOrNull()
        if (hmAkhir == null || hmAkhir < state.hmAwal) {
            viewModelScope.launch { _uiEvents.emit(UiMessage.Error("Validasi gagal: HM Akhir wajib diisi dan >= HM Awal (${String.format(Locale.US, "%.1f", state.hmAwal)})")) }
            return
        }

        // Breakdown mandatory notes validation
        if (state.unitStatus == "BREAKDOWN" && state.notes.trim().isBlank()) {
            viewModelScope.launch { _uiEvents.emit(UiMessage.Error("Status Breakdown: Kolom Keterangan wajib mencatat detail kerusakan unit!")) }
            return
        }

        // Fuel refuel validation
        val fuelLiters = if (state.isRefueled) {
            val parsed = state.fuelLitersInput.toDoubleOrNull()
            if (parsed == null || parsed <= 0.0) {
                viewModelScope.launch { _uiEvents.emit(UiMessage.Error("Pengisian solar aktif: Harap isi Jumlah Liter Solar dengan benar (> 0)")) }
                return
            }
            parsed
        } else {
            0.0
        }

        val workOutput = if (state.unitStatus == "OPERASI") {
            state.workOutputInput.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isSubmitting = true)

            val newReport = ActivityReportEntity(
                reportDate = state.reportDate,
                supervisorName = _activeSupervisor.value,
                unitCode = unit.unitCode,
                unitType = unit.unitType,
                activityName = activity.activityName,
                sapCode = activity.sapCode,
                unitOfMeasure = activity.unitOfMeasure,
                operatorName = operator.name,
                workLocation = state.workLocation,
                shift = state.shift,
                workDurationHours = state.workDurationHours,
                hmAwal = state.hmAwal,
                hmAkhir = hmAkhir,
                hmDailyRunning = hmAkhir - state.hmAwal,
                workOutput = workOutput,
                notes = state.notes,
                unitStatus = state.unitStatus,
                isRefueled = state.isRefueled,
                fuelLiters = fuelLiters,
                photoPath = state.photoPath,
                photoBase64 = state.photoBase64,
                photoSizeKb = state.photoSizeKb,
                photoDimensions = state.photoDimensions
            )

            val result = repository.submitReport(newReport)
            _formState.value = _formState.value.copy(isSubmitting = false)

            result.onSuccess { saved ->
                if (saved.isSynced) {
                    val photoTag = if (saved.photoSizeKb != null) " + Foto (${saved.photoSizeKb} KB)" else ""
                    val fuelTag = if (saved.isRefueled) " + Solar (${saved.fuelLiters} L)" else ""
                    _uiEvents.emit(UiMessage.Success("Laporan Unit ${saved.unitCode} [${saved.unitStatus}]$fuelTag$photoTag berhasil dikirim & tersinkron real-time!"))
                } else {
                    val photoTag = if (saved.photoPath != null) " bersama file foto lokal" else ""
                    _uiEvents.emit(UiMessage.Info("Laporan [${saved.unitStatus}]$photoTag tersimpan di SQLite lokal. Menunggu koneksi untuk sinkronisasi."))
                }

                _formState.value = _formState.value.copy(
                    unitStatus = "OPERASI",
                    isRefueled = false,
                    fuelLitersInput = "",
                    selectedUnit = null,
                    selectedActivity = null,
                    selectedOperator = null,
                    hmAwal = 0.0,
                    hmAkhirInput = "",
                    hmDailyRunning = 0.0,
                    isHmValid = true,
                    hmErrorMessage = null,
                    workOutputInput = "",
                    notes = "",
                    photoPath = null,
                    photoBase64 = null,
                    photoSizeKb = null,
                    photoDimensions = null
                )
            }.onFailure { err ->
                _uiEvents.emit(UiMessage.Error("Penyimpanan gagal: ${err.message}"))
            }
        }
    }

    /**
     * Client-side automated photo compression logic:
     * - Constrained to max 1280px (preserving aspect ratio)
     * - JPEG compression quality 72% (70%-75% target)
     * - Drastically reduces size from several MB down to ~100-250 KB
     * - Saves compressed file to internal storage and generates Base64
     */
    fun processAndCompressPhoto(context: Context, imageUri: Uri) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isCompressingPhoto = true)
            val result = ImageCompressor.compressImage(context, imageUri)
            _formState.value = _formState.value.copy(isCompressingPhoto = false)
            result.onSuccess { compressed ->
                _formState.value = _formState.value.copy(
                    photoPath = compressed.filePath,
                    photoBase64 = compressed.base64,
                    photoSizeKb = compressed.sizeKb,
                    photoDimensions = compressed.dimensionsText
                )
                _uiEvents.emit(
                    UiMessage.Success(
                        "Foto bukti kegiatan berhasil dikompresi: ${compressed.sizeKb} KB (${compressed.dimensionsText}, JPEG 72%)"
                    )
                )
            }.onFailure { err ->
                _uiEvents.emit(
                    UiMessage.Error("Gagal kompresi foto: ${err.localizedMessage ?: "File tidak valid"}")
                )
            }
        }
    }

    fun removePhoto() {
        val oldPath = _formState.value.photoPath
        if (!oldPath.isNullOrBlank()) {
            try {
                java.io.File(oldPath).delete()
            } catch (_: Exception) {}
        }
        _formState.value = _formState.value.copy(
            photoPath = null,
            photoBase64 = null,
            photoSizeKb = null,
            photoDimensions = null
        )
        viewModelScope.launch {
            _uiEvents.emit(UiMessage.Info("Foto bukti kegiatan dihapus"))
        }
    }

    fun syncPendingReports() {
        viewModelScope.launch {
            _isSyncingPending.value = true
            val result = repository.syncAllPendingReports()
            _isSyncingPending.value = false
            result.onSuccess { count ->
                if (count > 0) {
                    _uiEvents.emit(UiMessage.Success("Berhasil menyinkronkan $count laporan tertunda ke Web Admin!"))
                } else {
                    _uiEvents.emit(UiMessage.Info("Semua laporan sudah tersinkron."))
                }
            }.onFailure { err ->
                _uiEvents.emit(UiMessage.Error("Gagal menyinkronkan: ${err.message}"))
            }
        }
    }

    // Web Admin console actions (Add, Edit, Delete master data)
    fun adminAddUnit(unit: UnitPayload) {
        CompanyServerSimulator.addOrUpdateUnit(unit)
        fetchMasterData()
    }

    fun adminDeleteUnit(unitCode: String) {
        CompanyServerSimulator.deleteUnit(unitCode)
        fetchMasterData()
    }

    fun adminAddActivity(activity: ActivityPayload) {
        CompanyServerSimulator.addOrUpdateActivity(activity)
        fetchMasterData()
    }

    fun adminDeleteActivity(sapCode: String) {
        CompanyServerSimulator.deleteActivity(sapCode)
        fetchMasterData()
    }

    fun adminAddOperator(operator: OperatorPayload) {
        CompanyServerSimulator.addOrUpdateOperator(operator)
        fetchMasterData()
    }

    fun adminDeleteOperator(operatorId: String) {
        CompanyServerSimulator.deleteOperator(operatorId)
        fetchMasterData()
    }

    fun deleteReport(id: Long) {
        viewModelScope.launch {
            repository.deleteReport(id)
            _uiEvents.emit(UiMessage.Info("Laporan dihapus dari riwayat"))
        }
    }
}

class HeavyTrackViewModelFactory(
    private val repository: HeavyTrackRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HeavyTrackViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HeavyTrackViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
