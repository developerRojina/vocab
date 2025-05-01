package com.vocable.data.auth.domain.model

import com.google.firebase.Timestamp

data class FirebaseAppUser(
    val id: String,
    val email: String,
    val profileImage: String?,
    val displayName: String?,
    val phoneNumber: String?,
    val createdAt: Timestamp,
    val updatedAt: Timestamp
)