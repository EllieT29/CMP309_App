package com.example.bloom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.bloom.ui.theme.BloomTheme


// Sealed class to define the screens in the app
sealed class Screen(val route: String, val icon: ImageVector, val title: String) {
    object Test : Screen("test", Icons.Filled.Menu, "Test")
}

// List of items for the bottom navigation bar
val bottomNavItems = listOf(
    Screen.Test,
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BloomTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier = Modifier) {
    // Remember the NavController
    val navController = rememberNavController()

    // Scaffold provides a framework for the app's layout
    Scaffold(
        modifier = modifier,
        topBar = {
            // Top app bar
            TopBar(navController = navController)
        },
        bottomBar = {
            // Bottom navigation bar
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        // NavHost for navigating between screens
        NavHost(
            navController = navController, startDestination = Screen.Test.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            // Composable for the Test screen
            composable(Screen.Test.route) {
                Greeting()
            }
        }
    }
}


// Composable for the top app bar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    var menuExpanded by remember { mutableStateOf(false) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // A screen is a top-level screen if it's in our bottom nav.
    val isTopLevelDestination = bottomNavItems.any { it.route == currentDestination?.route }

    TopAppBar(
        title = { Text("Bloom") },
        navigationIcon = {
            // Show the back button if we're not on a top-level screen.
            if (!isTopLevelDestination) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // More icon button to expand the dropdown menu
            IconButton(onClick = { menuExpanded = !menuExpanded }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "More"
                )
            }
            // Dropdown menu for navigation
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false }
            ) {
                // Dropdown menu item for Movies
                DropdownMenuItem(
                    text = { Text("Test") },
                    onClick = {
                        navController.navigate(Screen.Test.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        menuExpanded = false
                    }
                )
            }
        }
    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val bottomBarRoutes = setOf(Screen.Test.route)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    if (currentDestination?.route in bottomBarRoutes) {
        NavigationBar {
            bottomNavItems.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = null) },
                    label = { Text(screen.title) },
                    selected = currentDestination?.hierarchy?.any {
                        it.route == screen.route
                    } == true,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            }
        }
    }
}



@Composable
fun Greeting() {
    Text(
        text = "Hello there!",
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BloomTheme {
        Greeting()
    }
}