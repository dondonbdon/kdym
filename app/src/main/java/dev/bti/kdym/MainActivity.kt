package dev.bti.kdym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.bti.kdym.ui.App
import dev.bti.kdym.ui.components.GlassNavigationBar
import dev.bti.kdym.ui.screens.admin.*
import dev.bti.kdym.ui.screens.events.EventDetailScreen
import dev.bti.kdym.ui.screens.events.EventsScreen
import dev.bti.kdym.ui.screens.groups.*
import dev.bti.kdym.ui.screens.home.*
import dev.bti.kdym.ui.screens.play.PlayScreen
import dev.bti.kdym.ui.screens.profile.ProfileScreen
import dev.bti.kdym.ui.screens.settings.NotificationPreferencesScreen
import dev.bti.kdym.ui.theme.KdymTheme
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        window.isNavigationBarContrastEnforced = false
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            KdymTheme {
                App(mainViewModel)
            }
        }
    }
}

@Composable
fun FeedbackBanner(message: String, isError: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = if (isError) Color(0xFFEF4444) else Color(0xFF10B981),
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 8.dp
    ) {
        Text(
            text = message,
            color = Color.White,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MainNavigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val groupsViewModel: GroupsViewModel = viewModel()
    val adminViewModel: AdminViewModel = viewModel()

    val user by mainViewModel.user.collectAsState()
    val uiState by mainViewModel.uiState.collectAsState()

    val baseItems = listOf(
        NavigationItem("home", "Home", R.drawable.ic_home),
        NavigationItem("events", "Events", R.drawable.ic_calendar),
        NavigationItem("play", "Play", R.drawable.ic_play),
        NavigationItem("community", "Community", R.drawable.ic_group_msg)
    )

    val items = if (user?.hasCommandAccess == true) {
        baseItems + NavigationItem("command", "Command", R.drawable.ic_profile_filled)
    } else {
        baseItems
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            val loadingState by mainViewModel.uiState.collectAsState()
            Column(modifier = Modifier.statusBarsPadding()) {
                if (loadingState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEF4444),
                        trackColor = Color.Transparent
                    )
                }
                loadingState.feedbackMessage?.let { message ->
                    FeedbackBanner(message = message, isError = loadingState.isError)
                }
            }
        },
        bottomBar = {
            if (currentRoute in items.map { it.route }) {
                GlassNavigationBar(navController, items)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (currentRoute in items.map { it.route }) 0.dp else paddingValues.calculateBottomPadding())
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToComments = { postId -> navController.navigate("comments/$postId") },
                    onNavigateToCreatePost = { navController.navigate("create_home_post") },
                    viewModel = mainViewModel
                )
            }
            composable("events") {
                EventsScreen(
                    onNavigateToEventDetail = { eventId -> navController.navigate("event_detail/$eventId") },
                    mainViewModel = mainViewModel
                )
            }
            composable("play") { PlayScreen(viewModel = mainViewModel) }
            composable("community") { 
                CommunityScreen(
                    onNavigateToChat = { groupId -> navController.navigate("chat/$groupId") },
                    onNavigateToAnnouncements = { navController.navigate("announcements") },
                    onNavigateToTribeWars = { navController.navigate("tribe_wars_admin") },
                    onExploreGroups = { navController.navigate("explore_groups") },
                    onCreateGroup = { navController.navigate("create_group") },
                    mainViewModel = mainViewModel,
                    viewModel = groupsViewModel,
                    adminViewModel = adminViewModel
                ) 
            }
            composable("explore_groups") {
                ExploreGroupsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = groupsViewModel
                )
            }
            composable("create_home_post") {
                CreateHomePostScreen(
                    onNavigateBack = { navController.popBackStack() },
                    adminViewModel = adminViewModel
                )
            }
            composable("create_poll?groupId={groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId")
                CreatePollScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() },
                    adminViewModel = adminViewModel
                )
            }
            composable("chat/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                ChatScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToInfo = { id -> navController.navigate("group_info/$id") },
                    onNavigateToCreatePoll = { id -> navController.navigate("create_poll?groupId=$id") },
                    viewModel = groupsViewModel,
                    adminViewModel = adminViewModel
                )
            }
            composable("group_info/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                GroupInfoScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditGroup = { id -> navController.navigate("create_group?groupId=$id") },
                    groupsViewModel = groupsViewModel,
                    mainViewModel = mainViewModel,
                    adminViewModel = adminViewModel
                )
            }

            // Command Navigation Group
            composable("command") {
                if (user?.hasCommandAccess == true) {
                    CommandCenterScreen(
                        onNavigateToHub = { navController.navigate("command_hub") },
                        onNavigateToAnnouncements = { navController.navigate("announcements") },
                        onNavigateToTribeWars = { navController.navigate("tribe_wars_admin") },
                        onNavigateToProfile = { navController.navigate("profile") },
                        viewModel = mainViewModel
                    )
                }
            }

            composable("command_hub") {
                if (user?.hasCommandAccess == true) {
                    CommandCenterHubScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToSettings = { navController.navigate("camp_settings") },
                        onNavigateToApprovals = { navController.navigate("approvals") },
                        onNavigateToTribes = { navController.navigate("manage_tribes") },
                        onNavigateToTribeWars = { navController.navigate("tribe_wars_admin") },
                        onNavigateToAnnouncements = { navController.navigate("announcements") },
                        onNavigateToGroups = { navController.navigate("manage_groups") },
                        onNavigateToCreateEvent = { /* TODO */ },
                        onNavigateToPostPlay = { /* TODO */ },
                        mainViewModel = mainViewModel,
                        viewModel = adminViewModel
                    )
                }
            }

            composable("camp_settings") { 
                if (user?.roleEnum?.canManageCampSettings == true) {
                    CampSettingsScreen(onNavigateBack = { navController.popBackStack() }, viewModel = adminViewModel)
                }
            }
            composable("approvals") { 
                if (user?.roleEnum?.canManageApprovals == true) {
                    ApprovalsScreen(onNavigateBack = { navController.popBackStack() }, viewModel = adminViewModel)
                }
            }
            composable("manage_tribes") {
                if (user?.roleEnum?.canManageTribes == true) {
                    ManageTribesScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onCreateTribe = { navController.navigate("create_tribe") },
                        onNavigateToTribeDetail = { tribeId -> navController.navigate("tribe_detail/$tribeId") },
                        viewModel = adminViewModel)
                }
            }
            composable("tribe_detail/{tribeId}") { backStackEntry ->
                if (user?.roleEnum?.canManageTribes == true) {
                    val tribeId = backStackEntry.arguments?.getString("tribeId") ?: return@composable
                    TribeDetailsScreen(
                        tribeId = tribeId,
                        onNavigateBack = { navController.popBackStack() },
                        onManagePeople = { id -> navController.navigate("manage_tribe_people/$id") },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("manage_tribe_people/{tribeId}") { backStackEntry ->
                if (user?.roleEnum?.canManageTribes == true) {
                    val tribeId = backStackEntry.arguments?.getString("tribeId") ?: return@composable
                    ManageTribePeopleScreen(
                        tribeId = tribeId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("create_tribe") { 
                if (user?.roleEnum?.canManageTribes == true) {
                    CreateTribeScreen(onNavigateBack = { navController.popBackStack() }, viewModel = adminViewModel)
                }
            }
            composable("manage_groups") {
                if (user?.roleEnum?.canManageGroups == true) {
                    ManageGroupsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onCreateGroup = { navController.navigate("create_group") },
                        onEditGroup = { groupId -> navController.navigate("group_info/$groupId") },
                        viewModel = adminViewModel)
                }
            }
            composable("create_group?groupId={groupId}") { backStackEntry ->
                if (user?.roleEnum?.canManageGroups == true) {
                    val groupId = backStackEntry.arguments?.getString("groupId")
                    CreateGroupScreen(
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("create_group") { 
                if (user?.roleEnum?.canManageGroups == true) {
                    CreateGroupScreen(
                        onNavigateBack = { navController.popBackStack() }, 
                        viewModel = adminViewModel
                    )
                }
            }
            composable("tribe_wars_admin") {
                if (user?.roleEnum?.canManagePoints == true) {
                    TribeWarsAdminScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAddScore = { tribeId -> navController.navigate("add_score?tribeId=$tribeId") },
                        onNavigateToCreateEvent = { navController.navigate("create_tribe_event") },
                        mainViewModel = mainViewModel,
                        viewModel = adminViewModel
                    )
                }
            }
            composable("add_score?tribeId={tribeId}") { backStackEntry ->
                if (user?.roleEnum?.canManagePoints == true) {
                    val tribeId = backStackEntry.arguments?.getString("tribeId")
                    AddScoreScreen(
                        initialTribeId = tribeId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("create_tribe_event") {
                if (user?.roleEnum?.canManagePoints == true) {
                    CreateTribeEventScreen(onNavigateBack = { navController.popBackStack() }, viewModel = adminViewModel)
                }
            }
            composable("notification_prefs") { NotificationPreferencesScreen(onNavigateBack = { navController.popBackStack() }, viewModel = mainViewModel) }
            composable("announcements") { 
                if (user?.roleEnum?.canManageAnnouncements == true) {
                    AnnouncementsScreen(onNavigateBack = { navController.popBackStack() }, mainViewModel = mainViewModel, adminViewModel = adminViewModel)
                }
            }
            composable("profile") {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNotificationPrefs = { navController.navigate("notification_prefs") },
                    viewModel = mainViewModel
                )
            }
            composable("comments/{postId}") { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                CommentThreadScreen(
                    postId = postId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = mainViewModel)
            }
            composable("event_detail/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                EventDetailScreen(
                    eventId = eventId,
                    onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}

data class NavigationItem(val route: String, val label: String, @field:DrawableRes val iconRes: Int)
