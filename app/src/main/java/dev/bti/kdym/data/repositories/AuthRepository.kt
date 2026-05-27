package dev.bti.kdym.data.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Repository for handling user authentication via Firebase Auth.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    /**
     * A [Flow] that emits the current [FirebaseUser] whenever the authentication state changes.
     */
    val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { 
            trySend(it.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    /**
     * Authenticates a user with email and password.
     *
     * @return The authenticated [FirebaseUser] or null if sign-in fails.
     */
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        return auth.signInWithEmailAndPassword(email, password).await().user
    }

    /**
     * Creates a new user account with email and password.
     *
     * @return The newly created [FirebaseUser] or null if sign-up fails.
     */
    suspend fun signUp(email: String, password: String): FirebaseUser? {
        return auth.createUserWithEmailAndPassword(email, password).await().user
    }

    /**
     * Signs out the current user.
     */
    fun signOut() {
        auth.signOut()
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }
}
