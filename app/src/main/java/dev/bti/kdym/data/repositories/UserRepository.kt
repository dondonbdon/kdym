package dev.bti.kdym.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.Query
import dev.bti.kdym.data.local.AppPrefs
import dev.bti.kdym.data.models.AccessStatus
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.NotificationPreferences
import dev.bti.kdym.data.models.UserRole
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing user profiles and permissions in Firestore.
 */
class UserRepository(
    private val appPrefs: AppPrefs? = null,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Returns a stream of a specific user's profile.
     */
    fun getUser(uid: String): Flow<AppUser?> {
        return firestore.collection("users")
            .document(uid)
            .snapshots()
            .map { it.toObject(AppUser::class.java) }
    }

    /**
     * Creates a new user document.
     */
    suspend fun createUser(user: AppUser) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }

    suspend fun isUsernameTaken(username: String): Boolean {
        val query = firestore.collection("users")
            .whereEqualTo("username", username)
            .limit(1)
            .get()
            .await()
        return !query.isEmpty
    }

    /**
     * Silently updates the user's last active timestamp.
     */
    suspend fun updateLastActive(uid: String) {
        firestore.collection("users")
            .document(uid)
            .set(mapOf("lastActiveAt" to Timestamp.now()), SetOptions.merge())
            .await()
    }

    /**
     * Returns a real-time stream of all registered users, ordered by registration date.
     */
    fun getAllUsers(): Flow<List<AppUser>> {
        return firestore.collection("users")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(AppUser::class.java) }
    }

    /**
     * Updates a user's role and access status (typically for administrative approvals).
     */
    suspend fun updateUserRole(uid: String, role: UserRole, status: AccessStatus) {
        firestore.collection("users")
            .document(uid)
            .update(
                "role", role.name,
                "accessStatus", status.name,
                "updatedAt", Timestamp.now()
            )
            .await()
    }

    /**
     * Marks a user as deleted and signs them out (soft deletion).
     */
    suspend fun softDeleteUser(uid: String) {
        firestore.collection("users")
            .document(uid)
            .update(
                "isDeleted", true,
                "accessStatus", AccessStatus.suspended.name,
                "updatedAt", Timestamp.now()
            )
            .await()
    }

    /**
     * Submits a request for camp access.
     */
    suspend fun submitCampAccessRequest(uid: String, campId: String, role: String) {
        firestore.collection("users")
            .document(uid)
            .update(
                "requestedCampId", campId,
                "requestedRole", role,
                "requestedAt", Timestamp.now(),
                "accessStatus", AccessStatus.pending.name,
                "role", UserRole.pending.name,
                "updatedAt", Timestamp.now()
            )
            .await()
    }

    /**
     * Updates arbitrary fields in a user's document.
     *
     * @param uid The user's UID.
     * @param updates A map of field names to new values.
     */
    suspend fun updateUser(uid: String, updates: Map<String, Any?>) {
        firestore.collection("users")
            .document(uid)
            .update(updates + ("updatedAt" to Timestamp.now()))
            .await()
    }

    /**
     * Saves a user's notification preferences.
     */
    suspend fun updateNotificationPreferences(
        uid: String,
        prefs: NotificationPreferences
    ) {
        firestore.collection("users")
            .document(uid)
            .update(
                "notificationPreferences", prefs,
                "updatedAt", Timestamp.now()
            )
            .await()
    }
}
