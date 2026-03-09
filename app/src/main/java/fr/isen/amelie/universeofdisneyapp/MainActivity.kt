package fr.isen.amelie.universeofdisneyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import fr.isen.amelie.universeofdisneyapp.model.Movie
import fr.isen.amelie.universeofdisneyapp.screen.HomeScreen
import fr.isen.amelie.universeofdisneyapp.screen.LoginScreen
import fr.isen.amelie.universeofdisneyapp.screen.MovieDetailScreen
import fr.isen.amelie.universeofdisneyapp.screen.MovieScreen
import fr.isen.amelie.universeofdisneyapp.screen.ProfileScreen
import fr.isen.amelie.universeofdisneyapp.screen.RegisterScreen
import fr.isen.amelie.universeofdisneyapp.screen.SearchScreen
import fr.isen.amelie.universeofdisneyapp.screen.UniverseScreen
import fr.isen.amelie.universeofdisneyapp.ui.theme.UniverseOfDisneyAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniverseOfDisneyAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var selectedMovie by remember { mutableStateOf(Movie()) }

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val showBottomBar = currentRoute != "login" && currentRoute != "register"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = {
                            navController.navigate("home") {
                                launchSingleTop = true
                            }
                        },
                        label = { Text("Home") },
                        icon = {
                            Icon(Icons.Default.Home, contentDescription = "Home")
                        }
                    )

                    NavigationBarItem(
                        selected = currentRoute == "universes" || currentRoute?.startsWith("movies/") == true,
                        onClick = {
                            navController.navigate("universes") {
                                launchSingleTop = true
                            }
                        },
                        label = { Text("Universes") },
                        icon = {
                            Icon(Icons.Default.Menu, contentDescription = "Universes")
                        }
                    )

                    NavigationBarItem(
                        selected = currentRoute == "search",
                        onClick = {
                            navController.navigate("search") {
                                launchSingleTop = true
                            }
                        },
                        label = { Text("Search") },
                        icon = {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    )

                    NavigationBarItem(
                        selected = currentRoute == "profile",
                        onClick = {
                            navController.navigate("profile") {
                                launchSingleTop = true
                            }
                        },
                        label = { Text("Profile") },
                        icon = {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                        }
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onGoToRegister = {
                        navController.navigate("register")
                    }
                )
            }

            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("login")
                    },
                    onGoToLogin = {
                        navController.navigate("login")
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    onMovieClick = { movie ->
                        selectedMovie = movie
                        navController.navigate("movieDetail")
                    }
                )
            }

            composable("universes") {
                UniverseScreen(
                    onUniverseClick = { universe ->
                        navController.navigate("movies/${universe.id}")
                    }
                )
            }

            composable(
                route = "movies/{universeId}",
                arguments = listOf(navArgument("universeId") { type = NavType.StringType })
            ) { backStackEntry ->
                val universeId = backStackEntry.arguments?.getString("universeId") ?: ""

                MovieScreen(
                    universeId = universeId,
                    onMovieClick = { movie ->
                        selectedMovie = movie
                        navController.navigate("movieDetail")
                    },
                    onBackToUniverses = {
                        navController.navigate("universes")
                    },
                    onProfileClick = {
                        navController.navigate("profile")
                    }
                )
            }

            composable("search") {
                SearchScreen(
                    onMovieClick = { movie ->
                        selectedMovie = movie
                        navController.navigate("movieDetail")
                    }
                )
            }

            composable("profile") {
                ProfileScreen(
                    onBackClick = {
                        navController.navigate("universes")
                    },
                    onLogoutClick = {
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                )
            }

            composable("movieDetail") {
                MovieDetailScreen(
                    movie = selectedMovie,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        }
    )
}