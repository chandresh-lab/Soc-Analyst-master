package com.example.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyRupee
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SocCareerData
import com.example.ui.theme.SocBlueAccent
import com.example.ui.theme.SocBlueBorder
import com.example.ui.theme.SocBlueDeep
import com.example.ui.theme.SocDarkBlueBg
import com.example.ui.theme.SocDarkBlueSurface
import com.example.ui.theme.SocDarkBlueSurfaceElevated
import com.example.ui.theme.SocGoldLight
import com.example.ui.theme.SocGoldPrimary
import com.example.ui.theme.SocTerminalGreen
import com.example.ui.theme.SocTextMuted
import com.example.ui.theme.SocTextPrimary
import com.example.ui.theme.SocTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareerPathScreen(
    onNavigateBack: () -> Unit
) {
    var selectedLevelIndex by remember { mutableIntStateOf(0) }
    val levels = SocCareerData.levels
    val currentLevel = levels[selectedLevelIndex]

    // Track completed checklist items in local state
    val checklistProgress = remember { mutableStateMapOf<String, Boolean>() }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "SOC CAREER PATH",
                            color = SocGoldPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "L1 -> L2 -> L3 -> SOC Manager Progression",
                            color = SocTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("career_path_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SocGoldPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SocDarkBlueSurface)
            )
        },
        containerColor = SocDarkBlueBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Level Selector Tabs: L1, L2, L3, SOC Manager
            ScrollableTabRow(
                selectedTabIndex = selectedLevelIndex,
                containerColor = SocDarkBlueSurfaceElevated,
                contentColor = SocGoldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedLevelIndex]),
                        color = SocGoldPrimary
                    )
                },
                edgePadding = 12.dp
            ) {
                levels.forEachIndexed { index, level ->
                    Tab(
                        selected = selectedLevelIndex == index,
                        onClick = { selectedLevelIndex = index },
                        text = {
                            Text(
                                text = "${level.levelCode}: ${level.title.split(" ").take(2).joinToString(" ")}",
                                color = if (selectedLevelIndex == index) SocGoldPrimary else SocTextSecondary,
                                fontWeight = if (selectedLevelIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_level_${level.levelCode}")
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SocGoldPrimary.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SocGoldPrimary)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = currentLevel.levelCode,
                                    color = Color.Black,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = currentLevel.title,
                                color = SocTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentLevel.subtitle,
                            color = SocBlueAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = SocBlueBorder)
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "SALARY BENCHMARK", color = SocTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = currentLevel.salaryRange, color = SocGoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text(text = "EXPERIENCE", color = SocTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                Text(text = currentLevel.experienceReq, color = SocTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = currentLevel.coreMission,
                            color = SocTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                // Key Responsibilities
                SectionDetailCard(
                    title = "Key Daily Responsibilities",
                    icon = Icons.Default.Work,
                    accentColor = SocBlueAccent
                ) {
                    currentLevel.keyResponsibilities.forEachIndexed { i, resp ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "•", color = SocGoldPrimary, fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
                            Text(text = resp, color = SocTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }

                // Mandatory Tools & Technology Stack
                SectionDetailCard(
                    title = "Mandatory Tools & Technology Stack",
                    icon = Icons.Default.Build,
                    accentColor = SocGoldPrimary
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            currentLevel.mandatoryTools.chunked(2).forEach { pair ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    pair.forEach { tool ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SocDarkBlueSurfaceElevated)
                                                .border(1.dp, SocBlueBorder, RoundedCornerShape(6.dp))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(text = tool, color = SocGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Recommended Certifications
                SectionDetailCard(
                    title = "Target Certifications",
                    icon = Icons.Default.School,
                    accentColor = SocTerminalGreen
                ) {
                    currentLevel.certifications.forEach { cert ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SocTerminalGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = cert, color = SocTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                // Interview Focus Areas
                SectionDetailCard(
                    title = "Interview & Technical Focus",
                    icon = Icons.Default.Psychology,
                    accentColor = Color(0xFFA855F7)
                ) {
                    currentLevel.interviewFocus.forEach { focus ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(text = "▶", color = Color(0xFFA855F7), fontSize = 10.sp, modifier = Modifier.padding(end = 8.dp, top = 2.dp))
                            Text(text = focus, color = SocTextSecondary, fontSize = 12.sp, lineHeight = 16.sp)
                        }
                    }
                }

                // Actionable Roadmap Checklist
                SectionDetailCard(
                    title = "Actionable Roadmap Checklist (Tap to Check)",
                    icon = Icons.Default.Check,
                    accentColor = SocGoldPrimary
                ) {
                    currentLevel.roadmapChecklist.forEachIndexed { index, task ->
                        val key = "${currentLevel.id}_$index"
                        val isDone = checklistProgress[key] == true
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDone) Color(0xFF0F2317) else SocDarkBlueSurfaceElevated)
                                .border(1.dp, if (isDone) SocTerminalGreen else SocBlueBorder, RoundedCornerShape(8.dp))
                                .clickable { checklistProgress[key] = !isDone }
                                .padding(10.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) SocTerminalGreen else Color.Transparent)
                                    .border(1.5.dp, if (isDone) SocTerminalGreen else SocTextMuted, CircleShape)
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = task,
                                color = if (isDone) SocTextPrimary else SocTextSecondary,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SectionDetailCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = title, color = SocTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
