package dev.bti.kdym.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.GroupAttachment
import dev.bti.kdym.data.models.GroupAttachmentType
import dev.bti.kdym.data.models.GroupJoinRequest
import dev.bti.kdym.data.models.GroupMessage
import dev.bti.kdym.data.models.MessageReaction
import dev.bti.kdym.data.repositories.GroupRepository
import dev.bti.kdym.data.repositories.StorageRepository
import dev.bti.kdym.data.repositories.UserRepository
import dev.bti.kdym.data.repositories.RepositoryProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for managing chat groups, messages, and interactive features like reactions and polls.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GroupsViewModel(
    private val repo: GroupRepository = GroupRepository(),
    private val storageRepo: StorageRepository = StorageRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    /**
     * The unique ID of the currently logged-in user.
     */
    val currentUserId: String?
        get() = auth.currentUser?.uid

    private val _groups = MutableStateFlow<List<AppGroup>>(emptyList())
    val groups: StateFlow<List<AppGroup>> = _groups

    // Community Tab Persistence
    private val _selectedTab = MutableStateFlow("GROUPS")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()

    private val prefs = RepositoryProvider.prefs

    val adminViewMode: StateFlow<Boolean> = (prefs?.adminViewMode ?: flowOf(false))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun selectTab(tab: String) {
        _selectedTab.value = tab
    }

    fun toggleAdminViewMode() = viewModelScope.launch {
        prefs?.saveAdminViewMode(!adminViewMode.value)
    }

    // Join Request Tracking
    private val _userRequests = MutableStateFlow<List<GroupJoinRequest>>(emptyList())
    val userRequests: StateFlow<List<GroupJoinRequest>> = _userRequests.asStateFlow()

    // Group Media
    private val _groupMedia = MutableStateFlow<Map<String, List<GroupAttachment>>>(emptyMap())
    val groupMedia: StateFlow<Map<String, List<GroupAttachment>>> = _groupMedia.asStateFlow()

    fun getGroupMedia(groupId: String): Flow<List<GroupAttachment>> {
        if (!_groupMedia.value.containsKey(groupId)) {
            viewModelScope.launch {
                repo.getGroupMedia(groupId).collect { newList ->
                    _groupMedia.update { it + (groupId to newList) }
                }
            }
        }
        return _groupMedia.map { it[groupId] ?: emptyList() }
    }

    // Optimistic state to provide immediate feedback for interactive UI elements.
    private val _optimisticReactions = MutableStateFlow<Map<String, String?>>(emptyMap()) // messageId -> emoji
    val optimisticReactions: StateFlow<Map<String, String?>> = _optimisticReactions.asStateFlow()

    private val _optimisticPollVotes = MutableStateFlow<Map<String, String?>>(emptyMap()) // pollId -> optionId
    val optimisticPollVotes: StateFlow<Map<String, String?>> = _optimisticPollVotes.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<GroupMessage>>>(emptyMap()) // groupId -> messages
    
    private val _messageReactions = MutableStateFlow<Map<String, List<MessageReaction>>>(emptyMap())
    val messageReactions: StateFlow<Map<String, List<MessageReaction>>> = _messageReactions.asStateFlow()

    /**
     * Returns a real-time stream of messages for a specific group.
     * Manages internal state to support pagination.
     */
    fun getMessages(groupId: String): Flow<List<GroupMessage>> {
        if (!_messages.value.containsKey(groupId)) {
            viewModelScope.launch {
                repo.getMessages(groupId).collect { newList ->
                    _messages.update { it + (groupId to newList) }
                }
            }
        }
        return _messages.map { it[groupId] ?: emptyList() }
    }

    /**
     * Loads the next page of messages for a group.
     */
    fun loadMoreMessages(groupId: String) {
        val currentList = _messages.value[groupId] ?: return
        val lastTimestamp = currentList.lastOrNull()?.createdAt ?: return
        
        viewModelScope.launch {
            // Note: In a real-world app, you might want to merge these flows or use a different strategy
            // for paginating real-time data. For now, we'll fetch the next page once.
            repo.getMessages(groupId, startAfterTimestamp = lastTimestamp).first().let { nextBatch ->
                if (nextBatch.isNotEmpty()) {
                    _messages.update { it + (groupId to (currentList + nextBatch)) }
                }
            }
        }
    }

    /**
     * Sends a message to a group, optionally including media attachments and replies.
     * Handles media uploads to Firebase Storage before sending the message document.
     */
    fun sendMessage(
        groupId: String,
        text: String,
        replyTo: GroupMessage? = null,
        attachments: List<Uri> = emptyList(),
        attachmentModels: List<GroupAttachment> = emptyList()
    ) {
        val uid = currentUserId ?: return
        viewModelScope.launch {
            try {
                // Upload all new attachments in parallel
                val uploadedAttachmentsFromUri = attachments.map { uri ->
                    val url = storageRepo.uploadChatMedia(groupId, uri)
                    
                    val type = when {
                        uri.toString().contains("video", ignoreCase = true) || 
                        uri.path?.contains(".mp4", ignoreCase = true) == true -> GroupAttachmentType.video
                        uri.toString().contains("pdf", ignoreCase = true) || 
                        uri.path?.contains(".pdf", ignoreCase = true) == true -> GroupAttachmentType.file
                        else -> GroupAttachmentType.image
                    }
                    
                    GroupAttachment(
                        url = url,
                        type = type,
                        fileName = uri.lastPathSegment
                    )
                }

                val allAttachments = uploadedAttachmentsFromUri + attachmentModels

                val message = GroupMessage(
                    groupId = groupId,
                    senderId = uid,
                    senderName = auth.currentUser?.displayName ?: "KDYM Member",
                    text = text,
                    attachments = allAttachments,
                    replyToMessageId = replyTo?.id,
                    replyToSenderName = replyTo?.senderName,
                    replyToText = replyTo?.text,
                    createdAt = null // Trigger @ServerTimestamp
                )
                repo.sendMessage(groupId, message)
            } catch (e: Exception) {
                // TODO: Handle send failure (e.g., UI feedback)
                println("Failed to send message: ${e.message}")
            }
        }
    }

    init {
        observeUserGroups()
        observeUserRequests()
    }

    /**
     * Set up listeners for join requests made by the current user.
     */
    private fun observeUserRequests() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { uid ->
                repo.getJoinRequestsForUser(uid).collect { requests ->
                    _userRequests.value = requests
                }
            }
        }
        
        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid ?: return@addAuthStateListener
            viewModelScope.launch {
                repo.getJoinRequestsForUser(uid).collect { requests ->
                    _userRequests.value = requests
                }
            }
        }
    }

    /**
     * Sets up listeners to keep the user's groups list up-to-date.
     */
    private fun observeUserGroups() {
        viewModelScope.launch {
            auth.currentUser?.uid?.let { uid ->
                userRepository.getUser(uid).flatMapLatest { user ->
                    if (user?.isAdmin == true) {
                        repo.getAllGroups()
                    } else {
                        repo.getGroupsForUser(uid, user?.tribeId)
                    }
                }.map { list ->
                    list.sortedByDescending { it.lastMessageAt ?: it.createdAt }
                }.collect { list ->
                    _groups.value = list
                }
            }
        }

        // Re-observe if authentication state changes
        auth.addAuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid ?: return@addAuthStateListener
            viewModelScope.launch {
                userRepository.getUser(uid).flatMapLatest { user ->
                    if (user?.isAdmin == true) {
                        repo.getAllGroups()
                    } else {
                        repo.getGroupsForUser(uid, user?.tribeId)
                    }
                }.map { list ->
                    list.sortedByDescending { it.lastMessageAt ?: it.createdAt }
                }.collect { list ->
                    _groups.value = list
                }
            }
        }
    }

    /**
     * Adds or toggles a reaction on a specific message. Updates UI optimistically.
     */
    fun reactToMessage(groupId: String, messageId: String, emoji: String) {
        val uid = currentUserId ?: return

        val currentOptimistic = _optimisticReactions.value[messageId]
        val newEmoji = if (currentOptimistic == emoji) null else emoji

        _optimisticReactions.update { it + (messageId to newEmoji) }

        viewModelScope.launch {
            try {
                repo.addReaction(groupId, messageId, uid, emoji)

                delay(500)

                _optimisticReactions.update { it - messageId }

            } catch (e: Exception) {
                _optimisticReactions.update { it + (messageId to currentOptimistic) }
                println("Failed to add reaction: ${e.message}")
            }
        }
    }

    /**
     * Casts or toggles a vote in a poll. Updates UI optimistically.
     */
    fun voteInPoll(groupId: String, pollId: String, optionId: String) {
        val uid = currentUserId ?: return

        val currentOptimistic = _optimisticPollVotes.value[pollId]
        val newOption = if (currentOptimistic == optionId) null else optionId

        _optimisticPollVotes.update { it + (pollId to newOption) }

        viewModelScope.launch {
            try {
                repo.voteInPoll(groupId, pollId, optionId, uid)
            } catch (e: Exception) {
                _optimisticPollVotes.update { it + (pollId to currentOptimistic) }
                println("Failed to vote in poll: ${e.message}")
            }
        }
    }

    /**
     * Submits a request to join a private group.
     */
    fun requestJoinGroup(group: AppGroup) {
        if (!group.isPublic) {
            println("Cannot join private group via request.")
            return
        }
        val uid = currentUserId ?: return
        val userName = auth.currentUser?.displayName ?: "KDYM Member"
        val userEmail = auth.currentUser?.email ?: ""
        
        viewModelScope.launch {
            try {
                val request = GroupJoinRequest(
                    groupId = group.id,
                    groupName = group.name,
                    requesterId = uid,
                    requesterName = userName,
                    requesterEmail = userEmail,
                    status = "pending",
                    createdAt = com.google.firebase.Timestamp.now(),
                    updatedAt = com.google.firebase.Timestamp.now()
                )
                repo.requestJoinGroup(request)
            } catch (e: Exception) {
                println("Failed to request join: ${e.message}")
            }
        }
    }

    private val reactionJobs = mutableMapOf<String, kotlinx.coroutines.Job>()

    /**
     * Fetches detailed reactions for a specific message and observes them.
     */
    fun fetchMessageReactions(groupId: String, messageId: String) {
        if (reactionJobs.containsKey(messageId)) return

        reactionJobs[messageId] = viewModelScope.launch {
            repo.getMessageReactions(groupId, messageId).collect { reactions ->
                _messageReactions.update { it + (messageId to reactions) }
            }
        }
    }
}
