package com.example.data.remote

import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Simulates company central backend and Web Admin database.
 * Supports:
 * - GET /api/master-data (filtered by supervisor)
 * - POST /api/aktivitas-unit
 * - Admin web updates (tambah, edit, hapus master data)
 * - Offline simulation toggle
 */
object CompanyServerSimulator {
    var isSimulatedOffline: Boolean = false

    // Central master units in company web admin
    val serverUnits = CopyOnWriteArrayList<UnitPayload>().apply {
        addAll(
            listOf(
                UnitPayload("HEH1", "EXCAVATOR", "Komatsu PC200-8", 4125.5, "Budi Santoso", "OPERASIONAL"),
                UnitPayload("EXC-02", "EXCAVATOR", "Caterpillar 320D", 6280.0, "Budi Santoso", "OPERASIONAL"),
                UnitPayload("DT-101", "DUMP TRUCK", "Hino FM 260 Ti", 3450.2, "Budi Santoso", "OPERASIONAL"),
                UnitPayload("DT-102", "DUMP TRUCK", "Scania P360", 2890.4, "Budi Santoso", "OPERASIONAL"),
                UnitPayload("DZ-01", "BULLDOZER", "Komatsu D85ESS-2", 5130.8, "Budi Santoso", "OPERASIONAL"),
                UnitPayload("GRD-01", "MOTOR GRADER", "Caterpillar 140K", 1870.0, "Budi Santoso", "OPERASIONAL"),
                // Units assigned to other supervisors
                UnitPayload("EXC-09", "EXCAVATOR", "Hitachi ZX210-5G", 3990.2, "Agus Wijaya", "OPERASIONAL"),
                UnitPayload("DT-205", "DUMP TRUCK", "Volvo FMX 440", 4150.0, "Agus Wijaya", "OPERASIONAL"),
                UnitPayload("DZ-04", "BULLDOZER", "CAT D6R", 2980.5, "Rudi Hartono", "OPERASIONAL")
            )
        )
    }

    // Central master activities in company web admin
    val serverActivities = CopyOnWriteArrayList<ActivityPayload>().apply {
        addAll(
            listOf(
                ActivityPayload("ACT-SAP-101", "Galian Tanah Lunak", "m3", "Penggalian area cut site A", "PRODUKSI"),
                ActivityPayload("ACT-SAP-102", "Loading Overburden", "m3", "Pemuatan tanah penutup ke dump truck", "PRODUKSI"),
                ActivityPayload("ACT-SAP-103", "Ripping Batuan Keras", "m3", "Pemecahan lapisan batuan menggunakan ripper dozer", "PRODUKSI"),
                ActivityPayload("ACT-SAP-201", "Dumping & Spreading", "m2", "Penghamparan material di area disposal", "SUPPORT"),
                ActivityPayload("ACT-SAP-202", "Grading Mainroad Tambang", "m", "Perawatan kerataan dan grading jalan hauling", "INFRASTRUKTUR"),
                ActivityPayload("ACT-SAP-203", "Clearing Lahan & Grubbing", "m2", "Pembersihan semak belukar dan vegetasi", "INFRASTRUKTUR"),
                ActivityPayload("ACT-SAP-301", "Hauling Material Subgrade", "ton", "Pengangkutan material perbaikan jalan", "HAULING"),
                ActivityPayload("ACT-SAP-302", "Pembuatan Parit Drainage", "m", "Penggalian parit saluran air tambang", "INFRASTRUKTUR")
            )
        )
    }

    // Central master operators in company web admin
    val serverOperators = CopyOnWriteArrayList<OperatorPayload>().apply {
        addAll(
            listOf(
                OperatorPayload("OPR-001", "Ahmad Fauzi", "NIK-94821", "Budi Santoso", "Senior Operator - Class A"),
                OperatorPayload("OPR-002", "Bambang Sudrajat", "NIK-88234", "Budi Santoso", "Senior Operator - Class A"),
                OperatorPayload("OPR-003", "Dedi Supriyadi", "NIK-77412", "Budi Santoso", "Operator Alat Berat - Class B"),
                OperatorPayload("OPR-004", "Eko Prasetyo", "NIK-65129", "Budi Santoso", "Operator Dump Truck - Class B"),
                OperatorPayload("OPR-005", "Hendra Gunawan", "NIK-91203", "Budi Santoso", "Master Grader Operator"),
                // Operators for Agus Wijaya
                OperatorPayload("OPR-006", "Indra Kusuma", "NIK-83190", "Agus Wijaya", "Senior Excavator Operator"),
                OperatorPayload("OPR-007", "Joko Susilo", "NIK-74102", "Agus Wijaya", "Dump Truck Driver"),
                // Operators for Rudi Hartono
                OperatorPayload("OPR-008", "Kurniawan Putra", "NIK-69214", "Rudi Hartono", "Dozer Operator")
            )
        )
    }

    // Central received reports in Web Admin
    val receivedReportsOnServer = CopyOnWriteArrayList<PostActivityReportRequest>()

    suspend fun fetchMasterData(supervisorName: String): ApiResponse<MasterDataPayload> {
        delay(600) // network latency
        if (isSimulatedOffline) {
            throw java.io.IOException("Koneksi gagal: Perangkat berada di luar jangkauan jaringan (Offline Lapangan)")
        }

        // Supervisor isolation
        val filteredUnits = serverUnits.filter { it.resolvedSupervisor.equals(supervisorName, ignoreCase = true) }
        val filteredOperators = serverOperators.filter { it.resolvedSupervisor.equals(supervisorName, ignoreCase = true) }
        val activities = serverActivities.toList()

        return ApiResponse(
            status = "SUCCESS",
            message = "Data master berhasil disinkronkan dari Web Admin untuk pengawas: $supervisorName",
            data = MasterDataPayload(
                units = filteredUnits,
                activities = activities,
                aktivitas = activities,
                operators = filteredOperators
            )
        )
    }

    suspend fun postActivityReport(report: PostActivityReportRequest): ApiResponse<String> {
        delay(700)
        if (isSimulatedOffline) {
            throw java.io.IOException("Jaringan tidak tersedia: Laporan disimpan ke penyimpanan lokal perangkat")
        }

        // Add to server log
        receivedReportsOnServer.add(report)

        val targetUnitCode = report.kode_unit ?: report.unit_code ?: ""
        val lastHmVal = report.hm_akhir ?: 0.0

        // Update server unit last HM
        val index = serverUnits.indexOfFirst { it.resolvedUnitCode.equals(targetUnitCode, ignoreCase = true) }
        if (index >= 0) {
            val old = serverUnits[index]
            serverUnits[index] = old.copy(last_hm = lastHmVal, lastHm = lastHmVal)
        }

        val hasPhoto = !report.foto_bukti.isNullOrBlank() || !report.photo_base64.isNullOrBlank()
        val photoInfo = if (hasPhoto) " [Foto Bukti Kegiatan Terverifikasi (${report.photo_size_kb ?: 145} KB, ${report.photo_dimensions ?: "1280x960 px"})]" else ""
        val statusInfo = " [Status: ${report.status_unit ?: "OPERASI"}]"
        val fuelInfo = if (report.is_isi_solar == true) " [Solar: ${report.jumlah_liter_solar} L]" else ""

        return ApiResponse(
            status = "SUCCESS",
            message = "Laporan berhasil dikirim dan diverifikasi oleh Server Web Admin.$statusInfo$fuelInfo. HM Unit diperbarui ke $lastHmVal.$photoInfo",
            data = "REPORT-ACK-${System.currentTimeMillis()}"
        )
    }

    // Methods for Web Admin simulator (Tambah / Edit / Hapus Master Data)
    fun addOrUpdateUnit(unit: UnitPayload) {
        val idx = serverUnits.indexOfFirst { it.resolvedUnitCode.equals(unit.resolvedUnitCode, ignoreCase = true) }
        if (idx >= 0) {
            serverUnits[idx] = unit
        } else {
            serverUnits.add(unit)
        }
    }

    fun deleteUnit(unitCode: String) {
        serverUnits.removeAll { it.resolvedUnitCode.equals(unitCode, ignoreCase = true) }
    }

    fun addOrUpdateActivity(activity: ActivityPayload) {
        val idx = serverActivities.indexOfFirst { it.resolvedSapCode.equals(activity.resolvedSapCode, ignoreCase = true) }
        if (idx >= 0) {
            serverActivities[idx] = activity
        } else {
            serverActivities.add(activity)
        }
    }

    fun deleteActivity(sapCode: String) {
        serverActivities.removeAll { it.resolvedSapCode.equals(sapCode, ignoreCase = true) }
    }

    fun addOrUpdateOperator(operator: OperatorPayload) {
        val idx = serverOperators.indexOfFirst { it.resolvedOperatorId.equals(operator.resolvedOperatorId, ignoreCase = true) }
        if (idx >= 0) {
            serverOperators[idx] = operator
        } else {
            serverOperators.add(operator)
        }
    }

    fun deleteOperator(operatorId: String) {
        serverOperators.removeAll { it.resolvedOperatorId.equals(operatorId, ignoreCase = true) }
    }
}
