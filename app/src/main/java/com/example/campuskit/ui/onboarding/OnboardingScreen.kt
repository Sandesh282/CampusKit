package com.example.campuskit.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.campuskit.ui.theme.InterFontFamily
import com.example.campuskit.ui.theme.OnboardingAccent
import com.example.campuskit.ui.theme.OnboardingBackground
import com.example.campuskit.ui.theme.OnboardingTextSecondary
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val currentPage = pagerState.currentPage

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OnboardingBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        // Progress indicator (thin bars)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 80.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            repeat(onboardingPages.size) { index ->
                val isActive = index <= currentPage
                val targetWidth by animateDpAsState(
                    targetValue = if (isActive) 1f.dp else 1f.dp,
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                    label = "barWidth",
                )
                val barColor by animateColorAsState(
                    targetValue = if (isActive) OnboardingAccent else OnboardingTextSecondary.copy(alpha = 0.25f),
                    animationSpec = tween(300),
                    label = "barColor",
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(barColor),
                )
            }
        }

        // Pager content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
        ) { page ->
            when (page) {
                0 -> WelcomePage()
                1 -> DailyPage()
                2 -> HabitsPage()
                3 -> CampusControlPage()
            }
        }

        // CTA button
        val ctaLabel = onboardingPages[currentPage].ctaLabel
        Button(
            onClick = {
                if (currentPage < onboardingPages.size - 1) {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            currentPage + 1,
                            animationSpec = tween(400, easing = FastOutSlowInEasing),
                        )
                    }
                } else {
                    onComplete()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(bottom = 16.dp)
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OnboardingAccent,
                contentColor = OnboardingBackground,
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
        ) {
            Text(
                text = ctaLabel,
                fontFamily = InterFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
            )
        }

        // Secondary link on screen 1
        if (currentPage == 0) {
            Text(
                text = "Already have an account? Log in",
                fontFamily = InterFontFamily,
                fontSize = 13.sp,
                color = OnboardingTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
