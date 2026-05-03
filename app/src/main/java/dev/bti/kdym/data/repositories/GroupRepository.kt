package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Group
import dev.bti.kdym.data.models.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class GroupRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getGroupsForUser(uid: String): Flow<List<Group>> {
        return firestore.collection("groups")
            .whereArrayContains("memberIds", uid)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { it.toObjects(Group::class.java) }
    }

    fun getPublicGroups(): Flow<List<Group>> {
        return firestore.collection("groups")
            .whereEqualTo("isPublic", true)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { it.toObjects(Group::class.java) }
    }

    fun getMessages(groupId: String): Flow<List<Message>> {
        return firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .snapshots()
            .map { it.toObjects(Message::class.java) }
    }

    suspend fun sendMessage(groupId: String, message: Message) {
        firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .add(message)
            .await()
    }
}
