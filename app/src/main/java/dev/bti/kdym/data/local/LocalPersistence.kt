package dev.bti.kdym.data.local

import android.content.Context
import androidx.room.*
import dev.bti.kdym.data.models.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// ==========================================
// MARK: - Entities (Wrapped for Room)
// ==========================================

@Entity(tableName = "feed_posts")
data class FeedPostEntity(
    @PrimaryKey val id: String,
    val dataJson: String,
    val createdAt: Long,
    val isPinned: Boolean
)

@Entity(tableName = "play_items")
data class PlayItemEntity(
    @PrimaryKey val id: String,
    val dataJson: String,
    val kind: String,
    val createdAt: Long
)

@Entity(tableName = "group_messages")
data class GroupMessageEntity(
    @PrimaryKey val id: String,
    val groupId: String,
    val dataJson: String,
    val createdAt: Long
)

@Entity(tableName = "tribes")
data class TribeEntity(
    @PrimaryKey val id: String,
    val dataJson: String,
    val totalPoints: Int,
    val rank: Int
)

@Entity(tableName = "app_groups")
data class AppGroupEntity(
    @PrimaryKey val id: String,
    val dataJson: String,
    val lastMessageAt: Long
)

// ==========================================
// MARK: - DAOs
// ==========================================

@Dao
interface FeedPostDao {
    @Query("SELECT * FROM feed_posts ORDER BY isPinned DESC, createdAt DESC")
    fun getFeedPosts(): Flow<List<FeedPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosts(posts: List<FeedPostEntity>)

    @Query("DELETE FROM feed_posts")
    suspend fun clearAll()
}

@Dao
interface PlayItemDao {
    @Query("SELECT * FROM play_items WHERE kind = :kind ORDER BY createdAt DESC")
    fun getPlayItems(kind: String): Flow<List<PlayItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<PlayItemEntity>)

    @Query("DELETE FROM play_items WHERE kind = :kind")
    suspend fun clearByKind(kind: String)
}

@Dao
interface GroupMessageDao {
    @Query("SELECT * FROM group_messages WHERE groupId = :groupId ORDER BY createdAt DESC LIMIT 100")
    fun getMessages(groupId: String): Flow<List<GroupMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<GroupMessageEntity>)

    @Query("DELETE FROM group_messages WHERE groupId = :groupId")
    suspend fun clearByGroup(groupId: String)
}

@Dao
interface TribeDao {
    @Query("SELECT * FROM tribes ORDER BY totalPoints DESC")
    fun getTribes(): Flow<List<TribeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTribes(tribes: List<TribeEntity>)

    @Query("DELETE FROM tribes")
    suspend fun clearAll()
}

@Dao
interface AppGroupDao {
    @Query("SELECT * FROM app_groups ORDER BY lastMessageAt DESC")
    fun getGroups(): Flow<List<AppGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroups(groups: List<AppGroupEntity>)

    @Query("DELETE FROM app_groups")
    suspend fun clearAll()
}

// ==========================================
// MARK: - Database
// ==========================================

@Database(
    entities = [FeedPostEntity::class, PlayItemEntity::class, GroupMessageEntity::class, TribeEntity::class, AppGroupEntity::class],
    version = 1,
    exportSchema = false
)
abstract class KdymDatabase : RoomDatabase() {
    abstract fun feedPostDao(): FeedPostDao
    abstract fun playItemDao(): PlayItemDao
    abstract fun groupMessageDao(): GroupMessageDao
    abstract fun tribeDao(): TribeDao
    abstract fun appGroupDao(): AppGroupDao

    companion object {
        @Volatile
        private var INSTANCE: KdymDatabase? = null

        fun getDatabase(context: Context): KdymDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    KdymDatabase::class.java,
                    "kdym_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// ==========================================
// MARK: - Extensions (Mapping)
// ==========================================

private val json = Json { ignoreUnknownKeys = true }

fun FeedPost.toEntity() = FeedPostEntity(
    id = id,
    dataJson = json.encodeToString(this),
    createdAt = createdAt.seconds,
    isPinned = isPinned
)

fun FeedPostEntity.toModel() = json.decodeFromString<FeedPost>(dataJson)

fun PlayItem.toEntity() = PlayItemEntity(
    id = id ?: "tmp_${System.currentTimeMillis()}",
    dataJson = json.encodeToString(this),
    kind = kind,
    createdAt = createdAt.seconds
)

fun PlayItemEntity.toModel() = json.decodeFromString<PlayItem>(dataJson)

fun GroupMessage.toEntity() = GroupMessageEntity(
    id = id,
    groupId = groupId,
    dataJson = json.encodeToString(this),
    createdAt = createdAt?.seconds ?: 0L
)

fun GroupMessageEntity.toModel() = json.decodeFromString<GroupMessage>(dataJson)

fun Tribe.toEntity() = TribeEntity(
    id = id,
    dataJson = json.encodeToString(this),
    totalPoints = totalPoints,
    rank = rank
)

fun TribeEntity.toModel() = json.decodeFromString<Tribe>(dataJson)

fun AppGroup.toEntity() = AppGroupEntity(
    id = id,
    dataJson = json.encodeToString(this),
    lastMessageAt = lastMessageAt?.seconds ?: createdAt?.seconds ?: 0L
)

fun AppGroupEntity.toModel() = json.decodeFromString<AppGroup>(dataJson)
