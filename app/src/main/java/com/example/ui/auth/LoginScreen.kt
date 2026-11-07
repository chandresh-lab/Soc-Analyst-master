package com.example.ui.auth

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.UserRole
import com.example.data.repository.SocRepository
import com.example.ui.theme.SocAlertRed
import com.example.ui.theme.SocBlueAccent
import com.example.ui.theme.SocBlueBorder
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
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateTo2fa: (String, UserRole) -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.CANDIDATE) }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    var identifierInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    if (showForgotPasswordDialog) {
        ForgotPasswordDialog(
            onDismiss = { showForgotPasswordDialog = false },
            onSubmitReset = { /* reset dispatched */ }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(SocDarkBlueBg, Color(0xFF0A0F1D), Color(0xFF0F172A))
                )
            )
            .testTag("login_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo & Header
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, SocGoldPrimary, CircleShape)
                    .background(SocDarkBlueSurfaceElevated)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.soc_gold_shield),
                    contentDescription = "SOC Master Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(82.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "SOC MASTER",
                color = SocGoldPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Text(
                text = "Security Operations Center Preparation",
                color = SocTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Container Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "1. SINGLE LOGIN CONSOLE",
                        color = SocGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Role Selector Dropdown
                    Text(
                        text = "Select Access Role",
                        color = SocTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    ExposedDropdownMenuBox(
                        expanded = roleDropdownExpanded,
                        onExpandedChange = { roleDropdownExpanded = !roleDropdownExpanded },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedRole.displayName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (selectedRole) {
                                        UserRole.CANDIDATE -> Icons.Default.Person
                                        UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                        UserRole.SUPER_ADMIN -> Icons.Default.Shield
                                    },
                                    contentDescription = null,
                                    tint = SocGoldPrimary
                                )
                            },
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
                                .menuAnchor()
                                .testTag("role_dropdown_field")
                        )

                        ExposedDropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false },
                            modifier = Modifier.background(SocDarkBlueSurfaceElevated)
                        ) {
                            UserRole.entries.forEach { role ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = role.displayName + if (role == UserRole.SUPER_ADMIN) " (76076ck@gmail.com)" else "",
                                            color = if (role == selectedRole) SocGoldPrimary else SocTextPrimary,
                                            fontWeight = if (role == selectedRole) FontWeight.Bold else FontWeight.Normal
                                        )
                                    },
                                    onClick = {
                                        selectedRole = role
                                        roleDropdownExpanded = false
                                        errorMessage = ""
                                        if (role == UserRole.SUPER_ADMIN) {
                                            identifierInput = "76076ck@gmail.com"
                                        }
                                    },
                                    modifier = Modifier.testTag("role_item_${role.name}")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email or Mobile Field
                    Text(
                        text = "Email ID or Mobile Number",
                        color = SocTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = identifierInput,
                        onValueChange = {
                            identifierInput = it
                            errorMessage = ""
                        },
                        placeholder = { Text("e.g. candidate@aaravitsolutons.com or +91 98...") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = SocGoldPrimary)
                        },
                        singleLine = true,
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
                            .testTag("login_email_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    Text(
                        text = "Password",
                        color = SocTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = {
                            passwordInput = it
                            errorMessage = ""
                        },
                        placeholder = { Text("Enter your password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = SocGoldPrimary)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = SocTextSecondary
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
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
                            .testTag("login_password_input")
                    )

                    // Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = { showForgotPasswordDialog = true },
                            modifier = Modifier.testTag("forgot_password_btn")
                        ) {
                            Text(
                                text = "Forgot Password?",
                                color = SocBlueAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (errorMessage.isNotEmpty()) {
                        Text(
                            text = errorMessage,
                            color = SocAlertRed,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Fixed-Size 280x48dp Center Aligned Button
                    com.example.ui.components.SocButton(
                        text = "LOGIN AS ${selectedRole.displayName.uppercase()}",
                        onClick = {
                            if (selectedRole == UserRole.SUPER_ADMIN && !identifierInput.trim().equals("76076ck@gmail.com", ignoreCase = true)) {
                                errorMessage = "Super Admin role is strictly restricted to 76076ck@gmail.com"
                                return@SocButton
                            }
                            if (identifierInput.isBlank()) {
                                errorMessage = "Please enter valid Email ID or Mobile Number"
                                return@SocButton
                            }
                            // Route directly to 2FA security verification
                            onNavigateTo2fa(identifierInput.trim(), selectedRole)
                        },
                        modifier = Modifier.testTag("login_as_role_button")
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Candidate & Admin Registration Link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "New Candidate or Admin?",
                    color = SocTextSecondary,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Register Here",
                    color = SocGoldPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onNavigateToRegister() }
                        .testTag("navigate_to_register_btn")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Text(
                text = "Powered by Aarav IT Solutions • aaravitsolutons.com",
                color = SocTextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
