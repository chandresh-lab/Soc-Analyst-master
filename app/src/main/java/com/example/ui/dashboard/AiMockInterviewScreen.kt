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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.example.ui.components.SocButton
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubscriptionPlan
import com.example.data.repository.SocRepository
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

data class InterviewScenario(
    val id: String,
    val roleTrack: String,
    val interviewerQuestion: String,
    val context: String,
    val expectedKeywords: List<String>,
    val modelAnswer: String
)

object InterviewData {
    val questions = listOf(
        InterviewScenario(
            id = "ai_q1",
            roleTrack = "SOC Analyst L1",
            interviewerQuestion = "Walk me through how you would investigate a suspicious email reported by an employee claiming their CEO asked for gift cards.",
            context = "Business Email Compromise (BEC) & Phishing Triage",
            expectedKeywords = listOf("headers", "SPF", "DKIM", "DMARC", "reply-to", "display name spoofing", "sandbox", "quarantine"),
            modelAnswer = "First, examine raw email headers (RFC 822) to verify SPF, DKIM, and DMARC alignment. Check if the 'From' vs 'Reply-To' addresses mismatch (common in display name spoofing). Detonate any links or attachments in an isolated sandbox. If malicious, block the sender domain/IP at the email security gateway, search SIEM for other recipients, purge copies across mailboxes, and inform the user."
        ),
        InterviewScenario(
            id = "ai_q2",
            roleTrack = "SOC Analyst L1",
            interviewerQuestion = "How do you distinguish between a True Positive and a False Positive when an alert fires for 'Potential PowerShell Encoded Command Execution'?",
            context = "Windows Triage & Alert Validation",
            expectedKeywords = listOf("Event ID 4688", "Sysmon Event ID 1", "parent process", "Base64", "CyberChef", "command line", "scheduled task"),
            modelAnswer = "I inspect the parent process tree via Sysmon Event ID 1 or Security Event 4688. If the parent is legitimate enterprise software (like SCCM/MECM running maintenance), I decode the Base64 command in CyberChef to inspect what executed. If the command downloads external scripts (IEX/DownloadString) or connects to an unapproved IP, it's a True Positive requiring immediate endpoint containment."
        ),
        InterviewScenario(
            id = "ai_q3",
            roleTrack = "Tier 2 Incident Responder",
            interviewerQuestion = "You detect an active Cobalt Strike beacon running inside svchost.exe on a production database server. What are your immediate containment and investigation actions?",
            context = "Host Memory & Process Injection Containment",
            expectedKeywords = listOf("network isolation", "EDR", "do not power off", "RAM acquisition", "Volatility", "process injection", "C2 jitter", "firewall block"),
            modelAnswer = "1. Contain immediately via EDR network isolation while preserving power to prevent RAM loss. 2. Capture volatile RAM using FTK Imager or WinPmem. 3. Extract C2 IP/domain and block at perimeter firewalls. 4. Run Volatility (windows.malfind) to carve the injected payload and extract malleable C2 profile configurations. 5. Scope lateral movement across domain controllers."
        ),
        InterviewScenario(
            id = "ai_q4",
            roleTrack = "Tier 3 Threat Hunter",
            interviewerQuestion = "Describe your methodology for developing a hypothesis-driven threat hunt for Kerberoasting in an Active Directory environment without existing alerts.",
            context = "Adversary TTP Hunting in Active Directory",
            expectedKeywords = listOf("Kerberos Port 88", "Event ID 4769", "Ticket Encryption 0x17 (RC4)", "Service Principal Name (SPN)", "Sigma rule", "BloodHound"),
            modelAnswer = "Hypothesis: Adversaries are requesting TGS tickets with RC4 encryption (0x17) for accounts with SPNs to crack offline. In SIEM, query Event ID 4769 where TicketEncryptionType is 0x17 and ServiceName is not krbtgt. Filter out benign service accounts. Correlate with rapid bursts of ticket requests from non-admin workstations. Author a Sigma detection rule for continuous alerting."
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiMockInterviewScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUpgrade: () -> Unit
) {
    val currentUser by SocRepository.currentUser.collectAsState()
    val isFreePlan = currentUser?.plan == SubscriptionPlan.FREE

    var currentQIndex by remember { mutableIntStateOf(0) }
    val scenario = InterviewData.questions[currentQIndex]

    var candidateAnswer by remember { mutableStateOf("") }
    var isEvaluating by remember { mutableStateOf(false) }
    var evaluationResult by remember { mutableStateOf<AiEvaluation?>(null) }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AI MOCK INTERVIEW",
                                color = SocGoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Interactive SOC Technical Interview Simulator",
                                color = SocTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("ai_interview_back_btn")
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Free plan lock banner
            if (isFreePlan) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1705)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SocGoldDark),
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToUpgrade() }
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "AI Mock Interview is a PRO / PREMIUM Feature", color = SocGoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "Try Question 1 in Preview Mode or upgrade for unlimited full simulation.", color = SocTextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Question Progress & Role Track
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SocBlueDeep)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "ROLE: ${scenario.roleTrack}",
                        color = SocBlueAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Text(
                    text = "Question ${currentQIndex + 1} of ${InterviewData.questions.size}",
                    color = SocTextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Interviewer Prompt Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1C1335))
                                .border(1.dp, Color(0xFFA855F7), CircleShape)
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color(0xFFA855F7), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI LEAD SOC INTERVIEWER",
                                color = Color(0xFFA855F7),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = scenario.context,
                                color = SocTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "\"${scenario.interviewerQuestion}\"",
                        color = SocTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 22.sp
                    )
                }
            }

            // Candidate Answer Input
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "YOUR TECHNICAL ANSWER",
                        color = SocGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = candidateAnswer,
                        onValueChange = { candidateAnswer = it },
                        placeholder = { Text("Formulate your answer explaining step-by-step triage, commands, IOCs, containment SOPs...") },
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SocTextPrimary,
                            unfocusedTextColor = SocTextPrimary,
                            focusedBorderColor = SocGoldPrimary,
                            unfocusedBorderColor = SocBlueBorder,
                            focusedContainerColor = SocDarkBlueSurfaceElevated,
                            unfocusedContainerColor = SocDarkBlueSurfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ai_answer_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Fixed-Size 280x48dp Center Aligned SOC Button
                    SocButton(
                        text = "SUBMIT TO AI INTERVIEWER",
                        icon = Icons.Default.AutoAwesome,
                        onClick = {
                            if (candidateAnswer.isNotBlank()) {
                                isEvaluating = true
                                evaluationResult = evaluateAnswer(candidateAnswer, scenario)
                                isEvaluating = false
                            }
                        },
                        modifier = Modifier.testTag("evaluate_answer_btn")
                    )
                }
            }

            // Evaluation Feedback
            evaluationResult?.let { eval ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C1625)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (eval.score >= 7) SocTerminalGreen else SocGoldPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "AI TECHNICAL EVALUATION",
                                color = SocGoldPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (eval.score >= 7) Color(0xFF0F2618) else Color(0xFF261D09))
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "${eval.score}/10 RATING",
                                    color = if (eval.score >= 7) SocTerminalGreen else SocGoldPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Key Technical Strengths:",
                            color = SocTerminalGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        eval.strengths.forEach { s ->
                            Text(text = "✔ $s", color = SocTextSecondary, fontSize = 11.sp, modifier = Modifier.padding(vertical = 1.dp))
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (eval.missingKeywords.isNotEmpty()) {
                            Text(
                                text = "Missing Technical Terms / Keywords:",
                                color = SocWarningOrange,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                                eval.missingKeywords.take(4).forEach { kw ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF2B1D0B))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = kw, color = SocGoldLight, fontSize = 10.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        HorizontalDivider(color = SocBlueBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Ideal Senior Analyst Model Answer:",
                            color = SocBlueAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = scenario.modelAnswer,
                            color = SocTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Fixed-Size 280x48dp Center Aligned SOC Button
                        SocButton(
                            text = "NEXT INTERVIEW QUESTION ▶",
                            onClick = {
                                if (currentQIndex < InterviewData.questions.size - 1) {
                                    currentQIndex++
                                    candidateAnswer = ""
                                    evaluationResult = null
                                }
                            },
                            modifier = Modifier.testTag("next_interview_q_btn")
                        )
                    }
                }
            }
        }
    }
}

data class AiEvaluation(
    val score: Int,
    val strengths: List<String>,
    val missingKeywords: List<String>
)

fun evaluateAnswer(answer: String, scenario: InterviewScenario): AiEvaluation {
    val answerLower = answer.lowercase()
    val matchedKeywords = scenario.expectedKeywords.filter { answerLower.contains(it.lowercase()) }
    val missing = scenario.expectedKeywords.filter { !answerLower.contains(it.lowercase()) }

    var calculatedScore = 5 // Base
    calculatedScore += (matchedKeywords.size * 1).coerceAtMost(4)
    if (answer.length > 120) calculatedScore += 1

    val finalScore = calculatedScore.coerceIn(4, 10)

    val strengths = mutableListOf<String>()
    if (matchedKeywords.isNotEmpty()) {
        strengths.add("Referenced vital security terminology (${matchedKeywords.take(3).joinToString(", ")})")
    }
    if (answer.length > 80) {
        strengths.add("Demonstrated structured incident handling and escalation logic")
    } else {
        strengths.add("Concise answer; can expand further with specific log telemetry sources")
    }

    return AiEvaluation(
        score = finalScore,
        strengths = strengths,
        missingKeywords = missing
    )
}
