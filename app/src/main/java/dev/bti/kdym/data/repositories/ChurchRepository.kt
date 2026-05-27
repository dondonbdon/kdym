package dev.bti.kdym.data.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.Church
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Repository for managing church records and pastor associations.
 */
class ChurchRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    /**
     * Returns a real-time stream of all churches, ordered by name.
     */
    fun getAllChurches(): Flow<List<Church>> {
        return firestore.collection("churches")
            .orderBy("name", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Church::class.java)?.copy(id = doc.id)
                }
            }
    }

    /**
     * Searches for churches by name or city.
     */
    fun searchChurches(query: String): Flow<List<Church>> {
        val normalizedQuery = query.uppercase()
        return firestore.collection("churches")
            .orderBy("name")
            .startAt(normalizedQuery)
            .endAt(normalizedQuery + "\uf8ff")
            .snapshots()
            .map { it.toObjects(Church::class.java) }
    }

    /**
     * Creates a new church record.
     */
    suspend fun createChurch(church: Church) {
        val docRef = firestore.collection("churches").document()
        firestore.collection("churches")
            .document(docRef.id)
            .set(church.copy(id = docRef.id, createdAt = Timestamp.now()))
            .await()
    }

    /**
     * Updates an existing church record.
     */
    suspend fun updateChurch(churchId: String, updates: Map<String, Any?>) {
        firestore.collection("churches")
            .document(churchId)
            .update(updates)
            .await()
    }
}
