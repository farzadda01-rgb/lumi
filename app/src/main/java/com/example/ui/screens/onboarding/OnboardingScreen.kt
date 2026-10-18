package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.common.DataStoreManager
import com.example.core.designsystem.LumiButton
import com.example.core.designsystem.LumiOutlinedButton
import com.example.ui.theme.LumiButtonShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiHeroCardShape
import com.example.ui.theme.LumiLavender
import com.example.ui.theme.LumiLavenderLight
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiWarmIvory
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val headline: String,
    val supportingText: String,
    val drawableRes: Int
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    dataStoreManager: DataStoreManager,
    onFinishOnboarding: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pages = listOf(
        OnboardingPageData(
            headline = "Make time for yourself.",
            supportingText = "Discover services and experiences designed around you.",
            drawableRes = R.drawable.lumi_hero_banner
        ),
        OnboardingPageData(
            headline = "Find your perfect match.",
            supportingText = "Explore services, compare providers, and choose what feels right.",
            drawableRes = R.drawable.lumi_service_hair
        ),
        OnboardingPageData(
            headline = "Your next appointment, simplified.",
            supportingText = "Choose a time that works for you and keep every booking organized.",
            drawableRes = R.drawable.lumi_service_spa
        )
    )

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pages.size })

    fun completeOnboarding() {
        scope.launch {
            dataStoreManager.setOnboardingCompleted(true)
            onFinishOnboarding()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .padding(vertical = 16.dp)
            .testTag("onboarding_screen")
    ) {
        // Top row with Skip action
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LUMI",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = LumiPlum
            )

            if (pagerState.currentPage < pages.size - 1) {
                TextButton(
                    onClick = { completeOnboarding() },
                    modifier = Modifier.testTag("onboarding_skip_button")
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge,
                        color = LumiMutedGray
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        // Pager Content
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(LumiHeroCardShape)
                ) {
                    Image(
                        painter = painterResource(id = page.drawableRes),
                        contentDescription = page.headline,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = page.headline,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = LumiCharcoal,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = page.supportingText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = LumiMutedGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // Indicators & Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Page Dots
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (isSelected) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) LumiPlum else LumiLavenderLight)
                    )
                }
            }

            // Controls
            if (pagerState.currentPage == pages.size - 1) {
                LumiButton(
                    text = "Get started",
                    onClick = { completeOnboarding() },
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "onboarding_get_started_button"
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (pagerState.currentPage > 0) {
                        LumiOutlinedButton(
                            text = "Back",
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            testTag = "onboarding_back_button"
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    LumiButton(
                        text = "Next",
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "onboarding_next_button"
                    )
                }
            }
        }
    }
}
