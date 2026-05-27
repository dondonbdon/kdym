package dev.bti.kdym.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Build
import dev.bti.kdym.MainNavigation
import dev.bti.kdym.ui.components.AnimatedSplash
import dev.bti.kdym.ui.components.UrgentOverlay
import dev.bti.kdym.viewmodels.MainViewModel

import dev.bti.kdym.ui.screens.auth.LoginScreen
import dev.bti.kdym.ui.screens.auth.SignUpScreen
import dev.bti.kdym.ui.screens.auth.WelcomeScreen
import dev.bti.kdym.ui.screens.auth.PostAuthWelcomeScreen

@Composable
fun App(viewModel: MainViewModel = viewModel()) {
    var showSplash by remember { mutableStateOf(true) }
    val firebaseUser by viewModel.firebaseUser.collectAsState()
    val appConfig by viewModel.appConfig.collectAsState()
    val user by viewModel.user.collectAsState()
    
    var currentAuthScreen by remember { mutableStateOf("welcome") }
    var hasShownWelcome by remember { mutableStateOf(false) }
    var dismissedOverlayId by remember { mutableStateOf<String?>(null) }
    
    val shouldRequestPermissions by viewModel.shouldRequestPermissions.collectAsState()
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { viewModel.onPermissionRequestHandled() }

    LaunchedEffect(shouldRequestPermissions) {
        if (shouldRequestPermissions) {
            val permissions = mutableListOf(android.Manifest.permission.CAMERA)
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
                permissions.add(android.Manifest.permission.READ_MEDIA_IMAGES)
                permissions.add(android.Manifest.permission.READ_MEDIA_VIDEO)
                permissions.add(android.Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                permissions.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            
            permissionLauncher.launch(permissions.toTypedArray())
        }
    }

    val showUrgentOverlay = remember(appConfig, user, dismissedOverlayId) {
        val config = appConfig?.urgentOverlay
        if (config == null || !config.isVisibleNow) return@remember false
        
        // Filter out if user already dismissed this specific version
        if (dismissedOverlayId == config.updatedAt?.toString()) return@remember false
        
        // Filter targets
        if (config.excludeAdmins && user?.isAdmin == true) return@remember false
        if (config.onlyApprovedCamp && user?.hasApprovedCampAccess != true) return@remember false
        
        val targetEmails = config.targetEmails?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() }
        if (!targetEmails.isNullOrEmpty() && user?.email !in targetEmails) return@remember false
        
        true
    }

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
            val uiState by viewModel.uiState.collectAsState()
            Box(modifier = Modifier.fillMaxSize()) {
                Column {
                    if (uiState.isLoading && firebaseUser == null) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth().statusBarsPadding(),
                            color = Color(0xFFEF4444),
                            trackColor = Color.Transparent
                        )
                    }
                    if (firebaseUser != null) {
                        if (!hasShownWelcome && user != null && currentAuthScreen != "welcome") {
                            PostAuthWelcomeScreen(
                                name = user?.displayName ?: "Friend",
                                onContinue = { hasShownWelcome = true }
                            )
                        } else {
                            MainNavigation(viewModel)
                        }
                    } else {
                        when (currentAuthScreen) {
                            "welcome" -> WelcomeScreen(
                                onNavigateToLogin = { currentAuthScreen = "login" },
                                onNavigateToSignUp = { currentAuthScreen = "signup" }
                            )
                            "login" -> LoginScreen(
                                onSignIn = { email, pass -> 
                                    viewModel.signIn(email, pass) { success ->
                                        if (success) { 
                                            currentAuthScreen = "post_auth"
                                            hasShownWelcome = false 
                                        }
                                    }
                                },
                                onNavigateToSignUp = { currentAuthScreen = "signup" }
                            )
                            "signup" -> SignUpScreen(
                                onSignUp = { firstName, lastName, username, phone, churchId, churchName, email, pass ->
                                    viewModel.signUp(firstName, lastName, username, phone, churchId, churchName, email, pass) { success ->
                                        if (success) { 
                                            currentAuthScreen = "post_auth"
                                            hasShownWelcome = false 
                                        }
                                    }
                                },
                                onNavigateToLogin = { currentAuthScreen = "login" }
                            )
                        }
                    }
                }
                
                if (showUrgentOverlay && appConfig?.urgentOverlay != null) {
                    UrgentOverlay(config = appConfig!!.urgentOverlay!!) {
                        dismissedOverlayId = appConfig!!.urgentOverlay!!.updatedAt?.toString()
                    }
                }
            }
        }
    }
}
