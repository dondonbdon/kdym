package dev.bti.kdym.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.bti.kdym.data.models.*
import dev.bti.kdym.data.repositories.AppConfigRepository
import dev.bti.kdym.data.repositories.TribeRepository
import dev.bti.kdym.data.repositories.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModel(
    private val appConfigRepository: AppConfigRepository = AppConfigRepository(),
    private val userRepository: UserRepository = UserRepository(),
    private val tribeRepository: TribeRepository = TribeRepository()
) : ViewModel() {

    val appConfig: StateFlow<AppConfig?> = appConfigRepository.getAppConfig()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<AppUser>> = userRepository.getAllUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tribes: StateFlow<List<Tribe>> = appConfig.flatMapLatest { config ->
        val campId = config?.activeCampId ?: "camp_2026"
        tribeRepository.getTribesForCamp(campId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateAppConfig(config: AppConfig) {
        viewModelScope.launch {
            appConfigRepository.updateAppConfig(config)
        }
    }

    fun createTribe(name: String, subtitle: String, colorHex: String, iconName: String) {
        viewModelScope.launch {
            val campId = appConfig.value?.activeCampId ?: "camp_2026"
            val newTribe = Tribe(
                name = name,
                subtitle = subtitle,
                colorHex = colorHex,
                iconName = iconName,
                campId = campId,
                isActive = true
            )
            tribeRepository.createTribe(newTribe)
        }
    }

    fun approveUser(uid: String, role: UserRole) {
        viewModelScope.launch {
            userRepository.updateUserRole(uid, role, AccessStatus.approved)
        }
    }

    fun rejectUser(uid: String) {
        viewModelScope.launch {
            userRepository.updateUserRole(uid, UserRole.publicUser, AccessStatus.rejected)
        }
    }
}
