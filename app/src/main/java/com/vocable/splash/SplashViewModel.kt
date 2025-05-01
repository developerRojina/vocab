package com.vocable.splash

import androidx.lifecycle.ViewModel
import com.vocable.data.user.domain.model.AppUser
import com.vocable.data.auth.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SplashViewModel(authRepository: AuthRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<AppUser?>(null)
    val currentUser: StateFlow<AppUser?> get() = _currentUser

    init {
        _currentUser.value = authRepository.getCurrentUser
    }
}