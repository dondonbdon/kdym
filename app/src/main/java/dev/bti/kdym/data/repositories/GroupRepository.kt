package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.AppGroup
import dev.bti.kdym.data.models.GroupMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class GroupRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getGroupsForUser(uid: String): Flow<List<AppGroup>> {
        return firestore.collection("groups")
            .whereArrayContains("memberIds", uid)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { it.toObjects(AppGroup::class.java) }
    }

    fun getPublicGroups(): Flow<List<AppGroup>> {
        return firestore.collection("groups")
            .whereEqualTo("isPublic", true)
            .whereEqualTo("isActive", true)
            .snapshots()
            .map { it.toObjects(AppGroup::class.java) }
    }

    fun getMessages(groupId: String): Flow<List<GroupMessage>> {
        return firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(100)
            .snapshots()
            .map { it.toObjects(GroupMessage::class.java) }
    }

    suspend fun sendMessage(groupId: String, message: GroupMessage) {
        firestore.collection("groups")
            .document(groupId)
            .collection("messages")
            .add(message)
            .await()
    }
}
