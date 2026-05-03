package dev.bti.kdym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.view.WindowCompat
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import dev.bti.kdym.ui.theme.KdymTheme
import dev.bti.kdym.viewmodels.MainViewModel
import dev.bti.kdym.ui.screens.home.HomeScreen
import dev.bti.kdym.ui.screens.events.EventsScreen
import dev.bti.kdym.ui.screens.play.PlayScreen
import dev.bti.kdym.ui.screens.groups.GroupsScreen
import dev.bti.kdym.ui.screens.admin.CommandCenterScreen
import dev.bti.kdym.ui.components.GlassCard
import dev.bti.kdym.ui.theme.CyanAccent
import dev.bti.kdym.ui.theme.RubikFontFamily
import dev.bti.kdym.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.isNavigationBarContrastEnforced = false

        setContent {
            KdymTheme {
                MainNavigation(mainViewModel)
            }
        }



    }
}

@Composable
fun MainNavigation(viewModel: MainViewModel) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            CustomBottomBar(navController)
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            composable("home") { HomeScreen() }
            composable("events") { EventsScreen() }
            composable("play") { PlayScreen() }
            composable("groups") { GroupsScreen() }
            composable("command") { CommandCenterScreen() }
        }
    }
}

@Composable
fun CustomBottomBar(navController: androidx.navigation.NavHostController) {
    val items = listOf(
        NavigationItem("home", "Home", Icons.Outlined.Home),
        NavigationItem("events", "Events", Icons.Outlined.CalendarMonth),
        NavigationItem("play", "Play", Icons.Outlined.SmartDisplay),
        NavigationItem("groups", "Groups", Icons.AutoMirrored.Outlined.Chat),
        NavigationItem("command", "Command", Icons.Outlined.AccountCircle)
    )
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            cornerRadius = 40.dp,
            backgroundColor = Color.Black.copy(alpha = 0.3f),
            borderColor = Color.White.copy(alpha = 0.15f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                            .padding(vertical = 4.dp)
                            .weight(1f)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = if (selected) Color(0xFFFCD34D) else Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.label,
                            color = if (selected) Color(0xFFFCD34D) else Color.White.copy(alpha = 0.6f),
                            fontSize = 10.sp,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            fontFamily = RubikFontFamily
                        )
                    }
                }
            }
        }
    }
}

data class NavigationItem(val route: String, val label: String, val icon: ImageVector)
