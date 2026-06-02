package dev.bti.kdym

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import dev.bti.kdym.data.models.UserRole
import dev.bti.kdym.data.repositories.RepositoryProvider
import dev.bti.kdym.ui.App
import dev.bti.kdym.ui.components.FeedbackBanner
import dev.bti.kdym.ui.components.GlassNavigationBar
import dev.bti.kdym.ui.screens.admin.*
import dev.bti.kdym.ui.screens.common.PdfViewerScreen
import dev.bti.kdym.ui.screens.events.EventDetailScreen
import dev.bti.kdym.ui.screens.events.EventsScreen
import dev.bti.kdym.ui.screens.groups.*
import dev.bti.kdym.ui.screens.home.*
import dev.bti.kdym.ui.screens.play.ClipsPager
import dev.bti.kdym.ui.screens.play.PlayScreen
import dev.bti.kdym.ui.screens.profile.ProfileScreen
import dev.bti.kdym.ui.screens.profile.PublicProfileScreen
import dev.bti.kdym.ui.screens.settings.NotificationPreferencesScreen
import dev.bti.kdym.ui.screens.settings.SettingsScreen
import dev.bti.kdym.ui.theme.KdymTheme
import dev.bti.kdym.viewmodels.AdminViewModel
import dev.bti.kdym.viewmodels.GroupsViewModel
import dev.bti.kdym.viewmodels.MainViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = 0x00
            )
        )
        RepositoryProvider.initialize(this)
        super.onCreate(savedInstanceState)
        setContent {
            KdymTheme {
                App(mainViewModel)
            }
        }

    }
}

@Composable
fun NotificationPermissionBanner() {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    if (!hasPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color(0xFFEF4444).copy(0.1f),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFEF4444).copy(0.2f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    tint = Color(0xFFEF4444)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Notifications Disabled",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Don't miss out on important updates.",
                        color = Color.White.copy(0.7f),
                        fontSize = 12.sp
                    )
                }
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Enable", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MainNavigation(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val viewModelStoreOwner = LocalViewModelStoreOwner.current ?: return

    // Scoped view models injected via Hilt
    val groupsViewModel: GroupsViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = "groups_vm"
    )
    val adminViewModel: AdminViewModel = hiltViewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        key = "admin_vm"
    )

    val user by mainViewModel.user.collectAsState()
    val appConfig by mainViewModel.appConfig.collectAsState()
    val isCampMode = appConfig?.campModeEnabled ?: false
    val uiState by mainViewModel.uiState.collectAsState()

    // Define main tabs
    val baseItems = listOf(
        NavigationItem("home", "Home", R.drawable.ic_home),
        NavigationItem("events", "Events", R.drawable.ic_calendar),
        NavigationItem("play", "Play", R.drawable.ic_play),
        NavigationItem("community", "Community", R.drawable.ic_group_msg),
        NavigationItem("settings", "Settings", R.drawable.ic_profile_filled)
    )

    // Filter items based on role and camp mode
    val items = remember(user, isCampMode) {
        baseItems.filter { item ->
            when (item.route) {
                "play" -> {
                    // Play: Show if camp mode is on OR user has special access (Admin, Tribe Leader, Group Leader)
                    isCampMode || user?.isAdmin == true || user?.roleEnum == UserRole.tribeLeader || user?.roleEnum == UserRole.groupLeader
                }
                "community" -> {
                    // Community: Hide for "public" and "pending" roles (only show for "camper" and above)
                    user?.roleEnum?.canAccessCampContent == true || user?.isAdmin == true
                }
                else -> true
            }
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                // Global loading indicator
                if (uiState.isLoading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color(0xFFEF4444),
                        trackColor = Color.Transparent
                    )
                }
                
                // Global feedback messages
                uiState.feedbackMessage?.let { message ->
                    FeedbackBanner(message = message, isError = uiState.isError)
                }
            }
        },
        bottomBar = {
            // Only show navigation bar on top-level routes
            if (currentRoute in items.map { it.route }) {
                GlassNavigationBar(navController, items)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.fillMaxSize()
        ) {
            // --- Primary Tabs ---
            composable("home") {
                HomeScreen(
                    onNavigateToComments = { postId -> navController.navigate("comments/$postId") },
                    onNavigateToRequestAccess = { navController.navigate("request_camp_access") },
                    onCreatePostClick = { navController.navigate("create_home_post") }, 
                    viewModel = mainViewModel
                )
            }
            composable("events") {
                EventsScreen(
                    onNavigateToEventDetail = { eventId -> navController.navigate("event_detail/$eventId") },
                    onNavigateToCreateEvent = { navController.navigate("create_event") },
                    mainViewModel = mainViewModel
                )
            }
            composable("play") {
                PlayScreen(
                    onNavigateToCreatePlayItem = { },
                    onNavigateToClips = { clipPath -> navController.navigate("clips/$clipPath") },
                    viewModel = mainViewModel
                )
            }
            composable("clips/{clipId}/{kind}") { backStackEntry ->
                val clipId = backStackEntry.arguments?.getString("clipId") ?: return@composable
                val kind = backStackEntry.arguments?.getString("kind") ?: return@composable
                ClipsPager(
                    initialClipId = clipId,
                    kind = kind,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = mainViewModel
                )
            }
            composable("community") {
                CommunityScreen(
                    onNavigateToChat = { groupId -> navController.navigate("chat/$groupId") },
                    onNavigateToAnnouncements = { navController.navigate("announcements") },
                    onNavigateToTribeWars = { navController.navigate("tribe_wars_admin") },
                    onExploreGroups = { navController.navigate("explore_groups") },
                    onCreateGroup = { navController.navigate("create_group") },
                    onAddScore = { tribeId ->
                        if (tribeId != null) navController.navigate("add_score?tribeId=$tribeId")
                        else navController.navigate("add_score")
                    },
                    onCreateTribeEvent = { eventId ->
                        if (eventId != null) navController.navigate("create_tribe_event?eventId=$eventId")
                        else navController.navigate("create_tribe_event")
                    },
                    mainViewModel = mainViewModel,
                    viewModel = groupsViewModel,
                    adminViewModel = adminViewModel
                )
            }

            // --- Secondary Flows (Groups & Chat) ---
            composable("explore_groups") {
                ExploreGroupsScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = groupsViewModel
                )
            }
            composable("chat/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                ChatScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToInfo = { id -> navController.navigate("group_info/$id") },
                    onNavigateToCreatePoll = { id -> navController.navigate("create_poll?groupId=$id") },
                    onNavigateToProfile = { userId -> navController.navigate("public_profile/$userId") },
                    onNavigateToPdf = { url -> navController.navigate("pdf_viewer/${Uri.encode(url)}") },
                    viewModel = groupsViewModel,
                    adminViewModel = adminViewModel
                )
            }
            composable("pdf_viewer/{url}") { backStackEntry ->
                val url = backStackEntry.arguments?.getString("url") ?: return@composable
                PdfViewerScreen(
                    url = url,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("group_info/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                GroupInfoScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProfile = { userId -> navController.navigate("public_profile/$userId") },
                    groupsViewModel = groupsViewModel,
                    mainViewModel = mainViewModel,
                    adminViewModel = adminViewModel
                )
            }

            // --- Command Center (Admin Only) ---
            composable("command") {
                if (user?.hasCommandAccess == true) {
                    CommandCenterScreen(
                        onNavigateToHub = { navController.navigate("command_hub") },
                        onNavigateToAnnouncements = { navController.navigate("announcements") },
                        onNavigateToTribeWars = { navController.navigate("tribe_wars_admin") },
                        onNavigateToProfile = { navController.navigate("profile") },
                        onNavigateToChurches = { navController.navigate("admin_churches") },
                        viewModel = mainViewModel
                    )
                }
            }
            composable("command_hub") {
                // Checking both the boolean flag and the roleEnum equivalent
                if ((user?.isAdmin == true) || (user?.roleEnum?.isAdmin == true)) {
                    CommandCenterHubScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToSettings = { navController.navigate("camp_settings") },
                        onNavigateToApprovals = { navController.navigate("approvals") },
                        onNavigateToTribes = { navController.navigate("manage_tribes") },
                        onNavigateToTribeWars = { navController.navigate("tribe_wars_admin") },
                        onNavigateToAnnouncements = { navController.navigate("announcements") },
                        onNavigateToUrgentOverlay = { navController.navigate("create_urgent_overlay") },
                        onNavigateToGroups = { navController.navigate("manage_groups") },
                        onNavigateToCreateEvent = { navController.navigate("create_home_post") },
                        onNavigateToPostPlay = { },
                        onNavigateToUsers = { navController.navigate("admin_users") },
                        onNavigateToChurches = { navController.navigate("admin_churches") },
                        mainViewModel = mainViewModel,
                        viewModel = adminViewModel
                    )
                }
            }
            composable("camp_settings") {
                if (user?.canManageCampSettings == true) {
                    CampSettingsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("approvals") {
                if (user?.canManageApprovals == true) {
                    UsersScreen(
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("admin_users") {
                if (user?.hasCommandAccess == true) {
                    UsersScreen(
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("admin_churches") {
                if (user?.hasCommandAccess == true) {
                    ChurchesScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToChurchDetail = { churchId -> 
                            navController.navigate("church_detail/$churchId") 
                        },
                        onAddChurch = { navController.navigate("admin_create_church") },
                        onEditChurch = { churchId -> navController.navigate("admin_edit_church/$churchId") },
                        onManagePastor = { churchId -> navController.navigate("admin_manage_pastor/$churchId") },
                        adminViewModel = adminViewModel,
                        mainViewModel = mainViewModel
                    )
                }
            }
            composable(
                route = "church_detail/{churchId}",
                arguments = listOf(navArgument("churchId") { type = NavType.StringType })
            ) { backStackEntry ->
                val churchId = backStackEntry.arguments?.getString("churchId") ?: return@composable
                ChurchDetailScreen(
                    churchId = churchId,
                    onNavigateBack = { navController.popBackStack() },
                    onEditChurch = { id -> navController.navigate("admin_edit_church/$id") },
                    onManagePastor = { id -> navController.navigate("admin_manage_pastor/$id") },
                    mainViewModel = mainViewModel
                )
            }
            composable("admin_create_church") {
                CreateChurchScreen(
                    onNavigateBack = { navController.popBackStack() },
                    adminViewModel = adminViewModel,
                    mainViewModel = mainViewModel
                )
            }
            composable(
                route = "admin_edit_church/{churchId}",
                arguments = listOf(navArgument("churchId") { type = NavType.StringType })
            ) { backStackEntry ->
                val churchId = backStackEntry.arguments?.getString("churchId") ?: return@composable
                CreateChurchScreen(
                    churchId = churchId,
                    onNavigateBack = { navController.popBackStack() },
                    adminViewModel = adminViewModel,
                    mainViewModel = mainViewModel
                )
            }
            composable(
                route = "admin_manage_pastor/{churchId}",
                arguments = listOf(navArgument("churchId") { type = NavType.StringType })
            ) { backStackEntry ->
                val churchId = backStackEntry.arguments?.getString("churchId") ?: return@composable
                PastorManagerScreen(
                    churchId = churchId,
                    onNavigateBack = { navController.popBackStack() },
                    adminViewModel = adminViewModel,
                    mainViewModel = mainViewModel
                )
            }

            // --- Tribe Management ---
            composable("manage_tribes") {
                if (user?.canManageTribes == true) {
                    ManageTribesScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onCreateTribe = { navController.navigate("create_tribe") },
                        onEditTribe = { id -> navController.navigate("create_tribe?tribeId=$id") },
                        onNavigateToTribeDetail = { tribeId -> navController.navigate("tribe_detail/$tribeId") },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("tribe_detail/{tribeId}") { backStackEntry ->
                if (user?.canManageTribes == true) {
                    val tribeId =
                        backStackEntry.arguments?.getString("tribeId") ?: return@composable
                    TribeDetailsScreen(
                        tribeId = tribeId,
                        onNavigateBack = { navController.popBackStack() },
                        onManagePeople = { id -> navController.navigate("manage_tribe_people/$id") },
                        onNavigateToChat = { id -> navController.navigate("chat/$id") },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("manage_tribe_people/{tribeId}") { backStackEntry ->
                if (user?.canManageTribes == true) {
                    val tribeId =
                        backStackEntry.arguments?.getString("tribeId") ?: return@composable
                    ManageTribePeopleScreen(
                        tribeId = tribeId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("create_tribe") {
                if (user?.canManageTribes == true) {
                    CreateTribeScreen(
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("create_tribe?tribeId={tribeId}") { backStackEntry ->
                if (user?.canManageTribes == true) {
                    val tribeId = backStackEntry.arguments?.getString("tribeId")
                    CreateTribeScreen(
                        tribeId = tribeId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }

            // --- Tribe Wars Admin ---
            composable("tribe_wars_admin") {
                if (user?.canManagePoints == true) {
                    TribeWarsAdminScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onNavigateToAddScore = { tribeId -> navController.navigate("add_score?tribeId=$tribeId") },
                        onNavigateToCreateEvent = { eventId -> 
                            if (eventId == null) navController.navigate("create_tribe_event")
                            else navController.navigate("create_tribe_event?eventId=$eventId")
                        },
                        mainViewModel = mainViewModel,
                        viewModel = adminViewModel
                    )
                }
            }
            composable("add_score?tribeId={tribeId}") { backStackEntry ->
                if (user?.canManagePoints == true) {
                    val tribeId = backStackEntry.arguments?.getString("tribeId")
                    AddScoreScreen(
                        initialTribeId = tribeId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel,
                        mainViewModel = mainViewModel
                    )
                }
            }
            composable("create_tribe_event?eventId={eventId}") { backStackEntry ->
                if (user?.canManagePoints == true) {
                    val eventId = backStackEntry.arguments?.getString("eventId")
                    CreateTribeEventScreen(
                        eventId = eventId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("create_tribe_event") {
                if (user?.canManagePoints == true) {
                    CreateTribeEventScreen(
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }

            // --- Group Management ---
            composable("manage_groups") {
                if (user?.canManageGroups == true) {
                    ManageGroupsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        onCreateGroup = { navController.navigate("create_group") },
                        onManageGroup = { groupId -> navController.navigate("manage_group_detail/$groupId") },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("manage_group_detail/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                GroupManagementDetailScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() },
                    onOpenChat = { id -> navController.navigate("chat/$id") },
                    onInfoAndPerms = { id -> navController.navigate("group_info_perms/$id") },
                    onMembersAndLeaders = { id -> navController.navigate("group_members_leaders/$id") },
                    viewModel = adminViewModel
                )
            }
            composable("group_info_perms/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                GroupInfoAndPermsScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() },
                    adminViewModel = adminViewModel
                )
            }
            composable("group_members_leaders/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId") ?: return@composable
                GroupMembersAndLeadersScreen(
                    groupId = groupId,
                    onNavigateBack = { navController.popBackStack() },
                    adminViewModel = adminViewModel
                )
            }
            composable("create_group?groupId={groupId}") { backStackEntry ->
                if (user?.canManageGroups == true) {
                    val groupId = backStackEntry.arguments?.getString("groupId")
                    CreateGroupScreen(
                        groupId = groupId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }

            // --- System Features (Polls, Announcements, Profile, Comments) ---
            composable("settings") {
                SettingsScreen(
                    onNavigateToProfile = { navController.navigate("profile") },
                    onNavigateToNotificationPrefs = { navController.navigate("notification_prefs") },
                    onNavigateToCommandHub = { navController.navigate("command_hub") },
                    onNavigateToChurches = { navController.navigate("admin_churches") },
                    onNavigateToRequestAccess = { navController.navigate("request_camp_access") },
                    viewModel = mainViewModel
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
            composable("announcements") {
                if (user?.canManageAnnouncements == true) {
                    AnnouncementsScreen(
                        onNavigateBack = { navController.popBackStack() },
                        mainViewModel = mainViewModel,
                        adminViewModel = adminViewModel
                    )
                }
            }
            composable("create_home_post") {
                CreateHomePostScreen(
                    onNavigateBack = { navController.popBackStack() },
                    adminViewModel = adminViewModel
                )
            }
            composable("create_urgent_overlay") {
                CreateUrgentOverlayScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = adminViewModel
                )
            }
            composable("profile") {
                ProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = mainViewModel,
                )
            }
            composable(
                route = "public_profile/{userId}",
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                PublicProfileScreen(
                    userId = userId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = mainViewModel
                )
            }
            composable("notification_prefs") {
                NotificationPreferencesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = mainViewModel
                )
            }
            composable("comments/{postId}") { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                CommentThreadScreen(
                    postId = postId,
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = mainViewModel
                )
            }
            composable("request_camp_access") {
                RequestCampAccessScreen(
                    onNavigateBack = { navController.popBackStack() },
                    viewModel = mainViewModel
                )
            }
            composable("create_event?eventId={eventId}&parentEventId={parentEventId}") { backStackEntry ->
                if (user?.isAdmin == true) {
                    val eventId = backStackEntry.arguments?.getString("eventId")
                    val parentEventId = backStackEntry.arguments?.getString("parentEventId")
                    CreateEventScreen(
                        eventId = eventId,
                        parentEventId = parentEventId,
                        onNavigateBack = { navController.popBackStack() },
                        viewModel = adminViewModel
                    )
                }
            }
            composable("event_detail/{eventId}") { backStackEntry ->
                val eventId = backStackEntry.arguments?.getString("eventId") ?: return@composable
                EventDetailScreen(
                    eventId = eventId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { id -> navController.navigate("create_event?eventId=$id") },
                    onNavigateToCreateScheduleItem = { parentId -> navController.navigate("create_event?parentEventId=$parentId") },
                    onNavigateToDetail = { id -> 
                        navController.navigate("event_detail/$id") {
                            popUpTo("event_detail/{eventId}") { inclusive = true }
                        }
                    },
                    adminViewModel = adminViewModel
                )
            }
        }
    }
}

/**
 * Metadata for items displayed in the bottom navigation bar.
 */
data class NavigationItem(val route: String, val label: String, @field:DrawableRes val iconRes: Int)
