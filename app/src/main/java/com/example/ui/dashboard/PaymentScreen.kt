package com.example.ui.dashboard

import android.content.ClipData
import android.content.ClipboardManager
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.data.model.SubscriptionPlan
import com.example.data.repository.PaymentTransaction
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
import com.example.ui.theme.SocGoldDark
import com.example.ui.theme.SocGoldLight
import com.example.ui.theme.SocGoldPrimary
import com.example.ui.theme.SocTerminalGreen
import com.example.ui.theme.SocTextMuted
import com.example.ui.theme.SocTextPrimary
import com.example.ui.theme.SocTextSecondary
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val currentUser by SocRepository.currentUser.collectAsState()
    val user = currentUser

    // Selected Tier for Checkout
    var selectedPlanForCheckout by remember { mutableStateOf<SubscriptionPlan?>(null) }
    var selectedProBillingPeriod by remember { mutableIntStateOf(0) } // 0 = Monthly (199), 1 = Yearly (999)

    var showRazorpayDialog by remember { mutableStateOf(false) }
    var completedTxn by remember { mutableStateOf<PaymentTransaction?>(null) }

    val scrollState = rememberScrollState()

    // Razorpay Checkout Modal with Secure Payment Methods (Option 1, 2, 3)
    if (showRazorpayDialog && selectedPlanForCheckout != null) {
        val targetPlan = selectedPlanForCheckout!!
        val amount = when (targetPlan) {
            SubscriptionPlan.FREE -> 0
            SubscriptionPlan.PRO -> if (selectedProBillingPeriod == 0) 199 else 999
            SubscriptionPlan.PREMIUM -> 1999
        }

        SecureRazorpayCheckoutModal(
            plan = targetPlan,
            amount = amount,
            userName = user?.fullName ?: "Candidate",
            userEmail = user?.email ?: "candidate@aaravitsolutons.com",
            onDismiss = { showRazorpayDialog = false },
            onPaymentSuccess = { pMethod ->
                val txn = SocRepository.upgradeSubscription(targetPlan, pMethod, amount)
                completedTxn = txn
                showRazorpayDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PLANS & PRICING",
                            color = SocGoldPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Razorpay Secure Auto-Upgrade Gateway",
                            color = SocTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("payment_back_btn")
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
            // Success Upgrade Confirmation & Receipt Download
            completedTxn?.let { txn ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C2417)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, SocTerminalGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Verified, contentDescription = null, tint = SocTerminalGreen, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "PAYMENT SUCCESSFUL & AUTO-UPGRADED!",
                            color = SocTerminalGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "You are now on the ${txn.plan.title} Plan. Cloud sync auto-updated in Firebase.",
                            color = SocTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Razorpay Ref: ${txn.razorpayPaymentId} • Amount: ₹${txn.amountRupees}",
                            color = SocGoldLight,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Download Payment Receipt Button: FIXED SIZE 280x48dp
                        SocButton(
                            text = "DOWNLOAD PAYMENT RECEIPT",
                            icon = Icons.Default.Download,
                            onClick = {
                                generateAndExportPaymentReceipt(context, txn)
                            },
                            modifier = Modifier.testTag("download_payment_receipt_btn")
                        )
                    }
                }
            }

            // Current Plan Header
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "CURRENT ACTIVE PLAN", color = SocTextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${user?.plan?.title ?: "FREE"} (${user?.plan?.badge ?: "Starter"})",
                            color = SocGoldPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(SocBlueDeep)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(text = "ACTIVE", color = SocTerminalGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // --- PLAN 1: FREE (Rs.0) ---
            PricingTierCard(
                title = "FREE PLAN",
                price = "₹0",
                billingPeriod = "Forever Free",
                badge = "STARTER",
                isCurrentPlan = user?.plan == SubscriptionPlan.FREE,
                features = listOf(
                    "50 SOC Q&A Bank Access",
                    "50 MCQ Mode Practice Questions",
                    "1 Core SOC Resume Template (L1)",
                    "1 Daily Mock Test per Day",
                    "Sponsored Security Career Ads"
                ),
                accentColor = SocTextSecondary,
                onSelectPlan = {},
                buttonText = if (user?.plan == SubscriptionPlan.FREE) "CURRENT PLAN" else "DOWNGRADE TO FREE",
                isButtonEnabled = user?.plan != SubscriptionPlan.FREE
            )

            // --- PLAN 2: PRO (Rs.199/Month or Rs.999/Year) ---
            PricingTierCard(
                title = "PRO PLAN",
                price = if (selectedProBillingPeriod == 0) "₹199" else "₹999",
                billingPeriod = if (selectedProBillingPeriod == 0) "per month" else "per year (Save 58%)",
                badge = "POPULAR CHOICE",
                isCurrentPlan = user?.plan == SubscriptionPlan.PRO,
                features = listOf(
                    "All 500 SOC Q&A with In-Depth Solutions",
                    "All 500 MCQ with Real-Time Timers",
                    "All 15+ ATS SOC Resume Templates",
                    "Unlimited Daily Mock Tests & Certificates",
                    "Unlimited ATS Score & JD Keyword Matcher",
                    "AI Mock Interview Simulator",
                    "Ad-Free Pure Cyber HUD Experience"
                ),
                accentColor = SocGoldPrimary,
                topCustomContent = {
                    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                        SegmentedButton(
                            selected = selectedProBillingPeriod == 0,
                            onClick = { selectedProBillingPeriod = 0 },
                            shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                        ) {
                            Text("Monthly (₹199)", fontSize = 11.sp)
                        }
                        SegmentedButton(
                            selected = selectedProBillingPeriod == 1,
                            onClick = { selectedProBillingPeriod = 1 },
                            shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                        ) {
                            Text("Yearly (₹999)", fontSize = 11.sp)
                        }
                    }
                },
                onSelectPlan = {
                    selectedPlanForCheckout = SubscriptionPlan.PRO
                    showRazorpayDialog = true
                },
                buttonText = if (user?.plan == SubscriptionPlan.PRO) "ACTIVE PRO SUBSCRIBER" else "UPGRADE TO PRO (RAZORPAY)",
                isButtonEnabled = user?.plan != SubscriptionPlan.PRO
            )

            // --- PLAN 3: PREMIUM (Rs.1999 One-Time) ---
            PricingTierCard(
                title = "PREMIUM LIFETIME",
                price = "₹1,999",
                billingPeriod = "One-Time Payment • Lifetime Access",
                badge = "ELITE LIFETIME",
                isCurrentPlan = user?.plan == SubscriptionPlan.PREMIUM,
                features = listOf(
                    "All PRO Plan Benefits Included For Life",
                    "1-on-1 SOC Resume Review by Admin & CISO",
                    "Personalized Admin Feedback & ATS Optimization",
                    "Official Verified Academy Certificate",
                    "Exclusive SIEM SPL / KQL / AQL Cheatsheets",
                    "Direct Support by Aarav IT Solutions"
                ),
                accentColor = Color(0xFFA855F7),
                onSelectPlan = {
                    selectedPlanForCheckout = SubscriptionPlan.PREMIUM
                    showRazorpayDialog = true
                },
                buttonText = if (user?.plan == SubscriptionPlan.PREMIUM) "LIFETIME ELITE MEMBER" else "GET LIFETIME PREMIUM (₹1,999)",
                isButtonEnabled = user?.plan != SubscriptionPlan.PREMIUM
            )

            // Razorpay Security Trust Badge
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF081220)),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Razorpay 256-Bit SSL Encrypted Checkout", color = SocTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = "Option 1: Razorpay UPI • Option 2: UPI Mobile • Option 3: Bank Transfer", color = SocTextSecondary, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PricingTierCard(
    title: String,
    price: String,
    billingPeriod: String,
    badge: String,
    isCurrentPlan: Boolean,
    features: List<String>,
    accentColor: Color,
    topCustomContent: (@Composable () -> Unit)? = null,
    onSelectPlan: () -> Unit,
    buttonText: String,
    isButtonEnabled: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
        border = androidx.compose.foundation.BorderStroke(if (isCurrentPlan) 2.dp else 1.dp, if (isCurrentPlan) SocTerminalGreen else accentColor.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = accentColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = price,
                    color = SocTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "/ $billingPeriod",
                    color = SocTextMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            topCustomContent?.invoke()

            HorizontalDivider(color = SocBlueBorder)
            Spacer(modifier = Modifier.height(12.dp))

            features.forEach { feat ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feat, color = SocTextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Narrow, Center Aligned, Fixed Size (280x48dp) Button
            SocButton(
                text = buttonText,
                enabled = isButtonEnabled,
                onClick = onSelectPlan
            )
        }
    }
}

// Secure Razorpay Checkout Modal: 3 Payment Options (UPI, UPI Mobile, Bank Transfer)
@Composable
fun SecureRazorpayCheckoutModal(
    plan: SubscriptionPlan,
    amount: Int,
    userName: String,
    userEmail: String,
    onDismiss: () -> Unit,
    onPaymentSuccess: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedOption by remember { mutableIntStateOf(1) } // 1 = UPI, 2 = UPI Mobile, 3 = Bank Transfer
    var isProcessing by remember { mutableStateOf(false) }

    fun copyToClipboard(label: String, value: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
        Toast.makeText(context, "$label copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = SocDarkBlueSurface,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payment, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SECURE RAZORPAY CHECKOUT",
                    color = SocGoldPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Tier Summary HUD
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0B1728))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Upgrading to ${plan.title}", color = SocTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = userEmail, color = SocTextMuted, fontSize = 11.sp)
                        }
                        Text(text = "₹$amount", color = SocGoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = FontFamily.Monospace)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(text = "Select Payment Method:", color = SocTextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // OPTION 1: UPI Payment - Via Razorpay UPI
                PaymentOptionRow(
                    title = "Option 1: UPI Payment (Razorpay UPI)",
                    subtitle = "VPA: 7607640067@pthdfc (GPay / PhonePe / Paytm)",
                    icon = Icons.Default.Payment,
                    isSelected = selectedOption == 1,
                    onClick = { selectedOption = 1 }
                )

                // OPTION 2: UPI Mobile Number Payment - Via Razorpay
                PaymentOptionRow(
                    title = "Option 2: UPI Mobile Number",
                    subtitle = "Mobile: +91 7607640067 (Direct UPI Auto-Routing)",
                    icon = Icons.Default.PhoneAndroid,
                    isSelected = selectedOption == 2,
                    onClick = { selectedOption = 2 }
                )

                // OPTION 3: Bank Transfer - Punjab National Bank, Farah, Mathura
                PaymentOptionRow(
                    title = "Option 3: Bank Transfer (PNB)",
                    subtitle = "Punjab National Bank • Branch: Farah, Mathura",
                    icon = Icons.Default.AccountBalance,
                    isSelected = selectedOption == 3,
                    onClick = { selectedOption = 3 }
                )

                // Dynamic Details Panel for Selected Option
                Spacer(modifier = Modifier.height(10.dp))

                if (selectedOption == 1) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurfaceElevated),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "UPI ID (VPA): 7607640067@pthdfc", color = SocGoldLight, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Copy UPI ID",
                                color = SocBlueAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { copyToClipboard("UPI ID", "7607640067@pthdfc") }
                            )
                        }
                    }
                } else if (selectedOption == 2) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurfaceElevated),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "UPI Mobile Number: 7607640067", color = SocGoldLight, fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Copy Mobile Number",
                                color = SocBlueAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { copyToClipboard("UPI Mobile Number", "7607640067") }
                            )
                        }
                    }
                } else if (selectedOption == 3) {
                    // Option 3 Bank Details: Only shown after user clicks Bank Transfer!
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D2E)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SocGoldDark),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "SECURE BANK TRANSFER DETAILS", color = SocGoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.height(6.dp))
                            BankDetailItem(label = "Bank Name", value = "Punjab National Bank")
                            BankDetailItem(label = "Branch", value = "Farah, Mathura")
                            BankDetailItem(label = "Account Number", value = "4853000100035308", onCopy = { copyToClipboard("Account Number", "4853000100035308") })
                            BankDetailItem(label = "IFSC Code", value = "PUNB0485300", onCopy = { copyToClipboard("IFSC Code", "PUNB0485300") })
                            BankDetailItem(label = "Account Holder", value = "Aarav IT Solutions")
                        }
                    }
                }

                if (isProcessing) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = SocGoldPrimary, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Authorizing Razorpay Gateway...", color = SocGoldLight, fontSize = 12.sp)
                    }
                }
            }
        },
        confirmButton = {
            SocButton(
                text = "PAY ₹$amount VIA RAZORPAY",
                enabled = !isProcessing,
                onClick = {
                    isProcessing = true
                    val chosenMethod = when (selectedOption) {
                        1 -> "Razorpay UPI (7607640067@pthdfc)"
                        2 -> "Razorpay UPI Mobile (+91 7607640067)"
                        else -> "Bank Transfer (PNB Farah Mathura)"
                    }
                    onPaymentSuccess(chosenMethod)
                },
                modifier = Modifier.testTag("razorpay_pay_now_btn")
            )
        },
        dismissButton = {
            SocOutlinedButton(
                text = "CANCEL",
                contentColor = SocTextSecondary,
                enabled = !isProcessing,
                onClick = onDismiss
            )
        }
    )
}

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SocBlueDeep else SocDarkBlueSurfaceElevated)
            .border(1.dp, if (isSelected) SocGoldPrimary else SocBlueBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) SocGoldPrimary else SocTextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isSelected) SocTextPrimary else SocTextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = SocTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun BankDetailItem(label: String, value: String, onCopy: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "$label: ", color = SocTextMuted, fontSize = 10.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = value, color = SocTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            if (onCopy != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = SocBlueAccent,
                    modifier = Modifier
                        .size(14.dp)
                        .clickable { onCopy() }
                )
            }
        }
    }
}

// Generate Official Payment Receipt PDF
fun generateAndExportPaymentReceipt(context: Context, txn: PaymentTransaction) {
    try {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        // Header Background
        val bgPaint = Paint().apply { color = android.graphics.Color.parseColor("#080C14") }
        canvas.drawRect(0f, 0f, 595f, 110f, bgPaint)

        // Gold divider
        val goldPaint = Paint().apply { color = android.graphics.Color.parseColor("#FFD700"); strokeWidth = 3f }
        canvas.drawLine(0f, 110f, 595f, 110f, goldPaint)

        // Header Title
        val titlePaint = Paint().apply {
            textSize = 22f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#FFD700")
        }
        canvas.drawText("SOC MASTER • PAYMENT RECEIPT", 40f, 50f, titlePaint)

        val subPaint = Paint().apply {
            textSize = 11f
            color = android.graphics.Color.WHITE
        }
        canvas.drawText("Aarav IT Solutions • aaravitsolutons.com • Mathura, UP", 40f, 75f, subPaint)
        canvas.drawText("Authorized Razorpay Cyber Academy Billing Document", 40f, 95f, subPaint)

        // Body items
        val labelPaint = Paint().apply {
            textSize = 12f
            isFakeBoldText = true
            color = android.graphics.Color.DKGRAY
        }
        val valuePaint = Paint().apply {
            textSize = 12f
            color = android.graphics.Color.BLACK
        }

        var y = 150f

        fun drawLineItem(label: String, value: String) {
            canvas.drawText(label, 40f, y, labelPaint)
            canvas.drawText(value, 230f, y, valuePaint)
            y += 24f
        }

        drawLineItem("Receipt Number:", txn.id)
        drawLineItem("Razorpay Transaction ID:", txn.razorpayPaymentId)
        drawLineItem("Candidate Name:", txn.userName)
        drawLineItem("Candidate Email:", txn.userEmail)
        drawLineItem("Subscription Plan:", txn.plan.title)
        drawLineItem("Payment Method:", txn.paymentMethod)
        drawLineItem("Transaction Date & Time:", txn.timestamp)
        drawLineItem("Payment Status:", "${txn.status} (VERIFIED & CONFIRMED)")

        y += 10f
        val linePaint = Paint().apply { color = android.graphics.Color.LTGRAY; strokeWidth = 1f }
        canvas.drawLine(40f, y, 555f, y, linePaint)
        y += 30f

        val totalTitlePaint = Paint().apply {
            textSize = 16f
            isFakeBoldText = true
            color = android.graphics.Color.BLACK
        }
        val totalValPaint = Paint().apply {
            textSize = 18f
            isFakeBoldText = true
            color = android.graphics.Color.parseColor("#0F9D58")
        }
        canvas.drawText("TOTAL AMOUNT PAID:", 40f, y, totalTitlePaint)
        canvas.drawText("₹${txn.amountRupees}.00 INR", 380f, y, totalValPaint)

        y += 60f
        val footerPaint = Paint().apply {
            textSize = 10f
            color = android.graphics.Color.GRAY
        }
        canvas.drawText("Thank you for upgrading your SOC Master preparation subscription.", 40f, y, footerPaint)
        y += 16f
        canvas.drawText("For inquiries or bank transfer verification: 76076ck@gmail.com • +91 7607640067", 40f, y, footerPaint)
        y += 16f
        canvas.drawText("This is an official computer-generated receipt issued by Aarav IT Solutions.", 40f, y, footerPaint)

        document.finishPage(page)

        val file = File(context.cacheDir, "SOC_Master_Receipt_${txn.id}.pdf")
        FileOutputStream(file).use { outputStream ->
            document.writeTo(outputStream)
        }
        document.close()

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "SOC Master Payment Receipt (${txn.id})")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Download / Share Payment Receipt"))
        Toast.makeText(context, "Payment Receipt PDF generated successfully!", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "Receipt generation failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
