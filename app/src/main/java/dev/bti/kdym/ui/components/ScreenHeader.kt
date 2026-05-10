package dev.bti.kdym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun ScreenHeader(
    modifier: Modifier = Modifier,
    isCampMode: Boolean = false,
    title: String? = null,
    subtitle: String? = null,
    icon: ImageVector? = null,
    iconColor: Color = Color(0xFFEF4444),
    onNavigateBack: (() -> Unit)? = null,
    isPrimary: Boolean = false,
    titleSize: androidx.compose.ui.unit.TextUnit = 32.sp
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (isPrimary) {
                HomeTopBar(isCampMode = isCampMode)
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                    
                    if (icon != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(iconColor.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = iconColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                if (title != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = titleSize,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily,
                        lineHeight = titleSize
                    )
                }

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = TextSecondary,
                        fontSize = 18.sp,
                        fontFamily = RubikFontFamily
                    )
                }
            }
        }
    }
}
