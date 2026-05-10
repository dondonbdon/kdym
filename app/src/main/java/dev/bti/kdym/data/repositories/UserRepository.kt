package dev.bti.kdym.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.firestore.Query
import dev.bti.kdym.data.models.AccessStatus
import dev.bti.kdym.data.models.AppUser
import dev.bti.kdym.data.models.NotificationPreferences
import dev.bti.kdym.data.models.UserRole
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getUser(uid: String): Flow<AppUser?> {
        return firestore.collection("users")
            .document(uid)
            .snapshots()
            .map { it.toObject(AppUser::class.java) }
    }

    suspend fun createUser(user: AppUser) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }

    suspend fun updateLastActive(uid: String) {
        firestore.collection("users")
            .document(uid)
            .update("lastActiveAt", Timestamp.now())
            .await()
    }

    fun getAllUsers(): Flow<List<AppUser>> {
        return firestore.collection("users")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(AppUser::class.java) }
    }

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

    suspend fun updateUser(uid: String, updates: Map<String, Any?>) {
        firestore.collection("users")
            .document(uid)
            .update(updates + ("updatedAt" to Timestamp.now()))
            .await()
    }

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
