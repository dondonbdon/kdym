package dev.bti.kdym.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.bti.kdym.data.local.*
import dev.bti.kdym.data.repositories.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepository(auth)

    @Provides
    @Singleton
    fun provideUserRepository(prefs: AppPrefs, firestore: FirebaseFirestore): UserRepository = UserRepository(prefs, firestore)

    @Provides
    @Singleton
    fun provideAppConfigRepository(prefs: AppPrefs, firestore: FirebaseFirestore): AppConfigRepository = AppConfigRepository(prefs, firestore)

    @Provides
    @Singleton
    fun provideTribeRepository(tribeDao: TribeDao, firestore: FirebaseFirestore): TribeRepository = TribeRepository(tribeDao, firestore)

    @Provides
    @Singleton
    fun provideFeedRepository(feedPostDao: FeedPostDao, firestore: FirebaseFirestore): FeedRepository = FeedRepository(feedPostDao, firestore)

    @Provides
    @Singleton
    fun provideAnnouncementRepository(firestore: FirebaseFirestore): AnnouncementRepository = AnnouncementRepository(firestore)

    @Provides
    @Singleton
    fun provideGroupRepository(appGroupDao: AppGroupDao, groupMessageDao: GroupMessageDao, firestore: FirebaseFirestore): GroupRepository = GroupRepository(appGroupDao, groupMessageDao, firestore)

    @Provides
    @Singleton
    fun provideEventRepository(firestore: FirebaseFirestore): EventRepository = EventRepository(firestore)

    @Provides
    @Singleton
    fun providePlayRepository(playItemDao: PlayItemDao, firestore: FirebaseFirestore): PlayRepository = PlayRepository(playItemDao, firestore)

    @Provides
    @Singleton
    fun provideStorageRepository(storage: FirebaseStorage): StorageRepository = StorageRepository(storage)

    @Provides
    @Singleton
    fun provideChurchRepository(firestore: FirebaseFirestore): ChurchRepository = ChurchRepository(firestore)

    @Provides
    @Singleton
    fun provideCampRepository(firestore: FirebaseFirestore): CampRepository = CampRepository(firestore)

    @Provides
    @Singleton
    fun provideGlobalOverlayRepository(firestore: FirebaseFirestore): GlobalOverlayRepository = GlobalOverlayRepository(firestore)

    @Provides
    @Singleton
    fun provideModerationRepository(firestore: FirebaseFirestore): ModerationRepository = ModerationRepository(firestore)
}
