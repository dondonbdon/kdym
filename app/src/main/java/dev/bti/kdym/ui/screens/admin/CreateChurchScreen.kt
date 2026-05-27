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
import dev.bti.kdym.data.models.Church
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.CommandSwitch
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun CreateChurchScreen(
    churchId: String? = null,
    onNavigateBack: () -> Unit,
    adminViewModel: AdminViewModel,
    mainViewModel: MainViewModel
) {
    val churches by mainViewModel.churches.collectAsState()
    val existingChurch = remember(churchId, churches) { churches.find { it.id == churchId } }

    var name by remember(existingChurch) { mutableStateOf(existingChurch?.name ?: "") }
    var pastorName by remember(existingChurch) { mutableStateOf(existingChurch?.pastorName ?: "") }
    var address by remember(existingChurch) { mutableStateOf(existingChurch?.address ?: "") }
    var city by remember(existingChurch) { mutableStateOf(existingChurch?.city ?: "") }
    var state by remember(existingChurch) { mutableStateOf(existingChurch?.state ?: "") }
    var zip by remember { mutableStateOf("") } // Assuming we add this to model or handle separately
    var phone by remember { mutableStateOf("") }
    var website by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }

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
                verticalAlignment = Alignment.CenterVertically
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
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF22D3EE).copy(0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Business, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (churchId == null) "ADD CHURCH" else "EDIT CHURCH",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "Manage district church records.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                CommandInputField(value = name, onValueChange = { name = it }, placeholder = "Church Name", icon = Icons.Default.Business)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = pastorName, onValueChange = { pastorName = it }, placeholder = "Pastor Name", icon = Icons.Default.Person)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = address, onValueChange = { address = it }, placeholder = "Address", icon = Icons.Default.LocationOn)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = city, onValueChange = { city = it }, placeholder = "City", icon = Icons.Default.LocationCity)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = state, onValueChange = { state = it }, placeholder = "State", icon = Icons.Default.Map)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = zip, onValueChange = { zip = it }, placeholder = "ZIP Code", icon = Icons.Default.Tag)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = phone, onValueChange = { phone = it }, placeholder = "Phone", icon = Icons.Default.Phone)
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(value = website, onValueChange = { website = it }, placeholder = "Website", icon = Icons.Default.Language)
                
                Spacer(modifier = Modifier.height(24.dp))

                CommandSwitch(
                    title = "Active Record",
                    description = "Whether this church is currently active in the district.",
                    checked = isActive,
                    onCheckedChange = { isActive = it }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val church = Church(
                            id = churchId ?: "",
                            name = name,
                            pastorName = pastorName,
                            address = address,
                            city = city,
                            state = state,
                            // zip = zip, // Model update needed if we want to save this
                        )
                        if (churchId == null) {
                            // adminViewModel.createChurch(church)
                        } else {
                            // adminViewModel.updateChurch(church)
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = CircleShape,
                    enabled = name.isNotEmpty() && pastorName.isNotEmpty()
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "SAVE CHURCH", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}
