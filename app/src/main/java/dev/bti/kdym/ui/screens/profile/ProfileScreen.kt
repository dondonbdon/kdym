package dev.bti.kdym.ui.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import dev.bti.kdym.ui.components.CommandInputField
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.RedAccent
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToNotificationPrefs: () -> Unit,
    viewModel: MainViewModel = viewModel(),
) {
    val user by viewModel.user.collectAsState()

    var displayName by remember(user) { mutableStateOf(user?.displayName ?: "") }
    var phoneNumber by remember(user) { mutableStateOf(user?.phoneNumber ?: "") }
    var bio by remember(user) { mutableStateOf(user?.bio ?: "") }
    var churchName by remember(user) { mutableStateOf(user?.churchName ?: "") }
    var churchCity by remember(user) { mutableStateOf(user?.churchCity ?: "") }
    var pastorName by remember(user) { mutableStateOf(user?.pastorName ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header with Back button
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
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "PROFILE",
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = QuickSandFontFamily
                        )
                        Text(
                            text = "Your KDYM identity and account details.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Profile Header with Change Photo
                ProfileEditHeader(
                    userDisplayName = user?.displayName,
                    email = user?.email,
                    photoUrl = user?.photoURL,
                    selectedImageUri = selectedImageUri,
                    onChangePhoto = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                CommandInputField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    placeholder = "Full Name",
                    icon = Icons.Default.Person
                )
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = "Phone Number",
                    icon = Icons.Default.Phone
                )
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(
                    value = churchName,
                    onValueChange = { churchName = it },
                    placeholder = "Church Name",
                    icon = Icons.Default.Business
                )
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(
                    value = churchCity,
                    onValueChange = { churchCity = it },
                    placeholder = "Church City",
                    icon = Icons.Default.LocationOn
                )
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(
                    value = pastorName,
                    onValueChange = { pastorName = it },
                    placeholder = "Pastor Name",
                    icon = Icons.Default.Badge
                )
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = "Hooray Yah!",
                    icon = Icons.AutoMirrored.Filled.Notes
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Account info section
                AccountInfoCard(
                    role = user?.roleEnum?.title ?: "Camper",
                    access = if (user?.hasApprovedCampAccess == true) "Camp Access Approved" else user?.accessStatus ?: "Public"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Redesigned Danger Zone
                DangerZoneRedesign(onDeleteClick = { showDeleteDialog = true })

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.updateProfile(
                            displayName = displayName,
                            phoneNumber = phoneNumber,
                            bio = bio,
                            churchName = churchName,
                            churchCity = churchCity,
                            pastorName = pastorName,
                            profilePhotoUri = selectedImageUri
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = "SAVE PROFILE", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily, fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete Account?", color = Color.White) },
                text = { Text("This will permanently delete your KDYM account and sign you out. This action cannot be undone.", color = Color.White.copy(0.7f)) },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.softDeleteUser()
                        showDeleteDialog = false
                    }) {
                        Text("DELETE", color = RedAccent, fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("CANCEL", color = Color.White.copy(0.6f))
                    }
                },
                containerColor = Color(0xFF111111),
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

@Composable
fun ProfileEditHeader(
    userDisplayName: String?,
    email: String?,
    photoUrl: String?,
    selectedImageUri: Uri?,
    onChangePhoto: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF22D3EE))), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val model = selectedImageUri ?: photoUrl
                if (model != null) {
                    AsyncImage(
                        model = model,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = (userDisplayName?.take(1) ?: "K") + (userDisplayName?.split(" ")?.getOrNull(1)?.take(1) ?: "D"),
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = userDisplayName ?: "Don Don",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = email ?: "don@don.don",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = QuickSandFontFamily
                )
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onChangePhoto,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "CHANGE PHOTO", fontWeight = FontWeight.Black, fontSize = 11.sp, fontFamily = QuickSandFontFamily)
                }
            }
        }
    }
}

@Composable
fun AccountInfoCard(role: String, access: String) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color(0xFF22D3EE), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Account", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
            }
            Spacer(modifier = Modifier.height(16.dp))
            AccountInfoRow(label = "ROLE", value = role)
            Spacer(modifier = Modifier.height(12.dp))
            AccountInfoRow(label = "ACCESS", value = access)
        }
    }
}

@Composable
fun AccountInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, fontFamily = QuickSandFontFamily)
        Text(text = value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
    }
}

@Composable
fun DangerZoneRedesign(onDeleteClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color(0xFFEF4444).copy(0.05f),
        borderColor = Color(0xFFEF4444).copy(0.2f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = RedAccent, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = "Danger Zone", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                    Text(
                        text = "Permanently delete your KDYM account, remove this device from notifications, anonymize your profile, and sign out.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onDeleteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = RedAccent.copy(0.1f), contentColor = RedAccent),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RedAccent.copy(0.3f))
            ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "DELETE ACCOUNT", fontWeight = FontWeight.Black, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
            }
        }
    }
}
