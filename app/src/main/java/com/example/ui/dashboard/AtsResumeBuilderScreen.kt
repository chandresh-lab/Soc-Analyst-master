package com.example.ui.dashboard

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.util.Base64
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Download
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import androidx.core.content.FileProvider
import com.example.data.model.ResumeTemplate
import com.example.data.model.ResumeTemplateData
import com.example.data.model.SubscriptionPlan
import com.example.data.repository.SocRepository
import com.example.ui.components.SocButton
import com.example.ui.components.SocOutlinedButton
import com.example.ui.theme.SocAlertRed
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
import com.example.ui.theme.SocWarningOrange
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtsResumeBuilderScreen(
    onNavigateBack: () -> Unit,
    onNavigateToUpgrade: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by SocRepository.currentUser.collectAsState()
    val isFreePlan = currentUser?.plan == SubscriptionPlan.FREE

    val allTemplates by SocRepository.templates.collectAsState()
    var selectedTemplateIndex by remember { mutableIntStateOf(0) }
    val currentTemplate = allTemplates[selectedTemplateIndex.coerceIn(0, allTemplates.size - 1)]

    // Editable candidate fields
    var candidateName by remember { mutableStateOf(currentUser?.fullName ?: "Rahul Sharma") }
    var candidateEmail by remember { mutableStateOf(currentUser?.email ?: "rahul.soc@aaravitsolutons.com") }
    var candidatePhone by remember { mutableStateOf(currentUser?.mobileNumber ?: "+91 98765 43210") }
    var candidateRole by remember { mutableStateOf(currentTemplate.targetRole) }
    var candidateSummary by remember { mutableStateOf(currentTemplate.summary) }

    val activeBullets = remember(selectedTemplateIndex) {
        mutableStateListOf<String>().apply { addAll(currentTemplate.sampleBullets) }
    }

    // Job Description Matcher state
    var jobDescriptionInput by remember { mutableStateOf("") }
    var jdMatchResult by remember { mutableStateOf<Pair<Int, List<String>>?>(null) }

    // Bullet Level selector for Ready SOC bullets
    var selectedExpLevelIndex by remember { mutableIntStateOf(0) }
    val expGroups = ResumeTemplateData.experienceBullets
    val currentExpGroup = expGroups[selectedExpLevelIndex]

    // Calculate ATS Score in real-time
    val fullResumeText = remember(candidateSummary, activeBullets.toList()) {
        "$candidateRole $candidateSummary ${activeBullets.joinToString(" ")}"
    }
    val (atsScore, atsFeedback) = remember(fullResumeText) {
        ResumeTemplateData.calculateAtsScore(
            jobRole = candidateRole,
            resumeText = fullResumeText,
            skillsCount = currentTemplate.coreSkills.size,
            experienceBulletsCount = activeBullets.size,
            certificationsCount = currentTemplate.recommendedCerts.size
        )
    }

    androidx.compose.runtime.LaunchedEffect(atsScore, candidateRole) {
        SocRepository.updateAtsScoreAndResume(atsScore, candidateRole, candidateSummary, activeBullets.toList())
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "ATS RESUME BUILDER",
                            color = SocGoldPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "15+ SOC Templates • ATS Score • A4 PDF Download",
                            color = SocTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("ats_builder_back_btn")
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
            // ATS Score Gauge Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocGoldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { atsScore / 100f },
                            color = if (atsScore >= 75) SocTerminalGreen else if (atsScore >= 50) SocGoldPrimary else SocAlertRed,
                            strokeWidth = 7.dp,
                            modifier = Modifier.fillMaxSize()
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$atsScore",
                                color = SocTextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "ATS SCORE",
                                color = SocTextMuted,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (atsScore >= 80) "Optimal ATS Compliance" else if (atsScore >= 60) "Good ATS Foundation" else "Needs Metric Polish",
                            color = if (atsScore >= 80) SocTerminalGreen else SocGoldPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        atsFeedback.entries.take(2).forEach { (cat, fb) ->
                            Text(
                                text = "• $fb",
                                color = SocTextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // 15+ SOC Templates Carousel
            Text(
                text = "15+ SOC RESUME TEMPLATES",
                color = SocGoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allTemplates.size) { index ->
                    val tmpl = allTemplates[index]
                    val isLocked = isFreePlan && index > 0
                    val isSelected = selectedTemplateIndex == index

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) SocBlueDeep else SocDarkBlueSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) SocGoldPrimary else SocBlueBorder
                        ),
                        modifier = Modifier
                            .width(180.dp)
                            .clickable {
                                if (isLocked) {
                                    onNavigateToUpgrade()
                                } else {
                                    selectedTemplateIndex = index
                                    candidateRole = tmpl.targetRole
                                    candidateSummary = tmpl.summary
                                    activeBullets.clear()
                                    activeBullets.addAll(tmpl.sampleBullets)
                                }
                            }
                            .testTag("template_card_$index")
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = tmpl.levelCategory,
                                    color = SocGoldPrimary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (isLocked) {
                                    Text(text = "PRO 🔒", color = SocWarningOrange, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = tmpl.title,
                                color = SocTextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${tmpl.atsKeywords.size} Keywords Included",
                                color = SocTextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            // Job Description (JD) Matcher Section
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Analytics, contentDescription = null, tint = SocBlueAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "SOC Job Description (JD) Matcher", color = SocTextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Paste any target SOC Job Description below to evaluate ATS match percentage and discover missing keywords.",
                        color = SocTextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = jobDescriptionInput,
                        onValueChange = { jobDescriptionInput = it },
                        placeholder = { Text("Paste Job Description (e.g. Seeking SOC Analyst with Splunk, CrowdStrike, PCAP analysis...)") },
                        maxLines = 4,
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
                            .testTag("jd_input_field")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // FIXED-SIZE 280x48dp Button
                    SocButton(
                        text = "ANALYZE JD MATCH %",
                        icon = Icons.Default.Analytics,
                        onClick = {
                            if (jobDescriptionInput.isNotBlank()) {
                                jdMatchResult = ResumeTemplateData.matchJobDescription(jobDescriptionInput, fullResumeText)
                            }
                        },
                        modifier = Modifier.testTag("calculate_jd_match_btn")
                    )

                    jdMatchResult?.let { (matchPct, missingWords) ->
                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SocBlueBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$matchPct% MATCH",
                                color = if (matchPct >= 70) SocTerminalGreen else SocGoldPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (matchPct >= 70) "Strong match for target role" else "Add missing keywords to improve ranking",
                                color = SocTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        if (missingWords.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Missing Keywords Detected in JD:",
                                color = SocWarningOrange,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                missingWords.forEach { word ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF2B1C0B))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "+ $word", color = SocGoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Ready Experience Bullets (Tap to Add)
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "READY SOC EXPERIENCE BULLETS (TAP TO INSERT)",
                        color = SocGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    ScrollableTabRow(
                        selectedTabIndex = selectedExpLevelIndex,
                        containerColor = SocDarkBlueSurfaceElevated,
                        contentColor = SocGoldPrimary,
                        edgePadding = 0.dp
                    ) {
                        expGroups.forEachIndexed { idx, grp ->
                            Tab(
                                selected = selectedExpLevelIndex == idx,
                                onClick = { selectedExpLevelIndex = idx },
                                text = { Text(grp.experienceLevel, fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    currentExpGroup.bullets.forEachIndexed { i, bullet ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(SocDarkBlueSurfaceElevated)
                                .clickable {
                                    if (!activeBullets.contains(bullet)) {
                                        activeBullets.add(bullet)
                                        Toast.makeText(context, "Bullet point added to resume!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .padding(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = SocGoldPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = bullet, color = SocTextSecondary, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Editable Candidate Info & Resume Preview
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CANDIDATE PROFILE & BULLETS",
                        color = SocGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = candidateName,
                        onValueChange = { candidateName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SocTextPrimary, unfocusedTextColor = SocTextPrimary,
                            focusedBorderColor = SocGoldPrimary, unfocusedBorderColor = SocBlueBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = candidateRole,
                        onValueChange = { candidateRole = it },
                        label = { Text("Target SOC Role") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SocTextPrimary, unfocusedTextColor = SocTextPrimary,
                            focusedBorderColor = SocGoldPrimary, unfocusedBorderColor = SocBlueBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = candidateSummary,
                        onValueChange = { candidateSummary = it },
                        label = { Text("Professional Summary") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SocTextPrimary, unfocusedTextColor = SocTextPrimary,
                            focusedBorderColor = SocGoldPrimary, unfocusedBorderColor = SocBlueBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Active Experience Bullets (${activeBullets.size}):",
                        color = SocTextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    activeBullets.forEachIndexed { idx, bullet ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.padding(vertical = 3.dp)
                        ) {
                            Text(text = "•", color = SocGoldPrimary, fontSize = 14.sp, modifier = Modifier.padding(end = 6.dp))
                            Text(text = bullet, color = SocTextSecondary, fontSize = 11.sp, lineHeight = 15.sp, modifier = Modifier.weight(1f))
                            Text(
                                text = "✕",
                                color = SocAlertRed,
                                fontSize = 12.sp,
                                modifier = Modifier
                                    .padding(start = 6.dp)
                                    .clickable { activeBullets.removeAt(idx) }
                            )
                        }
                    }
                }
            }

            // PDF Download & Export Action: FIXED SIZE 280x48dp
            SocButton(
                text = "DOWNLOAD ATS RESUME PDF",
                icon = Icons.Default.Download,
                onClick = {
                    exportResumeAsA4Pdf(
                        context = context,
                        name = candidateName,
                        email = candidateEmail,
                        phone = candidatePhone,
                        role = candidateRole,
                        summary = candidateSummary,
                        skills = currentTemplate.coreSkills,
                        bullets = activeBullets.toList(),
                        certs = currentTemplate.recommendedCerts,
                        photoUri = currentUser?.profilePhotoUri
                    )
                },
                modifier = Modifier.testTag("download_pdf_button")
            )

            // Admin Review Submission for Premium Users
            if (currentUser?.plan == SubscriptionPlan.PREMIUM) {
                SocOutlinedButton(
                    text = "SUBMIT FOR ADMIN REVIEW (PREMIUM)",
                    icon = Icons.Default.Send,
                    contentColor = SocGoldPrimary,
                    onClick = {
                        SocRepository.submitResumeForReview(candidateRole, candidateSummary)
                        Toast.makeText(context, "Resume submitted to Aarav IT Solutions Admin for review!", Toast.LENGTH_LONG).show()
                    },
                    modifier = Modifier.testTag("submit_resume_review_btn")
                )
            }
        }
    }
}

// Function to generate and export professional A4 PDF (Black + Gold Header, Photo Top Right)
fun exportResumeAsA4Pdf(
    context: Context,
    name: String,
    email: String,
    phone: String,
    role: String,
    summary: String,
    skills: List<String>,
    bullets: List<String>,
    certs: List<String>,
    photoUri: String? = null
) {
    try {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 standard 595x842
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // 1. BLACK + GOLD HEADER BANNER
        val headerBgPaint = Paint().apply { color = android.graphics.Color.parseColor("#080C14") }
        canvas.drawRect(0f, 0f, 595f, 120f, headerBgPaint)

        // Gold border bar under header
        val goldBarPaint = Paint().apply { color = android.graphics.Color.parseColor("#FFD700"); strokeWidth = 3f }
        canvas.drawLine(0f, 120f, 595f, 120f, goldBarPaint)

        // Candidate Name (in Gold Bold)
        val namePaint = Paint().apply {
            textSize = 22f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#FFD700")
        }
        canvas.drawText(name.uppercase(), 40f, 48f, namePaint)

        // Target Role (in White Bold)
        val rolePaint = Paint().apply {
            textSize = 13f
            isFakeBoldText = true
            color = android.graphics.Color.WHITE
        }
        canvas.drawText(role.uppercase(), 40f, 70f, rolePaint)

        // Contact Info (in Light Slate)
        val contactPaint = Paint().apply {
            textSize = 10f
            color = android.graphics.Color.parseColor("#94A3B8")
        }
        canvas.drawText("Email: $email   |   Phone: $phone   |   Aarav IT Solutions Certified", 40f, 92f, contactPaint)

        // Candidate Photo on TOP RIGHT (Circle Crop)
        val photoCenterX = 530f
        val photoCenterY = 60f
        val photoRadius = 38f

        // Draw Gold Circle Frame for Photo
        val photoFramePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#FFD700")
            style = Paint.Style.STROKE
            strokeWidth = 2.5f
            isAntiAlias = true
        }
        val photoFillPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#141E33")
            style = Paint.Style.FILL
            isAntiAlias = true
        }
        canvas.drawCircle(photoCenterX, photoCenterY, photoRadius, photoFillPaint)
        canvas.drawCircle(photoCenterX, photoCenterY, photoRadius, photoFramePaint)

        // If Base64 bitmap is available, render inside
        var photoDrawn = false
        if (!photoUri.isNullOrBlank() && photoUri.length > 50) {
            try {
                val decodedBytes = Base64.decode(photoUri, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                if (bmp != null) {
                    val scaled = Bitmap.createScaledBitmap(bmp, (photoRadius * 2).toInt(), (photoRadius * 2).toInt(), true)
                    canvas.drawBitmap(scaled, photoCenterX - photoRadius, photoCenterY - photoRadius, null)
                    canvas.drawCircle(photoCenterX, photoCenterY, photoRadius, photoFramePaint)
                    photoDrawn = true
                }
            } catch (_: Exception) {}
        }
        if (!photoDrawn) {
            val avatarTextPaint = Paint().apply {
                textSize = 18f
                color = android.graphics.Color.parseColor("#FFD700")
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }
            canvas.drawText(name.take(2).uppercase(), photoCenterX, photoCenterY + 7f, avatarTextPaint)
        }

        // Section Headers & Body Paints
        val secHeaderPaint = Paint().apply {
            textSize = 11f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#0B132B")
        }
        val secLinePaint = Paint().apply {
            color = android.graphics.Color.parseColor("#D4AF37")
            strokeWidth = 1f
        }
        val bodyPaint = Paint().apply {
            textSize = 9.5f
            color = android.graphics.Color.parseColor("#1F2937")
        }
        val bulletDotPaint = Paint().apply {
            color = android.graphics.Color.parseColor("#D4AF37")
            style = Paint.Style.FILL
        }

        var y = 145f

        // Helper for drawing Section Header
        fun drawSectionHeader(title: String) {
            canvas.drawText(title, 40f, y, secHeaderPaint)
            canvas.drawLine(40f, y + 4f, 555f, y + 4f, secLinePaint)
            y += 18f
        }

        // 1. PROFESSIONAL SUMMARY
        drawSectionHeader("1. PROFESSIONAL SUMMARY")
        val summaryWords = summary.split(" ")
        var line = ""
        summaryWords.forEach { word ->
            if (line.length + word.length > 88) {
                canvas.drawText(line, 40f, y, bodyPaint)
                y += 13f
                line = "$word "
            } else {
                line += "$word "
            }
        }
        if (line.isNotBlank()) {
            canvas.drawText(line, 40f, y, bodyPaint)
            y += 18f
        }

        // 2. CORE SECURITY COMPETENCIES
        drawSectionHeader("2. CORE TECHNICAL SKILLS & SIEM PROTOCOLS")
        canvas.drawText(skills.joinToString("   •   "), 40f, y, bodyPaint)
        y += 20f

        // 3. SECURITY OPERATIONS EXPERIENCE
        drawSectionHeader("3. SECURITY OPERATIONS & INCIDENT RESPONSE EXPERIENCE")
        bullets.take(6).forEach { bullet ->
            canvas.drawCircle(45f, y - 3f, 2.5f, bulletDotPaint)
            var bulletLine = ""
            val words = bullet.split(" ")
            words.forEach { w ->
                if (bulletLine.length + w.length > 82) {
                    canvas.drawText(bulletLine, 55f, y, bodyPaint)
                    y += 13f
                    bulletLine = "$w "
                } else {
                    bulletLine += "$w "
                }
            }
            if (bulletLine.isNotBlank()) {
                canvas.drawText(bulletLine, 55f, y, bodyPaint)
                y += 15f
            }
        }
        y += 5f

        // 4. KEY SOC PROJECTS & SIMULATIONS
        drawSectionHeader("4. KEY SOC PROJECTS & THREAT HUNTING SCENARIOS")
        val projects = listOf(
            "Splunk Enterprise Lab: Configured alert correlation searches for Brute Force & Lateral Movement (T1021).",
            "Wireshark Deep Packet Inspection: Reconstructed malicious payloads, extracted C2 beacons & analyzed TLS handshakes."
        )
        projects.forEach { prj ->
            canvas.drawCircle(45f, y - 3f, 2.5f, bulletDotPaint)
            canvas.drawText(prj, 55f, y, bodyPaint)
            y += 15f
        }
        y += 5f

        // 5. CERTIFICATIONS & ACCREDITATIONS
        drawSectionHeader("5. CERTIFICATIONS & INDUSTRY ACCREDITATIONS")
        canvas.drawText(certs.joinToString("   |   "), 40f, y, bodyPaint)

        // Footer
        val footerPaint = Paint().apply {
            textSize = 8.5f
            color = android.graphics.Color.GRAY
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("Generated via SOC Master • Aarav IT Solutions (aaravitsolutons.com)", 297f, 825f, footerPaint)

        document.finishPage(page)

        // Save PDF to cache / external files
        val file = File(context.cacheDir, "SOC_Master_Resume_${System.currentTimeMillis()}.pdf")
        FileOutputStream(file).use { outputStream ->
            document.writeTo(outputStream)
        }
        document.close()

        // Share or open PDF
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "SOC Master ATS Resume - $name")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Download / Share SOC Resume PDF"))
        Toast.makeText(context, "A4 ATS Resume PDF Generated & Saved!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "PDF Generation error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
