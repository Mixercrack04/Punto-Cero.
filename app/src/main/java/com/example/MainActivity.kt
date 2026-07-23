package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.local.HealthAlertEntity
import com.example.data.local.ReportEntity
import com.example.data.local.SurveyEntity
import com.example.data.local.UserProfileEntity
import com.example.data.repository.AppRepository
import com.example.ui.components.UniversalAccessibilityHeader
import com.example.ui.screens.*
import com.example.ui.theme.MosquiAlertaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        repository = AppRepository(db.appDao())

        setContent {
            MosquiAlertaApp(repository = repository)
        }
    }
}

@Composable
fun MosquiAlertaApp(repository: AppRepository) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Pre-populate DB if empty
    LaunchedEffect(Unit) {
        repository.initializeDefaultDataIfEmpty()
    }

    val userProfile by repository.userProfile.collectAsStateWithLifecycle(initialValue = null)
    val reports by repository.reports.collectAsStateWithLifecycle(initialValue = emptyList())
    val surveys by repository.surveys.collectAsStateWithLifecycle(initialValue = emptyList())
    val alerts by repository.alerts.collectAsStateWithLifecycle(initialValue = emptyList())
    val facilities by repository.healthFacilities.collectAsStateWithLifecycle(initialValue = emptyList())

    var isEasyMode by remember { mutableStateOf(userProfile?.isEasyModeActive ?: false) }
    var currentTab by remember { mutableIntStateOf(0) }

    val unreadAlertsCount = remember(alerts) { alerts.count { !it.isRead } }

    val openWhatsApp: () -> Unit = {
        val uri = Uri.parse("https://wa.me/573001234567?text=Hola,%20solicito%20soporte%20médico%20o%20información%20sobre%20zancudos%20para%20el%20proyecto%20escolar")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback web URL
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=573001234567"))
            context.startActivity(webIntent)
        }
    }

    MosquiAlertaTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_scaffold"),
            topBar = {
                UniversalAccessibilityHeader(
                    isEasyMode = isEasyMode,
                    onToggleEasyMode = {
                        isEasyMode = !isEasyMode
                        userProfile?.let { prof ->
                            coroutineScope.launch {
                                repository.saveUserProfile(prof.copy(isEasyModeActive = isEasyMode))
                            }
                        }
                    },
                    unreadAlertsCount = unreadAlertsCount,
                    onOpenAlerts = { currentTab = 5 },
                    onOpenWhatsApp = openWhatsApp
                )
            },
            bottomBar = {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_nav_bar"),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == 0,
                        onClick = { currentTab = 0 },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                        label = { Text("Inicio", fontSize = if (isEasyMode) 13.sp else 11.sp, fontWeight = FontWeight.Bold) },
                        modifier = Modifier.testTag("tab_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == 1,
                        onClick = { currentTab = 1 },
                        icon = { Icon(Icons.Default.School, contentDescription = "Educación") },
                        label = { Text("Encuestas", fontSize = if (isEasyMode) 13.sp else 11.sp) },
                        modifier = Modifier.testTag("tab_edu")
                    )

                    NavigationBarItem(
                        selected = currentTab == 2,
                        onClick = { currentTab = 2 },
                        icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                        label = { Text("Mapa", fontSize = if (isEasyMode) 13.sp else 11.sp) },
                        modifier = Modifier.testTag("tab_map")
                    )

                    NavigationBarItem(
                        selected = currentTab == 3,
                        onClick = { currentTab = 3 },
                        icon = { Icon(Icons.Default.SmartToy, contentDescription = "Dr. Zancudo") },
                        label = { Text("IA Chat", fontSize = if (isEasyMode) 13.sp else 11.sp) },
                        modifier = Modifier.testTag("tab_chat")
                    )

                    NavigationBarItem(
                        selected = currentTab == 4,
                        onClick = { currentTab = 4 },
                        icon = { Icon(Icons.Default.MedicalServices, contentDescription = "Perfil") },
                        label = { Text("Clínica", fontSize = if (isEasyMode) 13.sp else 11.sp) },
                        modifier = Modifier.testTag("tab_profile")
                    )

                    NavigationBarItem(
                        selected = currentTab == 5,
                        onClick = { currentTab = 5 },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (unreadAlertsCount > 0) {
                                        Badge { Text(unreadAlertsCount.toString()) }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Alertas")
                            }
                        },
                        label = { Text("Alertas", fontSize = if (isEasyMode) 13.sp else 11.sp) },
                        modifier = Modifier.testTag("tab_alerts")
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
                    0 -> HomeScreen(
                        isEasyMode = isEasyMode,
                        reports = reports,
                        alerts = alerts,
                        onNavigateToTab = { currentTab = it },
                        onOpenReportDialog = { currentTab = 2 },
                        onSyncWebClick = { currentTab = 5 }
                    )
                    1 -> EducationScreen(
                        isEasyMode = isEasyMode,
                        onSaveSurveyResult = { survey ->
                            coroutineScope.launch {
                                repository.addSurvey(survey)
                            }
                        },
                        surveysCompleted = surveys
                    )
                    2 -> InteractiveMapScreen(
                        isEasyMode = isEasyMode,
                        reports = reports,
                        facilities = facilities,
                        onAddReport = { newReport ->
                            coroutineScope.launch {
                                repository.addReport(newReport)
                            }
                        }
                    )
                    3 -> AiMedicalChatScreen(
                        isEasyMode = isEasyMode,
                        onOpenWhatsApp = openWhatsApp
                    )
                    4 -> ClinicalProfileScreen(
                        isEasyMode = isEasyMode,
                        userProfile = userProfile,
                        onSaveProfile = { updated ->
                            coroutineScope.launch {
                                repository.saveUserProfile(updated)
                            }
                        }
                    )
                    5 -> AlertsAndWebSyncScreen(
                        isEasyMode = isEasyMode,
                        alerts = alerts,
                        onMarkRead = { alertId ->
                            coroutineScope.launch {
                                repository.markAlertAsRead(alertId)
                            }
                        }
                    )
                }
            }
        }
    }
}
