package dev.bti.kdym.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import dev.bti.kdym.data.models.Announcement
import dev.bti.kdym.data.models.AnnouncementAudience
import dev.bti.kdym.data.models.AnnouncementPriority
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

@Composable
fun SendAnnouncementDialog(
    announcement: Announcement? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, String, AnnouncementPriority) -> Unit
) {
    var title by remember { mutableStateOf(announcement?.title ?: "") }
    var message by remember { mutableStateOf(announcement?.body ?: "") }
    var priority by remember { mutableStateOf(announcement?.priority ?: AnnouncementPriority.normal) }
    var audience by remember { mutableStateOf(announcement?.audience ?: AnnouncementAudience.everyone) }
    var sendPush by remember { mutableStateOf(true) }
    var hasExpiration by remember { mutableStateOf(announcement?.expiresAt != null) }

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
            cornerRadius = 32.dp,
            backgroundColor = Color.Black.copy(alpha = 0.95f)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (announcement == null) "SEND\nANNOUNCEMENT" else "EDIT\nANNOUNCEMENT",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily,
                        lineHeight = 32.sp
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.background(Color.White.copy(0.1f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close, 
                            contentDescription = "Close", 
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                CommandInputField(
                    value = title, 
                    onValueChange = { title = it }, 
                    placeholder = "Title", 
                    icon = Icons.Default.Campaign
                )
                Spacer(modifier = Modifier.height(12.dp))
                CommandInputField(
                    value = message, 
                    onValueChange = { message = it }, 
                    placeholder = "Message", 
                    icon = Icons.AutoMirrored.Filled.Notes
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "PRIORITY", 
                    color = Color(0xFFEF4444), 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriorityButton("NORMAL", priority == AnnouncementPriority.normal, Modifier.weight(1f)) { priority = AnnouncementPriority.normal }
                    PriorityButton("IMPORTANT", priority == AnnouncementPriority.important, Modifier.weight(1f)) { priority = AnnouncementPriority.important }
                    PriorityButton("URGENT", priority == AnnouncementPriority.urgent, Modifier.weight(1f)) { priority = AnnouncementPriority.urgent }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "AUDIENCE", 
                    color = Color(0xFFEF4444), 
                    fontSize = 10.sp, 
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    color = Color.White.copy(0.05f),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Everyone", color = Color.White, modifier = Modifier.weight(1f), fontFamily = QuickSandFontFamily)
                        Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                ToggleOption("Send Push Notification", "Also alert matching users on their phones.", sendPush) { sendPush = it }
                Spacer(modifier = Modifier.height(16.dp))
                ToggleOption("Expiration", "Automatically hide this announcement after a date.", hasExpiration) { hasExpiration = it }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { onConfirm(title, message, priority) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(28.dp),
                    enabled = title.isNotBlank() && message.isNotBlank()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (announcement == null) "PUBLISH ANNOUNCEMENT" else "UPDATE ANNOUNCEMENT", 
                            fontWeight = FontWeight.Black, 
                            fontFamily = QuickSandFontFamily
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PriorityButton(label: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(44.dp).clickable { onClick() },
        color = if (isSelected) Color.White.copy(0.1f) else Color.White.copy(0.05f),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) Color.White.copy(0.3f) else Color.Transparent)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = label, 
                color = if (isSelected) Color.White else TextSecondary, 
                fontSize = 11.sp, 
                fontWeight = FontWeight.Black,
                fontFamily = QuickSandFontFamily,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ToggleOption(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = QuickSandFontFamily)
            Text(text = subtitle, color = TextSecondary, fontSize = 12.sp, fontFamily = QuickSandFontFamily)
        }
        Switch(
            checked = checked, 
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFEF4444)
            )
        )
    }
}
