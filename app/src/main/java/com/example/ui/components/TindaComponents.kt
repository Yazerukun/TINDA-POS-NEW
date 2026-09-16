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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

/**
 * Secondary outlined button with crisp border and smooth press scaling.
 */
@Composable
fun TindaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    contentColor: Color = TextPrimary,
    borderColor: Color = BorderElevated,
    enabled: Boolean = true,
    testTag: String? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1.0f,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 600f),
        label = "outlinedButtonScale"
    )

    var btnModifier = modifier
        .scale(scale)
        .height(48.dp)

    if (testTag != null) {
        btnModifier = btnModifier.testTag(testTag)
    }

    OutlinedButton(
        onClick = onClick,
        modifier = btnModifier,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isPressed) EmeraldInteractive else borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = contentColor,
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
                    modifier = Modifier.size(18.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

/**
 * Retail styled input field with rounded corners, subtle border, focused emerald accent,
 * and high legibility.
 */
@Composable
fun TindaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: androidx.compose.foundation.text.KeyboardOptions = androidx.compose.foundation.text.KeyboardOptions.Default,
    testTag: String? = null
) {
    var fieldModifier = modifier.fillMaxWidth()
    if (testTag != null) {
        fieldModifier = fieldModifier.testTag(testTag)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontSize = 13.sp) },
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(placeholder, color = TextMuted, fontSize = 14.sp)
                }
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            isError = isError,
            keyboardOptions = keyboardOptions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BrandSurfaceElevated,
                unfocusedContainerColor = BrandSurfaceElevated,
                disabledContainerColor = BrandSurfaceSoft,
                focusedBorderColor = EmeraldInteractive,
                unfocusedBorderColor = BorderSubtle,
                errorBorderColor = DangerSoftRed,
                focusedLabelColor = EmeraldInteractive,
                unfocusedLabelColor = TextSecondary,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = EmeraldInteractive
            ),
            modifier = fieldModifier
        )
        if (isError && errorMessage != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = errorMessage,
                color = DangerSoftRed,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
        }
    }
}

/**
 * Modern Retail Search Field with leading search icon and instant clear button.
 */
@Composable
fun TindaSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search...",
    modifier: Modifier = Modifier,
    trailingAction: @Composable (() -> Unit)? = null,
    testTag: String? = null
) {
    var fieldModifier = modifier.fillMaxWidth()
    if (testTag != null) {
        fieldModifier = fieldModifier.testTag(testTag)
    }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, color = TextMuted, fontSize = 14.sp) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = { onQueryChange("") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                if (trailingAction != null) {
                    trailingAction()
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BrandSurfaceElevated,
            unfocusedContainerColor = BrandSurfaceElevated,
            focusedBorderColor = EmeraldInteractive,
            unfocusedBorderColor = BorderSubtle,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            cursorColor = EmeraldInteractive
        ),
        modifier = fieldModifier
    )
}

/**
 * Metric/KPI Card for executive summary cards across Dashboard, Inventory, and Reports.
 */
@Composable
fun TindaMetricCard(
    title: String,
    value: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconTint: Color = EmeraldInteractive,
    valueColor: Color = TextPrimary,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    testTag: String? = null
) {
    TindaCard(
        modifier = modifier,
        onClick = onClick,
        testTag = testTag
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(iconTint.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * 48dp Touch-target Quantity Stepper with smooth tactile feedback.
 */
@Composable
fun TindaQuantityStepper(
    quantity: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    modifier: Modifier = Modifier,
    minQuantity: Int = 1,
    maxQuantity: Int = 9999
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = BrandSurfaceSoft,
        border = BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(
                onClick = onDecrease,
                enabled = quantity > minQuantity,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease",
                    tint = if (quantity > minQuantity) TextPrimary else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(
                text = "$quantity",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(
                onClick = onIncrease,
                enabled = quantity < maxQuantity,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase",
                    tint = if (quantity < maxQuantity) EmeraldInteractive else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
