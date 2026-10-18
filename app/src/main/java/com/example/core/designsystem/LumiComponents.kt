package com.example.core.designsystem

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.LumiBorderDivider
import com.example.ui.theme.LumiButtonShape
import com.example.ui.theme.LumiCardShape
import com.example.ui.theme.LumiCharcoal
import com.example.ui.theme.LumiChipShape
import com.example.ui.theme.LumiCoral
import com.example.ui.theme.LumiGold
import com.example.ui.theme.LumiLavender
import com.example.ui.theme.LumiLavenderLight
import com.example.ui.theme.LumiLavenderSubtle
import com.example.ui.theme.LumiMutedGray
import com.example.ui.theme.LumiPillShape
import com.example.ui.theme.LumiPlum
import com.example.ui.theme.LumiPlumDark
import com.example.ui.theme.LumiPlumLight
import com.example.ui.theme.LumiSurfaceWhite
import com.example.ui.theme.LumiWarmGray
import com.example.ui.theme.LumiWarmIvory

@Composable
fun LumiButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "lumi_primary_button",
    leadingIcon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = LumiButtonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = LumiPlum,
            contentColor = LumiSurfaceWhite,
            disabledContainerColor = LumiBorderDivider,
            disabledContentColor = LumiMutedGray
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 52.dp)
            .testTag(testTag)
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) LumiSurfaceWhite else LumiMutedGray
        )
    }
}

@Composable
fun LumiOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    testTag: String = "lumi_outlined_button"
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = LumiButtonShape,
        border = BorderStroke(1.dp, if (enabled) LumiPlum else LumiBorderDivider),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = LumiPlum,
            disabledContentColor = LumiMutedGray
        ),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .testTag(testTag)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (enabled) LumiPlum else LumiMutedGray
        )
    }
}

@Composable
fun LumiFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String = "filter_chip_$label"
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) LumiPlum else LumiWarmGray,
        animationSpec = tween(200),
        label = "chip_bg"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) LumiSurfaceWhite else LumiCharcoal,
        animationSpec = tween(200),
        label = "chip_text"
    )

    Surface(
        shape = LumiChipShape,
        color = backgroundColor,
        border = if (isSelected) null else BorderStroke(1.dp, LumiBorderDivider),
        modifier = modifier
            .clip(LumiChipShape)
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                ),
                color = contentColor
            )
        }
    }
}

@Composable
fun LumiFavoriteButton(
    isFavorite: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    testTag: String = "favorite_toggle_button"
) {
    Surface(
        shape = CircleShape,
        color = LumiSurfaceWhite.copy(alpha = 0.92f),
        shadowElevation = 2.dp,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onToggle)
            .testTag(testTag)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                tint = if (isFavorite) LumiCoral else LumiMutedGray,
                modifier = Modifier.size(size * 0.55f)
            )
        }
    }
}

@Composable
fun LumiRatingBadge(
    rating: Float,
    reviewCount: Int? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(LumiWarmIvory, LumiPillShape)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = LumiGold,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = LumiCharcoal
        )
        if (reviewCount != null) {
            Text(
                text = " ($reviewCount)",
                style = MaterialTheme.typography.labelSmall,
                color = LumiMutedGray
            )
        }
    }
}

@Composable
fun LumiDemoNotice(
    modifier: Modifier = Modifier,
    text: String = "Demonstration Mode • Stored locally on device"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(LumiLavenderSubtle, LumiPillShape)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            tint = LumiPlumLight,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
            color = LumiPlumDark
        )
    }
}

@Composable
fun LumiAvatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    imageResName: String? = null
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "L" }

    Surface(
        shape = CircleShape,
        color = LumiLavenderSubtle,
        border = BorderStroke(1.5.dp, LumiLavenderLight),
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = initials,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.38f).sp
                ),
                color = LumiPlum
            )
        }
    }
}

@Composable
fun LumiEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Info,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    testTag: String = "empty_state"
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = LumiLavenderSubtle,
            modifier = Modifier.size(72.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = LumiPlum,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = LumiCharcoal,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = LumiMutedGray,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(20.dp))
            LumiButton(
                text = actionText,
                onClick = onActionClick,
                testTag = "empty_state_action"
            )
        }
    }
}

@Composable
fun LumiHeader(
    title: String,
    subtitle: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (onBack != null) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = LumiCharcoal
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = LumiCharcoal
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = LumiMutedGray
                    )
                }
            }
        }
        if (actions != null) {
            actions()
        }
    }
}

@Composable
fun LumiDrawableImage(
    imageName: String,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val context = LocalContext.current
    val resourceId = remember(imageName) {
        val res = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        if (res != 0) res else R.drawable.lumi_hero_banner
    }

    Image(
        painter = painterResource(id = resourceId),
        contentDescription = contentDescription,
        contentScale = contentScale,
        modifier = modifier
    )
}
