package dev.bti.kdym.data.repositories

import dev.bti.kdym.data.repositories.*

object RepositoryProvider {

    val appConfigRepository by lazy { AppConfigRepository() }
    val userRepository by lazy { UserRepository() }
    val tribeRepository by lazy { TribeRepository() }
    val feedRepository by lazy { FeedRepository() }
    val announcementRepository by lazy { AnnouncementRepository() }
    val groupRepository by lazy { GroupRepository() }
}