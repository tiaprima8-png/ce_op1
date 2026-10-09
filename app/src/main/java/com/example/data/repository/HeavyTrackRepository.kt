package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ActivityMasterEntity
import com.example.data.local.entity.ActivityReportEntity
import com.example.data.local.entity.OperatorEntity
import com.example.data.local.entity.UnitEntity
import com.example.data.remote.ApiClientManager
import com.example.data.remote.CompanyServerSimulator
import com.example.data.remote.PostActivityReportRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SyncLogItem(
    val timestamp: String,
    val endpoint: String,
    val method: String,
    val status: String,
    val detail: String,
    val isSuccess: Boolean
)

class HeavyTrackRepository(
    private val database: AppDatabase,
    val apiClientManager: ApiClientManager
) {
    private val unitDao = database.unitDao()
    private val activityDao = database.activityMasterDao()
    private val operatorDao = database.operatorDao()
    private val reportDao = database.activityReportDao()

    private val _syncLogs = MutableStateFlow<List<SyncLogItem>>(emptyList())
    val syncLogs: StateFlow<List<SyncLogItem>> = _syncLogs.asStateFlow()

    private fun addLog(endpoint: String, method: String, status: String, detail: String, isSuccess: Boolean) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val item = SyncLogItem(time, endpoint, method, status, detail, isSuccess)
        _syncLogs.value = listOf(item) + _syncLogs.value.take(20)
    }

    fun getUnitsForSupervisor(supervisor: String): Flow<List<UnitEntity>> =
        unitDao.getUnitsForSupervisor(supervisor)

    fun getAllUnits(): Flow<List<UnitEntity>> =
        unitDao.getAllUnits()

    fun getAllActivities(): Flow<List<ActivityMasterEntity>> =
        activityDao.getAllActivities()

    fun getOperatorsForSupervisor(supervisor: String): Flow<List<OperatorEntity>> =
        operatorDao.getOperatorsForSupervisor(supervisor)

    fun getAllReports(): Flow<List<ActivityReportEntity>> =
        reportDao.getAllReports()

    fun getReportsForSupervisor(supervisor: String): Flow<List<ActivityReportEntity>> =
        reportDao.getReportsForSupervisor(supervisor)

    fun getUnsyncedReports(): Flow<List<ActivityReportEntity>> =
        reportDao.getUnsyncedReports()

    fun getUnsyncedCount(): Flow<Int> =
        reportDao.getUnsyncedCount()

    suspend fun getUnitByCode(unitCode: String): UnitEntity? =
        unitDao.getUnitByCode(unitCode)

    suspend fun seedInitialDataIfEmpty(activeSupervisor: String) = withContext(Dispatchers.IO) {
        try {
            fetchMasterData(activeSupervisor)
        } catch (_: Exception) {
            val initialUnits = listOf(
                UnitEntity("HEH1", "EXCAVATOR", "Komatsu PC200-8", 4125.5, activeSupervisor),
                UnitEntity("EXC-02", "EXCAVATOR", "Caterpillar 320D", 6280.0, activeSupervisor),
                UnitEntity("DT-101", "DUMP TRUCK", "Hino FM 260 Ti", 3450.2, activeSupervisor),
                UnitEntity("DZ-01", "BULLDOZER", "Komatsu D85ESS-2", 5130.8, activeSupervisor)
            )
            unitDao.insertUnits(initialUnits)

            val initialActivities = listOf(
                ActivityMasterEntity("ACT-SAP-101", "Galian Tanah Lunak", "m3", "Galian area cut", "PRODUKSI"),
                ActivityMasterEntity("ACT-SAP-102", "Loading Overburden", "m3", "Pemuatan tanah penutup", "PRODUKSI"),
                ActivityMasterEntity("ACT-SAP-201", "Dumping & Spreading", "m2", "Penghamparan disposal", "SUPPORT"),
                ActivityMasterEntity("ACT-SAP-202", "Grading Mainroad Tambang", "m", "Perawatan jalan", "INFRASTRUKTUR")
            )
            activityDao.insertActivities(initialActivities)

            val initialOperators = listOf(
                OperatorEntity("OPR-001", "Ahmad Fauzi", "NIK-94821", activeSupervisor),
                OperatorEntity("OPR-002", "Bambang Sudrajat", "NIK-88234", activeSupervisor),
                OperatorEntity("OPR-003", "Dedi Supriyadi", "NIK-77412", activeSupervisor)
            )
            operatorDao.insertOperators(initialOperators)
        }
    }

    suspend fun fetchMasterData(supervisorName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val (payload, message) = apiClientManager.fetchMasterData(supervisorName)

            // 1. Process Units
            val incomingUnits = payload.units ?: emptyList()
            if (incomingUnits.isNotEmpty()) {
                val unitEntities = incomingUnits.map {
                    UnitEntity(
                        unitCode = it.resolvedUnitCode,
                        unitType = it.resolvedUnitType,
                        brandModel = it.resolvedBrandModel,
                        lastHm = it.resolvedLastHm,
                        supervisorAssigned = if (it.resolvedSupervisor.isNotBlank()) it.resolvedSupervisor else supervisorName,
                        status = it.resolvedStatus,
                        updatedAt = System.currentTimeMillis()
                    )
                }
                unitDao.insertUnits(unitEntities)
            }

            // 2. Process Activities
            val incomingActivities = payload.resolvedActivities
            if (incomingActivities.isNotEmpty()) {
                val actEntities = incomingActivities.map {
                    ActivityMasterEntity(
                        sapCode = it.resolvedSapCode,
                        activityName = it.resolvedActivityName,
                        unitOfMeasure = it.resolvedUnitOfMeasure,
                        description = it.resolvedDescription,
                        category = it.resolvedCategory,
                        updatedAt = System.currentTimeMillis()
                    )
                }
                activityDao.clearAllActivities()
                activityDao.insertActivities(actEntities)
            }

            // 3. Process Operators
            val incomingOperators = payload.operators ?: emptyList()
            if (incomingOperators.isNotEmpty()) {
                val opEntities = incomingOperators.map {
                    OperatorEntity(
                        operatorId = it.resolvedOperatorId,
                        name = it.resolvedName,
                        badgeNumber = it.resolvedBadgeNumber,
                        supervisorAssigned = if (it.resolvedSupervisor.isNotBlank()) it.resolvedSupervisor else supervisorName,
                        skillLevel = it.resolvedSkillLevel,
                        status = it.resolvedStatus,
                        updatedAt = System.currentTimeMillis()
                    )
                }
                operatorDao.insertOperators(opEntities)
            }

            val summary = "${incomingUnits.size} Unit, ${incomingActivities.size} Aktivitas, ${incomingOperators.size} Operator"
            addLog("GET /api/master-data", "GET", "200 OK", summary, true)

            Result.success("$message ($summary)")
        } catch (e: Exception) {
            addLog("GET /api/master-data", "GET", "ERROR", e.localizedMessage ?: "Gagal", false)
            Result.failure(e)
        }
    }

    suspend fun submitReport(report: ActivityReportEntity): Result<ActivityReportEntity> = withContext(Dispatchers.IO) {
        try {
            val insertedId = reportDao.insertReport(report.copy(isSynced = false))
            val savedReport = report.copy(id = insertedId)

            val apiReq = PostActivityReportRequest.create(
                clientUuid = savedReport.clientUuid,
                reportDate = savedReport.reportDate,
                supervisorName = savedReport.supervisorName,
                unitCode = savedReport.unitCode,
                unitType = savedReport.unitType,
                activityName = savedReport.activityName,
                sapCode = savedReport.sapCode,
                unitOfMeasure = savedReport.unitOfMeasure,
                operatorName = savedReport.operatorName,
                workLocation = savedReport.workLocation,
                shift = savedReport.shift,
                workDurationHours = savedReport.workDurationHours,
                hmAwal = savedReport.hmAwal,
                hmAkhir = savedReport.hmAkhir,
                hmDailyRunning = savedReport.hmDailyRunning,
                workOutput = savedReport.workOutput,
                notes = savedReport.notes,
                unitStatus = savedReport.unitStatus,
                isRefueled = savedReport.isRefueled,
                fuelLiters = savedReport.fuelLiters,
                photoBase64 = savedReport.photoBase64,
                photoSizeKb = savedReport.photoSizeKb,
                photoDimensions = savedReport.photoDimensions,
                photoPath = savedReport.photoPath
            )

            try {
                val responseMsg = apiClientManager.postActivityReport(apiReq)
                reportDao.markReportAsSynced(insertedId)
                unitDao.updateUnitHm(savedReport.unitCode, savedReport.hmAkhir)

                val fuelTag = if (savedReport.isRefueled) " [Solar: ${savedReport.fuelLiters}L]" else ""
                addLog("POST /api/aktivitas-unit", "POST", "200 OK", "Unit ${savedReport.unitCode} [${savedReport.unitStatus}]$fuelTag (HM ${savedReport.hmAkhir})", true)
                Result.success(savedReport.copy(isSynced = true, syncedAt = System.currentTimeMillis()))
            } catch (netEx: Exception) {
                unitDao.updateUnitHm(savedReport.unitCode, savedReport.hmAkhir)
                addLog("POST /api/aktivitas-unit", "POST", "OFFLINE/PENDING", "Unit ${savedReport.unitCode} [${savedReport.unitStatus}] disimpan di SQLite lokal", false)
                Result.success(savedReport.copy(isSynced = false))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncAllPendingReports(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val pending = reportDao.getUnsyncedReportsList()
            if (pending.isEmpty()) {
                return@withContext Result.success(0)
            }

            var successCount = 0
            for (report in pending) {
                try {
                    val apiReq = PostActivityReportRequest.create(
                        clientUuid = report.clientUuid,
                        reportDate = report.reportDate,
                        supervisorName = report.supervisorName,
                        unitCode = report.unitCode,
                        unitType = report.unitType,
                        activityName = report.activityName,
                        sapCode = report.sapCode,
                        unitOfMeasure = report.unitOfMeasure,
                        operatorName = report.operatorName,
                        workLocation = report.workLocation,
                        shift = report.shift,
                        workDurationHours = report.workDurationHours,
                        hmAwal = report.hmAwal,
                        hmAkhir = report.hmAkhir,
                        hmDailyRunning = report.hmDailyRunning,
                        workOutput = report.workOutput,
                        notes = report.notes,
                        unitStatus = report.unitStatus,
                        isRefueled = report.isRefueled,
                        fuelLiters = report.fuelLiters,
                        photoBase64 = report.photoBase64,
                        photoSizeKb = report.photoSizeKb,
                        photoDimensions = report.photoDimensions,
                        photoPath = report.photoPath
                    )
                    apiClientManager.postActivityReport(apiReq)
                    reportDao.markReportAsSynced(report.id)
                    unitDao.updateUnitHm(report.unitCode, report.hmAkhir)
                    successCount++
                } catch (_: Exception) {}
            }

            addLog("POST /api/aktivitas-unit (BATCH)", "POST", "SYNC COMPLETED", "Sinkron $successCount dari ${pending.size} laporan", true)
            Result.success(successCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun testConnection(url: String): Pair<Boolean, String> {
        val result = apiClientManager.testConnection(url)
        addLog("TEST $url", "PING", if (result.first) "CONNECTED" else "FAILED", result.second, result.first)
        return result
    }

    suspend fun deleteReport(reportId: Long) = withContext(Dispatchers.IO) {
        reportDao.deleteReport(reportId)
    }
}
