package com.example.ui.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.SocBlueAccent
import com.example.ui.theme.SocDarkBlueBg
import com.example.ui.theme.SocDarkBlueSurface
import com.example.ui.theme.SocGoldLight
import com.example.ui.theme.SocGoldPrimary
import com.example.ui.theme.SocTextMuted
import com.example.ui.theme.SocTextPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shield_scale"
    )

    LaunchedEffect(Unit) {
        delay(2200)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF040711),
                        SocDarkBlueBg,
                        Color(0xFF0D1B2A)
                    )
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Gold Shield Logo
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(170.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .border(2.dp, Brush.linearGradient(listOf(SocGoldPrimary, SocBlueAccent)), CircleShape)
                    .background(SocDarkBlueSurface)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.soc_gold_shield),
                    contentDescription = "Gold Shield Logo - SOC Master",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(160.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // SOC MASTER Text
            Text(
                text = "SOC MASTER",
                color = SocGoldPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.testTag("splash_title")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tagline: "Security Operations Center Preparation"
            Text(
                text = "Security Operations Center Preparation",
                color = SocTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Triage • Incident Response • Threat Hunting • SIEM",
                color = SocBlueAccent,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(44.dp))

            // Fixed Size 280x48 Center Aligned SOC Button
            com.example.ui.components.SocButton(
                text = "INITIALIZE SOC CONSOLE",
                onClick = onSplashFinished,
                modifier = Modifier.testTag("splash_enter_button")
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Developer & Domain
            Text(
                text = "Developer: Aarav IT Solutions",
                color = SocGoldLight,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "aaravitsolutons.com",
                color = SocTextMuted,
                fontSize = 11.sp
            )
        }
    }
}
