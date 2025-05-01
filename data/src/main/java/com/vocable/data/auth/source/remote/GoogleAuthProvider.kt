package com.vocable.data.auth.source.remote

import android.app.Activity
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

class GoogleAuthProvider(private val activity: Activity) : AuthProvider {
    override suspend fun getCredential(): Credential? {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(false)
            .setServerClientId("73675317726-4hse4tiut4a10rsa0onbql8ogvak6oll.apps.googleusercontent.com")
            .build()

        val credentialManager = CredentialManager.create(activity)

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val result = credentialManager.getCredential(
                request = request,
                context = activity // Using Activity context here
            ).credential
            result
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}