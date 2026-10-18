package com.example.ui.screens.about

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.core.designsystem.LumiDemoNotice
import com.example.core.designsystem.LumiHeader
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiLavender
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmIvory

@Composable
fun AboutLumiScreen(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LumiWarmIvory)
            .testTag("about_lumi_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LumiHeader(
                title = "About LUMI",
                subtitle = "Brand philosophy & craftsmanship",
                onBack = onBack
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Brand Emblem Header
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = LumiLavenderSubtle,
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.lumi_app_icon),
                                contentDescription = "LUMI Emblem",
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "L U M I",
                            style = MaterialTheme.typography.displayMedium.copy(
                                letterSpacing = 6.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = LumiPlum
                        )

                        Text(
                            text = "Your time. Your ritual.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = LumiLavender
                        )

                        Text(
                            text = "Version 1.0.0 (Portfolio Edition)",
                            style = MaterialTheme.typography.labelSmall,
                            color = LumiMutedGray
                        )
                    }
                }

                // Brand Story
                item {
                    Card(
                        shape = LumiCardShape,
                        colors = CardDefaults.cardColors(containerColor = LumiSurfaceWhite),
                        border = BorderStroke(1.dp, LumiBorderDivider),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "The Philosophy",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "LUMI was conceived around a fundamental belief: modern self-care should feel effortless, intentional, and genuinely restorative. From bespoke hair architecture to calming botanical touch, every service in LUMI is curated as an experience rather than a routine transaction.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LumiCharcoal.copy(alpha = 0.85f),
                                lineHeight = 24.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = LumiBorderDivider)
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "Digital Craftsmanship",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = LumiCharcoal
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Engineered with 100% native Kotlin, Jetpack Compose Material 3, Room local database persistence, and a custom editorial design system. Built with calm micro-interactions and strict accessibility standards.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = LumiCharcoal.copy(alpha = 0.85f),
                                lineHeight = 24.sp
                            )
                        }
                    }
                }

                // Demo Architecture Notice
                item {
                    LumiDemoNotice(
                        text = "Demonstration Portfolio Project • Offline Architecture • No Cloud Dependencies"
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
