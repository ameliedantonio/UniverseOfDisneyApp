package fr.isen.amelie.universeofdisneyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import fr.isen.amelie.universeofdisneyapp.model.Movie
import fr.isen.amelie.universeofdisneyapp.model.Universe
import fr.isen.amelie.universeofdisneyapp.screen.LoginScreen
import fr.isen.amelie.universeofdisneyapp.screen.MovieDetailScreen
import fr.isen.amelie.universeofdisneyapp.screen.MovieScreen
import fr.isen.amelie.universeofdisneyapp.screen.RegisterScreen
import fr.isen.amelie.universeofdisneyapp.screen.UniverseScreen
import fr.isen.amelie.universeofdisneyapp.ui.theme.UniverseOfDisneyAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UniverseOfDisneyAppTheme {

                var screen by remember { mutableStateOf("login") }
                var selectedUniverse by remember { mutableStateOf(Universe()) }
                var selectedMovie by remember { mutableStateOf(Movie()) }

                when (screen) {
                    "login" -> LoginScreen(
                        onLoginSuccess = {
                            screen = "home"
                        },
                        onGoToRegister = {
                            screen = "register"
                        }
                    )

                    "register" -> RegisterScreen(
                        onRegisterSuccess = {
                            screen = "login"
                        },
                        onGoToLogin = {
                            screen = "login"
                        }
                    )

                    "home" -> UniverseScreen(
                        onUniverseClick = { universe ->
                            selectedUniverse = universe
                            screen = "movies"
                        }
                    )

                    "movies" -> MovieScreen(
                        universeId = selectedUniverse.id,
                        onMovieClick = { movie ->
                            selectedMovie = movie
                            screen = "movieDetail"
                        }
                    )

                    "movieDetail" -> MovieDetailScreen(
                        movie = selectedMovie,
                        onBackClick = {
                            screen = "movies"
                        }
                    )
                }
            }
        }
    }
}