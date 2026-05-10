package dev.bti.kdym.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun CreateHomePostScreen(
    onNavigateBack: () -> Unit,
    adminViewModel: AdminViewModel
) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("Campers") }
    var postType by remember { mutableStateOf("Normal") }
    
    var linkTitle by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CREATE",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                    Text(
                        text = "HOME POST",
                        color = TextSecondary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = RubikFontFamily
                    )
                }
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "This is the unified announcement/home update composer.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(24.dp))

                CommandInputField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = "Title",
                    icon = Icons.Default.Campaign
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandInputField(
                    value = body,
                    onValueChange = { body = it },
                    placeholder = "Post body",
                    icon = Icons.Default.Notes
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Audience/Type Pickers (Mocked for UI)
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = Color(0xFF22D3EE))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "AUDIENCE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text(text = audience, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = Color.White)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(0.1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF22D3EE))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "TYPE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text(text = postType, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "OPTIONAL",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = RubikFontFamily
                )
                Text(
                    text = "ATTACH LINK",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                CommandInputField(
                    value = linkTitle,
                    onValueChange = { linkTitle = it },
                    placeholder = "Link title",
                    icon = Icons.Default.Link
                )
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(
                    value = url,
                    onValueChange = { url = it },
                    placeholder = "URL",
                    icon = Icons.Default.Language
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { /* TODO: Post logic */ onNavigateBack() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(28.dp),
                    enabled = title.isNotBlank() && body.isNotBlank()
                ) {
                    Text(text = "CREATE POST", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
