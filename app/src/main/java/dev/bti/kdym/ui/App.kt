package dev.bti.kdym.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import dev.bti.kdym.MainNavigation
import dev.bti.kdym.ui.components.AnimatedSplash
import dev.bti.kdym.viewmodels.MainViewModel

import dev.bti.kdym.ui.screens.auth.LoginScreen
import dev.bti.kdym.ui.screens.auth.SignUpScreen
import dev.bti.kdym.ui.screens.auth.WelcomeScreen

@Composable
fun App(viewModel: MainViewModel = viewModel()) {
    var showSplash by remember { mutableStateOf(true) }
    val firebaseUser by viewModel.firebaseUser.collectAsState()
    
    var currentAuthScreen by remember { mutableStateOf("welcome") }

    AnimatedContent(
        targetState = showSplash,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "app_transition"
    ) { isSplash ->
        if (isSplash) {
            AnimatedSplash {
                showSplash = false
            }
        } else {
            if (firebaseUser != null) {
                MainNavigation()
            } else {
                when (currentAuthScreen) {
                    "welcome" -> WelcomeScreen(
                        onNavigateToLogin = { currentAuthScreen = "login" },
                        onNavigateToSignUp = { currentAuthScreen = "signup" },
                        onNavigateToPlayPreview = { /* TODO */ }
                    )
                    "login" -> LoginScreen(
                        onSignIn = { email, pass -> 
                            viewModel.signIn(email, pass) { success ->
                                if (!success) { /* Show error */ }
                            }
                        },
                        onNavigateToSignUp = { currentAuthScreen = "signup" }
                    )
                    "signup" -> SignUpScreen(
                        onSignUp = { name, email, pass ->
                            viewModel.signUp(name, email, pass) { success ->
                                if (!success) { /* Show error */ }
                            }
                        },
                        onNavigateToLogin = { currentAuthScreen = "login" }
                    )
                }
            }
        }
    }
}
