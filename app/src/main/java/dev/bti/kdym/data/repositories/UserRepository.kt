package dev.bti.kdym.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getUser(uid: String): Flow<User?> {
        return firestore.collection("users")
            .document(uid)
            .snapshots()
            .map { it.toObject(User::class.java) }
    }

    suspend fun createUser(user: User) {
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
}
