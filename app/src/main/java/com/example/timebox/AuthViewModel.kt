package com.example.timebox

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

class AuthViewModel : ViewModel() {

    var state = mutableStateOf(AuthState())
        private set

    private val OTP_EXPIRY_MS = 60_000L
    private val MAX_ATTEMPTS = 3

    private val otpStore = mutableMapOf<String, OtpData>()

    data class OtpData(
        val otp: String,
        val createdAt: Long,
        val attempts: Int
    )

    fun onEmailChange(email: String) {
        state.value = state.value.copy(email = email)
    }

    fun sendOtp() {
        val email = state.value.email
        val otp = Random.nextInt(100000, 999999).toString()

        Timber.d("Generated OTP for ${state.value.email} : $otp")


        otpStore[email] = OtpData(
            otp = otp,
            createdAt = System.currentTimeMillis(),
            attempts = 0
        )

        state.value = state.value.copy(
            currentScreen = Screen.OTP,
            errorMessage = null
        )
    }

    fun resendOtp() {
        Timber.d("OTP regenerated")
        sendOtp()
    }

    fun verifyOtp(input: String) {
        val email = state.value.email
        val otpData = otpStore[email]

        if (otpData == null) {
            Timber.d("OTP validation failure")
            state.value = state.value.copy(errorMessage = "OTP not found")
            return
        }

        if (System.currentTimeMillis() - otpData.createdAt > OTP_EXPIRY_MS) {
            Timber.d("OTP expired")
            otpStore.remove(email)
            state.value = state.value.copy(errorMessage = "OTP expired")
            return
        }

        if (otpData.attempts >= MAX_ATTEMPTS) {
            Timber.d("OTP attempts exceeded")
            state.value = state.value.copy(errorMessage = "Maximum attempts reached")
            return
        }

        if (input == otpData.otp) {
            Timber.d("OTP validation success")
            otpStore.remove(email)
            startSession()
        } else {
            Timber.d("OTP validation failure")
            otpStore[email] = otpData.copy(attempts = otpData.attempts + 1)
            state.value = state.value.copy(errorMessage = "Invalid OTP")
        }
    }

    private fun startSession() {
        val startTime = System.currentTimeMillis()

        state.value = state.value.copy(
            isLoggedIn = true,
            sessionStartTime = startTime,
            currentScreen = Screen.SESSION,
            errorMessage = null
        )

        startTimer()
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (state.value.isLoggedIn) {
                delay(1000)
                state.value = state.value.copy(
                    sessionDuration =
                        System.currentTimeMillis() - state.value.sessionStartTime
                )
            }
        }
    }

    fun logout() {
        Timber.d("Logout")
        otpStore.remove(state.value.email)
        state.value = AuthState()
    }
}
