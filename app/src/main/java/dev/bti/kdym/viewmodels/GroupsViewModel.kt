package dev.bti.kdym.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.GroupAttachment
import dev.bti.kdym.data.models.GroupMessage
import dev.bti.kdym.data.repositories.GroupRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class GroupsViewModel(
    private val repo: GroupRepository = GroupRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    val currentUserId: String?
        get() = auth.currentUser?.uid

    private val _groups = MutableStateFlow<List<AppGroup>>(emptyList())
    val groups: StateFlow<List<AppGroup>> = _groups

    fun getMessages(groupId: String): Flow<List<GroupMessage>> {
        return repo.getMessages(groupId)
    }

    fun sendMessage(groupId: String, text: String, replyTo: GroupMessage? = null, attachments: List<GroupAttachment> = emptyList()) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            val message = GroupMessage(
                groupId = groupId,
                senderId = uid,
                senderName = auth.currentUser?.displayName ?: "KDYM Member",
                text = text,
                attachments = attachments,
                replyToMessageId = replyTo?.id,
                replyToSenderName = replyTo?.senderName,
                replyToText = replyTo?.text,
                createdAt = com.google.firebase.Timestamp.now()
            )
            repo.sendMessage(groupId, message)
        }
    }

    init {
        observeUserGroups()
    }

    private fun observeUserGroups() {
        viewModelScope.launch {
            auth.currentUser?.uid
                ?.let { uid ->
                    repo.getGroupsForUser(uid)
                        .collect { list ->
                            _groups.value = list
                        }
                }
        }

        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid ?: return@addAuthStateListener

            viewModelScope.launch {
                println("UID = ${auth.currentUser?.uid}")
                repo.getGroupsForUser(uid).collect { list ->
                    _groups.value = list
                }
            }
        }
    }
    // Add this inside GroupsViewModel
    fun reactToMessage(groupId: String, messageId: String, emoji: String) {
        val uid = currentUserId ?: return // Don't allow reactions if not logged in

        viewModelScope.launch {
            try {
                repo.addReaction(groupId, messageId, uid, emoji)
            } catch (e: Exception) {
                // Handle failure (e.g., log error or update a UI state to show a toast)
                println("Failed to add reaction: ${e.message}")
            }
        }
    }
}