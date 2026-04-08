package com.example.bloom

import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext

sealed class Screen(val route: String, val icon: ImageVector, val title : String) {
    object Home : Screen("home", Icons.Filled.Home, "Home")
    object Journal : Screen("journal", Icons.Filled.Create, "Journal")
    object Tasks : Screen("tasks", Icons.Filled.Task, "Tasks")
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Journal,
    Screen.Tasks
)


@Composable
fun BottomNavigationBar(currentRoute: String) {
    val context = LocalContext.current

    NavigationBar (containerColor = MaterialTheme.colorScheme.tertiary) {
        bottomNavItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        when (screen) {
                            Screen.Home -> {
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }

                            Screen.Journal -> {
                                val intent = Intent(context, JournalActivity::class.java)
                                context.startActivity(intent)
                            }

                            Screen.Tasks -> {
                                val intent = Intent(context, TaskActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            )
        }
    }
}
