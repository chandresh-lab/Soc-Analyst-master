package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.repository.SocRepository
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.RegisterScreen
import com.example.ui.auth.SplashScreen
import com.example.ui.dashboard.AdminPanelScreen
import com.example.ui.dashboard.AiMockInterviewScreen
import com.example.ui.dashboard.AtsResumeBuilderScreen
import com.example.ui.dashboard.CareerPathScreen
import com.example.ui.dashboard.DailyMockTestScreen
import com.example.ui.dashboard.HelpSupportScreen
import com.example.ui.dashboard.PaymentScreen
import com.example.ui.dashboard.QuestionsScreen
import com.example.ui.dashboard.SocHomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SocDarkBlueBg

enum class Screen {
    SPLASH,
    LOGIN,
    TWO_FACTOR,
    REGISTER,
    HOME,
    CAREER_PATH,
    ATS_BUILDER,
    QUESTIONS,
    DAILY_MOCK,
    AI_INTERVIEW,
    PAYMENT,
    ADMIN_PANEL,
    HELP_SUPPORT
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Initialize Cloud Sync & Firestore listener
        com.example.data.repository.FirebaseSyncManager.initialize(applicationContext)
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SocDarkBlueBg
                ) {
                    SocMasterApp()
                }
            }
        }
    }
}

@Composable
fun SocMasterApp() {
    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    val currentUser by SocRepository.currentUser.collectAsState()

    var pendingEmail by remember { mutableStateOf("") }
    var pendingRole by remember { mutableStateOf(com.example.data.model.UserRole.CANDIDATE) }

    Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
        when (screen) {
            Screen.SPLASH -> {
                SplashScreen(
                    onSplashFinished = {
                        currentScreen = if (currentUser != null) Screen.HOME else Screen.LOGIN
                    }
                )
            }
            Screen.LOGIN -> {
                LoginScreen(
                    onLoginSuccess = {
                        currentScreen = Screen.HOME
                    },
                    onNavigateToRegister = {
                        currentScreen = Screen.REGISTER
                    },
                    onNavigateTo2fa = { email, role ->
                        pendingEmail = email
                        pendingRole = role
                        currentScreen = Screen.TWO_FACTOR
                    }
                )
            }
            Screen.TWO_FACTOR -> {
                com.example.ui.auth.TwoFactorScreen(
                    userEmail = pendingEmail,
                    userRole = pendingRole,
                    onNavigateBack = {
                        currentScreen = Screen.LOGIN
                    },
                    onVerificationSuccess = {
                        currentScreen = Screen.HOME
                    }
                )
            }
            Screen.REGISTER -> {
                BackHandler {
                    currentScreen = Screen.LOGIN
                }
                RegisterScreen(
                    onNavigateBackToLogin = {
                        currentScreen = Screen.LOGIN
                    },
                    onRegistrationSuccess = {
                        currentScreen = Screen.HOME
                    }
                )
            }
            Screen.HOME -> {
                SocHomeScreen(
                    onNavigateToCareerPath = { currentScreen = Screen.CAREER_PATH },
                    onNavigateToAtsBuilder = { currentScreen = Screen.ATS_BUILDER },
                    onNavigateToQuestions = { currentScreen = Screen.QUESTIONS },
                    onNavigateToDailyMock = { currentScreen = Screen.DAILY_MOCK },
                    onNavigateToAiInterview = { currentScreen = Screen.AI_INTERVIEW },
                    onNavigateToPayment = { currentScreen = Screen.PAYMENT },
                    onNavigateToAdminPanel = { currentScreen = Screen.ADMIN_PANEL },
                    onNavigateToHelpSupport = { currentScreen = Screen.HELP_SUPPORT },
                    onLogout = {
                        SocRepository.logout()
                        currentScreen = Screen.LOGIN
                    }
                )
            }
            Screen.CAREER_PATH -> {
                BackHandler {
                    currentScreen = Screen.HOME
                }
                CareerPathScreen(
                    onNavigateBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.ATS_BUILDER -> {
                BackHandler {
                    currentScreen = Screen.HOME
                }
                AtsResumeBuilderScreen(
                    onNavigateBack = { currentScreen = Screen.HOME },
                    onNavigateToUpgrade = { currentScreen = Screen.PAYMENT }
                )
            }
            Screen.QUESTIONS -> {
                BackHandler {
                    currentScreen = Screen.HOME
                }
                QuestionsScreen(
                    onNavigateBack = { currentScreen = Screen.HOME },
                    onNavigateToUpgrade = { currentScreen = Screen.PAYMENT }
                )
            }
            Screen.DAILY_MOCK -> {
                BackHandler {
                    currentScreen = Screen.HOME
                }
                DailyMockTestScreen(
                    onNavigateBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.AI_INTERVIEW -> {
                BackHandler {
                    currentScreen = Screen.HOME
                }
                AiMockInterviewScreen(
                    onNavigateBack = { currentScreen = Screen.HOME },
                    onNavigateToUpgrade = { currentScreen = Screen.PAYMENT }
                )
            }
            Screen.PAYMENT -> {
                BackHandler {
                    currentScreen = Screen.HOME
                }
                PaymentScreen(
                    onNavigateBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.ADMIN_PANEL -> {
                BackHandler {
                    currentScreen = Screen.HOME
                }
                AdminPanelScreen(
                    onNavigateBack = { currentScreen = Screen.HOME }
                )
            }
            Screen.HELP_SUPPORT -> {
                BackHandler {
                    currentScreen = Screen.HOME
                }
                HelpSupportScreen(
                    onNavigateBack = { currentScreen = Screen.HOME }
                )
            }
        }
    }
}
