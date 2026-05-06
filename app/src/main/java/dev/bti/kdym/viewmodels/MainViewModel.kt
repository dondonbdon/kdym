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

import dev.bti.kdym.data.models.FeedComment
import dev.bti.kdym.data.models.FeedPost
import dev.bti.kdym.data.models.KDYMEvent
import dev.bti.kdym.data.repositories.*

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val appConfigRepository: AppConfigRepository = AppConfigRepository(),
    private val feedRepository: FeedRepository = FeedRepository(),
    private val eventRepository: EventRepository = EventRepository()
) : ViewModel() {

    private val _firebaseUser = authRepository.currentUser
    val firebaseUser: StateFlow<FirebaseUser?> = _firebaseUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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
        viewModelScope.launch {
            try {
                val fbUser = authRepository.signIn(email, password)
                onResult(fbUser != null)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun signUp(name: String, email: String, password: String, onResult: (Boolean) -> Unit) {
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
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    // Feed Actions
    fun toggleReaction(postId: String, reaction: String) {
        val uid = firebaseUser.value?.uid ?: return
        viewModelScope.launch {
            feedRepository.toggleReaction(postId, uid, reaction)
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
        val uid = firebaseUser.value?.uid ?: return flowOf(null)
        return feedRepository.getUserReaction(postId, uid)
    }

    fun updateNotificationPreferences(prefs: NotificationPreferences) {
        val uid = firebaseUser.value?.uid ?: return

        viewModelScope.launch {
            userRepository.updateNotificationPreferences(uid, prefs)
        }
    }
}
