package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BorderElevated
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.BrandSurfaceElevated
import com.example.ui.theme.BrandSurfacePrimary
import com.example.ui.theme.BrandSurfaceSoft
import com.example.ui.theme.DangerSoftRed
import com.example.ui.theme.EmeraldInteractive
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SuccessEmerald
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import java.util.Locale

enum class ExpirationRisk {
    EXPIRED,
    EXPIRING_SOON,
    NEED_DATE_REVIEW,
    SAFE
}

/**
 * Clean, consistent modern retail card with subtle border and soft layered surface.
 * Features smooth micro-interaction press scaling when clickable.
 */
@Composable
fun TindaCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = BrandSurfaceElevated,
    borderColor: Color = BorderSubtle,
    cornerRadius: Dp = 16.dp,
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && onClick != null) 0.97f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 600f),
        label = "cardScale"
    )

    var cardModifier = modifier
        .scale(scale)
        .clip(RoundedCornerShape(cornerRadius))
        .background(backgroundColor)

    if (testTag != null) {
        cardModifier = cardModifier.testTag(testTag)
    }

    if (onClick != null) {
        cardModifier = cardModifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    }

    Surface(
        modifier = cardModifier,
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
        border = BorderStroke(1.dp, if (isPressed) EmeraldInteractive.copy(alpha = 0.5f) else borderColor),
        tonalElevation = 2.dp
    ) {
        content()
    }
}

/**
 * Primary CTA Button with subtle scale down micro-interaction, 48dp touch ergonomics,
 * and high-contrast styling.
 */
@Composable
fun TindaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    containerColor: Color = EmeraldPrimary,
    contentColor: Color = Color.White,
    enabled: Boolean = true,
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 600f),
        label = "buttonScale"
    )

    var btnModifier = modifier
        .scale(scale)
        .height(48.dp)

    if (testTag != null) {
        btnModifier = btnModifier.testTag(testTag)
    }

    Button(
        onClick = onClick,
        modifier = btnModifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = BrandSurfaceSoft,
            disabledContentColor = TextMuted
        ),
        interactionSource = interactionSource
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }
    }
}

/**
 * Formatted currency display with tabular digits and clear financial hierarchy.
 */
@Composable
fun MoneyText(
    amount: Double,
    modifier: Modifier = Modifier,
    currencySymbol: String = "₱",
    fontSize: TextUnit = 24.sp,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = TextPrimary
) {
    Text(
        text = "$currencySymbol${String.format(Locale.US, "%,.2f", amount)}",
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        fontFamily = FontFamily.Default, // Clean standard tabular rendering
        maxLines = 1
    )
}

/**
 * Badges for retail inventory status (In Stock, Low Stock, Expired, Expiring Soon, Needs Review).
 */
@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

/**
 * Helper to produce the appropriate stock badge based on stock quantity and minimum threshold.
 */
@Composable
fun ProductStockBadge(
    stockQuantity: Int,
    minStockAlert: Int = 5,
    unit: String = "pc",
    modifier: Modifier = Modifier
) {
    when {
        stockQuantity <= 0 -> {
            StatusBadge(
                text = "Out of Stock",
                containerColor = DangerSoftRed.copy(alpha = 0.15f),
                contentColor = DangerSoftRed,
                icon = Icons.Default.ErrorOutline,
                modifier = modifier
            )
        }
        stockQuantity <= minStockAlert -> {
            StatusBadge(
                text = "Low: $stockQuantity $unit",
                containerColor = WarningAmber.copy(alpha = 0.15f),
                contentColor = WarningAmber,
                icon = Icons.Default.Warning,
                modifier = modifier
            )
        }
        else -> {
            StatusBadge(
                text = "$stockQuantity $unit",
                containerColor = SuccessEmerald.copy(alpha = 0.15f),
                contentColor = SuccessEmerald,
                icon = Icons.Default.CheckCircle,
                modifier = modifier
            )
        }
    }
}

/**
 * Helper for Expiration status badge.
 */
@Composable
fun ExpirationBadge(risk: ExpirationRisk, daysRemaining: Long?, modifier: Modifier = Modifier) {
    when (risk) {
        ExpirationRisk.EXPIRED -> {
            StatusBadge(
                text = "Expired",
                containerColor = DangerSoftRed.copy(alpha = 0.2f),
                contentColor = DangerSoftRed,
                icon = Icons.Default.ErrorOutline,
                modifier = modifier
            )
        }
        ExpirationRisk.EXPIRING_SOON -> {
            val label = if (daysRemaining != null) "$daysRemaining d left" else "Expiring Soon"
            StatusBadge(
                text = label,
                containerColor = WarningAmber.copy(alpha = 0.2f),
                contentColor = WarningAmber,
                icon = Icons.Default.Schedule,
                modifier = modifier
            )
        }
        ExpirationRisk.NEED_DATE_REVIEW -> {
            StatusBadge(
                text = "Review Date",
                containerColor = BrandSurfaceSoft,
                contentColor = TextMuted,
                icon = Icons.AutoMirrored.Filled.HelpOutline,
                modifier = modifier
            )
        }
        ExpirationRisk.SAFE -> {
            // No badge needed for safe items to keep UI clean and calm
        }
    }
}

/**
 * Consistent empty state display without giant overwhelming graphics.
 */
@Composable
fun TindaEmptyState(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BrandSurfaceSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(30.dp),
                    tint = TextMuted
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                fontSize = 13.sp
            )
            if (actionButtonText != null && onActionClick != null) {
                Spacer(modifier = Modifier.height(18.dp))
                TindaButton(
                    text = actionButtonText,
                    onClick = onActionClick,
                    containerColor = EmeraldPrimary
                )
            }
        }
    }
}
