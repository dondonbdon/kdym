package dev.bti.kdym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.bti.kdym.ui.App
import dev.bti.kdym.ui.components.GlassNavigationBar
import dev.bti.kdym.ui.screens.admin.ApprovalsScreen
import dev.bti.kdym.ui.screens.admin.CampSettingsScreen
import dev.bti.kdym.ui.screens.admin.CommandCenterHubScreen
import dev.bti.kdym.ui.screens.admin.CommandCenterScreen
import dev.bti.kdym.ui.screens.admin.CreateTribeScreen
import dev.bti.kdym.ui.screens.admin.ManageGroupsScreen
import dev.bti.kdym.ui.screens.admin.ManageTribesScreen
import dev.bti.kdym.ui.screens.admin.TribeWarsAdminScreen
import dev.bti.kdym.ui.screens.events.EventsScreen
import dev.bti.kdym.ui.screens.groups.GroupsScreen
import dev.bti.kdym.ui.screens.home.HomeScreen
import dev.bti.kdym.ui.screens.play.PlayScreen
import dev.bti.kdym.ui.screens.settings.NotificationPreferencesScreen
import dev.bti.kdym.ui.theme.KdymTheme
import dev.bti.kdym.viewmodels.MainViewModel

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.isNavigationBarContrastEnforced = false

        setContent {
            KdymTheme {
                App(mainViewModel)
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val items = listOf(
        NavigationItem("home", "Home", R.drawable.ic_home),
        NavigationItem("events", "Events", R.drawable.ic_calendar),
        NavigationItem("play", "Play", R.drawable.ic_play),
        NavigationItem("groups", "Groups", R.drawable.ic_group_msg),
        NavigationItem("command", "Command", R.drawable.ic_profile_filled)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in items.map { it.route }) {
                GlassNavigationBar(navController, items)
            }
        },
        containerColor = Color.Transparent
    ) { _ ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()

        ) {
            composable("home") { HomeScreen() }
            composable("events") { EventsScreen() }
            composable("play") { PlayScreen() }
            composable("groups") { GroupsScreen() }

            // Command Navigation Group
            composable("command") {
                CommandCenterScreen(
                    onNavigateToHub = { navController.navigate("command_hub") },
                    onNavigateToAnnouncements = { /* User View */ },
                    onNavigateToNotificationPrefs = { navController.navigate("notification_prefs") },
                    onNavigateToTribeWars = { /* User View */ },
                    onNavigateToAccessStatus = { /* User View */ },
                    onNavigateToProfile = { /* User View */ }
                )
            }

            composable("command_hub") {
                CommandCenterHubScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToSettings = { navController.navigate("camp_settings") },
                    onNavigateToApprovals = { navController.navigate("approvals") },
                    onNavigateToTribes = { navController.navigate("manage_tribes") },
                    onNavigateToTribeWars = { navController.navigate("tribe_wars_admin") },
                    onNavigateToAnnouncements = { /* Admin View */ },
                    onNavigateToGroups = { navController.navigate("manage_groups") },
                    onNavigateToCreateEvent = { /* TODO */ },
                    onNavigateToPostPlay = { /* TODO */ }
                )
            }

            composable("camp_settings") { CampSettingsScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("approvals") { ApprovalsScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("manage_tribes") {
                ManageTribesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCreateTribe = { navController.navigate("create_tribe") })
            }
            composable("create_tribe") { CreateTribeScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("manage_groups") {
                ManageGroupsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onCreateGroup = { /* TODO */ })
            }
            composable("tribe_wars_admin") { TribeWarsAdminScreen(onNavigateBack = { navController.popBackStack() }) }
            composable("notification_prefs") { NotificationPreferencesScreen(onNavigateBack = { navController.popBackStack() }) }
        }
    }
}

data class NavigationItem(val route: String, val label: String, @field:DrawableRes val iconRes: Int)
