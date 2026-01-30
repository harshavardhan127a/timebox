package com.example.timebox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.example.timebox.ui.theme.TimeboxTheme
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        setContent {
            TimeboxTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppContent(viewModel)
                }
            }
        }
    }
}

@Composable
fun AppContent(viewModel: AuthViewModel) {
    when (viewModel.state.value.currentScreen) {
        Screen.LOGIN -> LoginScreen(viewModel)
        Screen.OTP -> OtpScreen(viewModel)
        Screen.SESSION -> SessionScreen(viewModel)
    }
}
