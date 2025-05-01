package com.vocable.data.auth.source.remote

import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.vocable.data.auth.mapper.toAppUser
import com.vocable.data.user.domain.model.AppUser
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource(
    private val auth: FirebaseAuth,
) {

    fun getCurrentUser(): AppUser? {
        return auth.currentUser?.toAppUser()
    }

    suspend fun login(authProvider: AuthProvider): AppUser? {
        val result = authProvider.getCredential()
        val authResult = result?.let { handleSignIn(it) }
        return authResult?.user?.toAppUser()
            ?.copy(isNewUser = authResult.additionalUserInfo?.isNewUser)
    }

    private suspend fun handleSignIn(credential: Credential): AuthResult? {
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.Companion.createFrom(credential.data)
            return firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            return null
        }
    }

    private suspend fun firebaseAuthWithGoogle(idToken: String): AuthResult? {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val response = auth.signInWithCredential(credential).await()
        return response
    }


}