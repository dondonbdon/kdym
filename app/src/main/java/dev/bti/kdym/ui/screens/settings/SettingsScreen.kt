package dev.bti.kdym.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FrontHand
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.components.OutpourBackground
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.MainViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import dev.bti.kdym.data.models.UserRole

@Composable
fun SettingsScreen(
    onNavigateToProfile: () -> Unit,
    onNavigateToNotificationPreferences: () -> Unit,
    onNavigateToCommandHub: () -> Unit,
    onNavigateToChurches: () -> Unit,
    onNavigateToRequestAccess: () -> Unit,
    onNavigateToModeration: () -> Unit,
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val user by viewModel.user.collectAsState()

    var showReportDialog by remember { mutableStateOf(false) }

    if (showReportDialog && user != null) {
        dev.bti.kdym.ui.components.ReportMessageDialog(
            message = dev.bti.kdym.data.models.GroupMessage(
                id = "general_app_report",
                text = "General App/Account Report",
                senderName = user!!.displayName,
                senderId = user!!.uid
            ),
            group = dev.bti.kdym.data.models.AppGroup(
                id = "kdym_app",
                name = "KDYM App"
            ),
            currentUser = user!!,
            onDismiss = { showReportDialog = false },
            onSubmit = { reason, details ->
                viewModel.submitReport(
                    groupId = "kdym_app",
                    groupName = "KDYM App",
                    message = dev.bti.kdym.data.models.GroupMessage(
                        id = "general_app_report",
                        text = "General App/Account Report",
                        senderName = user!!.displayName,
                        senderId = user!!.uid
                    ),
                    reporter = user!!,
                    reason = reason,
                    details = details,
                    onSuccess = {
                        showReportDialog = false
                        viewModel.showFeedback("Report submitted successfully")
                    }
                )
            }
        )
    }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // User Info Card
                UserInfoCard(user = user)

                Spacer(modifier = Modifier.height(16.dp))

                // Approval Status Card
                if (user?.hasApprovedCampAccess == true) {
                    ApprovalStatusCard(
                        role = user?.roleEnum?.title ?: "Camper",
                        isApproved = true
                    )
                } else if (user?.accessStatus == "pending") {
                    ApprovalStatusCard(
                        role = user?.requestedRole ?: "Camper",
                        isApproved = false
                    )
                } else if (user?.requestedCampId == null && user?.requestedRole == null && user?.requestedAt == null) {
                    RequestCampAccessCard(onNavigateToRequestAccess = onNavigateToRequestAccess)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Command Center Card
                if (user?.roleEnum?.isSuperAdmin == true) {
                    CommandCenterCard(onClick = onNavigateToCommandHub)
                    Spacer(modifier = Modifier.height(16.dp))

                    // TODO: REMOVE BEFORE PRODUCTION
                    DevMigrationCard(viewModel = viewModel)

                    Spacer(modifier = Modifier.height(24.dp))
                }

                Text(
                    text = "ACCOUNT",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                Text(
                    text = "PROFILE",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.Black.copy(0.3f),
                    contentPadding = 0.dp
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.AccountCircle,
                            title = "Profile Details",
                            subtitle = "Edit your name, photo, phone number, bio, and account information.",
                            onClick = onNavigateToProfile
                        )
                        SettingsItem(
                            icon = Icons.Default.NotificationsActive,
                            title = "Notifications",
                            subtitle = "Choose the KDYM updates and reminders you want to receive.",
                            onClick = onNavigateToNotificationPreferences
                        )
                    }
                }

                if (user?.isAdmin == true) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "SAFETY",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontFamily = QuickSandFontFamily,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "MODERATION",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                    )

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.Black.copy(0.3f),
                        contentPadding = 0.dp
                    ) {
                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Moderation Reports",
                            subtitle = "Review and manage user-submitted reports and flagged content.",
                            onClick = onNavigateToModeration
                        )
                    }
                }

                if (user?.isAdmin == true) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "KDYM",
                        color = Color(0xFFEF4444),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp,
                        fontFamily = QuickSandFontFamily,
                        modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "CHURCHES",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                    )

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.Black.copy(0.3f),
                        contentPadding = 0.dp
                    ) {
                        SettingsItem(
                            icon = Icons.Default.Business,
                            title = "Churches & Pastors",
                            subtitle = "Manage church records, pastor claims, and pastor assignments.",
                            onClick = onNavigateToChurches
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "LEGAL",
                    color = Color(0xFFEF4444),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
                )
                Text(
                    text = "SAFETY",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily,
                    modifier = Modifier.padding(start = 8.dp, bottom = 16.dp)
                )

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color.Black.copy(0.3f),
                    contentPadding = 0.dp
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.FrontHand,
                            title = "Privacy Policy",
                            subtitle = "How KDYM handles account, app, notification, and community data.",
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://kdym.org/privacy".toUri()
                                )
                                context.startActivity(intent)
                            }
                        )
                        SettingsItem(
                            icon = Icons.Default.Description,
                            title = "Terms of Service",
                            subtitle = "Rules for using KDYM app, accounts, groups, and services.",
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://kdym.org/terms".toUri()
                                )
                                context.startActivity(intent)
                            }
                        )
                        SettingsItem(
                            icon = Icons.Default.Security,
                            title = "Community Guidelines",
                            subtitle = "Safety expectations for posts, comments, messages, groups, and media.",
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    "https://kdym.org/community-guidelines".toUri()
                                )
                                context.startActivity(intent)
                            }
                        )
                        SettingsItem(
                            icon = Icons.Default.Campaign,
                            title = "Report a Concern",
                            subtitle = "Report inappropriate content, users, or other safety issues.",
                            onClick = {
                                showReportDialog = true
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.signOut() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black.copy(0.3f),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(0.1f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "SIGN OUT",
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }
    }
}

@Composable
fun UserInfoCard(user: AppUser?) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 32.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFEF4444), Color(0xFF22D3EE))),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (user?.photoURL != null) {
                        AsyncImage(
                            model = user.photoURL,
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = user?.initials ?: "DD",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = user?.displayName ?: "Don Don",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "Your KDYM account",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        fontFamily = QuickSandFontFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        RoleBadge(
                            text = user?.roleEnum?.title?.uppercase() ?: "CAMPER",
                            color = Color(0xFF10B981)
                        )
                        if (user?.isAdmin == true) {
                            RoleBadge(text = "COMMAND", color = Color(0xFFEF4444))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            UserInfoRow(
                icon = Icons.Default.Email,
                label = "EMAIL",
                value = user?.email ?: "don@don.don"
            )
            Spacer(modifier = Modifier.height(12.dp))
            UserInfoRow(
                icon = Icons.Default.Phone,
                label = "PHONE",
                value = user?.phoneNumber ?: "Not added"
            )
            Spacer(modifier = Modifier.height(12.dp))
            UserInfoRow(
                icon = Icons.Default.Business,
                label = "CHURCH",
                value = user?.churchName ?: "Not selected"
            )
        }
    }
}

@Composable
fun RoleBadge(text: String, color: Color) {
    Surface(
        color = color.copy(0.15f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(0.3f))
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontFamily = QuickSandFontFamily,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun UserInfoRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF22D3EE),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            modifier = Modifier.width(60.dp),
            fontFamily = QuickSandFontFamily
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = QuickSandFontFamily
        )
    }
}

@Composable
fun RequestCampAccessCard(onNavigateToRequestAccess: () -> Unit) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color(0xFF22D3EE).copy(0.1f),
        borderColor = Color(0xFF22D3EE).copy(0.2f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF22D3EE).copy(0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = Color(0xFF22D3EE)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Camp Access",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "Unlock camp schedules, tribes, and exclusive content by requesting access.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToRequestAccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22D3EE).copy(0.1f),
                    contentColor = Color(0xFF22D3EE)
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF22D3EE).copy(0.3f))
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REQUEST ACCESS",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}

@Composable
fun ApprovalStatusCard(role: String, isApproved: Boolean) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        cornerRadius = 24.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (isApproved) Color(0xFF10B981).copy(0.1f) else Color(0xFFEAB308).copy(
                            0.1f
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isApproved) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isApproved) Color(0xFF10B981) else Color(0xFFEAB308)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = if (isApproved) "You're approved as $role" else "Access request sent",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = if (isApproved) "Your account has unlocked the features connected to your role."
                    else "Your request has been sent to the admins for review.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}

@Composable
fun CommandCenterCard(onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        cornerRadius = 24.dp,
        backgroundColor = Color.Black.copy(0.3f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEF4444).copy(0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = Color(0xFFEF4444)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "COMMAND CENTER",
                    color = Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "Manage KDYM",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "Camp mode, users, updates, groups, churches, tribes, and Play.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Color.White.copy(0.3f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(Color.White.copy(0.05f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF22D3EE),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = QuickSandFontFamily
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = QuickSandFontFamily
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            tint = Color.White.copy(0.2f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun DevMigrationCard(viewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var isMigrating by remember { mutableStateOf(false) }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isMigrating) {
                if (isMigrating) return@clickable
                isMigrating = true
                coroutineScope.launch {
                    try {
                        val db = FirebaseFirestore.getInstance()
                        val snapshot = db.collection("users").get().await()
                        var batch = db.batch()
                        var operationsInBatch = 0
                        var totalUpdated = 0

                        val leaderRoles = listOf("tribeLeader", "groupLeader", "staff", "admin", "superAdmin")

                        for (doc in snapshot.documents) {
                            val role = doc.getString("role") ?: "public"
                            val updates = mutableMapOf<String, Any>()

                            // 1. Set required booleans
                            updates["isAdmin"] = role == "admin" || role == "superAdmin"
                            updates["isLeader"] = leaderRoles.contains(role)

                            // 2. Name Splitting Logic (Only if missing)
                            if (!doc.contains("firstName") || !doc.contains("lastName")) {
                                val displayName = doc.getString("displayName")?.trim() ?: ""
                                if (displayName.isNotBlank()) {
                                    val parts = displayName.split("\\s+".toRegex())
                                    updates["firstName"] = parts.first()
                                    updates["lastName"] = if (parts.size > 1) {
                                        parts.drop(1).joinToString(" ")
                                    } else {
                                        "" // Leave empty if they only provided one name
                                    }
                                }
                            }

                            // 3. Queue bloat fields for deletion
                            val redundantFields = listOf(
                                "canManageAnnouncements", "canManageApprovals", "canManageCampSettings",
                                "canManageGroups", "canManagePoints", "canManageTribes",
                                "hasApprovedCampAccess", "hasCommandAccess", "isPublic",
                                "initials", "roleEnum", "statusEnum"
                            )

                            for (field in redundantFields) {
                                if (doc.contains(field)) {
                                    updates[field] = FieldValue.delete()
                                }
                            }

                            if (updates.isNotEmpty()) {
                                batch.update(doc.reference, updates)
                                operationsInBatch++
                                totalUpdated++
                            }

                            // Commit at 450 to respect Firestore's 500 operation limit
                            if (operationsInBatch >= 450) {
                                batch.commit().await()
                                batch = db.batch()
                                operationsInBatch = 0
                            }

                            // Inside your migration loop:
                            if (doc.get("updatedAt") == null) {
                                updates["updatedAt"] = FieldValue.serverTimestamp()
                            }
                            if (doc.get("createdAt") == null) {
                                updates["createdAt"] = FieldValue.serverTimestamp()
                            }
                        }

                        // Commit remainder
                        if (operationsInBatch > 0) {
                            batch.commit().await()
                        }

                        viewModel.showFeedback("Migrated $totalUpdated users!")
                    } catch (e: Exception) {
                        viewModel.showFeedback("Migration failed: ${e.message}")
                    } finally {
                        isMigrating = false
                    }
                }
            },
        cornerRadius = 24.dp,
        backgroundColor = Color(0xFFEF4444).copy(0.2f),
        borderColor = Color(0xFFEF4444).copy(0.4f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFEF4444).copy(0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isMigrating) {
                    CircularProgressIndicator(
                        color = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFEF4444)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "DEV TOOL",
                    color = Color(0xFFEF4444),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = if (isMigrating) "Migrating Database..." else "Run User Migration",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "Fixes fields, trims bloat, and splits names.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontFamily = QuickSandFontFamily
                )
            }
        }
    }
}
