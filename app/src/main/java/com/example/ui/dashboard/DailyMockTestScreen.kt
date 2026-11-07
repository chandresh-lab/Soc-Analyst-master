package com.example.ui.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.ui.components.SocButton
import com.example.ui.components.SocOutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.R
import com.example.data.model.MockTestCertificate
import com.example.data.model.Question
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
import kotlinx.coroutines.delay
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyMockTestScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by SocRepository.currentUser.collectAsState()

    // 20 Questions (4 from each category)
    var questions by remember { mutableStateOf<List<Question>>(emptyList()) }
    LaunchedEffect(Unit) {
        questions = SocRepository.getDailyMockQuestions()
    }

    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionIndex -> selectedOptionIndex

    // 20-minute timer = 1200 seconds
    var secondsRemaining by remember { mutableIntStateOf(1200) }
    var isSubmitted by remember { mutableStateOf(false) }

    LaunchedEffect(isSubmitted) {
        if (!isSubmitted) {
            while (secondsRemaining > 0 && !isSubmitted) {
                delay(1000)
                secondsRemaining--
            }
            if (secondsRemaining == 0) {
                isSubmitted = true
            }
        }
    }

    // Score Calculation
    val score = remember(isSubmitted) {
        if (!isSubmitted) 0
        else {
            questions.indices.count { idx ->
                val chosen = userAnswers[idx]
                chosen != null && chosen == questions[idx].correctOptionIndex
            }
        }
    }

    LaunchedEffect(isSubmitted) {
        if (isSubmitted && score > 0) {
            SocRepository.updateMockScore(score)
        }
    }

    val percentage = remember(score, questions.size) {
        if (questions.isEmpty()) 0 else (score * 100) / questions.size
    }

    // Generated Certificate
    val certificateId = remember(isSubmitted) {
        "SOC-AARAV-${System.currentTimeMillis().toString().takeLast(6)}"
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "DAILY SOC MOCK TEST",
                            color = SocGoldPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = if (!isSubmitted) "20 Questions • 20-Minute Simulator" else "Evaluation & Certificate",
                            color = SocTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("daily_mock_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = SocGoldPrimary
                        )
                    }
                },
                actions = {
                    if (!isSubmitted) {
                        // Timer display
                        val minutes = secondsRemaining / 60
                        val secs = secondsRemaining % 60
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SocDarkBlueSurfaceElevated)
                                .border(1.dp, if (secondsRemaining < 180) SocAlertRed else SocGoldPrimary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%02d:%02d", minutes, secs),
                                color = if (secondsRemaining < 180) SocAlertRed else SocGoldPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SocDarkBlueSurface)
            )
        },
        containerColor = SocDarkBlueBg
    ) { innerPadding ->
        if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Initializing 20-Question SOC Simulator...", color = SocTextSecondary)
            }
            return@Scaffold
        }

        if (!isSubmitted) {
            // Live Test Mode
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                // Question Jump Palette (1..20)
                Text(
                    text = "QUESTION PALETTE (20 QUESTIONS)",
                    color = SocTextMuted,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(questions) { idx, _ ->
                        val isAnswered = userAnswers.containsKey(idx)
                        val isCurrent = currentQuestionIndex == idx
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        isCurrent -> SocGoldPrimary
                                        isAnswered -> SocTerminalGreen
                                        else -> SocDarkBlueSurfaceElevated
                                    }
                                )
                                .border(1.dp, if (isCurrent) SocGoldLight else SocBlueBorder, RoundedCornerShape(6.dp))
                                .clickable { currentQuestionIndex = idx }
                        ) {
                            Text(
                                text = "${idx + 1}",
                                color = if (isCurrent) Color.Black else if (isAnswered) Color.Black else SocTextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Current Question Card
                val q = questions[currentQuestionIndex]

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(SocBlueDeep)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "QUESTION ${currentQuestionIndex + 1} OF 20",
                                    color = SocBlueAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Text(
                                text = q.category.shortCode,
                                color = SocGoldLight,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = q.question,
                            color = SocTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            lineHeight = 20.sp
                        )

                        q.codeSnippet?.let { snippet ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF070B13))
                                    .border(1.dp, SocBlueBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = snippet,
                                    color = SocTerminalGreen,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Options
                        q.options.forEachIndexed { optIdx, optText ->
                            val isChosen = userAnswers[currentQuestionIndex] == optIdx
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) SocBlueDeep else SocDarkBlueSurfaceElevated)
                                    .border(1.dp, if (isChosen) SocGoldPrimary else SocBlueBorder, RoundedCornerShape(8.dp))
                                    .clickable {
                                        userAnswers[currentQuestionIndex] = optIdx
                                    }
                                    .padding(12.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(if (isChosen) SocGoldPrimary else Color.Transparent)
                                        .border(1.dp, if (isChosen) SocGoldPrimary else SocTextSecondary, CircleShape)
                                ) {
                                    Text(
                                        text = ('A'.code + optIdx).toChar().toString(),
                                        color = if (isChosen) Color.Black else SocTextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = optText,
                                    color = if (isChosen) SocTextPrimary else SocTextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Fixed-Size 280x48dp Navigation Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (currentQuestionIndex < questions.size - 1) {
                        SocButton(
                            text = "NEXT QUESTION ▶",
                            onClick = { currentQuestionIndex++ },
                            modifier = Modifier.testTag("next_mock_question_btn")
                        )
                    } else {
                        SocButton(
                            text = "FINISH & EVALUATE",
                            onClick = { isSubmitted = true },
                            modifier = Modifier.testTag("submit_mock_test_btn")
                        )
                    }

                    if (currentQuestionIndex > 0) {
                        SocOutlinedButton(
                            text = "◀ PREVIOUS QUESTION",
                            contentColor = SocTextSecondary,
                            onClick = { currentQuestionIndex-- },
                            modifier = Modifier.testTag("prev_mock_question_btn")
                        )
                    }
                }
            }
        } else {
            // Test Completed & Certificate Generator
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Official Certificate Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1322)),
                    border = androidx.compose.foundation.BorderStroke(2.dp, Brush.linearGradient(listOf(SocGoldPrimary, SocGoldDark))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Shield Emblem
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(SocDarkBlueSurfaceElevated)
                                .border(1.5.dp, SocGoldPrimary, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.soc_gold_shield),
                                contentDescription = "Seal",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(56.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "AARAV IT SOLUTIONS",
                            color = SocGoldPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "CYBER DEFENSE OPERATIONS ACADEMY",
                            color = SocTextSecondary,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = SocGoldDark)
                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "CERTIFICATE OF PROFICIENCY",
                            color = SocGoldLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "This is officially awarded to",
                            color = SocTextMuted,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = currentUser?.fullName?.uppercase() ?: "SECURITY ANALYST CANDIDATE",
                            color = SocTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "For successfully completing the rigorous 20-Question SOC Master Technical Evaluation encompassing SIEM Query Formulation, Network Protocol Analysis, and Incident Response Playbooks.",
                            color = SocTextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Score & Verification
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "SCORE ACHIEVED", color = SocTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                Text(text = "$score / 20 ($percentage%)", color = if (percentage >= 70) SocTerminalGreen else SocGoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "VERIFICATION ID", color = SocTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                Text(text = certificateId, color = SocBlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Verified at: aaravitsolutons.com/verify",
                            color = SocTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Actions: Share Certificate & Retake Test (Fixed 280x48dp)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SocButton(
                        text = "SHARE CERTIFICATE",
                        icon = Icons.Default.Share,
                        onClick = {
                            shareCertificate(
                                context = context,
                                candidateName = currentUser?.fullName ?: "Candidate",
                                score = score,
                                percentage = percentage,
                                certId = certificateId
                            )
                        },
                        modifier = Modifier.testTag("share_cert_btn")
                    )

                    SocOutlinedButton(
                        text = "RETAKE TEST",
                        icon = Icons.Default.Refresh,
                        contentColor = SocGoldPrimary,
                        onClick = {
                            questions = SocRepository.getDailyMockQuestions()
                            userAnswers.clear()
                            secondsRemaining = 1200
                            currentQuestionIndex = 0
                            isSubmitted = false
                        },
                        modifier = Modifier.testTag("retake_mock_btn")
                    )
                }

                // Detailed Answers Review (All 20 Questions)
                Text(
                    text = "DETAILED ANSWERS & TECHNICAL REVIEW",
                    color = SocGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.align(Alignment.Start)
                )

                questions.forEachIndexed { i, q ->
                    val userChoice = userAnswers[i]
                    val isCorrect = userChoice != null && userChoice == q.correctOptionIndex

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isCorrect) SocTerminalGreen else SocAlertRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Q${i + 1}: ${q.category.shortCode}", color = SocBlueAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Text(
                                    text = if (isCorrect) "CORRECT (+1)" else "INCORRECT (0)",
                                    color = if (isCorrect) SocTerminalGreen else SocAlertRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = q.question, color = SocTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Correct Answer: ${q.options[q.correctOptionIndex]}",
                                color = SocTerminalGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = q.answerExplanation,
                                color = SocTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

fun shareCertificate(
    context: Context,
    candidateName: String,
    score: Int,
    percentage: Int,
    certId: String
) {
    val message = """
        🛡️ SOC MASTER - CERTIFICATE OF PROFICIENCY 🛡️
        Awarded by Aarav IT Solutions (aaravitsolutons.com)
        
        Candidate: $candidateName
        Technical Score: $score / 20 ($percentage%)
        Status: Verified Cyber Operations Candidate
        Certificate ID: $certId
        Verify URL: https://aaravitsolutons.com/verify?id=$certId
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "SOC Master Certificate - $candidateName")
        putExtra(Intent.EXTRA_TEXT, message)
    }
    context.startActivity(Intent.createChooser(intent, "Share Certificate"))
}
