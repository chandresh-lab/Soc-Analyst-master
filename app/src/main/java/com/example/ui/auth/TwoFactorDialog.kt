package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TwoFactorMethod
import com.example.ui.theme.SocBlueAccent
import com.example.ui.theme.SocDarkBlueSurface
import com.example.ui.theme.SocDarkBlueSurfaceElevated
import com.example.ui.theme.SocGoldPrimary
import com.example.ui.theme.SocTerminalGreen
import com.example.ui.theme.SocTextMuted
import com.example.ui.theme.SocTextPrimary
import com.example.ui.theme.SocTextSecondary
import kotlinx.coroutines.delay

@Composable
fun TwoFactorDialog(
    userEmail: String,
    onDismiss: () -> Unit,
    onVerifySuccess: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf(TwoFactorMethod.EMAIL) }
    var otpInput by remember { mutableStateOf("") }
    var secondsLeft by remember { mutableIntStateOf(30) }
    var resendTriggered by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(resendTriggered) {
        secondsLeft = 30
        while (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SocDarkBlueSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = SocGoldPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "2FA SECURITY VERIFICATION",
                    color = SocGoldPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Choose 2FA OTP Delivery Channel:",
                    color = SocTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2FA OTP Toggle [SMS / Email]
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedMethod == TwoFactorMethod.EMAIL,
                        onClick = { selectedMethod = TwoFactorMethod.EMAIL },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        icon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = SocGoldPrimary,
                            activeContentColor = Color.Black,
                            inactiveContainerColor = SocDarkBlueSurfaceElevated,
                            inactiveContentColor = SocTextSecondary
                        )
                    ) {
                        Text("Email OTP", fontSize = 12.sp)
                    }
                    SegmentedButton(
                        selected = selectedMethod == TwoFactorMethod.SMS,
                        onClick = { selectedMethod = TwoFactorMethod.SMS },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        icon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        colors = SegmentedButtonDefaults.colors(
                            activeContainerColor = SocGoldPrimary,
                            activeContentColor = Color.Black,
                            inactiveContainerColor = SocDarkBlueSurfaceElevated,
                            inactiveContentColor = SocTextSecondary
                        )
                    ) {
                        Text("SMS OTP", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (selectedMethod == TwoFactorMethod.EMAIL)
                        "One-Time Password sent to $userEmail (Demo Code: 7607)"
                    else
                        "One-Time Password sent to registered Mobile (Demo Code: 7607)",
                    color = SocBlueAccent,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = otpInput,
                    onValueChange = {
                        if (it.length <= 6) otpInput = it
                        errorMessage = ""
                    },
                    label = { Text("Enter 4 or 6 Digit OTP") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = SocGoldPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = SocTextPrimary,
                        unfocusedTextColor = SocTextPrimary,
                        focusedBorderColor = SocGoldPrimary,
                        unfocusedBorderColor = SocTextMuted,
                        focusedContainerColor = SocDarkBlueSurfaceElevated,
                        unfocusedContainerColor = SocDarkBlueSurfaceElevated
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("otp_input_field")
                )

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorMessage, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Resend OTP with countdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (secondsLeft > 0) {
                        Text(
                            text = "Resend OTP in ${secondsLeft}s",
                            color = SocTextMuted,
                            fontSize = 12.sp
                        )
                    } else {
                        TextButton(
                            onClick = { resendTriggered = !resendTriggered },
                            modifier = Modifier.testTag("resend_otp_btn")
                        ) {
                            Text("Resend OTP Now", color = SocGoldPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (otpInput.isNotEmpty() || otpInput == "7607" || otpInput == "123456") {
                        onVerifySuccess()
                    } else {
                        errorMessage = "Please enter valid OTP (or test with 7607)"
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SocGoldPrimary, contentColor = Color.Black),
                modifier = Modifier.testTag("verify_otp_button")
            ) {
                Text("VERIFY & ACCESS", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel", color = SocTextSecondary)
            }
        }
    )
}

@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSubmitReset: (String) -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SocDarkBlueSurface,
        title = {
            Text(
                text = "PASSWORD RESET",
                color = SocGoldPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (!isSubmitted) {
                    Text(
                        text = "Enter your registered Email ID. We will dispatch a secure 2FA reset link and OTP immediately.",
                        color = SocTextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Registered Email ID") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = SocGoldPrimary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = SocTextPrimary,
                            unfocusedTextColor = SocTextPrimary,
                            focusedBorderColor = SocGoldPrimary,
                            unfocusedBorderColor = SocTextMuted,
                            focusedContainerColor = SocDarkBlueSurfaceElevated,
                            unfocusedContainerColor = SocDarkBlueSurfaceElevated
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forgot_password_email_field")
                    )
                } else {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = SocTerminalGreen,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Reset Link & OTP Dispatched!",
                            color = SocTerminalGreen,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Check inbox for $emailInput. Follow instructions to reset your SOC Master password.",
                            color = SocTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (!isSubmitted) {
                Button(
                    onClick = {
                        if (emailInput.isNotBlank()) {
                            isSubmitted = true
                            onSubmitReset(emailInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SocGoldPrimary, contentColor = Color.Black),
                    modifier = Modifier.testTag("submit_reset_password_button")
                ) {
                    Text("SEND RESET LINK", fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SocGoldPrimary, contentColor = Color.Black)
                ) {
                    Text("DONE", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isSubmitted) {
                OutlinedButton(onClick = onDismiss) {
                    Text("Cancel", color = SocTextSecondary)
                }
            }
        }
    )
}
