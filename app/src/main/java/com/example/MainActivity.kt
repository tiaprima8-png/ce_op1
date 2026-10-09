package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.remote.ApiClientManager
import com.example.data.repository.HeavyTrackRepository
import com.example.ui.screens.FormReportScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MasterDataScreen
import com.example.ui.theme.Emerald100
import com.example.ui.theme.Emerald600
import com.example.ui.theme.Emerald900
import com.example.ui.theme.HeavyTrackTheme
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate500
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate900
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.HeavyTrackViewModel
import com.example.ui.viewmodel.HeavyTrackViewModelFactory
import com.example.ui.viewmodel.UiMessage
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HeavyTrackTheme {
                HeavyTrackApp()
            }
        }
    }
}

@Composable
fun HeavyTrackApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val apiClientManager = remember { ApiClientManager(context) }
    val repository = remember { HeavyTrackRepository(database, apiClientManager) }
    val viewModel: HeavyTrackViewModel = viewModel(
        factory = HeavyTrackViewModelFactory(repository)
    )

    var currentTab by remember { mutableIntStateOf(0) }
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val unsyncedCount by viewModel.unsyncedCount.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle back button: return to Form tab if in other tabs
    BackHandler(enabled = isLoggedIn && currentTab != 0) {
        currentTab = 0
    }

    // Collect UI messages / notifications
    LaunchedEffect(viewModel) {
        viewModel.uiEvents.collectLatest { event ->
            when (event) {
                is UiMessage.Success -> snackbarHostState.showSnackbar(event.message)
                is UiMessage.Error -> snackbarHostState.showSnackbar(event.message)
                is UiMessage.Info -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (!isLoggedIn) {
        Box(modifier = Modifier.fillMaxSize()) {
            LoginScreen(
                onLoginSuccess = { supervisor ->
                    viewModel.login(supervisor)
                }
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(androidx.compose.ui.Alignment.BottomCenter)
            )
        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { data ->
                        Snackbar(
                            snackbarData = data,
                            containerColor = Slate900,
                            contentColor = Color.White,
                            actionColor = Emerald600
                        )
                    }
                )
            },
            bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                // Tab 0: Formulir Input
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { currentTab = 0 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = "Formulir",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Formulir",
                            fontWeight = if (currentTab == 0) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald900,
                        selectedTextColor = Emerald900,
                        indicatorColor = Emerald100,
                        unselectedIconColor = Slate500,
                        unselectedTextColor = Slate500
                    ),
                    modifier = Modifier.testTag("nav_item_form")
                )

                // Tab 1: Riwayat Aktivitas
                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { currentTab = 1 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (unsyncedCount > 0) {
                                    Badge(
                                        containerColor = WarningAmber,
                                        contentColor = Slate900
                                    ) {
                                        Text("$unsyncedCount", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Riwayat Saya",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    label = {
                        Text(
                            text = "Riwayat",
                            fontWeight = if (currentTab == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald900,
                        selectedTextColor = Emerald900,
                        indicatorColor = Emerald100,
                        unselectedIconColor = Slate500,
                        unselectedTextColor = Slate500
                    ),
                    modifier = Modifier.testTag("nav_item_history")
                )

                // Tab 2: Master Data & Sync
                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { currentTab = 2 },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Master Data",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "Master Data",
                            fontWeight = if (currentTab == 2) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Emerald900,
                        selectedTextColor = Emerald900,
                        indicatorColor = Emerald100,
                        unselectedIconColor = Slate500,
                        unselectedTextColor = Slate500
                    ),
                    modifier = Modifier.testTag("nav_item_master")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> FormReportScreen(
                    viewModel = viewModel,
                    onNavigateToHistory = { currentTab = 1 }
                )
                1 -> HistoryScreen(
                    viewModel = viewModel,
                    onNavigateToForm = { currentTab = 0 }
                )
                2 -> MasterDataScreen(
                    viewModel = viewModel
                )
            }
        }
    }
}
}
