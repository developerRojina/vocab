package com.vocable.data.auth.source.remote

import androidx.credentials.Credential
import com.google.firebase.auth.AuthCredential


interface AuthProvider {
    suspend fun getCredential(): Credential?
}