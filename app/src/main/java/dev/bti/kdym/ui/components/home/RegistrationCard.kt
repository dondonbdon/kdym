package dev.bti.kdym.ui.components.home

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import dev.bti.kdym.R
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun RegistrationCard() {
    val context = LocalContext.current
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color.Black.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_group_msg),
                        contentDescription = null,
                        tint = RedAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Registration Open",
                        color = RedAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }
                Text(
                    text = "HEARTLAND",
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Ready for camp?",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Register for Heartland Youth Camp through the official Kansas UPCI form.",
                color = TextSecondary,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontFamily = QuickSandFontFamily
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    context.startActivity(
                        Intent(
                            Intent.ACTION_VIEW,
                            "https://kansasupci.churchtrac.com/connect?ei=ZTZ5JAI".toUri()
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTER FOR CAMP",
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
