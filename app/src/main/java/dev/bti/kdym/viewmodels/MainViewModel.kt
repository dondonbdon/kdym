package dev.bti.kdym.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import dev.bti.kdym.data.models.AppConfig
import dev.bti.kdym.data.models.User
import dev.bti.kdym.data.repositories.AppConfigRepository
import dev.bti.kdym.data.repositories.AuthRepository
import dev.bti.kdym.data.repositories.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val authRepository: AuthRepository = AuthRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val appConfigRepository: AppConfigRepository = AppConfigRepository()
) : ViewModel() {

    private val _firebaseUser = authRepository.currentUser
    val firebaseUser: StateFlow<FirebaseUser?> = _firebaseUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val user: StateFlow<User?> = firebaseUser
        .flatMapLatest { fbUser ->
            if (fbUser != null) userRepository.getUser(fbUser.uid)
            else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val appConfig: StateFlow<AppConfig?> = appConfigRepository.getAppConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            firebaseUser.collect { fbUser ->
                fbUser?.let {
                    userRepository.updateLastActive(it.uid)
                }
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}
