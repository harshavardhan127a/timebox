package com.example.timebox

data class AuthState(
    val email: String = "",
    val currentScreen: Screen = Screen.LOGIN,

    val errorMessage: String? = null,

    val isLoggedIn: Boolean = false,
    val sessionStartTime: Long = 0L,
    val sessionDuration: Long = 0L
)

enum class Screen {
    LOGIN,
    OTP,
    SESSION
}
