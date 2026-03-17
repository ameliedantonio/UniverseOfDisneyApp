package fr.isen.amelie.universeofdisneyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.isen.amelie.universeofdisneyapp.activity.Movie
import fr.isen.amelie.universeofdisneyapp.screen.HomeScreen
import fr.isen.amelie.universeofdisneyapp.screen.LoginScreen
import fr.isen.amelie.universeofdisneyapp.screen.MovieDetailScreen
import fr.isen.amelie.universeofdisneyapp.screen.MovieScreen
import fr.isen.amelie.universeofdisneyapp.screen.MyMoviesScreen
import fr.isen.amelie.universeofdisneyapp.screen.ProfileScreen
import fr.isen.amelie.universeofdisneyapp.screen.RegisterScreen
import fr.isen.amelie.universeofdisneyapp.screen.SearchScreen
import fr.isen.amelie.universeofdisneyapp.screen.UniverseScreen
import fr.isen.amelie.universeofdisneyapp.ui.theme.UniverseOfDisneyAppTheme
import fr.isen.amelie.universeofdisneyapp.screen.EditProfileScreen
import com.google.firebase.auth.FirebaseAuth
import fr.isen.amelie.universeofdisneyapp.screen.OwnedMoviesScreen
import fr.isen.amelie.universeofdisneyapp.screen.SharedGetRidScreen
import fr.isen.amelie.universeofdisneyapp.screen.WantToGetRidScreen


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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        colorResource(id = R.color.blue_dark),
                        colorResource(id = R.color.blue_mid),
                        colorResource(id = R.color.blue_light),
                        colorResource(id = R.color.blue_soft_white)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = Color.Transparent
                    ) {
                        NavigationBarItem(
                            selected = currentRoute == "home",
                            onClick = {
                                navController.navigate("home") {
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = "Home"
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorResource(id = R.color.blue_light),
                                unselectedIconColor = colorResource(id = R.color.blue_dark),
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = currentRoute == "universes" || currentRoute?.startsWith("movies/") == true,
                            onClick = {
                                navController.navigate("universes") {
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    Icons.Default.Menu,
                                    contentDescription = "Universes"
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorResource(id = R.color.blue_light),
                                unselectedIconColor = colorResource(id = R.color.blue_dark),
                                indicatorColor = Color.Transparent
                            )
                        )
                        val isSelected = currentRoute == "mymovies"

                        NavigationBarItem(
                            selected = isSelected,
                            onClick = {
                                navController.navigate("mymovies") {
                                    launchSingleTop = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent
                            ),
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(
                                            color = if (isSelected)
                                                colorResource(id = R.color.blue_light)
                                            else
                                                colorResource(id = R.color.blue_dark),
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Movie,
                                        contentDescription = "My movies",
                                        tint = Color.White,
                                    )
                                }
                            }
                        )
                        NavigationBarItem(
                            selected = currentRoute == "search",
                            onClick = {
                                navController.navigate("search") {
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search"
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorResource(id = R.color.blue_light),
                                unselectedIconColor = colorResource(id = R.color.blue_dark),
                                indicatorColor = Color.Transparent
                            )
                        )
                        NavigationBarItem(
                            selected = currentRoute == "profile",
                            onClick = {
                                navController.navigate("profile") {
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = "Profile"
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = colorResource(id = R.color.blue_light),
                                unselectedIconColor = colorResource(id = R.color.blue_dark),
                                indicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "login",
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
                            navController.navigate("movies/${universe.id}/${universe.name}")
                        },
                        onGenreClick = { genre ->
                            navController.navigate("moviesByGenre/$genre")
                        }
                    )
                }
                composable(
                    route = "moviesByGenre/{genre}",
                    arguments = listOf(
                        navArgument("genre") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val genre = backStackEntry.arguments?.getString("genre") ?: ""

                    MovieScreen(
                        universeId = "",
                        genre = genre,
                        universeName = genre,
                        onMovieClick = { movie ->
                            selectedMovie = movie
                            navController.navigate("movieDetail")
                        },
                        onBackToUniverses = {
                            navController.popBackStack()
                        }
                    )
                }
                composable(
                    route = "movies/{universeId}/{universeName}",
                    arguments = listOf(
                        navArgument("universeId") { type = NavType.StringType },
                        navArgument("universeName") { type = NavType.StringType }
                        )
                ) { backStackEntry ->
                    val universeId = backStackEntry.arguments?.getString("universeId") ?: ""
                    val universeName = backStackEntry.arguments?.getString("universeName") ?: ""

                    MovieScreen(
                        universeId = universeId,
                        universeName = universeName,
                        onMovieClick = { movie ->
                            selectedMovie = movie
                            navController.navigate("movieDetail")
                        },
                        onBackToUniverses = {
                            navController.navigate("universes")
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
                composable("mymovies") {
                    MyMoviesScreen(
                        onMovieClick = { movie ->
                            selectedMovie = movie
                            navController.navigate("movieDetail")
                        }
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        onLogoutClick = {
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onEditProfileClick = {
                            navController.navigate("edit_profile")
                        },
                        onOwnedClick = {
                            navController.navigate("ownedMovies")
                        },
                        onGetRidClick = {
                            navController.navigate("wantToGetRid")
                        },
                        onSharedGetRidClick = {
                            navController.navigate("sharedGetRid")
                        }
                    )
                }
                composable("edit_profile") {
                    EditProfileScreen(
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }
                composable("ownedMovies") {
                    OwnedMoviesScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onMovieClick = { movie ->
                            selectedMovie = movie
                            navController.navigate("movieDetail")
                        }
                    )
                }
                composable("wantToGetRid") {
                    WantToGetRidScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                        onMovieClick = { movie ->
                            selectedMovie = movie
                            navController.navigate("movieDetail")
                        }
                    )
                }
                composable("sharedGetRid") {
                    SharedGetRidScreen(
                        onBackClick = {
                            navController.popBackStack()
                        },
                    ) { movie ->
                        selectedMovie = movie
                        navController.navigate("movieDetail")
                    }
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = Color.White
        )
    )
}