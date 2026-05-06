package dev.bti.kdym.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.R
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun SignUpScreen(
    onSignUp: (String, String, String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(2.dp))
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Image(
                painter = painterResource(id = R.drawable.white_kdym_logo),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "CREATE ACCOUNT",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                fontFamily = RubikFontFamily
            )
            Text(
                text = "Anyone can create an account. Camp access is unlocked separately by leadership.",
                color = TextSecondary,
                fontSize = 16.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontFamily = RubikFontFamily
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            CommandInputField(
                value = name,
                onValueChange = { name = it },
                placeholder = "Full Name",
                icon = Icons.Default.Person
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                icon = Icons.Default.Email
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            CommandInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                icon = Icons.Default.Lock
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { onSignUp(name, email, password) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f), contentColor = Color.White.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(28.dp),
                enabled = name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = R.drawable.ic_profile_filled), contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "CREATE ACCOUNT", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Already have an account? Sign in",
                color = Color(0xFF22D3EE),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = RubikFontFamily,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            GlassCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
                cornerRadius = 24.dp,
                backgroundColor = Color(0xFFEF4444).copy(alpha = 0.05f),
                borderColor = Color(0xFFEF4444).copy(alpha = 0.1f)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = RedAccent, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Camp access is separate",
                            color = RedAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = RubikFontFamily
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Creating an account gives access to the general KDYM app. Camp Mode features like tribe groups, camp schedule, and Tribe Wars require approval.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            fontFamily = RubikFontFamily
                        )
                    }
                }
            }
        }
    }
}
