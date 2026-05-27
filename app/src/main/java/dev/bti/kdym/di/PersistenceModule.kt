package dev.bti.kdym.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.bti.kdym.data.local.AppPrefs
import dev.bti.kdym.data.local.KdymDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideAppPrefs(@ApplicationContext context: Context): AppPrefs {
        return AppPrefs(context)
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): KdymDatabase {
        return KdymDatabase.getDatabase(context)
    }

    @Provides
    fun provideFeedPostDao(db: KdymDatabase) = db.feedPostDao()

    @Provides
    fun providePlayItemDao(db: KdymDatabase) = db.playItemDao()

    @Provides
    fun provideGroupMessageDao(db: KdymDatabase) = db.groupMessageDao()

    @Provides
    fun provideTribeDao(db: KdymDatabase) = db.tribeDao()

    @Provides
    fun provideAppGroupDao(db: KdymDatabase) = db.appGroupDao()
}
