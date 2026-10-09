package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ActivitySelectorItemCard
import com.example.ui.components.HmCalculationCard
import com.example.ui.components.OperatorSelectorItemCard
import com.example.ui.components.PhotoProofCard
import com.example.ui.components.SearchableSelectorSheet
import com.example.ui.components.SelectorFieldTrigger
import com.example.ui.components.UnitSelectorItemCard
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald50
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald700
import com.example.ui.theme.Emerald900
import com.example.ui.theme.ErrorRose
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmber50
import com.example.ui.viewmodel.HeavyTrackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormReportScreen(
    viewModel: HeavyTrackViewModel,
    onNavigateToHistory: () -> Unit
) {
    val formState by viewModel.formState.collectAsState()
    val activeSupervisor by viewModel.activeSupervisor.collectAsState()
    val isOfflineSimulated by viewModel.isOfflineSimulated.collectAsState()
    val units by viewModel.units.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val operators by viewModel.operators.collectAsState()
    val isSyncingMaster by viewModel.isSyncingMaster.collectAsState()

    // Selector Bottom Sheet visibility states
    var isUnitSheetOpen by remember { mutableStateOf(false) }
    var isActivitySheetOpen by remember { mutableStateOf(false) }
    var isOperatorSheetOpen by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("form_report_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. HEADER FORM: Tanggal otomatis, Pengawas aktif, Sinyal status
        item {
            HeaderFormCard(
                reportDate = formState.reportDate,
                supervisorName = activeSupervisor,
                isOffline = isOfflineSimulated,
                isSyncing = isSyncingMaster,
                onRefreshSync = { viewModel.fetchMasterData() },
                onToggleOffline = { viewModel.toggleOfflineSimulation() },
                onLogout = { viewModel.logout() }
            )
        }

        // 2. INPUT OPERASIONAL CARD
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("operational_inputs_card"),
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
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Emerald50, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PrecisionManufacturing,
                                contentDescription = null,
                                tint = Emerald600,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Data Operasional Lapangan",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                        )
                    }

                    // 1. STATUS AKTIVITAS UNIT: [Operasi Kerja] | [Standby / Tunggu] | [Breakdown / Perbaikan]
                    Text(
                        text = "Status Aktivitas Unit *",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("unit_status_selector"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. OPERASI
                        val isOperasi = formState.unitStatus == "OPERASI"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isOperasi) Emerald600 else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (isOperasi) Emerald600 else Slate200),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setUnitStatus("OPERASI") }
                                .testTag("status_btn_operasi")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isOperasi) Color.White else Slate500,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Operasi",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isOperasi) Color.White else Slate700
                                )
                            }
                        }

                        // 2. STANDBY
                        val isStandby = formState.unitStatus == "STANDBY"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isStandby) WarningAmber else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (isStandby) WarningAmber else Slate200),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setUnitStatus("STANDBY") }
                                .testTag("status_btn_standby")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = if (isStandby) Color.White else Slate500,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Standby",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isStandby) Color.White else Slate700
                                )
                            }
                        }

                        // 3. BREAKDOWN
                        val isBreakdown = formState.unitStatus == "BREAKDOWN"
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isBreakdown) ErrorRose else Color(0xFFF1F5F9),
                            border = BorderStroke(1.dp, if (isBreakdown) ErrorRose else Slate200),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setUnitStatus("BREAKDOWN") }
                                .testTag("status_btn_breakdown")
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = null,
                                    tint = if (isBreakdown) Color.White else Slate500,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Breakdown",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isBreakdown) Color.White else Slate700
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Conditional Quick Reason for Standby
                    AnimatedVisibility(visible = formState.unitStatus == "STANDBY") {
                        Column(modifier = Modifier.padding(bottom = 10.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = WarningAmber50,
                                border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "⚠️ Status Standby / Tunggu: Aktivitas terkunci otomatis, Hasil Kerja dinonaktifkan (0). HM tetap dicatat.",
                                    color = Color(0xFFB45309),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pilih Keterangan Standby:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(viewModel.standbyOptions) { reason ->
                                    val isSelected = formState.standbyReason == reason
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, if (isSelected) WarningAmber else Slate200),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { viewModel.setStandbyReason(reason) }
                                    ) {
                                        Text(
                                            text = reason,
                                            color = if (isSelected) Color(0xFF92400E) else Slate700,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Conditional Quick Troubles for Breakdown
                    AnimatedVisibility(visible = formState.unitStatus == "BREAKDOWN") {
                        Column(modifier = Modifier.padding(bottom = 10.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFFE4E6),
                                border = BorderStroke(1.dp, ErrorRose.copy(alpha = 0.5f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "🛑 Status Breakdown: Pilihan beralih ke trouble mekanik, Hasil Kerja 0, Keterangan wajib mencatat detail kerusakan.",
                                    color = Color(0xFFBE123C),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Pilih Pekerjaan Mekanik / Trouble Unit:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Slate700
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(viewModel.breakdownOptions) { trouble ->
                                    val isSelected = formState.breakdownTrouble == trouble
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) Color(0xFFFFE4E6) else Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, if (isSelected) ErrorRose else Slate200),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { viewModel.setBreakdownTrouble(trouble) }
                                    ) {
                                        Text(
                                            text = trouble,
                                            color = if (isSelected) Color(0xFFBE123C) else Slate700,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // A. DROP DOWN SEARCHABLE KODE UNIT ALAT BERAT
                    SelectorFieldTrigger(
                        label = "Unit Alat Berat *",
                        selectedValue = formState.selectedUnit?.let { "${it.unitCode} - ${it.unitType} (${it.brandModel})" },
                        placeholder = "Pilih Unit Alat Berat...",
                        leadingIcon = Icons.Default.PrecisionManufacturing,
                        badgeText = formState.selectedUnit?.unitType,
                        testTag = "trigger_select_unit",
                        onClick = { isUnitSheetOpen = true }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // B. DROP DOWN SEARCHABLE AKTIVITAS UNIT KERJA (Auto SAP & Satuan)
                    val isActivityLocked = formState.unitStatus != "OPERASI"
                    val activityLabel = when (formState.unitStatus) {
                        "STANDBY" -> "Aktivitas Unit Kerja (Terkunci Standby) *"
                        "BREAKDOWN" -> "Pekerjaan Mekanik / Trouble Unit (Terkunci Breakdown) *"
                        else -> "Aktivitas Unit Kerja *"
                    }
                    val activityIcon = when (formState.unitStatus) {
                        "STANDBY" -> Icons.Default.Lock
                        "BREAKDOWN" -> Icons.Default.Build
                        else -> Icons.Default.Assignment
                    }
                    val activityBadge = when (formState.unitStatus) {
                        "STANDBY" -> "STANDBY"
                        "BREAKDOWN" -> "BREAKDOWN"
                        else -> formState.selectedActivity?.sapCode
                    }

                    SelectorFieldTrigger(
                        label = activityLabel,
                        selectedValue = formState.selectedActivity?.let {
                            if (isActivityLocked) it.activityName else "${it.activityName} (${it.sapCode})"
                        },
                        placeholder = if (isActivityLocked) "Otomatis diisi sesuai status..." else "Pilih Aktivitas & Kode SAP...",
                        leadingIcon = activityIcon,
                        badgeText = activityBadge,
                        testTag = "trigger_select_activity",
                        onClick = {
                            if (!isActivityLocked) {
                                isActivitySheetOpen = true
                            }
                        }
                    )

                    // Binding visual reminder if activity is selected
                    if (formState.selectedActivity != null && !isActivityLocked) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            color = Emerald50,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Emerald100)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Emerald600,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Terikat SAP: ${formState.selectedActivity?.sapCode} • Satuan Hasil: ${formState.selectedActivity?.unitOfMeasure}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Emerald900,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // C. DROP DOWN SEARCHABLE OPERATOR PELAKSANA
                    SelectorFieldTrigger(
                        label = "Operator Pelaksana *",
                        selectedValue = formState.selectedOperator?.let { "${it.name} (${it.badgeNumber})" },
                        placeholder = "Pilih Operator Pelaksana...",
                        leadingIcon = Icons.Default.Person,
                        badgeText = formState.selectedOperator?.badgeNumber,
                        testTag = "trigger_select_operator",
                        onClick = { isOperatorSheetOpen = true }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // D. LOKASI KERJA
                    Text(
                        text = "Lokasi Kerja Lapangan *",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = formState.workLocation,
                        onValueChange = { viewModel.setWorkLocation(it) },
                        placeholder = { Text("Contoh: 001A / Mainroad atau Pit North-2", color = Slate400) },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Emerald600)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald600,
                            unfocusedBorderColor = Slate200,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_work_location")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // E. SHIFT KERJA (Segmented Tab SIANG / MALAM with soft mint green pill)
                    Text(
                        text = "Shift Kerja Operasional *",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val isDay = formState.shift.equals("SIANG", ignoreCase = true)
                        val isNight = formState.shift.equals("MALAM", ignoreCase = true)

                        // Tab Siang
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isDay) Emerald100 else Color.Transparent)
                                .border(
                                    width = if (isDay) 1.5.dp else 0.dp,
                                    color = if (isDay) Emerald600 else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setShift("SIANG") }
                                .testTag("shift_siang_tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.WbSunny,
                                    contentDescription = null,
                                    tint = if (isDay) Emerald900 else Slate500,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SHIFT SIANG",
                                    fontWeight = if (isDay) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isDay) Emerald900 else Slate500,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        // Tab Malam
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isNight) Emerald100 else Color.Transparent)
                                .border(
                                    width = if (isNight) 1.5.dp else 0.dp,
                                    color = if (isNight) Emerald600 else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.setShift("MALAM") }
                                .testTag("shift_malam_tab"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NightlightRound,
                                    contentDescription = null,
                                    tint = if (isNight) Emerald900 else Slate500,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "SHIFT MALAM",
                                    fontWeight = if (isNight) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isNight) Emerald900 else Slate500,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // F. DURASI JAM KERJA (Default 7.00 jam)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Durasi Jam Kerja Operasional",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Slate700
                            )
                        )
                        Text(
                            text = "${String.format("%.2f", formState.workDurationHours)} Jam",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Emerald700
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(4.0, 7.0, 8.0, 10.0, 12.0).forEach { dur ->
                            val isSelected = formState.workDurationHours == dur
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) Emerald600 else Color(0xFFF1F5F9),
                                border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Emerald600 else Slate200),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.setWorkDuration(dur) }
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "${dur.toInt()}j",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) Color.White else Slate700
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. BLOK KALKULASI HOUR METER (HM) OTOMATIS
        item {
            HmCalculationCard(
                hmAwal = formState.hmAwal,
                hmAkhirInput = formState.hmAkhirInput,
                onHmAkhirChange = { viewModel.setHmAkhir(it) },
                onResetHm = { viewModel.resetHm() },
                onQuickAddHours = { viewModel.quickAddHours(it) },
                isValid = formState.isHmValid,
                errorMessage = formState.hmErrorMessage,
                hmDailyRunning = formState.hmDailyRunning,
                unitSelected = formState.selectedUnit != null
            )
        }

        // 4. FITUR INPUT PENGISIAN BAHAN BAKAR (SOLAR / FUEL LOG)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("fuel_log_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Slate200),
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFFEF3C7), RoundedCornerShape(10.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalGasStation,
                                    contentDescription = null,
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Pengisian Bahan Bakar (Solar)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Slate900
                                    )
                                )
                                Text(
                                    text = "Unit Melakukan Pengisian Solar Hari Ini?",
                                    style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                                )
                            }
                        }

                        Switch(
                            checked = formState.isRefueled,
                            onCheckedChange = { viewModel.setRefueled(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Emerald600,
                                uncheckedThumbColor = Slate400,
                                uncheckedTrackColor = Color(0xFFF1F5F9)
                            ),
                            modifier = Modifier.testTag("fuel_toggle_switch")
                        )
                    }

                    AnimatedVisibility(visible = formState.isRefueled) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        ) {
                            HorizontalDivider(color = Slate200, modifier = Modifier.padding(bottom = 12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Jumlah Liter Solar *",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate700
                                    )
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFFEF3C7)
                                ) {
                                    Text(
                                        text = "Wajib Diisi Desimal",
                                        color = Color(0xFF92400E),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = formState.fuelLitersInput,
                                onValueChange = { viewModel.setFuelLiters(it) },
                                placeholder = { Text("Contoh: 150 atau 175.5", color = Slate400) },
                                trailingIcon = {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFFEF3C7),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "Liter",
                                            color = Color(0xFF92400E),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Emerald600,
                                    unfocusedBorderColor = Slate200,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_fuel_liters")
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Pilihan Cepat Liter Solar
                            Text(
                                text = "Pilihan Cepat Liter Solar:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate500
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(listOf(50, 100, 150, 200, 250, 300)) { liters ->
                                    val isSelected = formState.fuelLitersInput == liters.toString()
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (isSelected) Color(0xFFFEF3C7) else Color(0xFFF1F5F9),
                                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFF59E0B) else Slate200),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { viewModel.setFuelLiters(liters.toString()) }
                                    ) {
                                        Text(
                                            text = "${liters} L",
                                            color = if (isSelected) Color(0xFF92400E) else Slate700,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. HASIL KERJA & KETERANGAN LAPANGAN
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("output_and_notes_card"),
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
                        text = "Hasil Kerja & Catatan Lapangan",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Slate900
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // HASIL KERJA DENGAN SATUAN DINAMIS
                    val isWorkOutputDisabled = formState.unitStatus != "OPERASI"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isWorkOutputDisabled) "Hasil Kerja (Dinonaktifkan) *" else "Hasil Kerja (Volume/Panjang) *",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isWorkOutputDisabled) Slate500 else Slate700
                            )
                        )
                        if (isWorkOutputDisabled) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (formState.unitStatus == "STANDBY") WarningAmber50 else Color(0xFFFFE4E6)
                            ) {
                                Text(
                                    text = "Otomatis 0 (${formState.unitStatus})",
                                    color = if (formState.unitStatus == "STANDBY") Color(0xFFB45309) else Color(0xFFBE123C),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = if (isWorkOutputDisabled) "0" else formState.workOutputInput,
                        onValueChange = { if (!isWorkOutputDisabled) viewModel.setWorkOutput(it) },
                        placeholder = { Text("Contoh: 250", color = Slate400) },
                        enabled = !isWorkOutputDisabled,
                        trailingIcon = {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isWorkOutputDisabled) Slate200 else Emerald100,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                Text(
                                    text = if (isWorkOutputDisabled) "N/A" else (formState.selectedActivity?.unitOfMeasure ?: "Satuan"),
                                    color = if (isWorkOutputDisabled) Slate500 else Emerald900,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Emerald600,
                            unfocusedBorderColor = Slate200,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = if (isWorkOutputDisabled) Color(0xFFF1F5F9) else Color.White,
                            disabledContainerColor = Color(0xFFF1F5F9),
                            disabledTextColor = Slate500,
                            disabledBorderColor = Slate200
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_work_output")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // KETERANGAN LAPANGAN
                    val isBreakdown = formState.unitStatus == "BREAKDOWN"
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isBreakdown) "Keterangan Detail Kerusakan Lapangan *" else "Keterangan Kondisi Lapangan",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (isBreakdown) Color(0xFFBE123C) else Slate700
                            )
                        )
                        if (isBreakdown) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFE4E6)
                            ) {
                                Text(
                                    text = "Wajib Diisi",
                                    color = Color(0xFFBE123C),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = formState.notes,
                        onValueChange = { viewModel.setNotes(it) },
                        placeholder = {
                            Text(
                                text = if (isBreakdown) "Jelaskan kronologi breakdown, komponen rusak, dan tindakan perbaikan mekanik..."
                                else "Catat kendala alat, cuaca hujan, kondisi pit, atau catatan khusus...",
                                color = Slate400
                            )
                        },
                        minLines = 3,
                        maxLines = 5,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isBreakdown) ErrorRose else Emerald600,
                            unfocusedBorderColor = if (isBreakdown && formState.notes.isBlank()) ErrorRose.copy(alpha = 0.5f) else Slate200,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_notes")
                    )
                }
            }
        }

        // 5. KARTU INPUT FOTO BUKTI KEGIATAN LAPANGAN (KOMPRESI OTOMATIS)
        item {
            val context = androidx.compose.ui.platform.LocalContext.current
            PhotoProofCard(
                photoPath = formState.photoPath,
                photoSizeKb = formState.photoSizeKb,
                photoDimensions = formState.photoDimensions,
                isCompressing = formState.isCompressingPhoto,
                onPhotoSelected = { uri ->
                    viewModel.processAndCompressPhoto(context, uri)
                },
                onRemovePhoto = {
                    viewModel.removePhoto()
                }
            )
        }

        // 6. TOMBOL KIRIM LAPORAN AKTIVITAS (REAL-TIME)
        item {
            val isBreakdownReady = if (formState.unitStatus == "BREAKDOWN") formState.notes.isNotBlank() else true
            val isFuelReady = if (formState.isRefueled) formState.fuelLitersInput.isNotBlank() else true
            val isOutputReady = if (formState.unitStatus == "OPERASI") formState.workOutputInput.isNotBlank() else true
            val isReady = formState.selectedUnit != null &&
                    formState.selectedActivity != null &&
                    formState.selectedOperator != null &&
                    formState.isHmValid &&
                    formState.hmAkhirInput.isNotBlank() &&
                    isBreakdownReady &&
                    isFuelReady &&
                    isOutputReady &&
                    !formState.isSubmitting

            Button(
                onClick = { viewModel.submitReport() },
                enabled = isReady,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("submit_report_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Emerald600,
                    disabledContainerColor = Emerald600.copy(alpha = 0.45f)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                if (formState.isSubmitting) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Memproses Laporan...",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isOfflineSimulated) "Simpan Laporan (Mode Offline)" else "Kirim Laporan Aktivitas (Real-Time)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    // --- SEARCHABLE MODAL BOTTOM SHEETS ---

    // 1. UNIT ALAT BERAT SELECTOR SHEET
    SearchableSelectorSheet(
        isOpen = isUnitSheetOpen,
        title = "Pilih Unit Alat Berat",
        searchPlaceholder = "Cari kode unit atau jenis unit (e.g. HEH1 / EXC)...",
        items = units,
        filterPredicate = { unit, query ->
            unit.unitCode.contains(query, ignoreCase = true) ||
                    unit.unitType.contains(query, ignoreCase = true) ||
                    unit.brandModel.contains(query, ignoreCase = true)
        },
        onItemSelected = { unit ->
            viewModel.selectUnit(unit)
        },
        onDismissRequest = { isUnitSheetOpen = false }
    ) { unit, isSelected, onClick ->
        UnitSelectorItemCard(
            unit = unit,
            isSelected = isSelected || formState.selectedUnit?.unitCode == unit.unitCode,
            onClick = onClick
        )
    }

    // 2. AKTIVITAS KERJA SELECTOR SHEET (Search by Name OR SAP Code)
    SearchableSelectorSheet(
        isOpen = isActivitySheetOpen,
        title = "Pilih Aktivitas Unit Kerja",
        searchPlaceholder = "Ketik nama aktivitas atau kode SAP (e.g. 'gali' / '101')...",
        items = activities,
        filterPredicate = { activity, query ->
            activity.activityName.contains(query, ignoreCase = true) ||
                    activity.sapCode.contains(query, ignoreCase = true)
        },
        onItemSelected = { activity ->
            viewModel.selectActivity(activity)
        },
        onDismissRequest = { isActivitySheetOpen = false }
    ) { activity, isSelected, onClick ->
        ActivitySelectorItemCard(
            activity = activity,
            isSelected = isSelected || formState.selectedActivity?.sapCode == activity.sapCode,
            onClick = onClick
        )
    }

    // 3. OPERATOR PELAKSANA SELECTOR SHEET
    SearchableSelectorSheet(
        isOpen = isOperatorSheetOpen,
        title = "Pilih Operator Pelaksana",
        searchPlaceholder = "Cari nama operator atau NIK...",
        items = operators,
        filterPredicate = { op, query ->
            op.name.contains(query, ignoreCase = true) ||
                    op.badgeNumber.contains(query, ignoreCase = true)
        },
        onItemSelected = { op ->
            viewModel.selectOperator(op)
        },
        onDismissRequest = { isOperatorSheetOpen = false }
    ) { op, isSelected, onClick ->
        OperatorSelectorItemCard(
            operator = op,
            isSelected = isSelected || formState.selectedOperator?.operatorId == op.operatorId,
            onClick = onClick
        )
    }
}

/**
 * Header Form Card: Displays automatic current date, active supervisor name, and connectivity pill.
 */
@Composable
fun HeaderFormCard(
    reportDate: String,
    supervisorName: String,
    isOffline: Boolean,
    isSyncing: Boolean,
    onRefreshSync: () -> Unit,
    onToggleOffline: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("header_form_card"),
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
                Column {
                    Text(
                        text = "HeavyTrack Mobile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )
                    )
                    Text(
                        text = "Sistem Pelaporan Aktivitas Alat Berat",
                        style = MaterialTheme.typography.bodySmall.copy(color = Slate500)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Sinyal / Offline indicator pill (interactive toggle)
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isOffline) WarningAmber50 else Emerald50,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isOffline) WarningAmber.copy(alpha = 0.5f) else Emerald100
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { onToggleOffline() }
                            .testTag("network_status_toggle_pill")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isOffline) Icons.Default.WifiOff else Icons.Default.Wifi,
                                contentDescription = null,
                                tint = if (isOffline) Color(0xFFB45309) else Emerald600,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isOffline) "Offline" else "Online",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOffline) Color(0xFFB45309) else Emerald900
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Tombol Keluar / Ganti Pengawas
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF1F5F9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate200),
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .clickable { onLogout() }
                            .testTag("btn_logout_supervisor")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = "Keluar Sesi",
                                tint = Slate500,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Two-column Info: Tanggal Otomatis & Pengawas Aktif
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Tanggal Otomatis
                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Emerald100, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Emerald900,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Tanggal",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate500,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = reportDate,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            )
                        }
                    }
                }

                // Pengawas Aktif (Read-Only)
                Surface(
                    modifier = Modifier.weight(1.3f),
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Engineering,
                                contentDescription = null,
                                tint = Slate700,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pengawas Lapangan",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Slate500,
                                    fontSize = 11.sp
                                )
                            )
                            Text(
                                text = supervisorName,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
