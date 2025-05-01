package com.vocable.data.auth.mapper

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.vocable.data.auth.domain.model.FirebaseAppUser
import com.vocable.data.user.domain.model.AppUser


fun FirebaseUser.toAppUser() = AppUser(
    id = uid,
    email = email ?: "",
    displayName = displayName,
    phoneNumber = phoneNumber,
    profileImage = photoUrl.toString()
)

fun FirebaseUser.toFirebaseRemoteUser() = FirebaseAppUser(
    id = uid,
    email = email ?: "",
    displayName = displayName,
    phoneNumber = phoneNumber,
    profileImage = photoUrl.toString(),
    createdAt = Timestamp.now(),
    updatedAt = Timestamp.now(),
)