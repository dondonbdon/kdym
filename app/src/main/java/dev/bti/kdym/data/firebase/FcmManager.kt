package dev.bti.kdym.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await

object FcmManager {

    suspend fun syncCurrentToken() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Log.e("FCM", "No user logged in, skipping sync")
            return
        }

        try {
            val token = FirebaseMessaging.getInstance().token.await()
            if (token == null) {
                Log.w("FCM", "Fetched token is null")
                return
            }

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .set(
                    mapOf("fcmTokens" to FieldValue.arrayUnion(token)),
                    SetOptions.merge()
                )
                .await()


        } catch (e: Exception) {
            Log.e("FCM", "Failed to sync token", e)
        }
    }
}