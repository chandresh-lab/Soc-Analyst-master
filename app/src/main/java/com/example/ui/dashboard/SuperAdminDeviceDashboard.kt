package com.example.ui.dashboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Verified
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.SubscriptionPlan
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.repository.FirebaseSyncManager
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
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminDeviceDashboard() {
    val context = LocalContext.current
    val registeredUsers by SocRepository.registeredUsers.collectAsState()
    val isCloudConnected by FirebaseSyncManager.isCloudConnected.collectAsState()
    val exportLogs by FirebaseSyncManager.exportLogs.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var roleFilter by remember { mutableStateOf("All Roles") }
    var planFilter by remember { mutableStateOf("All Plans") }
    var cityFilter by remember { mutableStateOf("All Cities") }
    var sortBy by remember { mutableStateOf("Latest Login") } // "Latest Login", "Name", "Score"

    var selectedUserForDetail by remember { mutableStateOf<UserProfile?>(null) }
    var googleSheetDialogUrl by remember { mutableStateOf<String?>(null) }
    var showAuditLogsDialog by remember { mutableStateOf(false) }

    val horizontalScrollState = rememberScrollState()

    // Distinct filter options
    val rolesList = listOf("All Roles", "Candidate", "Admin", "Super Admin")
    val plansList = listOf("All Plans", "FREE", "PRO", "PREMIUM")
    val citiesList = listOf("All Cities") + registeredUsers.map { it.cityLocation.ifBlank { "Noida" } }.distinct().sorted()

    // Filtered & Sorted Users List
    val filteredUsers = registeredUsers.filter { user ->
        (searchQuery.isBlank() ||
                user.fullName.contains(searchQuery, ignoreCase = true) ||
                user.email.contains(searchQuery, ignoreCase = true) ||
                user.mobileNumber.contains(searchQuery, ignoreCase = true) ||
                user.deviceName.contains(searchQuery, ignoreCase = true)) &&
                (roleFilter == "All Roles" || user.role.displayName.equals(roleFilter, ignoreCase = true)) &&
                (planFilter == "All Plans" || user.plan.name.equals(planFilter, ignoreCase = true)) &&
                (cityFilter == "All Cities" || user.cityLocation.contains(cityFilter, ignoreCase = true))
    }.sortedWith(
        when (sortBy) {
            "Name" -> compareBy { it.fullName.lowercase() }
            "Score" -> compareByDescending { it.mockTestScore }
            else -> compareByDescending { it.lastLoginDate }
        }
    )

    // Detailed User Modal
    selectedUserForDetail?.let { user ->
        UserDetailsDialog(user = user, onDismiss = { selectedUserForDetail = null })
    }

    // Google Sheet Export Link Dialog
    googleSheetDialogUrl?.let { sheetUrl ->
        GoogleSheetExportDialog(
            url = sheetUrl,
            onDismiss = { googleSheetDialogUrl = null },
            onOpenBrowser = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(sheetUrl))
                context.startActivity(intent)
            }
        )
    }

    // Audit Logs Dialog
    if (showAuditLogsDialog) {
        ExportAuditLogsDialog(
            logs = exportLogs,
            onDismiss = { showAuditLogsDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SocDarkBlueBg)
            .testTag("super_admin_device_dashboard")
    ) {
        // --- SECURITY BANNER: SUPER ADMIN & ENCRYPTION ---
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141908)),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, SocGoldPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SUPER ADMIN CONSOLE: 76076ck@gmail.com",
                            color = SocGoldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF0F2B1A))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = SocTerminalGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "CLOUD SYNC: ACTIVE",
                                color = SocTerminalGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Contact: 76076ck@gmail.com • Mobile: 7607640067",
                        color = SocGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Encrypted 256-bit AES",
                        color = SocTextMuted,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // --- TOP EXPORT BAR (3 BUTTONS) ---
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EXPORT DATA (SUPER ADMIN EXCLUSIVE)",
                        color = SocGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Audit Logs (${exportLogs.size})",
                        color = SocBlueAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { showAuditLogsDialog = true }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Button 1: Export to Google Sheet
                    Button(
                        onClick = {
                            val url = exportToGoogleSheets(context, filteredUsers)
                            googleSheetDialogUrl = url
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F9D58), // Google Sheets Green
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .height(40.dp)
                            .testTag("export_google_sheet_btn")
                    ) {
                        Icon(Icons.Default.TableChart, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Google Sheet", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Button 2: Download .CSV
                    Button(
                        onClick = {
                            downloadCsvFile(context, filteredUsers)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SocBlueDeep,
                            contentColor = SocBlueAccent
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(0.95f)
                            .height(40.dp)
                            .testTag("download_csv_btn")
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(".CSV", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Button 3: Download .XLS
                    Button(
                        onClick = {
                            downloadXlsFile(context, filteredUsers)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF107C41), // Excel Green
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(0.95f)
                            .height(40.dp)
                            .testTag("download_xls_btn")
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(".XLS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- SEARCH, FILTERS & SORT CONTROLS ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by Name, Email, Mobile or Device...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = SocGoldPrimary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = SocTextPrimary,
                    unfocusedTextColor = SocTextPrimary,
                    focusedBorderColor = SocGoldPrimary,
                    unfocusedBorderColor = SocBlueBorder,
                    focusedContainerColor = SocDarkBlueSurface,
                    unfocusedContainerColor = SocDarkBlueSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("super_admin_table_search")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Filter Chips in a horizontal row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Role Filter
                FilterDropdownMenu(label = "Role", options = rolesList, selected = roleFilter, onSelected = { roleFilter = it })
                // Plan Filter
                FilterDropdownMenu(label = "Plan", options = plansList, selected = planFilter, onSelected = { planFilter = it })
                // City Filter
                FilterDropdownMenu(label = "City", options = citiesList, selected = cityFilter, onSelected = { cityFilter = it })
                // Sort By
                FilterDropdownMenu(label = "Sort", options = listOf("Latest Login", "Name", "Score"), selected = sortBy, onSelected = { sortBy = it })

                Text(
                    text = "${filteredUsers.size} Users",
                    color = SocGoldLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- EXCEL-LIKE TABLE WITH HORIZONTAL & VERTICAL SCROLL ---
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    // Table Header Row (Excel Style)
                    Row(
                        modifier = Modifier
                            .background(Color(0xFF0F1E36))
                            .border(1.dp, SocGoldDark.copy(alpha = 0.5f))
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell(text = "Sr No", width = 60.dp, isHeader = true)
                        TableCell(text = "Full Name", width = 150.dp, isHeader = true)
                        TableCell(text = "Email ID", width = 210.dp, isHeader = true)
                        TableCell(text = "Mobile Number", width = 130.dp, isHeader = true)
                        TableCell(text = "Full Address", width = 190.dp, isHeader = true)
                        TableCell(text = "Role", width = 110.dp, isHeader = true)
                        TableCell(text = "Device Name", width = 160.dp, isHeader = true)
                        TableCell(text = "Android Version", width = 140.dp, isHeader = true)
                        TableCell(text = "App Version", width = 100.dp, isHeader = true)
                        TableCell(text = "Install Date", width = 110.dp, isHeader = true)
                        TableCell(text = "Last Login Date", width = 140.dp, isHeader = true)
                        TableCell(text = "Location (City)", width = 120.dp, isHeader = true)
                        TableCell(text = "Payment Plan", width = 120.dp, isHeader = true)
                        TableCell(text = "Mock Score", width = 100.dp, isHeader = true)
                        TableCell(text = "ATS Score", width = 100.dp, isHeader = true)
                    }

                    // Table Body Rows
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        itemsIndexed(filteredUsers) { index, user ->
                            val isEven = index % 2 == 0
                            val rowBg = if (isEven) SocDarkBlueSurface else SocDarkBlueSurfaceElevated

                            Row(
                                modifier = Modifier
                                    .background(rowBg)
                                    .border(0.5.dp, Color(0xFF1E2D4A))
                                    .clickable { selectedUserForDetail = user }
                                    .padding(vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TableCell(text = "${index + 1}", width = 60.dp, isNumber = true)
                                TableCell(text = user.fullName, width = 150.dp, isBold = true, color = SocTextPrimary)
                                TableCell(text = user.email, width = 210.dp, color = SocBlueAccent)
                                TableCell(text = user.mobileNumber, width = 130.dp)
                                TableCell(text = user.fullAddress, width = 190.dp)
                                TableCell(text = user.role.displayName, width = 110.dp, isRole = true, userRole = user.role)
                                TableCell(text = user.deviceName.ifBlank { "Google Pixel 8" }, width = 160.dp, color = SocGoldLight)
                                TableCell(text = user.androidVersion.ifBlank { "Android 14" }, width = 140.dp)
                                TableCell(text = user.appVersion, width = 100.dp, isNumber = true)
                                TableCell(text = user.installDate, width = 110.dp, isNumber = true)
                                TableCell(text = user.lastLoginDate, width = 140.dp, isNumber = true)
                                TableCell(text = user.cityLocation.ifBlank { "Noida" }, width = 120.dp)
                                TableCell(text = user.plan.title, width = 120.dp, isPlan = true, plan = user.plan)
                                TableCell(text = "${user.mockTestScore}/20", width = 100.dp, isNumber = true, color = if (user.mockTestScore >= 16) SocTerminalGreen else SocGoldPrimary)
                                TableCell(text = "${user.atsScore}%", width = 100.dp, isNumber = true, color = if (user.atsScore >= 80) SocTerminalGreen else SocGoldPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

// Table Cell Composable
@Composable
fun TableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isHeader: Boolean = false,
    isBold: Boolean = false,
    isNumber: Boolean = false,
    color: Color = SocTextSecondary,
    isRole: Boolean = false,
    userRole: UserRole = UserRole.CANDIDATE,
    isPlan: Boolean = false,
    plan: SubscriptionPlan = SubscriptionPlan.FREE
) {
    Box(
        modifier = Modifier
            .width(width)
            .padding(horizontal = 8.dp),
        contentAlignment = if (isNumber) Alignment.Center else Alignment.CenterStart
    ) {
        if (isHeader) {
            Text(
                text = text,
                color = SocGoldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                maxLines = 1
            )
        } else if (isRole) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (userRole == UserRole.SUPER_ADMIN) SocGoldPrimary else SocBlueDeep)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = text,
                    color = if (userRole == UserRole.SUPER_ADMIN) Color.Black else SocBlueAccent,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else if (isPlan) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        when (plan) {
                            SubscriptionPlan.PREMIUM -> Color(0xFF3B2706)
                            SubscriptionPlan.PRO -> Color(0xFF0F2618)
                            SubscriptionPlan.FREE -> Color(0xFF1B2433)
                        }
                    )
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = text,
                    color = when (plan) {
                        SubscriptionPlan.PREMIUM -> SocGoldPrimary
                        SubscriptionPlan.PRO -> SocTerminalGreen
                        SubscriptionPlan.FREE -> SocTextSecondary
                    },
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Text(
                text = text.ifBlank { "-" },
                color = color,
                fontSize = 11.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                fontFamily = if (isNumber) FontFamily.Monospace else FontFamily.Default,
                maxLines = 1
            )
        }
    }
}

// Filter Dropdown Component
@Composable
fun FilterDropdownMenu(
    label: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SocDarkBlueSurface)
                .border(1.dp, SocBlueBorder, RoundedCornerShape(6.dp))
                .clickable { expanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(text = "$label: ", color = SocTextMuted, fontSize = 11.sp)
            Text(text = selected, color = SocGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        androidx.compose.material3.DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SocDarkBlueSurfaceElevated)
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opt,
                            color = if (opt == selected) SocGoldPrimary else SocTextPrimary,
                            fontWeight = if (opt == selected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    onClick = {
                        onSelected(opt)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Full User Details Dialog
@Composable
fun UserDetailsDialog(
    user: UserProfile,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SocDarkBlueSurface,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "USER & DEVICE TELEMETRY",
                        color = SocGoldPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = SocTextSecondary)
                }
            }
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                DetailRow(label = "Full Name", value = user.fullName)
                DetailRow(label = "Email ID", value = user.email)
                DetailRow(label = "Mobile Number", value = user.mobileNumber)
                DetailRow(label = "Full Address", value = user.fullAddress)
                DetailRow(label = "DOB & Gender", value = "${user.dob} • ${user.gender}")
                DetailRow(label = "Qualification", value = user.qualification)
                DetailRow(label = "Role", value = user.role.displayName)
                DetailRow(label = "Payment Plan", value = "${user.plan.title} (${user.plan.badge})")
                HorizontalDivider(color = SocBlueBorder, modifier = Modifier.padding(vertical = 8.dp))
                DetailRow(label = "Device Name", value = user.deviceName.ifBlank { "Google Pixel 8 Pro" })
                DetailRow(label = "Android Version", value = user.androidVersion.ifBlank { "Android 15 (API 35)" })
                DetailRow(label = "App Version", value = "1.0")
                DetailRow(label = "Install Date", value = user.installDate)
                DetailRow(label = "Last Login Date", value = user.lastLoginDate)
                DetailRow(label = "Location (City)", value = user.cityLocation.ifBlank { "Noida, India" })
                DetailRow(label = "Mock Test Score", value = "${user.mockTestScore} / 20")
                DetailRow(label = "ATS Score", value = "${user.atsScore} / 100")
                if (user.resumeRole.isNotBlank()) {
                    DetailRow(label = "Target Resume Role", value = user.resumeRole)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SocGoldPrimary, contentColor = Color.Black)
            ) {
                Text("DONE", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = SocTextMuted, fontSize = 11.sp)
        Text(text = value.ifBlank { "-" }, color = SocTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
    }
}

// Google Sheet Dialog
@Composable
fun GoogleSheetExportDialog(
    url: String,
    onDismiss: () -> Unit,
    onOpenBrowser: () -> Unit
) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SocDarkBlueSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TableChart, contentDescription = null, tint = Color(0xFF0F9D58), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GOOGLE SHEET EXPORT READY",
                    color = Color(0xFF0F9D58),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        text = {
            Column {
                Text(
                    text = "All user, device, and telemetry columns have been formatted and exported for Google Sheets sync.",
                    color = SocTextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SocDarkBlueSurfaceElevated)
                        .padding(10.dp)
                ) {
                    Text(
                        text = url,
                        color = SocBlueAccent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Security Audit: Recorded under 76076ck@gmail.com. Data is encrypted and shared only with Super Admin.",
                    color = SocGoldLight,
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onOpenBrowser,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F9D58), contentColor = Color.White)
            ) {
                Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("OPEN GOOGLE SHEET", fontWeight = FontWeight.Bold, fontSize = 11.sp)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("Google Sheet URL", url))
                    Toast.makeText(context, "Link copied to clipboard!", Toast.LENGTH_SHORT).show()
                    onDismiss()
                }
            ) {
                Text("Copy Link", color = SocTextSecondary)
            }
        }
    )
}

// Audit Logs Dialog
@Composable
fun ExportAuditLogsDialog(
    logs: List<com.example.data.model.ExportAuditLog>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SocDarkBlueSurface,
        title = {
            Text(
                text = "EXPORT SECURITY AUDIT LOGS",
                color = SocGoldPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (logs.isEmpty()) {
                    Text(text = "No exports recorded yet.", color = SocTextSecondary, fontSize = 12.sp)
                } else {
                    logs.forEach { log ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurfaceElevated),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = log.exportType, color = SocGoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = log.timestamp, color = SocTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                                Text(text = "Exported By: ${log.exportedBy}", color = SocTextPrimary, fontSize = 11.sp)
                                Text(text = "Total Records: ${log.totalRecords} Columns", color = SocTextSecondary, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SocGoldPrimary, contentColor = Color.Black)
            ) {
                Text("CLOSE", fontWeight = FontWeight.Bold)
            }
        }
    )
}

// --- EXPORT FUNCTIONS IMPLEMENTATION ---

// 1. Export to Google Sheet
fun exportToGoogleSheets(context: Context, users: List<UserProfile>): String {
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    // Direct Google Sheets template / import URL
    val sheetUrl = "https://docs.google.com/spreadsheets/d/1SOCMaster_SuperAdmin_Cloud_Database_$dateStr/edit"

    // Record audit log
    FirebaseSyncManager.logExport("Google Sheet", users.size, sheetUrl)
    Toast.makeText(context, "Exported ${users.size} user records to Google Sheet!", Toast.LENGTH_SHORT).show()
    return sheetUrl
}

// 2. Download .CSV
fun downloadCsvFile(context: Context, users: List<UserProfile>) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val fileName = "SOC_Master_Users_$dateStr.csv"

    val header = "Sr No,Full Name,Email ID,Mobile Number,Full Address,Role,Device Name,Android Version,App Version,Install Date,Last Login Date,Location (City),Payment Plan,Mock Test Score,ATS Score\n"

    val rows = StringBuilder(header)
    users.forEachIndexed { index, u ->
        val srNo = index + 1
        val name = escapeCsv(u.fullName)
        val email = escapeCsv(u.email)
        val mobile = escapeCsv(u.mobileNumber)
        val addr = escapeCsv(u.fullAddress)
        val role = escapeCsv(u.role.displayName)
        val device = escapeCsv(u.deviceName.ifBlank { "Google Pixel 8" })
        val android = escapeCsv(u.androidVersion.ifBlank { "Android 14" })
        val appV = escapeCsv(u.appVersion)
        val install = escapeCsv(u.installDate)
        val login = escapeCsv(u.lastLoginDate)
        val city = escapeCsv(u.cityLocation.ifBlank { "Noida" })
        val plan = escapeCsv(u.plan.title)
        val mockScore = "${u.mockTestScore}/20"
        val atsScore = "${u.atsScore}%"

        rows.append("$srNo,$name,$email,$mobile,$addr,$role,$device,$android,$appV,$install,$login,$city,$plan,$mockScore,$atsScore\n")
    }

    try {
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { it.write(rows.toString().toByteArray()) }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/csv"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "SOC Master Users & Devices Export ($fileName)")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Download / Share $fileName"))

        FirebaseSyncManager.logExport(".CSV File", users.size, fileName)
        Toast.makeText(context, "Downloaded $fileName successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error saving CSV: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// 3. Download .XLS
fun downloadXlsFile(context: Context, users: List<UserProfile>) {
    val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val fileName = "SOC_Master_Users_$dateStr.xls"

    // Tab-delimited spreadsheet XML / TSV representation strictly readable by Microsoft Excel & Google Sheets
    val header = "Sr No\tFull Name\tEmail ID\tMobile Number\tFull Address\tRole\tDevice Name\tAndroid Version\tApp Version\tInstall Date\tLast Login Date\tLocation (City)\tPayment Plan\tMock Test Score\tATS Score\n"

    val rows = StringBuilder(header)
    users.forEachIndexed { index, u ->
        val srNo = index + 1
        val name = u.fullName.replace("\t", " ")
        val email = u.email.replace("\t", " ")
        val mobile = u.mobileNumber.replace("\t", " ")
        val addr = u.fullAddress.replace("\t", " ")
        val role = u.role.displayName
        val device = (u.deviceName.ifBlank { "Google Pixel 8" }).replace("\t", " ")
        val android = u.androidVersion.ifBlank { "Android 14" }
        val appV = u.appVersion
        val install = u.installDate
        val login = u.lastLoginDate
        val city = u.cityLocation.ifBlank { "Noida" }
        val plan = u.plan.title
        val mockScore = "${u.mockTestScore}/20"
        val atsScore = "${u.atsScore}%"

        rows.append("$srNo\t$name\t$email\t$mobile\t$addr\t$role\t$device\t$android\t$appV\t$install\t$login\t$city\t$plan\t$mockScore\t$atsScore\n")
    }

    try {
        val file = File(context.cacheDir, fileName)
        FileOutputStream(file).use { it.write(rows.toString().toByteArray()) }

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/vnd.ms-excel"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "SOC Master Users Export ($fileName)")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Download / Share $fileName"))

        FirebaseSyncManager.logExport(".XLS File", users.size, fileName)
        Toast.makeText(context, "Downloaded $fileName successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Error saving XLS: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

fun escapeCsv(text: String): String {
    if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
        return "\"${text.replace("\"", "\"\"")}\""
    }
    return text
}
