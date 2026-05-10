package dev.bti.kdym.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import dev.bti.kdym.data.models.*
import dev.bti.kdym.data.repositories.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UIState(
    val isLoading: Boolean = false,
    val feedbackMessage: String? = null,
    val isError: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val appConfigRepository: AppConfigRepository = AppConfigRepository(),
    private val feedRepository: FeedRepository = FeedRepository(),
    private val eventRepository: EventRepository = EventRepository(),
    private val announcementRepository: AnnouncementRepository = AnnouncementRepository()
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

    val allEvents: StateFlow<List<KDYMEvent>> = eventRepository.getAllPublishedEvents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<Announcement>> = announcementRepository.getPublishedAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(UIState())
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun showFeedback(message: String, isError: Boolean = false) {
        _uiState.update { it.copy(feedbackMessage = message, isError = isError) }
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            clearFeedback()
        }
    }

    fun clearFeedback() {
        _uiState.update { it.copy(feedbackMessage = null, isError = false) }
    }

    init {
        viewModelScope.launch {
            firebaseUser.collect { fbUser ->
                fbUser?.let {
                    userRepository.updateLastActive(it.uid)
                }
            }
        }
    }

    // Auth Actions
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

    fun signUp(name: String, email: String, password: String, onResult: (Boolean) -> Unit) {
        _authLoading.value = true
        setLoading(true)
        _authError.value = null
        viewModelScope.launch {
            try {
                val fbUser = authRepository.signUp(email, password)
                if (fbUser != null) {
                    val newUser = AppUser(
                        uid = fbUser.uid,
                        displayName = name,
                        email = email,
                        createdAt = Timestamp.now()
                    )
                    userRepository.createUser(newUser)
                    _authLoading.value = false
                    setLoading(false)
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

    // Feed Actions
    fun toggleReaction(postId: String, reaction: String) {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            setLoading(true)
            try {
                feedRepository.toggleReaction(postId, uid, reaction)
            } catch (e: Exception) {
                showFeedback("Failed to update reaction", isError = true)
            } finally {
                setLoading(false)
            }
        }
    }

    fun addComment(postId: String, body: String) {
        val currentUser = user.value ?: return
        val comment = FeedComment(
            postId = postId,
            body = body,
            createdBy = currentUser.uid,
            createdByName = currentUser.displayName,
            createdAt = Timestamp.now()
        )
        viewModelScope.launch {
            feedRepository.addComment(postId, comment)
        }
    }

    fun getComments(postId: String): Flow<List<FeedComment>> = feedRepository.getComments(postId)

    fun getUserReaction(postId: String): Flow<String?> {
        return firebaseUser.flatMapLatest { user ->
            val uid = user?.uid ?: return@flatMapLatest flowOf(null)
            feedRepository.getUserReaction(postId, uid)
        }
    }

    fun updateProfile(displayName: String, phoneNumber: String, bio: String) {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            setLoading(true)
            try {
                userRepository.updateUser(uid, mapOf(
                    "displayName" to displayName,
                    "phoneNumber" to phoneNumber,
                    "bio" to bio
                ))
                showFeedback("Profile updated successfully")
            } catch (e: Exception) {
                showFeedback("Failed to update profile", isError = true)
            } finally {
                setLoading(false)
            }
        }
    }

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
}
