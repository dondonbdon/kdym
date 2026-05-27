package dev.bti.kdym.ui.screens.groups

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
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel

@Composable
fun CreatePollScreen(
    groupId: String? = null,
    onNavigateBack: () -> Unit,
    adminViewModel: AdminViewModel
) {
    var question by remember { mutableStateOf("") }
    var options by remember { mutableStateOf(listOf("", "", "", "")) }
    var multipleVotes by remember { mutableStateOf(false) }
    var showOnHome by remember { mutableStateOf(false) }

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
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "POLL",
                        color = TextSecondary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
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
                    text = "Ask the group a question and collect votes.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = QuickSandFontFamily
                )

                Spacer(modifier = Modifier.height(24.dp))

                CommandInputField(
                    value = question,
                    onValueChange = { question = it },
                    placeholder = "Question",
                    icon = Icons.Default.QuestionMark
                )

                Spacer(modifier = Modifier.height(12.dp))

                options.forEachIndexed { index, option ->
                    CommandInputField(
                        value = option,
                        onValueChange = { newValue ->
                            options = options.toMutableList().apply { this[index] = newValue }
                        },
                        placeholder = "Option ${index + 1}",
                        icon = when(index) {
                            0 -> Icons.Default.Filter1
                            1 -> Icons.Default.Filter2
                            2 -> Icons.Default.Filter3
                            else -> Icons.Default.Filter4
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                CommandSwitch(
                    title = "Multiple Votes",
                    description = "Allow people to select more than one option.",
                    checked = multipleVotes,
                    onCheckedChange = { multipleVotes = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CommandSwitch(
                    title = "Show on Home",
                    description = "Promote this poll to the relevant Home feed.",
                    checked = showOnHome,
                    onCheckedChange = { showOnHome = it }
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { 
                        groupId?.let {
                            adminViewModel.createPoll(
                                groupId = it,
                                question = question,
                                options = options.filter { it.isNotBlank() },
                                allowMultiple = multipleVotes,
                                showOnHome = showOnHome
                            )
                        }
                        onNavigateBack() 
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(0.2f), contentColor = Color.White.copy(0.4f)),
                    shape = RoundedCornerShape(28.dp),
                    enabled = question.isNotBlank() && options.any { it.isNotBlank() } && groupId != null
                ) {
                    Icon(imageVector = Icons.Default.Poll, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "CREATE POLL", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
