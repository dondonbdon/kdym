package dev.bti.kdym.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dev.bti.kdym.data.models.*
import dev.bti.kdym.data.repositories.RepositoryProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * ViewModel for administrative operations.
 * Provides streams for system-wide data (users, tribes, groups) and methods for
 * managing configurations, approvals, and tribe scores.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModel : ViewModel() {

    private val appConfigRepository = RepositoryProvider.appConfigRepository
    private val userRepository = RepositoryProvider.userRepository
    private val tribeRepository = RepositoryProvider.tribeRepository
    private val feedRepository = RepositoryProvider.feedRepository
    private val announcementRepository = RepositoryProvider.announcementRepository
    private val groupRepository = RepositoryProvider.groupRepository
    private val churchRepository = RepositoryProvider.churchRepository
    private val eventRepository = RepositoryProvider.eventRepository
    private val authRepository = RepositoryProvider.authRepository

    // --- STATE STREAMS ---

    /** The currently logged in user. */
    val appUser: StateFlow<AppUser?> = authRepository.currentUser
        .flatMapLatest { fbUser ->
            if (fbUser != null) userRepository.getUser(fbUser.uid)
            else kotlinx.coroutines.flow.flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** The global app configuration. */
    val appConfig: StateFlow<AppConfig?> =
        appConfigRepository.getAppConfig()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** All registered users in the system. */
    val allUsers: StateFlow<List<AppUser>> =
        userRepository.getAllUsers()
            .map { users -> 
                users.filter { it.displayName.isNotBlank() && it.email.isNotBlank() } 
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingUserCount: StateFlow<Int> = allUsers
        .map { users -> users.count { it.accessStatus == "pending" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalUserCount: StateFlow<Int> = allUsers
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val approvedUserCount: StateFlow<Int> = allUsers
        .map { users -> users.count { it.accessStatus == "approved" } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Tribes associated with the currently active camp. */
    val tribes: StateFlow<List<Tribe>> =
        appConfig.flatMapLatest { config ->
            val campId = config?.activeCampId ?: "camp_2026"
            tribeRepository.getTribesForCamp(campId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Competitive events for tribe wars. */
    val tribeEvents: StateFlow<List<TribeWarEvent>> =
        appConfig.flatMapLatest { config ->
            val campId = config?.activeCampId ?: "camp_2026"
            tribeRepository.getTribeWarEvents(campId)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** All chat and community groups. */
    val groups: StateFlow<List<AppGroup>> =
        groupRepository.getAllGroups()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** All main calendar events. */
    val allEvents: StateFlow<List<KDYMEvent>> =
        eventRepository.getAllPublishedEvents()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- OPERATIONS ---

    /** Updates the global app configuration. */
    fun updateAppConfig(config: AppConfig) = viewModelScope.launch {
        try {
            appConfigRepository.updateAppConfig(config)
        } catch (e: Exception) {
            Log.e("AdminViewModel", "updateAppConfig failed", e)
        }
    }

    /** Creates a new tribe in the current camp. */
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

    /** Updates an existing tribe's data. */
    fun updateTribe(tribe: Tribe) = viewModelScope.launch {
        try {
            tribeRepository.updateTribe(tribe)
        } catch (e: Exception) {
            Log.e("AdminViewModel", "updateTribe failed", e)
        }
    }

    /**
     * Awards points to a tribe.
     * @param tribe The target tribe.
     * @param points Number of points to add.
     * @param reason Description of why points were awarded.
     */
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

    /** Creates a new tribe war competition event. */
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
                        createdAt = Timestamp.now(),
                        updatedAt = Timestamp.now(),
                        startDate = Timestamp.now()
                    )
                )
            } catch (e: Exception) {
                Log.e("AdminViewModel", "createTribeEvent failed", e)
            }
        }

    fun updateTribeEvent(event: TribeWarEvent) = viewModelScope.launch {
        try {
            tribeRepository.updateTribeWarEvent(event.copy(updatedAt = Timestamp.now()))
        } catch (e: Exception) {
            Log.e("AdminViewModel", "updateTribeEvent failed", e)
        }
    }

    /** Posts a summary of current tribe standings to the main feed. */
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

    /** Sends a new system announcement. */
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

    /** Updates an existing announcement. */
    fun updateAnnouncement(announcement: Announcement) = viewModelScope.launch {
        announcementRepository.updateAnnouncement(announcement)
    }

    /** Deletes an announcement by ID. */
    fun deleteAnnouncement(id: String) = viewModelScope.launch {
        announcementRepository.deleteAnnouncement(id)
    }

    /** Creates a new group. */
    fun createGroup(group: AppGroup) = viewModelScope.launch {
        groupRepository.createGroup(group)
    }

    /** Updates an existing group. */
    fun updateGroup(group: AppGroup) = viewModelScope.launch {
        groupRepository.updateGroup(group)
    }

    fun getJoinRequestsForGroup(groupId: String): Flow<List<GroupJoinRequest>> {
        return groupRepository.getJoinRequestsForGroup(groupId)
    }

    fun updateJoinRequestStatus(requestId: String, status: String, reviewedBy: String) = viewModelScope.launch {
        groupRepository.updateJoinRequestStatus(requestId, status, reviewedBy)
    }

    /** Fetches a specific poll's real-time data. */
    fun getPoll(groupId: String, pollId: String): Flow<Poll?> {
        return groupRepository.getPoll(groupId, pollId)
    }

    /** Casts a vote in a poll using the currently logged-in user's UID. */
    fun voteInPoll(groupId: String, pollId: String, optionId: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            groupRepository.voteInPoll(groupId, pollId, optionId, uid)
        }
    }

    /**
     * Creates a poll and sends a corresponding message to the group chat.
     */
    fun createPoll(groupId: String, question: String, options: List<String>, allowMultiple: Boolean, showOnHome: Boolean) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val poll = Poll(
                question = question,
                options = options.map { PollOption(id = UUID.randomUUID().toString(), title = it) },
                allowMultipleVotes = allowMultiple,
                showOnHome = showOnHome,
                createdBy = uid,
                groupIds = listOf(groupId)
            )
            val pollId = groupRepository.createPoll(groupId, poll)

            // Send message to group
            val message = GroupMessage(
                groupId = groupId,
                senderId = uid,
                senderName = FirebaseAuth.getInstance().currentUser?.displayName ?: "Admin",
                text = "Poll: $question",
                pollId = pollId,
                pollQuestion = question,
                createdAt = Timestamp.now()
            )
            groupRepository.sendMessage(groupId, message)
        }
    }

    /** Approves a pending user and assigns them a role. */
    fun approveUser(uid: String, role: UserRole) = viewModelScope.launch {
        userRepository.updateUserRole(uid, role, AccessStatus.approved)
    }

    /** Rejects a user's camp access request. */
    fun rejectUser(uid: String) = viewModelScope.launch {
        userRepository.updateUserRole(uid, UserRole.publicUser, AccessStatus.rejected)
    }

    /** Updates specific fields for a user. */
    fun updateUser(uid: String, updates: Map<String, Any?>) = viewModelScope.launch {
        try {
            userRepository.updateUser(uid, updates)
        } catch (e: Exception) {
            Log.e("AdminViewModel", "updateUser failed", e)
        }
    }

    /** Creates a new church record. */
    fun createChurch(church: Church) = viewModelScope.launch {
        churchRepository.createChurch(church)
    }

    /** Updates an existing church record. */
    fun updateChurch(church: Church) = viewModelScope.launch {
        churchRepository.updateChurch(church.id, mapOf(
            "name" to church.name,
            "pastorName" to church.pastorName,
            "address" to church.address,
            "city" to church.city,
            "state" to church.state,
            "memberCount" to church.memberCount
        ))
    }

    /** Assigns a user as a pastor of a church. */
    fun assignPastor(churchId: String, pastorId: String, pastorName: String) = viewModelScope.launch {
        churchRepository.updateChurch(churchId, mapOf(
            "pastorId" to pastorId,
            "pastorName" to pastorName
        ))
        userRepository.updateUser(pastorId, mapOf("role" to UserRole.pastor.name))
    }

    /** Creates a new calendar event. */
    fun createEvent(
        title: String,
        subtitle: String?,
        description: String,
        location: String?,
        registrationURL: String?,
        category: EventCategory,
        startDate: Timestamp,
        endDate: Timestamp?,
        isCampEvent: Boolean,
        isPublished: Boolean,
        parentEventId: String? = null,
        eventKind: String = "event"
    ) = viewModelScope.launch {
        try {
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            val campId = appConfig.value?.activeCampId ?: "camp_2026"

            val event = KDYMEvent(
                title = title,
                subtitle = subtitle,
                description = description,
                location = location,
                registrationURL = registrationURL,
                category = category,
                startDate = startDate,
                endDate = endDate,
                isCampEvent = isCampEvent,
                isPublished = isPublished,
                campId = campId,
                createdBy = uid,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now(),
                parentEventId = parentEventId,
                eventKind = eventKind
            )
            eventRepository.createEvent(event)
        } catch (e: Exception) {
            Log.e("AdminViewModel", "createEvent failed", e)
        }
    }

    fun updateEvent(event: KDYMEvent) = viewModelScope.launch {
        try {
            eventRepository.updateEvent(event.copy(updatedAt = Timestamp.now()))
        } catch (e: Exception) {
            Log.e("AdminViewModel", "updateEvent failed", e)
        }
    }

    fun deleteEvent(eventId: String) = viewModelScope.launch {
        try {
            eventRepository.deleteEvent(eventId)
        } catch (e: Exception) {
            Log.e("AdminViewModel", "deleteEvent failed", e)
        }
    }


}
