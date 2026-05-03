package dev.bti.kdym.data.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import dev.bti.kdym.data.models.PlayItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlayRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun getPublishedPlayItems(kind: String? = null): Flow<List<PlayItem>> {
        var query = firestore.collection("playItems")
            .whereEqualTo("isPublished", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
        
        if (kind != null) {
            query = query.whereEqualTo("kind", kind)
        }
        
        return query.snapshots().map { snapshot ->
            snapshot.toObjects(PlayItem::class.java)
        }
    }
}
