package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun ChurchDetailScreen(
    churchId: String,
    onNavigateBack: () -> Unit,
    onEditChurch: (String) -> Unit,
    onManagePastor: (String) -> Unit,
    mainViewModel: MainViewModel
) {
    val churches by mainViewModel.churches.collectAsState()
    val church = remember(churchId, churches) { churches.find { it.id == churchId } }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onNavigateBack,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White.copy(0.1f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "BACK", fontWeight = FontWeight.Black, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
                }

                IconButton(
                    onClick = { onEditChurch(churchId) },
                    modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Church", tint = Color.White)
                }
            }

            if (church == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(0xFF22D3EE).copy(0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = church.name.uppercase(),
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = QuickSandFontFamily
                            )
                            Text(
                                text = "District Church Detail",
                                color = TextSecondary,
                                fontSize = 14.sp,
                                fontFamily = QuickSandFontFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    DetailSection(title = "PASTORAL LEADERSHIP")
                    ChurchDetailCard(
                        icon = Icons.Default.Person,
                        label = "Senior Pastor",
                        value = church.pastorName,
                        actionLabel = "MANAGE"
                    ) { onManagePastor(churchId) }

                    Spacer(modifier = Modifier.height(24.dp))

                    DetailSection(title = "LOCATION")
                    ChurchDetailCard(
                        icon = Icons.Default.LocationOn,
                        label = "Address",
                        value = "${church.address}\n${church.city}, ${church.state}",
                        onAction = null
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    DetailSection(title = "CONTACT & STATUS")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatCard(
                            label = "STATUS",
                            value = "ACTIVE",
                            color = Color(0xFF4ADE80),
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "MEMBERS",
                            value = "0",
                            color = Color(0xFF22D3EE),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                    
                    Button(
                        onClick = { onEditChurch(churchId) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "EDIT CHURCH RECORDS", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                    }
                    
                    Spacer(modifier = Modifier.height(120.dp))
                }
            }
        }
    }
}

@Composable
fun DetailSection(title: String) {
    Text(
        text = title,
        color = Color(0xFFEF4444),
        fontSize = 10.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 2.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun ChurchDetailCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    GlassCard(modifier = Modifier.fillMaxWidth(), backgroundColor = Color.White.copy(0.05f)) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White.copy(0.7f), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
                Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium, fontFamily = QuickSandFontFamily)
            }
            if ((actionLabel != null) && (onAction != null)) {
                TextButton(onClick = onAction) {
                    Text(text = actionLabel, color = Color(0xFF22D3EE), fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    GlassCard(modifier = modifier, backgroundColor = Color.White.copy(0.05f)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = color, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
        }
    }
}
