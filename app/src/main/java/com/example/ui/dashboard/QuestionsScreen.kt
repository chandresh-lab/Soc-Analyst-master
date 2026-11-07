package com.example.ui.dashboard

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Question
import com.example.data.model.QuestionCategory
import com.example.data.model.SubscriptionPlan
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
import kotlinx.coroutines.delay

enum class ViewStudyMode {
    MCQ_TIMER,
    INTERVIEW_EXPLANATION
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUpgrade: () -> Unit
) {
    val currentUser by SocRepository.currentUser.collectAsState()
    val isFreePlan = currentUser?.plan == SubscriptionPlan.FREE

    val allQuestions by SocRepository.allQuestions.collectAsState()

    var studyMode by remember { mutableStateOf(ViewStudyMode.MCQ_TIMER) }
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyBookmarked by remember { mutableStateOf(false) }

    val categories = QuestionCategory.entries

    // User selections in MCQ mode
    val userSelectedOptions = remember { mutableStateMapOf<String, Int>() }

    // Active Category filtered list
    val currentCategory = categories[selectedCategoryIndex]
    val filteredQuestions = allQuestions.filter { q ->
        q.category == currentCategory &&
                (!showOnlyBookmarked || q.isBookmarked) &&
                (searchQuery.isBlank() || q.question.contains(searchQuery, ignoreCase = true) || (q.codeSnippet?.contains(searchQuery, ignoreCase = true) == true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "500 SOC Q&A + MCQ BANK",
                            color = SocGoldPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "100 Per Category • MCQ & Interview Modes",
                            color = SocTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("questions_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SocGoldPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showOnlyBookmarked = !showOnlyBookmarked },
                        modifier = Modifier.testTag("toggle_bookmark_filter_btn")
                    ) {
                        Icon(
                            imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Filter Bookmarks",
                            tint = if (showOnlyBookmarked) SocGoldPrimary else SocTextSecondary
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
            // Category Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedCategoryIndex,
                containerColor = SocDarkBlueSurfaceElevated,
                contentColor = SocGoldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedCategoryIndex]),
                        color = SocGoldPrimary
                    )
                },
                edgePadding = 8.dp
            ) {
                categories.forEachIndexed { index, cat ->
                    Tab(
                        selected = selectedCategoryIndex == index,
                        onClick = { selectedCategoryIndex = index },
                        text = {
                            Text(
                                text = "${cat.shortCode} (100)",
                                color = if (selectedCategoryIndex == index) SocGoldPrimary else SocTextSecondary,
                                fontWeight = if (selectedCategoryIndex == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier.testTag("tab_cat_${cat.name}")
                    )
                }
            }

            // Controls Bar: Search + Mode Switch
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SocDarkBlueSurface)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                // Search Input
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by keyword, Event ID, or query...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SocGoldPrimary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SocTextPrimary, unfocusedTextColor = SocTextPrimary,
                        focusedBorderColor = SocGoldPrimary, unfocusedBorderColor = SocBlueBorder,
                        focusedContainerColor = SocDarkBlueSurfaceElevated, unfocusedContainerColor = SocDarkBlueSurfaceElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("questions_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mode Switch: MCQ Mode vs Interview Mode
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = studyMode == ViewStudyMode.MCQ_TIMER,
                        onClick = { studyMode = ViewStudyMode.MCQ_TIMER },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = SocGoldPrimary,
                            activeContentColor = Color.Black,
                            inactiveContainerColor = SocDarkBlueSurfaceElevated,
                            inactiveContentColor = SocTextSecondary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("MCQ Mode (Timer)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    SegmentedButton(
                        selected = studyMode == ViewStudyMode.INTERVIEW_EXPLANATION,
                        onClick = { studyMode = ViewStudyMode.INTERVIEW_EXPLANATION },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = SocGoldPrimary,
                            activeContentColor = Color.Black,
                            inactiveContainerColor = SocDarkBlueSurfaceElevated,
                            inactiveContentColor = SocTextSecondary
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Interview Mode (Q&A)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Questions List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("questions_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Free Plan Notice
                if (isFreePlan) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1604)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SocGoldDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToUpgrade() }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "FREE Plan: Showing First 50 Q&A and 50 MCQ", color = SocGoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Upgrade to PRO (₹199/m) to unlock all 500 questions & full SIEM solutions.", color = SocTextSecondary, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }

                // If filtering by bookmark and empty
                if (filteredQuestions.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp)
                        ) {
                            Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = SocTextMuted, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "No questions found matching criteria", color = SocTextSecondary, fontSize = 14.sp)
                        }
                    }
                }

                val displayedQuestions = if (isFreePlan) filteredQuestions.take(50) else filteredQuestions

                itemsIndexed(displayedQuestions) { index, question ->
                    QuestionItemCard(
                        question = question,
                        mode = studyMode,
                        userAnswerIndex = userSelectedOptions[question.id],
                        onSelectOption = { optIndex ->
                            userSelectedOptions[question.id] = optIndex
                        },
                        onToggleBookmark = {
                            SocRepository.toggleBookmark(question.id)
                        }
                    )
                }

                if (isFreePlan && filteredQuestions.size > 50) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SocGoldPrimary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToUpgrade() }
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Unlock Remaining 450 SOC Questions",
                                    color = SocTextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Get unlimited access to all 500 questions across Splunk SPL, KQL, AQL, and incident scenarios.",
                                    color = SocTextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                com.example.ui.components.SocButton(
                                    text = "UPGRADE TO PRO (₹199)",
                                    onClick = onNavigateToUpgrade
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionItemCard(
    question: Question,
    mode: ViewStudyMode,
    userAnswerIndex: Int?,
    onSelectOption: (Int) -> Unit,
    onToggleBookmark: () -> Unit
) {
    // 60-Second Countdown for MCQ mode
    var secondsRemaining by remember { mutableIntStateOf(60) }
    var isTimerActive by remember { mutableStateOf(userAnswerIndex == null) }

    LaunchedEffect(question.id, userAnswerIndex) {
        if (userAnswerIndex == null) {
            secondsRemaining = 60
            isTimerActive = true
            while (secondsRemaining > 0 && isTimerActive) {
                delay(1000)
                secondsRemaining--
            }
        } else {
            isTimerActive = false
        }
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Category, Question Number, Difficulty & Bookmark
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SocBlueDeep)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Q#${question.questionNumber}",
                            color = SocBlueAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SocDarkBlueSurfaceElevated)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = question.difficulty,
                            color = SocTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (mode == ViewStudyMode.MCQ_TIMER) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (secondsRemaining <= 15) SocAlertRed else SocGoldPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${secondsRemaining}s",
                                color = if (secondsRemaining <= 15) SocAlertRed else SocGoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    IconButton(
                        onClick = onToggleBookmark,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (question.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (question.isBookmarked) SocGoldPrimary else SocTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.question,
                color = SocTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 19.sp
            )

            // Technical Code Snippet (if available)
            question.codeSnippet?.let { snippet ->
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF070C15))
                        .border(1.dp, SocBlueBorder, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = SocTerminalGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = snippet,
                            color = SocTerminalGreen,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // MCQ Options
            question.options.forEachIndexed { optIndex, optionText ->
                val isSelected = userAnswerIndex == optIndex
                val isCorrect = optIndex == question.correctOptionIndex
                val showResult = userAnswerIndex != null || mode == ViewStudyMode.INTERVIEW_EXPLANATION

                val (bgColor, borderColor, textColor) = when {
                    showResult && isCorrect -> Triple(Color(0xFF0F2618), SocTerminalGreen, SocTextPrimary)
                    showResult && isSelected && !isCorrect -> Triple(Color(0xFF290E10), SocAlertRed, SocTextPrimary)
                    isSelected -> Triple(SocBlueDeep, SocBlueAccent, SocTextPrimary)
                    else -> Triple(SocDarkBlueSurfaceElevated, SocBlueBorder, SocTextSecondary)
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                        .clickable(enabled = userAnswerIndex == null) {
                            onSelectOption(optIndex)
                        }
                        .padding(12.dp)
                ) {
                    // Option letter (A, B, C, D)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(if (isSelected || (showResult && isCorrect)) borderColor else Color.Transparent)
                            .border(1.dp, borderColor, CircleShape)
                    ) {
                        Text(
                            text = ('A'.code + optIndex).toChar().toString(),
                            color = if (isSelected || (showResult && isCorrect)) Color.Black else SocTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = optionText,
                        color = textColor,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Answer & Technical Explanation (In Interview Mode or after MCQ selection)
            if (userAnswerIndex != null || mode == ViewStudyMode.INTERVIEW_EXPLANATION) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = SocBlueBorder)
                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF091424))
                        .border(1.dp, SocBlueAccent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "TECHNICAL EXPLANATION & INTERVIEW PRO-TIP",
                                color = SocGoldPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = question.answerExplanation,
                            color = SocTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
