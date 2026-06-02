package dev.bti.kdym.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.bti.kdym.data.models.Tribe
import dev.bti.kdym.data.models.TribeWarEvent
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun AddScoreScreen(
    initialTribeId: String? = null,
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel,
    mainViewModel: MainViewModel
) {
    val tribes by viewModel.tribes.collectAsState()
    val events by viewModel.tribeEvents.collectAsState()
    val user by mainViewModel.user.collectAsState()

    var selectedTribe by remember { mutableStateOf<Tribe?>(null) }
    var selectedEvent by remember { mutableStateOf<TribeWarEvent?>(null) }
    var points by remember { mutableIntStateOf(25) }
    var reason by remember { mutableStateOf("") }

    var showTribePicker by remember { mutableStateOf(false) }
    var showEventPicker by remember { mutableStateOf(false) }

    val hasPermission = remember(user) {
        user?.roleEnum?.isSuperAdmin == true || user?.email == "nathanleonard1127@gmail.com".trim()
    }

    LaunchedEffect(tribes, initialTribeId) {
        if (selectedTribe == null && initialTribeId != null) {
            selectedTribe = tribes.find { it.id == initialTribeId }
        } else if (selectedTribe == null && tribes.isNotEmpty()) {
            selectedTribe = tribes.first()
        }
    }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ScreenHeader(
                onNavigateBack = onNavigateBack,
                icon = Icons.Default.AddBusiness,
                title = "ADD"
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "SCORE",
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    lineHeight = 44.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "TRIBE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(12.dp))
                PickerField(
                    text = selectedTribe?.name ?: "Choose Tribe",
                    onClick = { showTribePicker = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "EVENT", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(12.dp))
                PickerField(
                    text = selectedEvent?.title ?: "General Score",
                    onClick = { showEventPicker = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "POINTS", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
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
                        text = "$points points",
                        color = Color(0xFF10B981),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily,
                        modifier = Modifier.weight(1f)
                    )
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(0.1f), CircleShape)
                            .padding(horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { if (points > 0) points -= 5 }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Minus", tint = Color.White, modifier = Modifier.graphicsLayer { rotationZ = 45f })
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(Color.White.copy(0.1f)))
                        IconButton(onClick = { points += 5 }) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Plus", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                CommandInputField(
                    value = reason,
                    onValueChange = { reason = it },
                    placeholder = "Reason, example First place relay",
                    icon = Icons.Default.AddBusiness
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (!hasPermission) return@Button

                        selectedTribe?.let {
                            viewModel.addPointsToTribe(it, points, reason, selectedEvent?.id, selectedEvent?.title)
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(28.dp),
                    enabled = selectedTribe != null && reason.isNotBlank() && hasPermission
                ) {
                    Text(
                        text = if (hasPermission) "ADD SCORE" else "UNAUTHORIZED",
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }

        if (showTribePicker) {
            PickerModal(
                title = "SELECT TRIBE",
                items = tribes,
                itemLabel = { it.name },
                onItemSelected = { selectedTribe = it; showTribePicker = false },
                onDismiss = { showTribePicker = false }
            )
        }

        if (showEventPicker) {
            PickerModal(
                title = "SELECT EVENT",
                items = listOf(null) + events,
                itemLabel = { it?.title ?: "General Score" },
                onItemSelected = { selectedEvent = it; showEventPicker = false },
                onDismiss = { showEventPicker = false }
            )
        }
    }
}

@Composable
fun PickerField(text: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(56.dp).clickable { onClick() },
        color = Color.White.copy(0.05f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, color = Color.White, modifier = Modifier.weight(1f), fontFamily = QuickSandFontFamily)
            Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = TextSecondary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> PickerModal(
    title: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF111111),
        scrimColor = Color.Black.copy(0.6f)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Text(text = title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
            Spacer(modifier = Modifier.height(16.dp))
            items.forEach { item ->
                Surface(
                    modifier = Modifier.fillMaxWidth().clickable { onItemSelected(item) },
                    color = Color.Transparent
                ) {
                    Text(text = itemLabel(item), color = Color.White, modifier = Modifier.padding(16.dp), fontSize = 16.sp, fontFamily = QuickSandFontFamily)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
