package com.vocable.auth

sealed class LoginUiState {
    object Initial : LoginUiState()
    object LoggedIn : LoginUiState()
    object Loading : LoginUiState()
    class Error : LoginUiState()

}