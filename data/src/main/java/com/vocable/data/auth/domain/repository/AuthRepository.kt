package com.vocable.data.auth.domain.repository

import com.vocable.data.auth.source.remote.AuthProvider
import com.vocable.data.user.domain.model.AppUser

interface AuthRepository {

    val getCurrentUser: AppUser?
    suspend fun login(authProvider: AuthProvider): AppUser?
}