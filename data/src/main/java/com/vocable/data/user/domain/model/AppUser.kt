package com.vocable.data.user.domain.model

data class AppUser(
    val id: String,
    val email: String,
    val isNewUser: Boolean? = false,
    val profileImage: String?,
    val displayName: String?,
    val phoneNumber: String?
)