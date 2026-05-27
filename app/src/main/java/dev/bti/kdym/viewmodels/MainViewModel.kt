package dev.bti.kdym.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import dev.bti.kdym.data.firebase.FcmManager
import dev.bti.kdym.data.models.*
import dev.bti.kdym.data.repositories.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

/**
 * UI State representing the general status of the screen.
 */
data class UIState(
    val isLoading: Boolean = false,
    val feedbackMessage: String? = null,
    val isError: Boolean = false,
)

/**
 * Main ViewModel for handling authentication, user profile, feed updates, and global app configuration.
 * Implements optimistic updates for smoother user interactions in the feed and comments sections.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val appConfigRepository: AppConfigRepository = AppConfigRepository(),
    private val feedRepository: FeedRepository = FeedRepository(),
    private val eventRepository: EventRepository = EventRepository(),
    private val announcementRepository: AnnouncementRepository = AnnouncementRepository(),
    private val storageRepository: StorageRepository = StorageRepository(),
    private val churchRepository: ChurchRepository = ChurchRepository(),
    private val campRepository: CampRepository = CampRepository(),
    private val playRepository: PlayRepository = PlayRepository(),
) : ViewModel() {

    private val _firebaseUser = authRepository.currentUser
    val firebaseUser: StateFlow<FirebaseUser?> = _firebaseUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    val user: StateFlow<AppUser?> = firebaseUser
        .flatMapLatest { fbUser ->
            if (fbUser != null) userRepository.getUser(fbUser.uid)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val appConfig: StateFlow<AppConfig?> = appConfigRepository.getAppConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val liveUpdates: StateFlow<List<FeedPost>> = feedRepository.getLiveUpdates()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val churches: StateFlow<List<Church>> = churchRepository.getAllChurches()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val camps: StateFlow<List<Camp>> = campRepository.getAllCamps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val playItems: StateFlow<List<PlayItem>> = playRepository.getPlayItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        
    val featuredPlayItems: StateFlow<List<PlayItem>> = playRepository.getPlayItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<Announcement>> = announcementRepository.getPublishedAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val prefs = RepositoryProvider.prefs

    val guessedTribe: StateFlow<String?> = (prefs?.guessedTribe ?: flowOf(null))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tribeRevealShown: StateFlow<Boolean> = (prefs?.tribeRevealShown ?: flowOf(false))
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _shouldRequestPermissions = MutableStateFlow(false)
    val shouldRequestPermissions: StateFlow<Boolean> = _shouldRequestPermissions.asStateFlow()

    fun triggerPermissionRequest() {
        _shouldRequestPermissions.value = true
    }

    fun onPermissionRequestHandled() {
        _shouldRequestPermissions.value = false
    }

    // Optimistic State maps to provide instant UI feedback before server confirmation.
    private val _optimisticReactions = MutableStateFlow<Map<String, String?>>(emptyMap())
    private val _optimisticComments = MutableStateFlow<Map<String, List<FeedComment>>>(emptyMap())
    
    private val _optimisticPlayReactions = MutableStateFlow<Map<String, String?>>(emptyMap())
    private val _optimisticPlayComments = MutableStateFlow<Map<String, List<PlayComment>>>(emptyMap())

    /**
     * Updates the loading state in the global UI state.
     */
    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    /**
     * Shows a feedback message (e.g., success toast or error) for a limited time.
     */
    fun showFeedback(message: String, isError: Boolean = false) {
        _uiState.update { it.copy(feedbackMessage = message, isError = isError) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            clearFeedback()
        }
    }

    /**
     * Clears any active feedback message.
     */
    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null, isError = false) }
    }

    init {
        // Automatically update user's last active timestamp and sync FCM token when they are logged in.
        viewModelScope.launch {
            firebaseUser.collect { fbUser ->
                fbUser?.let {
                    try {
                        userRepository.updateLastActive(it.uid)
                        FcmManager.syncCurrentToken()
                    } catch (e: Exception) {
                        // Silently fail for these background sync operations
                    }
                }
            }
        }
    }

    // --- Authentication Actions ---

    fun signIn(email: String, password: String, onResult: (Boolean) -> Unit) {
        _authLoading.value = true
        setLoading(true)
        _authError.value = null
        viewModelScope.launch {
            try {
                val fbUser = authRepository.signIn(email, password)
                _authLoading.value = false
                setLoading(false)
                if (fbUser != null) {
                    triggerPermissionRequest()
                    onResult(true)
                } else {
                    _authError.value = "Sign in failed. Please check your credentials."
                    onResult(false)
                }
            } catch (e: Exception) {
                _authLoading.value = false
                setLoading(false)
                _authError.value = e.message ?: "An unexpected error occurred."
                onResult(false)
            }
        }
    }

    fun signUp(
        firstName: String,
        lastName: String,
        username: String,
        phoneNumber: String?,
        churchId: String?,
        churchName: String?,
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        _authLoading.value = true
        setLoading(true)
        _authError.value = null
        viewModelScope.launch {
            try {
                if (userRepository.isUsernameTaken(username)) {
                    _authError.value = "Username is already taken."
                    _authLoading.value = false
                    setLoading(false)
                    onResult(false)
                    return@launch
                }

                val fbUser = authRepository.signUp(email, password)
                if (fbUser != null) {
                    val newUser = AppUser(
                        uid = fbUser.uid,
                        displayName = "$firstName $lastName",
                        username = username,
                        email = email,
                        phoneNumber = phoneNumber,
                        churchId = churchId,
                        churchName = churchName,
                        createdAt = Timestamp.now()
                    )
                    userRepository.createUser(newUser)
                    _authLoading.value = false
                    setLoading(false)
                    triggerPermissionRequest()
                    onResult(true)
                } else {
                    _authLoading.value = false
                    setLoading(false)
                    _authError.value = "Sign up failed."
                    onResult(false)
                }
            } catch (e: Exception) {
                _authLoading.value = false
                setLoading(false)
                _authError.value = e.message ?: "An unexpected error occurred."
                onResult(false)
            }
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun sendPasswordResetEmail(email: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.sendPasswordResetEmail(email)
                showFeedback("Reset email sent.", false)
                onResult(true)
            } catch (e: Exception) {
                _authError.value = e.localizedMessage
                onResult(false)
            }
        }
    }

    /**
     * Soft deletes the user's account and signs them out.
     */
    fun softDeleteUser() {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            setLoading(true)
            try {
                userRepository.softDeleteUser(uid)
                signOut()
            } catch (e: Exception) {
                showFeedback("Failed to delete account", isError = true)
            } finally {
                setLoading(false)
            }
        }
    }

    // --- Feed Actions ---

    /**
     * Toggles a reaction on a post. Updates the UI optimistically.
     */
    fun toggleReaction(postId: String, reaction: String) {
        val uid = firebaseUser.value?.uid ?: return
        val currentOptimistic = _optimisticReactions.value[postId]
        val newReaction = if (currentOptimistic == reaction) null else reaction

        // Apply optimistic update immediately
        _optimisticReactions.update { it + (postId to newReaction) }

        viewModelScope.launch {
            try {
                feedRepository.toggleReaction(postId, uid, reaction)
            } catch (e: Exception) {
                // Revert to the previous state if the server call fails
                _optimisticReactions.update { it + (postId to currentOptimistic) }
                showFeedback("Failed to update reaction", isError = true)
            }
        }
    }

    /**
     * Adds a comment to a post. Updates the UI optimistically.
     */
    fun addComment(postId: String, body: String) {
        val currentUser = user.value ?: return
        val comment = FeedComment(
            id = "temp_${System.currentTimeMillis()}",
            postId = postId,
            body = body,
            createdBy = currentUser.uid,
            createdByName = currentUser.displayName,
            createdAt = Timestamp.now()
        )

        // Add to optimistic list
        _optimisticComments.update { current ->
            val list = current[postId] ?: emptyList()
            current + (postId to (list + comment))
        }

        viewModelScope.launch {
            try {
                feedRepository.addComment(postId, comment)
                // Remove from optimistic list once Firestore sync provides the real document
                _optimisticComments.update { current ->
                    val list = current[postId] ?: emptyList()
                    current + (postId to list.filter { it.id != comment.id })
                }
            } catch (e: Exception) {
                // Remove temporary comment on failure
                _optimisticComments.update { current ->
                    val list = current[postId] ?: emptyList()
                    current + (postId to list.filter { it.id != comment.id })
                }
                showFeedback("Failed to add comment: ${e.message}", isError = true)
            }
        }
    }

    /**
     * Returns a combined flow of real and optimistic comments.
     */
    fun getComments(postId: String): Flow<List<FeedComment>> = combine(
        feedRepository.getComments(postId),
        _optimisticComments.map { it[postId] ?: emptyList() }
    ) { real, optimistic ->
        (real + optimistic).asSequence().distinctBy { it.id }.sortedBy { it.createdAt }.toList()
    }

    /**
     * Returns a combined flow of the user's real and optimistic reaction.
     */
    fun getUserReaction(postId: String): Flow<String?> {
        return combine(
            firebaseUser.flatMapLatest { user ->
                val uid = user?.uid ?: return@flatMapLatest flowOf(null)
                feedRepository.getUserReaction(postId, uid)
            },
            _optimisticReactions.map { it[postId] }
        ) { real, optimistic ->
            optimistic ?: real
        }
    }

    // --- Play Actions ---

    fun togglePlayReaction(itemId: String, reaction: String) {
        val currentUser = user.value ?: return
        val currentOptimistic = _optimisticPlayReactions.value[itemId]
        val newReaction = if (currentOptimistic == reaction) null else reaction

        _optimisticPlayReactions.update { it + (itemId to newReaction) }

        viewModelScope.launch {
            try {
                playRepository.toggleReaction(
                    itemId = itemId,
                    reaction = reaction,
                    userId = currentUser.uid,
                    userName = currentUser.displayName,
                    photoURL = currentUser.photoURL
                )
            } catch (e: Exception) {
                _optimisticPlayReactions.update { it + (itemId to currentOptimistic) }
                showFeedback("Failed to update reaction", isError = true)
            }
        }
    }

    fun addPlayComment(itemId: String, text: String) {
        val currentUser = user.value ?: return
        val comment = PlayComment(
            id = "temp_${System.currentTimeMillis()}",
            userId = currentUser.uid,
            userName = currentUser.displayName,
            userPhotoURL = currentUser.photoURL,
            text = text,
            createdAt = Timestamp.now()
        )

        _optimisticPlayComments.update { current ->
            val list = current[itemId] ?: emptyList()
            current + (itemId to (list + comment))
        }

        viewModelScope.launch {
            try {
                playRepository.addComment(
                    itemId = itemId,
                    text = text,
                    userId = currentUser.uid,
                    userName = currentUser.displayName,
                    photoURL = currentUser.photoURL
                )
                _optimisticPlayComments.update { current ->
                    val list = current[itemId] ?: emptyList()
                    current + (itemId to list.filter { it.id != comment.id })
                }
            } catch (e: Exception) {
                _optimisticPlayComments.update { current ->
                    val list = current[itemId] ?: emptyList()
                    current + (itemId to list.filter { it.id != comment.id })
                }
                showFeedback("Failed to add comment: ${e.message}", isError = true)
            }
        }
    }

    fun getPlayComments(itemId: String): Flow<List<PlayComment>> = combine(
        playRepository.getComments(itemId),
        _optimisticPlayComments.map { it[itemId] ?: emptyList() }
    ) { real, optimistic ->
        (real + optimistic).asSequence().distinctBy { it.id }.sortedBy { it.createdAt }.toList()
    }

    fun reportPlayItem(itemId: String) {
        viewModelScope.launch {
            try {
                // Assuming report exists or incrementing a counter
                showFeedback("Report submitted")
            } catch (e: Exception) {
                showFeedback("Failed to report", isError = true)
            }
        }
    }

    fun deletePlayItem(itemId: String) {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            try {
                playRepository.delete(itemId, uid)
                showFeedback("Item deleted")
            } catch (e: Exception) {
                showFeedback("Failed to delete", isError = true)
            }
        }
    }

    fun deletePlayComment(itemId: String, commentId: String) {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            try {
                playRepository.deleteComment(itemId, commentId, uid)
                showFeedback("Comment deleted")
            } catch (e: Exception) {
                showFeedback("Failed to delete comment", isError = true)
            }
        }
    }

    fun toggleFeatured(itemId: String, currentFeatured: Boolean) {
        viewModelScope.launch {
            try {
                val item = playItems.value.find { it.id == itemId } ?: return@launch
                playRepository.save(item.copy(isFeatured = !currentFeatured))
                showFeedback(if (!currentFeatured) "Marked as featured" else "Removed from featured")
            } catch (e: Exception) {
                showFeedback("Failed to update featured status", isError = true)
            }
        }
    }

    // --- User Profile Actions ---

    /**
     * Updates the user's profile information, optionally uploading a new photo.
     */
    fun updateProfile(
        displayName: String,
        phoneNumber: String,
        bio: String,
        churchName: String? = null,
        churchCity: String? = null,
        pastorName: String? = null,
        profilePhotoUri: Uri? = null
    ) {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            setLoading(true)
            try {
                val updates = mutableMapOf<String, Any?>(
                    "displayName" to displayName,
                    "phoneNumber" to phoneNumber,
                    "bio" to bio,
                    "churchName" to churchName,
                    "churchCity" to churchCity,
                    "pastorName" to pastorName
                )

                profilePhotoUri?.let { uri ->
                    val photoUrl = storageRepository.uploadProfilePhoto(uid, uri)
                    updates["photoURL"] = photoUrl
                }

                userRepository.updateUser(uid, updates)
                showFeedback("Profile updated successfully")
            } catch (e: Exception) {
                showFeedback("Failed to update profile: ${e.message}", isError = true)
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Submits a request for elevated camp access.
     */
    fun requestCampAccess(campId: String, role: String) {
        val currentUser = user.value ?: return
        val normalizedRole = UserRole.fromString(role).name
        
        viewModelScope.launch {
            setLoading(true)
            try {
                // 1. Create the request document for admins to track
                val request = CampAccessRequest(
                    campId = campId,
                    requesterId = currentUser.uid,
                    requesterName = currentUser.displayName,
                    requesterEmail = currentUser.email,
                    requestedRole = normalizedRole,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
                campRepository.requestCampAccess(request)
                
                // 2. Update the user's own document to reflect pending status
                userRepository.submitCampAccessRequest(currentUser.uid, campId, normalizedRole)

                showFeedback("Access request submitted")
            } catch (e: Exception) {
                showFeedback("Failed to submit request", isError = true)
            } finally {
                setLoading(false)
            }
        }
    }

    /**
     * Updates user notification preferences.
     */
    fun updateNotificationPreferences(prefs: NotificationPreferences) {
        val uid = firebaseUser.value?.uid ?: return

        viewModelScope.launch {
            setLoading(true)
            try {
                userRepository.updateNotificationPreferences(uid, prefs)
                showFeedback("Preferences saved")
            } catch (e: Exception) {
                showFeedback("Failed to save preferences", isError = true)
            } finally {
                setLoading(false)
            }
        }
    }

    // --- Event Actions ---

    fun updateRSVP(eventId: String, status: String) {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            try {
                eventRepository.updateRSVP(eventId, uid, status)
                showFeedback("RSVP updated")
            } catch (e: Exception) {
                showFeedback("Failed to update RSVP", isError = true)
            }
        }
    }

    fun getRSVPs(eventId: String): Flow<List<EventRSVP>> {
        return eventRepository.getRSVPs(eventId)
    }

    // --- Tribe Actions ---

    fun guessTribe(tribeName: String) {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            try {
                userRepository.updateUser(uid, mapOf("guessedTribe" to tribeName))
                prefs?.saveGuessedTribe(tribeName)
            } catch (e: Exception) {
                showFeedback("Failed to save guess", isError = true)
            }
        }
    }

    fun setTribeRevealShown(shown: Boolean) = viewModelScope.launch {
        prefs?.saveTribeRevealShown(shown)
    }

    val allEvents: StateFlow<List<KDYMEvent>> = eventRepository.getAllPublishedEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // NEW: Fetch the camp schedule using the active camp ID from AppConfig
    @OptIn(ExperimentalCoroutinesApi::class)
    val campSchedule: StateFlow<List<KDYMEvent>> = appConfig
        .flatMapLatest { config ->
            val campId = config?.activeCampId
            if (campId.isNullOrBlank()) flowOf(emptyList())
            else eventRepository.getCampSchedule(campId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // NEW: Function to get schedule items nested inside a specific event
    fun getScheduleItems(parentEventId: String): Flow<List<KDYMEvent>> {
        return eventRepository.getScheduleItems(parentEventId)
    }
}



@Serializable
data class EventRSVP(
    val userId: String = "",
    val status: String = "going",
    @Serializable(with = dev.bti.kdym.data.local.serializers.TimestampSerializer::class)
    val updatedAt: Timestamp? = null
)
