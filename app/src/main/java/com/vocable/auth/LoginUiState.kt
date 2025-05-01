package com.vocable.auth

sealed class LoginUiState {
    object Initial : LoginUiState()
    object LoggedIn : LoginUiState()
    class Error : LoginUiState()

}