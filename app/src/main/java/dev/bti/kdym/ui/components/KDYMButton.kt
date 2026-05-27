package dev.bti.kdym.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.CardBackground
import dev.bti.kdym.ui.theme.CardBorder
import dev.bti.kdym.ui.theme.QuickSandFontFamily

/**
 * Visual styles for the [KDYMButton].
 */
enum class ButtonVariant {
    /** High-contrast primary action (Black text on White background). */
    Primary,
    /** Outlined secondary action (White text on Translucent background). */
    Secondary
}

/**
 * Standard branded button used across the application.
 *
 * @param text The label displayed on the button (auto-uppercased).
 * @param onClick Callback triggered when the button is pressed.
 * @param modifier Custom modifier for layout.
 * @param variant Visual style of the button (Primary or Secondary).
 * @param icon Optional trailing icon composable.
 */
@Composable
fun KDYMButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: ButtonVariant = ButtonVariant.Primary,
    icon: (@Composable () -> Unit)? = null
) {
    val containerColor = if (variant == ButtonVariant.Primary) Color.White else CardBackground
    val contentColor = if (variant == ButtonVariant.Primary) Color.Black else Color.White
    val border = if (variant == ButtonVariant.Secondary) BorderStroke(1.dp, CardBorder) else null

    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = border,
        contentPadding = PaddingValues(horizontal = 24.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text.uppercase(),
                fontFamily = QuickSandFontFamily,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 2.sp
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                icon()
            }
        }
    }
}
