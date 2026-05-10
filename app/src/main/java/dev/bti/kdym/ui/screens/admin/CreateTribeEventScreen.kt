package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateTribeEventScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var maxPoints by remember { mutableIntStateOf(100) }
    var status by remember { mutableStateOf("Upcoming") }
    var startDate by remember { mutableStateOf(Calendar.getInstance()) }

    val statusOptions = listOf("Upcoming", "Active", "Completed", "Archived")

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.Flag,
                title = "CREATE",
                subtitle = "Define a new Tribe War event."
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "TRIBE EVENT",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = RubikFontFamily,
                    lineHeight = 32.sp
                )
                Text(
                    text = "Camp ID: camp_2026",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = RubikFontFamily
                )

                Spacer(modifier = Modifier.height(32.dp))

                CommandInputField(value = title, onValueChange = { title = it }, placeholder = "Event Title", icon = Icons.Default.Flag)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = description, onValueChange = { description = it }, placeholder = "Description", icon = Icons.AutoMirrored.Default.Notes)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = location, onValueChange = { location = it }, placeholder = "Location", icon = Icons.Default.Place)

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "MAX POINTS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(Color.White.copy(0.05f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$maxPoints points",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = RubikFontFamily,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(0.1f), CircleShape)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (maxPoints > 0) maxPoints -= 10 }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Minus", tint = Color.White, modifier = Modifier.graphicsLayer { rotationZ = 45f })
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(0.1f)))
                        IconButton(onClick = { maxPoints += 10 }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Plus", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "STATUS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusOptions.forEach { option ->
                        val isSelected = status == option
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable { status = option },
                            color = if (isSelected) Color.White.copy(0.2f) else Color.White.copy(0.05f),
                            shape = RoundedCornerShape(22.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.White) else null
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = option, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = RubikFontFamily)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    color = Color.White.copy(0.05f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Start Date", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier.background(Color.White.copy(0.1f), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = SimpleDateFormat("d MMM 2026", Locale.getDefault()).format(startDate.time), color = Color.White, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Row(
                            modifier = Modifier.background(Color.White.copy(0.1f), RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(startDate.time), color = Color.White, fontSize = 14.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.createTribeEvent(title, description, maxPoints)
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f), contentColor = Color.White),
                    shape = RoundedCornerShape(28.dp),
                    enabled = title.isNotBlank()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "CREATE TRIBE EVENT", fontWeight = FontWeight.Black, fontFamily = RubikFontFamily)
                    }
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}
