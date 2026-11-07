package com.example.ui.dashboard

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.SubscriptionPlan
import com.example.data.model.UserRole
import com.example.data.repository.SocRepository
import com.example.ui.theme.SocAlertRed
import com.example.ui.theme.SocBlueAccent
import com.example.ui.theme.SocBlueBorder
import com.example.ui.theme.SocBlueDeep
import com.example.ui.theme.SocDarkBlueBg
import com.example.ui.theme.SocDarkBlueSurface
import com.example.ui.theme.SocDarkBlueSurfaceElevated
import com.example.ui.theme.SocGoldDark
import com.example.ui.theme.SocGoldLight
import com.example.ui.theme.SocGoldPrimary
import com.example.ui.theme.SocTerminalGreen
import com.example.ui.theme.SocTextMuted
import com.example.ui.theme.SocTextPrimary
import com.example.ui.theme.SocTextSecondary
import com.example.ui.theme.SocWarningOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocHomeScreen(
    onNavigateToCareerPath: () -> Unit,
    onNavigateToAtsBuilder: () -> Unit,
    onNavigateToQuestions: () -> Unit,
    onNavigateToDailyMock: () -> Unit,
    onNavigateToAiInterview: () -> Unit,
    onNavigateToPayment: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToHelpSupport: () -> Unit,
    onLogout: () -> Unit
) {
    val currentUser by SocRepository.currentUser.collectAsState()
    val user = currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.soc_gold_shield),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "SOC MASTER",
                                color = SocGoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Security Operations Center Preparation",
                                color = SocTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier.testTag("home_logout_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = SocTextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SocDarkBlueSurface
                )
            )
        },
        containerColor = SocDarkBlueBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("soc_home_screen"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // User Profile Banner & 2FA Control
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(SocDarkBlueSurfaceElevated)
                                    .border(1.5.dp, SocGoldPrimary, CircleShape)
                            ) {
                                val userBmp = remember(user?.profilePhotoUri) {
                                    val photoStr = user?.profilePhotoUri ?: ""
                                    if (photoStr.length > 50) {
                                        try {
                                            val bytes = android.util.Base64.decode(photoStr, android.util.Base64.DEFAULT)
                                            android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                        } catch (_: Exception) {
                                            null
                                        }
                                    } else {
                                        null
                                    }
                                }
                                if (userBmp != null) {
                                    Image(
                                        bitmap = userBmp.asImageBitmap(),
                                        contentDescription = "Profile Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Text(
                                        text = user?.profilePhotoUri?.takeIf { it.length <= 10 && it.isNotBlank() } ?: "🛡️",
                                        fontSize = 24.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = user?.fullName ?: "Candidate",
                                        color = SocTextPrimary,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    if (user?.plan == SubscriptionPlan.PREMIUM) {
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Premium",
                                            tint = SocGoldPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = user?.email ?: "candidate@aaravitsolutons.com",
                                    color = SocTextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Role Chip
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(SocBlueDeep)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = user?.role?.displayName ?: "Candidate",
                                            color = SocBlueAccent,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    // Plan Chip
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (user?.plan == SubscriptionPlan.PREMIUM) SocGoldDark else Color(0xFF1B2A4A))
                                            .clickable { onNavigateToPayment() }
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "${user?.plan?.title ?: "FREE"} PLAN",
                                            color = if (user?.plan == SubscriptionPlan.PREMIUM) Color.Black else SocGoldPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SocBlueBorder)
                        Spacer(modifier = Modifier.height(8.dp))

                        // 2FA Security Setting Toggle: User can toggle ON / OFF as requested!
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = SocTerminalGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "2FA Verification Status: ${if (user?.is2faEnabled == true) "ENABLED" else "DISABLED"}",
                                        color = if (user?.is2faEnabled == true) SocTerminalGreen else SocAlertRed,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = if (user?.is2faEnabled == true) "Active Delivery: ${user?.twoFactorMethod?.name} OTP" else "Enable 2FA for enhanced account protection",
                                    color = SocTextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            androidx.compose.material3.Switch(
                                checked = user?.is2faEnabled ?: true,
                                onCheckedChange = { isChecked ->
                                    if (user != null) {
                                        val updated = user.copy(is2faEnabled = isChecked)
                                        SocRepository.updateUserProfile(updated)
                                    }
                                },
                                colors = androidx.compose.material3.SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = SocGoldPrimary,
                                    uncheckedThumbColor = SocTextSecondary,
                                    uncheckedTrackColor = SocDarkBlueSurfaceElevated
                                )
                            )
                        }
                    }
                }
            }

            // Real-Time SOC Telemetry Radar Ticker
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF07111E)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Radar,
                                contentDescription = null,
                                tint = SocTerminalGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIVE SOC TELEMETRY CONSOLE",
                                color = SocTerminalGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            SocMetricItem(label = "ACTIVE EPS", value = "14,200", color = SocBlueAccent)
                            SocMetricItem(label = "AVG MTTR", value = "18m", color = SocGoldPrimary)
                            SocMetricItem(label = "P1 ALERTS", value = "0 ACTIVE", color = SocTerminalGreen)
                            SocMetricItem(label = "SIEM HEALTH", value = "99.8%", color = SocTextPrimary)
                        }
                    }
                }
            }

            // Simulated Banner Ads for FREE Plan as requested
            if (user?.plan == SubscriptionPlan.FREE) {
                item {
                    Card(
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1B180A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SocGoldDark),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToPayment() }
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AD",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(SocGoldPrimary)
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Upgrade to PRO (₹199/m) to remove ads & unlock all 500 Q&A + AI Interview!",
                                color = SocGoldLight,
                                fontSize = 11.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Section: CORE SOC PREPARATION MODULES
            item {
                Text(
                    text = "SOC PREPARATION MODULES",
                    color = SocGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            // 1. SOC Career Path
            item {
                DashboardActionCard(
                    title = "SOC Career Path",
                    subtitle = "L1 -> L2 -> L3 -> SOC Manager Roadmap & Certifications",
                    badge = "CAREER ROADMAP",
                    icon = Icons.Default.TrendingUp,
                    accentColor = SocGoldPrimary,
                    onClick = onNavigateToCareerPath,
                    testTag = "card_career_path"
                )
            }

            // 2. ATS Resume Builder
            item {
                DashboardActionCard(
                    title = "ATS Resume Builder",
                    subtitle = "15+ SOC Templates, ATS Score 0-100, JD Match, Ready Bullets & PDF",
                    badge = "15+ TEMPLATES",
                    icon = Icons.Default.Description,
                    accentColor = SocBlueAccent,
                    onClick = onNavigateToAtsBuilder,
                    testTag = "card_ats_builder"
                )
            }

            // 3. 500 SOC Q&A + MCQ
            item {
                DashboardActionCard(
                    title = "500 SOC Q&A + MCQ Bank",
                    subtitle = "100 Fundamentals • 100 Networking • 100 SIEM (SPL/KQL/AQL) • 100 IR • 100 HR",
                    badge = "500 QUESTIONS",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    accentColor = SocTerminalGreen,
                    onClick = onNavigateToQuestions,
                    testTag = "card_questions_bank"
                )
            }

            // 4. Daily Mock Test
            item {
                DashboardActionCard(
                    title = "Daily Mock Test (20 Q)",
                    subtitle = "20-Min Timed Simulator, Instant Scoring & Verifiable Certificate",
                    badge = "CERTIFIED",
                    icon = Icons.Default.FitnessCenter,
                    accentColor = SocWarningOrange,
                    onClick = onNavigateToDailyMock,
                    testTag = "card_daily_mock"
                )
            }

            // 5. AI Mock Interview
            item {
                DashboardActionCard(
                    title = "AI Mock Interview",
                    subtitle = "Simulate Real SOC Technical Interview with Instant AI Feedback & Scoring",
                    badge = "AI POWERED",
                    icon = Icons.Default.AutoAwesome,
                    accentColor = Color(0xFFA855F7),
                    onClick = onNavigateToAiInterview,
                    testTag = "card_ai_interview"
                )
            }

            // 6. Subscription / Razorpay
            item {
                DashboardActionCard(
                    title = "Upgrade Plan (Razorpay)",
                    subtitle = "FREE ₹0 • PRO ₹199/m or ₹999/yr • PREMIUM ₹1,999 One-Time Lifetime",
                    badge = "INSTANT UPGRADE",
                    icon = Icons.Default.Payment,
                    accentColor = SocGoldPrimary,
                    onClick = onNavigateToPayment,
                    testTag = "card_payment_plans"
                )
            }

            // Super Admin Exclusive Device & Cloud Sync Dashboard
            if (user?.role == UserRole.SUPER_ADMIN) {
                item {
                    DashboardActionCard(
                        title = "All Devices & Users (Excel Table)",
                        subtitle = "Live Cloud Sync • Google Sheet, CSV & XLS Exports • Full 15-Column Telemetry",
                        badge = "ROOT ADMIN",
                        icon = Icons.Default.TableChart,
                        accentColor = SocGoldPrimary,
                        onClick = onNavigateToAdminPanel,
                        testTag = "card_super_admin_devices"
                    )
                }
            }

            // 7. Admin Panel (If user is Admin or Super Admin)
            if (user?.role == UserRole.ADMIN || user?.role == UserRole.SUPER_ADMIN) {
                item {
                    DashboardActionCard(
                        title = "Admin Operations Console",
                        subtitle = "Add/Edit Questions, Resume Templates, View Users & Real-Time Payments",
                        badge = if (user?.role == UserRole.SUPER_ADMIN) "SUPER ADMIN" else "ADMIN",
                        icon = Icons.Default.AdminPanelSettings,
                        accentColor = SocAlertRed,
                        onClick = onNavigateToAdminPanel,
                        testTag = "card_admin_panel"
                    )
                }
            }

            // 8. Help, Disclaimer, Support
            item {
                DashboardActionCard(
                    title = "Help, Disclaimer & Support",
                    subtitle = "Aarav IT Solutions • Linked to aaravitsolutons.com • Ethics & Privacy",
                    badge = "OFFICIAL",
                    icon = Icons.AutoMirrored.Filled.Help,
                    accentColor = SocTextSecondary,
                    onClick = onNavigateToHelpSupport,
                    testTag = "card_help_support"
                )
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "SOC Master • Aarav IT Solutions",
                        color = SocGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "aaravitsolutons.com • Security Operations Center Preparation",
                        color = SocTextMuted,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun SocMetricItem(label: String, value: String, color: Color) {
    Column {
        Text(text = label, color = SocTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
        Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SocDarkBlueSurfaceElevated)
                    .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = SocTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(accentColor.copy(alpha = 0.15f))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            color = accentColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = SocTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = SocTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
