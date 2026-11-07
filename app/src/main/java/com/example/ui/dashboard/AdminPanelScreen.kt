package com.example.ui.dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuestionCategory
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by SocRepository.currentUser.collectAsState()
    val isSuperAdmin = currentUser?.role == UserRole.SUPER_ADMIN

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = if (isSuperAdmin) {
        listOf("All Devices & Users", "Questions (500)", "Users Directory", "Payments & Revenue", "Resume Reviews")
    } else {
        listOf("Questions (500)", "Users Directory", "Payments & Revenue", "Resume Reviews")
    }

    val allQuestions by SocRepository.allQuestions.collectAsState()
    val registeredUsers by SocRepository.registeredUsers.collectAsState()
    val transactions by SocRepository.transactions.collectAsState()
    val resumeReviews by SocRepository.resumeReviews.collectAsState()

    var showAddQuestionDialog by remember { mutableStateOf(false) }
    var reviewFeedbackDialogId by remember { mutableStateOf<String?>(null) }
    var feedbackInput by remember { mutableStateOf("") }

    if (showAddQuestionDialog) {
        AddQuestionDialog(
            onDismiss = { showAddQuestionDialog = false },
            onAddQuestion = { cat, qText, opts, correctIdx, exp, snippet ->
                SocRepository.addQuestion(cat, qText, opts, correctIdx, exp, snippet)
                showAddQuestionDialog = false
                Toast.makeText(context, "New question added to SOC Question Bank!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    if (reviewFeedbackDialogId != null) {
        val review = resumeReviews.firstOrNull { it.id == reviewFeedbackDialogId }
        AlertDialog(
            onDismissRequest = { reviewFeedbackDialogId = null },
            containerColor = SocDarkBlueSurface,
            title = {
                Text(
                    text = "SUBMIT ADMIN RESUME FEEDBACK",
                    color = SocGoldPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Column {
                    Text(text = "Candidate: ${review?.userName} (${review?.targetRole})", color = SocTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = feedbackInput,
                        onValueChange = { feedbackInput = it },
                        placeholder = { Text("Enter detailed feedback on ATS keyword density, formatting, and SIEM bullet metrics...") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SocTextPrimary, unfocusedTextColor = SocTextPrimary,
                            focusedBorderColor = SocGoldPrimary, unfocusedBorderColor = SocBlueBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        SocRepository.addAdminFeedback(reviewFeedbackDialogId!!, feedbackInput)
                        reviewFeedbackDialogId = null
                        feedbackInput = ""
                        Toast.makeText(context, "Feedback sent to candidate!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SocGoldPrimary, contentColor = Color.Black)
                ) {
                    Text("SUBMIT REVIEW", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { reviewFeedbackDialogId = null }) {
                    Text("Cancel", color = SocTextSecondary)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isSuperAdmin) Icons.Default.Shield else Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = if (isSuperAdmin) SocGoldPrimary else SocAlertRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isSuperAdmin) "SUPER ADMIN CONSOLE" else "ADMIN PANEL",
                                color = if (isSuperAdmin) SocGoldPrimary else SocTextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = if (isSuperAdmin) "Privileged Root Control • 76076ck@gmail.com" else "Aarav IT Solutions Operations",
                                color = SocTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("admin_panel_back_btn")
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
            // Admin Navigation Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SocDarkBlueSurfaceElevated,
                contentColor = SocGoldPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = SocGoldPrimary
                    )
                },
                edgePadding = 8.dp
            ) {
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = tabTitle,
                                color = if (selectedTab == index) SocGoldPrimary else SocTextSecondary,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        modifier = Modifier.testTag("admin_tab_$index")
                    )
                }
            }

            val currentTabTitle = tabs.getOrNull(selectedTab) ?: tabs[0]

            when (currentTabTitle) {
                "All Devices & Users" -> {
                    SuperAdminDeviceDashboard()
                }
                "Questions (500)" -> {
                    // TAB 1: Questions Management
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SocDarkBlueSurface)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL: ${allQuestions.size} QUESTIONS",
                                color = SocGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Button(
                                onClick = { showAddQuestionDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SocGoldPrimary, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("admin_add_question_btn")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ADD QUESTION", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(allQuestions.take(50)) { q ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(text = "[${q.category.shortCode}] Q#${q.questionNumber}", color = SocBlueAccent, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = q.question, color = SocTextPrimary, fontSize = 12.sp, maxLines = 2)
                                        }
                                        IconButton(
                                            onClick = {
                                                SocRepository.deleteQuestion(q.id)
                                                Toast.makeText(context, "Question deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SocAlertRed, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                "Users Directory" -> {
                    // TAB 2: Users Directory
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "REGISTERED USERS & CANDIDATES (${registeredUsers.size})",
                                color = SocGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        items(registeredUsers) { u ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = u.fullName, color = SocTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (u.role == UserRole.SUPER_ADMIN) SocGoldPrimary else SocBlueDeep)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = u.role.displayName,
                                                color = if (u.role == UserRole.SUPER_ADMIN) Color.Black else SocBlueAccent,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Email: ${u.email} • Mobile: ${u.mobileNumber}", color = SocTextSecondary, fontSize = 11.sp)
                                    Text(text = "Qualification: ${u.qualification}", color = SocTextMuted, fontSize = 10.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = "Active Plan: ", color = SocTextSecondary, fontSize = 11.sp)
                                        Text(text = u.plan.title, color = SocGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(text = "2FA: ${if (u.is2faEnabled) "Enabled (${u.twoFactorMethod})" else "Disabled"}", color = SocTerminalGreen, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
                "Payments & Revenue" -> {
                    // TAB 3: Payments & Revenue
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            val totalRevenue = transactions.sumOf { it.amountRupees }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1E13)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SocTerminalGreen),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "TOTAL REVENUE (RAZORPAY)", color = SocTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                        Text(text = "₹$totalRevenue", color = SocTerminalGreen, fontSize = 22.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                    }
                                    Text(text = "${transactions.size} TRANSACTIONS", color = SocGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        items(transactions) { txn ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = txn.id, color = SocBlueAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                        Text(text = "₹${txn.amountRupees}", color = SocGoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = "User: ${txn.userName} (${txn.userEmail})", color = SocTextPrimary, fontSize = 11.sp)
                                    Text(text = "Method: ${txn.paymentMethod} • Status: ${txn.status}", color = SocTextSecondary, fontSize = 10.sp)
                                    Text(text = "Razorpay Ref: ${txn.razorpayPaymentId} • ${txn.timestamp}", color = SocTextMuted, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
                "Resume Reviews" -> {
                    // TAB 4: Resume Reviews
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Text(
                                text = "PREMIUM CANDIDATE RESUME REVIEW INBOX",
                                color = SocGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        items(resumeReviews) { rev ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = rev.userName, color = SocTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (rev.status.contains("Reviewed")) Color(0xFF0F2618) else Color(0xFF261D09))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = rev.status,
                                                color = if (rev.status.contains("Reviewed")) SocTerminalGreen else SocGoldPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Target Role: ${rev.targetRole} • ${rev.userEmail}", color = SocBlueAccent, fontSize = 11.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = rev.resumeSummary, color = SocTextSecondary, fontSize = 11.sp, maxLines = 2)

                                    if (rev.adminFeedback.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(SocDarkBlueSurfaceElevated)
                                                .padding(8.dp)
                                        ) {
                                            Text(text = "Admin Feedback: ${rev.adminFeedback}", color = SocTerminalGreen, fontSize = 11.sp)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    com.example.ui.components.SocButton(
                                        text = if (rev.adminFeedback.isBlank()) "WRITE FEEDBACK" else "UPDATE FEEDBACK",
                                        onClick = { reviewFeedbackDialogId = rev.id }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// Add Question Dialog for Admin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddQuestionDialog(
    onDismiss: () -> Unit,
    onAddQuestion: (QuestionCategory, String, List<String>, Int, String, String?) -> Unit
) {
    var selectedCategory by remember { mutableStateOf(QuestionCategory.SOC_FUNDAMENTALS) }
    var catExpanded by remember { mutableStateOf(false) }

    var questionText by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableIntStateOf(0) }
    var explanation by remember { mutableStateOf("") }
    var codeSnippet by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SocDarkBlueSurface,
        title = {
            Text(
                text = "ADD QUESTION TO BANK",
                color = SocGoldPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            val scrollState = rememberScrollState()
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(scrollState)) {
                // Category Selector
                Text(text = "Category", color = SocTextSecondary, fontSize = 11.sp)
                ExposedDropdownMenuBox(expanded = catExpanded, onExpandedChange = { catExpanded = !catExpanded }) {
                    OutlinedTextField(
                        value = selectedCategory.title,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = catExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                        QuestionCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.title) },
                                onClick = { selectedCategory = cat; catExpanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = questionText,
                    onValueChange = { questionText = it },
                    label = { Text("Question Statement") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = optionA, onValueChange = { optionA = it }, label = { Text("Option A (Correct default)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = optionB, onValueChange = { optionB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = optionC, onValueChange = { optionC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(value = optionD, onValueChange = { optionD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = explanation, onValueChange = { explanation = it }, label = { Text("Technical Explanation") }, maxLines = 3, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = codeSnippet, onValueChange = { codeSnippet = it }, label = { Text("Splunk/KQL/Wireshark Snippet (Optional)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (questionText.isNotBlank() && optionA.isNotBlank() && optionB.isNotBlank()) {
                        val opts = listOf(
                            optionA,
                            optionB,
                            optionC.ifBlank { "Third alternative response" },
                            optionD.ifBlank { "Fourth alternative response" }
                        )
                        onAddQuestion(
                            selectedCategory,
                            questionText,
                            opts,
                            correctIndex,
                            explanation.ifBlank { "Standard security best practice and protocol." },
                            codeSnippet.ifBlank { null }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SocGoldPrimary, contentColor = Color.Black)
            ) {
                Text("SAVE QUESTION", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel", color = SocTextSecondary) }
        }
    )
}
