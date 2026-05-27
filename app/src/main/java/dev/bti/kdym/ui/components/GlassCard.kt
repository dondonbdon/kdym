package dev.bti.kdym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.bti.kdym.ui.theme.CardBackground
import dev.bti.kdym.ui.theme.CardBorder

/**
 * A reusable card component with a "glassmorphism" aesthetic.
 * Features rounded corners, a subtle border, and a translucent background.
 *
 * @param modifier Custom modifier for the card's layout.
 * @param cornerRadius The radius of the card's corners.
 * @param borderWidth Thickness of the card's outer stroke.
 * @param borderColor Color of the card's outer stroke.
 * @param backgroundColor Translucent color of the card's fill.
 * @param contentPadding Internal spacing between the border and the content.
 * @param content Composable lambda for the card's body.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 32.dp,
    borderWidth: Dp = 1.dp,
    borderColor: Color = CardBorder,
    backgroundColor: Color = CardBackground,
    contentPadding: Dp = 16.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(contentPadding)
    ) {
        content()
    }
}
