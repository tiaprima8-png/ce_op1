package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.remote.ActivityPayload
import com.example.data.remote.OperatorPayload
import com.example.data.remote.UnitPayload
import com.example.data.repository.SyncLogItem
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.ErrorRose50
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmber50
import com.example.ui.viewmodel.HeavyTrackViewModel

@Composable
fun MasterDataScreen(
    viewModel: HeavyTrackViewModel
) {
    val activeSupervisor by viewModel.activeSupervisor.collectAsState()
    val isOfflineSimulated by viewModel.isOfflineSimulated.collectAsState()
    val isSyncingMaster by viewModel.isSyncingMaster.collectAsState()
    val apiBaseUrl by viewModel.apiBaseUrl.collectAsState()
    val isTestingConnection by viewModel.isTestingConnection.collectAsState()
    val syncLogs by viewModel.syncLogs.collectAsState()
    val units by viewModel.units.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val operators by viewModel.operators.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var showUrlConfigDialog by remember { mutableStateOf(false) }
    var showAddUnitDialog by remember { mutableStateOf(false) }
    var showAddActivityDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("master_data_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // CARD 1: Konfigurasi Endpoint & Status API Web Admin
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("card_api_web_admin_config"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Emerald50, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Emerald600,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Integrasi API Web Admin",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                )
                                Text(
                                    text = "Koneksi REST API & Auto-Sync Lapangan",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                            }
                        }

                        IconButton(
                            onClick = { showUrlConfigDialog = true },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Ubah URL", tint = Emerald600)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // URL Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showUrlConfigDialog = true },
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "URL Endpoint Web Admin:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Slate500,
                                        fontSize = 11.sp
                                    )
                                )
                                Text(
                                    text = apiBaseUrl,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Slate900,
                                        fontWeight = FontWeight.SemiBold,
                                        fontFamily = FontFamily.Monospace
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = Slate400,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Buttons: Test Connection & Sync Now
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.testApiConnection(apiBaseUrl) { _, _ -> }
                            },
                            enabled = !isTestingConnection,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald600),
                            modifier = Modifier.weight(1f)
                        ) {
                            if (isTestingConnection) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Emerald600)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Menguji...", fontSize = 12.sp, color = Emerald600)
                            } else {
                                Icon(Icons.Default.NetworkCheck, contentDescription = null, modifier = Modifier.size(16.dp), tint = Emerald600)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tes Koneksi", fontSize = 12.sp, color = Emerald600, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { viewModel.fetchMasterData() },
                            enabled = !isSyncingMaster,
                            colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.3f)
                        ) {
                            if (isSyncingMaster) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sinkron...", fontSize = 12.sp)
                            } else {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Sinkronkan API", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // CARD 2: Isolasi Pengawas & Mode Offline
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Isolasi Sesi Pengawas Aktif:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate700
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Budi Santoso", "Agus Wijaya", "Rudi Hartono").forEach { sup ->
                            val isSelected = activeSupervisor.equals(sup, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) Emerald600 else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Emerald600 else Slate200),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.switchSupervisor(sup) }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = sup.split(" ").first(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isSelected) Color.White else Slate700
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pengawas Aktif: $activeSupervisor",
                            style = MaterialTheme.typography.bodySmall.copy(color = Slate500, fontSize = 11.sp)
                        )
                        OutlinedButton(
                            onClick = { viewModel.logout() },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(12.dp), tint = Slate500)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Keluar Sesi", fontSize = 11.sp, color = Slate700)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Mode Offline Lapangan Switch
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isOfflineSimulated) WarningAmber50 else Color(0xFFF8FAFC),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isOfflineSimulated) WarningAmber.copy(alpha = 0.5f) else Slate200),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isOfflineSimulated) Icons.Default.WifiOff else Icons.Default.Wifi,
                                    contentDescription = null,
                                    tint = if (isOfflineSimulated) Color(0xFFB45309) else Emerald600,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isOfflineSimulated) "Simulasi Offline Lapangan" else "Mode Online Terhubung",
                                        fontWeight = FontWeight.Bold,
                                        color = if (isOfflineSimulated) Color(0xFFB45309) else Slate900,
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        text = if (isOfflineSimulated) "Simpan entri ke SQLite lokal (is_synced = 0)" else "Kirim langsung ke POST /api/aktivitas-unit",
                                        fontSize = 11.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            Switch(
                                checked = isOfflineSimulated,
                                onCheckedChange = { viewModel.toggleOfflineSimulation() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = WarningAmber,
                                    uncheckedThumbColor = Color.White,
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
            }
        }

        // CARD 3: Multi-Tab Master Data, Log API, dan Web Admin Simulator
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Color.White,
                        contentColor = Emerald600,
                        edgePadding = 12.dp
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Unit (${units.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Aktivitas (${activities.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Operator (${operators.size})", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                        )
                        Tab(
                            selected = selectedTab == 3,
                            onClick = { selectedTab = 3 },
                            text = { Text("Log Sync (${syncLogs.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Emerald700) }
                        )
                        Tab(
                            selected = selectedTab == 4,
                            onClick = { selectedTab = 4 },
                            text = { Text("Panduan API", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                        )
                    }

                    Box(modifier = Modifier.padding(14.dp)) {
                        when (selectedTab) {
                            0 -> UnitsTabContent(units, onDelete = { viewModel.adminDeleteUnit(it) })
                            1 -> ActivitiesTabContent(activities, onDelete = { viewModel.adminDeleteActivity(it) })
                            2 -> OperatorsTabContent(operators, onDelete = { viewModel.adminDeleteOperator(it) })
                            3 -> SyncLogsTabContent(syncLogs)
                            4 -> ApiGuideTabContent(apiBaseUrl = apiBaseUrl)
                        }
                    }
                }
            }
        }
    }

    // Dialog Ganti URL Web Admin
    if (showUrlConfigDialog) {
        UrlConfigDialog(
            currentUrl = apiBaseUrl,
            onDismiss = { showUrlConfigDialog = false },
            onSave = { newUrl ->
                viewModel.updateApiBaseUrl(newUrl)
                showUrlConfigDialog = false
            }
        )
    }

    // Dialog Tambah Unit di Web Admin Simulator
    if (showAddUnitDialog) {
        AddUnitAdminDialog(
            activeSupervisor = activeSupervisor,
            onDismiss = { showAddUnitDialog = false },
            onAdd = { unit ->
                viewModel.adminAddUnit(unit)
                showAddUnitDialog = false
            }
        )
    }

    // Dialog Tambah Aktivitas SAP di Web Admin Simulator
    if (showAddActivityDialog) {
        AddActivityAdminDialog(
            onDismiss = { showAddActivityDialog = false },
            onAdd = { act ->
                viewModel.adminAddActivity(act)
                showAddActivityDialog = false
            }
        )
    }
}

@Composable
fun SyncLogsTabContent(logs: List<SyncLogItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Log Transaksi Jaringan Real-Time:", fontSize = 12.sp, color = Slate500)
            Surface(shape = RoundedCornerShape(4.dp), color = Emerald100) {
                Text("Live", color = Emerald900, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
            }
        }

        if (logs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada log request. Tekan 'Sinkronkan API' untuk memulai.", color = Slate400, fontSize = 12.sp)
            }
        } else {
            logs.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (item.isSuccess) Color(0xFFF8FAFC) else ErrorRose50,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (item.isSuccess) Slate200 else ErrorRose.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (item.method == "GET") Emerald100 else Color(0xFFDBEAFE)
                                ) {
                                    Text(
                                        text = item.method,
                                        color = if (item.method == "GET") Emerald900 else Color(0xFF1E40AF),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.endpoint,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Slate900,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(text = item.timestamp, fontSize = 10.sp, color = Slate400)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = item.detail, fontSize = 11.sp, color = Slate700, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                text = item.status,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (item.isSuccess) Emerald700 else ErrorRose
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ApiGuideTabContent(apiBaseUrl: String) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = Emerald50,
            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Spesifikasi Kontrak API Web Admin", fontWeight = FontWeight.Bold, color = Emerald900, fontSize = 13.sp)
                Text(
                    "Aplikasi Android HeavyTrack secara otomatis memanggil endpoint berikut pada server Web Admin:",
                    fontSize = 11.sp,
                    color = Emerald700
                )
            }
        }

        // Endpoint 1
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("1. GET /api/master-data?supervisor={nama}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900, fontFamily = FontFamily.Monospace)
                Text("Mengambil daftar unit, aktivitas & SAP, dan operator. Mendukung format JSON snake_case dan camelCase.", fontSize = 11.sp, color = Slate500)
            }
        }

        // Endpoint 2
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("2. POST /api/aktivitas-unit", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Slate900, fontFamily = FontFamily.Monospace)
                Text("Mengirim laporan lapangan (unit, operator, hm_awal, hm_akhir, hm_daily_running, hasil_kerja).", fontSize = 11.sp, color = Slate500)
            }
        }
    }
}

@Composable
fun UrlConfigDialog(
    currentUrl: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var inputUrl by remember { mutableStateOf(currentUrl) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = Emerald600, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Pengaturan URL Web Admin", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Masukkan URL backend web admin perusahaan atau AI Studio applet URL:",
                    fontSize = 12.sp,
                    color = Slate500
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = inputUrl,
                    onValueChange = { inputUrl = it },
                    placeholder = { Text("https://contoh-web-admin.run.app/") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Emerald600,
                        unfocusedBorderColor = Slate200
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Pilihan Cepat URL:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Slate700)
                Spacer(modifier = Modifier.height(4.dp))

                listOf(
                    "Portal Web Admin (1-portalce)" to "https://1-portalce.ai.studio/",
                    "Web Admin AI Studio App" to "https://aistudio.google.com/apps/390ddce8-0bb3-4b0a-bd67-8b3a490b66ff",
                    "Localhost Emulator (10.0.2.2)" to "http://10.0.2.2:8000/"
                ).forEach { (label, url) ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { inputUrl = url }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Slate700)
                            Text(url.take(24) + "...", fontSize = 10.sp, color = Slate400, fontFamily = FontFamily.Monospace)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Batal")
                    }
                    Button(
                        onClick = { onSave(inputUrl) },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Text("Simpan URL", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun UnitsTabContent(
    units: List<com.example.data.local.entity.UnitEntity>,
    onDelete: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Daftar Unit Terdaftar di SQLite Lokal (Sesuai Pengawas Aktif):", fontSize = 12.sp, color = Slate500)
        if (units.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Tidak ada unit untuk pengawas ini.", color = Slate400, fontSize = 12.sp)
            }
        } else {
            units.forEach { unit ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(unit.unitCode, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Emerald100) {
                                    Text(unit.unitType, color = Emerald900, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text("${unit.brandModel} • Ditugaskan ke: ${unit.supervisorAssigned}", fontSize = 11.sp, color = Slate500)
                            Text("HM Terakhir: ${String.format("%.1f", unit.lastHm)} Jam", fontSize = 11.sp, color = Emerald700, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivitiesTabContent(
    activities: List<com.example.data.local.entity.ActivityMasterEntity>,
    onDelete: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Daftar Master Aktivitas & Kode SAP (Terikat ke Dropdown):", fontSize = 12.sp, color = Slate500)
        if (activities.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Tidak ada aktivitas kerja.", color = Slate400, fontSize = 12.sp)
            }
        } else {
            activities.forEach { act ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(act.activityName, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(shape = RoundedCornerShape(4.dp), color = Emerald100) {
                                    Text(act.sapCode, color = Emerald900, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Text("Satuan: ${act.unitOfMeasure}", fontSize = 11.sp, color = Slate500)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OperatorsTabContent(
    operators: List<com.example.data.local.entity.OperatorEntity>,
    onDelete: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Daftar Operator Terdaftar di Bawah Pengawas Aktif:", fontSize = 12.sp, color = Slate500)
        if (operators.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Tidak ada operator untuk pengawas ini.", color = Slate400, fontSize = 12.sp)
            }
        } else {
            operators.forEach { op ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFF8FAFC),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(op.name, fontWeight = FontWeight.Bold, color = Slate900, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("(${op.badgeNumber})", color = Slate500, fontSize = 11.sp)
                            }
                            Text("Pengawas: ${op.supervisorAssigned}", fontSize = 11.sp, color = Slate500)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddUnitAdminDialog(
    activeSupervisor: String,
    onDismiss: () -> Unit,
    onAdd: (UnitPayload) -> Unit
) {
    var code by remember { mutableStateOf("EXC-99") }
    var type by remember { mutableStateOf("EXCAVATOR") }
    var brand by remember { mutableStateOf("Komatsu PC300-8") }
    var lastHm by remember { mutableStateOf("1250.0") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Tambah Unit di Web Admin", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                Text("Otomatis ditugaskan ke $activeSupervisor", fontSize = 12.sp, color = Slate500)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Kode Unit") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Jenis Unit (EXCAVATOR / DUMP TRUCK / DOZER)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Merk & Model") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = lastHm,
                    onValueChange = { lastHm = it },
                    label = { Text("HM Terakhir") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Batal")
                    }
                    Button(
                        onClick = {
                            val hm = lastHm.toDoubleOrNull() ?: 1000.0
                            onAdd(UnitPayload(code, type.uppercase(), brand, hm, activeSupervisor))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simpan & Sync")
                    }
                }
            }
        }
    }
}

@Composable
fun AddActivityAdminDialog(
    onDismiss: () -> Unit,
    onAdd: (ActivityPayload) -> Unit
) {
    var name by remember { mutableStateOf("Pengupasan Top Soil") }
    var sap by remember { mutableStateOf("ACT-SAP-404") }
    var unit by remember { mutableStateOf("m3") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text("Tambah Aktivitas SAP di Web Admin", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Slate900)
                Text("Akan otomatis muncul di dropdown setelah sinkronisasi", fontSize = 12.sp, color = Slate500)

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Aktivitas") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = sap,
                    onValueChange = { sap = it },
                    label = { Text("Kode SAP (misal ACT-SAP-404)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Satuan (m, m2, m3, ton, rit)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Batal")
                    }
                    Button(
                        onClick = {
                            onAdd(ActivityPayload(sap, name, unit))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald600),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Simpan & Sync")
                    }
                }
            }
        }
    }
}
