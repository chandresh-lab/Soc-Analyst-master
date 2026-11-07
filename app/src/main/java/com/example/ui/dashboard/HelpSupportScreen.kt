package com.example.ui.dashboard

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SocBlueAccent
import com.example.ui.theme.SocBlueBorder
import com.example.ui.theme.SocBlueDeep
import com.example.ui.theme.SocDarkBlueBg
import com.example.ui.theme.SocDarkBlueSurface
import com.example.ui.theme.SocDarkBlueSurfaceElevated
import com.example.ui.theme.SocGoldLight
import com.example.ui.theme.SocGoldPrimary
import com.example.ui.theme.SocTextMuted
import com.example.ui.theme.SocTextPrimary
import com.example.ui.theme.SocTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    fun openWebUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "HELP, DISCLAIMER & SUPPORT",
                            color = SocGoldPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Aarav IT Solutions • aaravitsolutons.com",
                            color = SocTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("help_support_back_btn")
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
            // Organization Header
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocGoldPrimary.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SocDarkBlueSurfaceElevated)
                            .border(1.5.dp, SocGoldPrimary, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.soc_gold_shield),
                            contentDescription = "Aarav IT Solutions Logo",
                            modifier = Modifier.size(50.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "AARAV IT SOLUTIONS",
                        color = SocGoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "Cyber Defense Academy & Security Operations Engineering",
                        color = SocTextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Fixed-Size 280x48dp Center Aligned SOC Button
                    SocButton(
                        text = "VISIT AARAVITSOLUTONS.COM",
                        icon = Icons.Default.Language,
                        onClick = { openWebUrl("https://aaravitsolutons.com") },
                        modifier = Modifier.testTag("visit_website_btn")
                    )
                }
            }

            // Direct Contact & Support
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "OFFICIAL CANDIDATE SUPPORT",
                        color = SocGoldLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@aaravitsolutons.com"))
                                context.startActivity(Intent.createChooser(emailIntent, "Send Email"))
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "Official Support Desk", color = SocTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "support@aaravitsolutons.com", color = SocBlueAccent, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = SocBlueBorder)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val phoneIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+919876543210"))
                                context.startActivity(phoneIntent)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text = "SOC Career Helpline", color = SocTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = "+91 98765 43210 (Mon-Sat, 9 AM - 7 PM IST)", color = SocTextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }

            // Legal & Educational Disclaimer
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Policy, contentDescription = null, tint = SocGoldPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Educational Disclaimer", color = SocTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SOC Master is engineered strictly for educational, defensive interview preparation, and skill certification. All SIEM queries (Splunk SPL, KQL, AQL), network packet filters, and incident response playbooks provided herein are designed to enhance defensive enterprise security monitoring. Unauthorized testing or offensive execution against unapproved networks without explicit authorization is strictly prohibited.",
                        color = SocTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Cybersecurity Ethics Pledge
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SocDarkBlueSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, SocBlueBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = SocBlueAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Blue Team Ethics & Integrity", color = SocTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "As certified SOC defenders, candidates pledge to safeguard organizational assets, maintain strict confidentiality of confidential triage records, adhere to standard operational SLAs, and uphold ethical cybersecurity practices globally.",
                        color = SocTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            // Links to Privacy Policy & Terms on aaravitsolutons.com (Fixed 280x48dp)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocOutlinedButton(
                    text = "PRIVACY POLICY",
                    contentColor = SocTextSecondary,
                    onClick = { openWebUrl("https://aaravitsolutons.com/privacy-policy") }
                )
                SocOutlinedButton(
                    text = "TERMS OF SERVICE",
                    contentColor = SocTextSecondary,
                    onClick = { openWebUrl("https://aaravitsolutons.com/terms") }
                )
            }
        }
    }
}
