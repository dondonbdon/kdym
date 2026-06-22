package dev.bti.kdym.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Timestamp
import dev.bti.kdym.data.models.FeedPost
import dev.bti.kdym.data.models.FeedPostAudience
import dev.bti.kdym.data.models.FeedPostPriority
import dev.bti.kdym.ui.components.*
import dev.bti.kdym.ui.components.admin.PickerBottomSheet
import dev.bti.kdym.ui.theme.QuickSandFontFamily
import dev.bti.kdym.ui.theme.TextSecondary
import dev.bti.kdym.viewmodels.AdminViewModel
import java.util.Locale

@Composable
fun CreateHomePostScreen(
    postId: String? = null,
    onNavigateBack: () -> Unit,
    adminViewModel: AdminViewModel
) {
    val updates by adminViewModel.liveUpdates.collectAsState()
    val tribes by adminViewModel.tribes.collectAsState()
    val groups by adminViewModel.groups.collectAsState()

    val existingPost = remember(postId, updates) { updates.find { it.id == postId } }

    var title by remember(existingPost) { mutableStateOf(existingPost?.title ?: "") }
    var body by remember(existingPost) { mutableStateOf(existingPost?.body ?: "") }
    
    val audiences = listOf("Everyone", "Campers", "Leaders", "Admins", "Specific Tribe", "Specific Group")
    var selectedAudience by remember(existingPost) { 
        mutableStateOf(
            existingPost?.audience?.let {
                when(it) {
                    FeedPostAudience.everyone -> "Everyone"
                    FeedPostAudience.campers -> "Campers"
                    FeedPostAudience.leaders -> "Leaders"
                    FeedPostAudience.admins -> "Admins"
                    FeedPostAudience.tribe -> "Specific Tribe"
                    FeedPostAudience.group -> "Specific Group"
                }
            } ?: audiences[0]
        ) 
    }
    
    val postTypes = listOf("Normal", "Important", "Schedule", "Urgent", "Link", "Vote")
    var selectedPostType by remember(existingPost) { 
        mutableStateOf(
            existingPost?.priority?.let {
                it.name.lowercase().replaceFirstChar { char -> if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() }
            } ?: postTypes[0]
        ) 
    }
    
    var selectedTribeId by remember(existingPost) { mutableStateOf(existingPost?.targetTribeId) }
    var selectedGroupId by remember(existingPost) { mutableStateOf(existingPost?.targetGroupId) }
    
    var showAudiencePicker by remember { mutableStateOf(false) }
    var showTypePicker by remember { mutableStateOf(false) }
    var showTribePicker by remember { mutableStateOf(false) }
    var showGroupPicker by remember { mutableStateOf(false) }
    
    var linkTitle by remember(existingPost) { mutableStateOf(existingPost?.linkTitle ?: "") }
    var url by remember(existingPost) { mutableStateOf(existingPost?.linkURL ?: "") }

    OutpourBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header with Back Button
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "BACK",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        fontFamily = QuickSandFontFamily
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Column {
                    Text(
                        text = if (postId == null) "CREATE" else "EDIT",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                    Text(
                        text = "HOME POST",
                        color = TextSecondary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = QuickSandFontFamily
                    )
                }

                Text(
                    text = "This is the unified announcement/home update composer.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    fontFamily = QuickSandFontFamily
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
                    icon = Icons.AutoMirrored.Filled.Notes
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Audience/Type Pickers
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showAudiencePicker = true }
                        ) {
                            Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = Color(0xFF22D3EE))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "AUDIENCE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text(text = selectedAudience, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = Color.White)
                        }
                        
                        if (selectedAudience == "Specific Tribe") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(0.1f))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showTribePicker = true }
                            ) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = Color(0xFFEAB308))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "TRIBE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    val tribe = tribes.find { it.id == selectedTribeId }
                                    Text(text = tribe?.name ?: "Select Tribe", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = Color.White)
                            }
                        }

                        if (selectedAudience == "Specific Group") {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(0.1f))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable { showGroupPicker = true }
                            ) {
                                Icon(imageVector = Icons.Default.Forum, contentDescription = null, tint = Color(0xFF22D3EE))
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "GROUP", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    val group = groups.find { it.id == selectedGroupId }
                                    Text(text = group?.name ?: "Select Group", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                                Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = Color.White)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.White.copy(0.1f))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showTypePicker = true }
                        ) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF22D3EE))
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "TYPE", color = TextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Text(text = selectedPostType, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    fontFamily = QuickSandFontFamily
                )
                Text(
                    text = "ATTACH LINK",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = QuickSandFontFamily
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
                    onClick = {
                        if (postId == null) {
                            adminViewModel.createHomePost(
                                title = title,
                                body = body,
                                audience = selectedAudience,
                                priority = selectedPostType,
                                linkTitle = linkTitle,
                                linkURL = url,
                                targetTribeId = selectedTribeId,
                                targetGroupId = selectedGroupId
                            )
                        } else {
                            existingPost?.let {
                                adminViewModel.updateHomePost(it.copy(
                                    title = title,
                                    body = body,
                                    audience = when (selectedAudience) {
                                        "Campers" -> FeedPostAudience.campers
                                        "Leaders" -> FeedPostAudience.leaders
                                        "Admins" -> FeedPostAudience.admins
                                        "Specific Tribe" -> FeedPostAudience.tribe
                                        "Specific Group" -> FeedPostAudience.group
                                        else -> FeedPostAudience.everyone
                                    },
                                    priority = when (selectedPostType) {
                                        "Important" -> FeedPostPriority.important
                                        "Urgent" -> FeedPostPriority.urgent
                                        "Schedule" -> FeedPostPriority.schedule
                                        "Link" -> FeedPostPriority.link
                                        "Vote" -> FeedPostPriority.vote
                                        else -> FeedPostPriority.normal
                                    },
                                    linkTitle = linkTitle.takeIf { it.isNotBlank() },
                                    linkURL = url.takeIf { it.isNotBlank() },
                                    targetTribeId = selectedTribeId,
                                    targetGroupId = selectedGroupId,
                                    updatedAt = Timestamp.now()
                                ))
                            }
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(28.dp),
                    enabled = title.isNotBlank() && body.isNotBlank()
                ) {
                    Text(text = if (postId == null) "CREATE POST" else "SAVE CHANGES", fontWeight = FontWeight.Black, fontFamily = QuickSandFontFamily)
                }

                Spacer(modifier = Modifier.height(140.dp))
            }
        }

        // Pickers
        if (showAudiencePicker) {
            PickerBottomSheet(
                title = "SELECT AUDIENCE",
                options = audiences,
                selectedOption = selectedAudience,
                onOptionSelected = { selectedAudience = it; showAudiencePicker = false },
                onDismiss = { showAudiencePicker = false }
            )
        }

        if (showTypePicker) {
            PickerBottomSheet(
                title = "SELECT TYPE",
                options = postTypes,
                selectedOption = selectedPostType,
                onOptionSelected = { selectedPostType = it; showTypePicker = false },
                onDismiss = { showTypePicker = false }
            )
        }

        if (showTribePicker) {
            PickerBottomSheet(
                title = "SELECT TRIBE",
                options = tribes.map { it.name },
                selectedOption = tribes.find { it.id == selectedTribeId }?.name ?: "",
                onOptionSelected = { name -> 
                    selectedTribeId = tribes.find { it.name == name }?.id
                    showTribePicker = false 
                },
                onDismiss = { showTribePicker = false }
            )
        }

        if (showGroupPicker) {
            PickerBottomSheet(
                title = "SELECT GROUP",
                options = groups.map { it.name },
                selectedOption = groups.find { it.id == selectedGroupId }?.name ?: "",
                onOptionSelected = { name -> 
                    selectedGroupId = groups.find { it.name == name }?.id
                    showGroupPicker = false 
                },
                onDismiss = { showGroupPicker = false }
            )
        }
    }
}
