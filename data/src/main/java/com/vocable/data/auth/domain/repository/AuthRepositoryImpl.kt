package com.vocable.data.auth.domain.repository

import com.vocable.data.auth.source.remote.AuthProvider
import com.vocable.data.auth.source.remote.FirebaseAuthSource
import com.vocable.data.user.domain.model.AppUser

class AuthRepositoryImpl(private val firebaseAuthSource: FirebaseAuthSource) : AuthRepository {

    override val getCurrentUser: AppUser?
        get() = firebaseAuthSource.getCurrentUser()

    override suspend fun login(authProvider: AuthProvider): AppUser? {
        return firebaseAuthSource.login(authProvider)
    }
}