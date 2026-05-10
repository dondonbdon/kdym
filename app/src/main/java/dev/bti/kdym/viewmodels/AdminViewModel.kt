
package dev.bti.kdym.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import dev.bti.kdym.data.models.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dev.bti.kdym.data.repositories.RepositoryProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModel : ViewModel() {

    private val appConfigRepository = RepositoryProvider.appConfigRepository
    private val userRepository = RepositoryProvider.userRepository
    private val tribeRepository = RepositoryProvider.tribeRepository
    private val feedRepository = RepositoryProvider.feedRepository
    private val announcementRepository = RepositoryProvider.announcementRepository
    private val groupRepository = RepositoryProvider.groupRepository

    // --- STATE STREAMS ---

    val appConfig: StateFlow<AppConfig?> =
        appConfigRepository.getAppConfig()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<AppUser>> =
        userRepository.getAllUsers()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tribes: StateFlow<List<Tribe>> =
        appConfig.flatMapLatest { config ->
            val campId = config?.activeCampId ?: "camp_2026"
            tribeRepository.getTribesForCamp(campId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tribeEvents: StateFlow<List<TribeWarEvent>> =
        appConfig.flatMapLatest { config ->
            val campId = config?.activeCampId ?: "camp_2026"
            tribeRepository.getTribeWarEvents(campId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups: StateFlow<List<AppGroup>> =
        groupRepository.getAllGroups()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- OPERATIONS ---

    fun updateAppConfig(config: AppConfig) = viewModelScope.launch {
        try {
            appConfigRepository.updateAppConfig(config)
        } catch (e: Exception) {
            Log.e("AdminViewModel", "updateAppConfig failed", e)
        }
    }

    fun createTribe(name: String, subtitle: String, colorHex: String, iconName: String) =
        viewModelScope.launch {
            try {
                val campId = appConfig.value?.activeCampId ?: "camp_2026"

                tribeRepository.createTribe(
                    Tribe(
                        name = name,
                        subtitle = subtitle,
                        colorHex = colorHex,
                        iconName = iconName,
                        campId = campId,
                        isActive = true,
                        createdAt = Timestamp.now()
                    )
                )
            } catch (e: Exception) {
                Log.e("AdminViewModel", "createTribe failed", e)
            }
        }

    fun updateTribe(tribe: Tribe) = viewModelScope.launch {
        try {
            tribeRepository.updateTribe(tribe)
        } catch (e: Exception) {
            Log.e("AdminViewModel", "updateTribe failed", e)
        }
    }

    fun addPointsToTribe(
        tribe: Tribe,
        points: Int,
        reason: String,
        eventId: String? = null,
        eventTitle: String? = null
    ) = viewModelScope.launch {
        try {
            val campId = appConfig.value?.activeCampId ?: "camp_2026"

            val entry = ScoreEntry(
                campId = campId,
                tribeId = tribe.id,
                tribeName = tribe.name,
                points = points,
                reason = reason,
                eventId = eventId,
                eventTitle = eventTitle,
                createdAt = Timestamp.now()
            )

            tribeRepository.addScoreEntry(entry)

        } catch (e: Exception) {
            Log.e("AdminViewModel", "addPointsToTribe failed", e)
        }
    }

    fun createTribeEvent(title: String, description: String, maxPoints: Int) =
        viewModelScope.launch {
            try {
                val campId = appConfig.value?.activeCampId ?: "camp_2026"

                tribeRepository.createTribeWarEvent(
                    TribeWarEvent(
                        campId = campId,
                        title = title,
                        description = description,
                        maxPoints = maxPoints,
                        status = "upcoming",
                        createdAt = Timestamp.now()
                    )
                )
            } catch (e: Exception) {
                Log.e("AdminViewModel", "createTribeEvent failed", e)
            }
        }

    fun finalizeScoreboard() = viewModelScope.launch {
        val currentTribes = tribes.value
        if (currentTribes.isEmpty()) return@launch

        val summary = currentTribes
            .take(3)
            .joinToString("\n") { "${it.rank}. ${it.name} - ${it.totalPoints} pts" }

        feedRepository.createPost(
            FeedPost(
                title = "TRIBE WARS STANDINGS",
                body = "Current standings:\n\n$summary",
                priority = FeedPostPriority.important,
                createdByName = "COMMAND CENTER",
                createdAt = Timestamp.now()
            )
        )
    }

    fun sendAnnouncement(title: String, message: String, priority: AnnouncementPriority) =
        viewModelScope.launch {
            try {
                announcementRepository.createAnnouncement(
                    Announcement(
                        title = title,
                        body = message,
                        priority = priority,
                        createdAt = Timestamp.now(),
                        isPublished = true
                    )
                )
            } catch (e: Exception) {
                Log.e("AdminViewModel", "sendAnnouncement failed", e)
            }
        }

    fun updateAnnouncement(announcement: Announcement) = viewModelScope.launch {
        announcementRepository.updateAnnouncement(announcement)
    }

    fun deleteAnnouncement(id: String) = viewModelScope.launch {
        announcementRepository.deleteAnnouncement(id)
    }

    fun createGroup(group: AppGroup) = viewModelScope.launch {
        groupRepository.createGroup(group)
    }

    fun updateGroup(group: AppGroup) = viewModelScope.launch {
        groupRepository.updateGroup(group)
    }

    fun getPoll(pollId: String): Flow<Poll?> {
        return groupRepository.getPoll(pollId)
    }

    fun voteInPoll(pollId: String, optionId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            groupRepository.voteInPoll(pollId, optionId, uid)
        }
    }

    fun createPoll(groupId: String, question: String, options: List<String>, allowMultiple: Boolean, showOnHome: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val poll = Poll(
                question = question,
                options = options.map { dev.bti.kdym.data.models.PollOption(id = UUID.randomUUID().toString(), text = it) },
                allowMultipleVotes = allowMultiple,
                showOnHome = showOnHome,
                createdBy = uid,
                groupIds = listOf(groupId)
            )
            val pollId = groupRepository.createPoll(poll)

            // Send message to group
            val message = GroupMessage(
                groupId = groupId,
                senderId = uid,
                senderName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Admin",
                text = "Poll: $question",
                pollId = pollId,
                pollQuestion = question,
                createdAt = com.google.firebase.Timestamp.now()
            )
            groupRepository.sendMessage(groupId, message)
        }
    }

    fun approveUser(uid: String, role: UserRole) = viewModelScope.launch {
        userRepository.updateUserRole(uid, role, AccessStatus.approved)
    }

    fun rejectUser(uid: String) = viewModelScope.launch {
        userRepository.updateUserRole(uid, UserRole.publicUser, AccessStatus.rejected)
    }
}
