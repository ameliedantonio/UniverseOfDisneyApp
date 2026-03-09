package fr.isen.amelie.universeofdisneyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import fr.isen.amelie.universeofdisneyapp.screen.LoginScreen
import fr.isen.amelie.universeofdisneyapp.screen.RegisterScreen
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import fr.isen.amelie.universeofdisneyapp.ui.theme.UniverseOfDisneyAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            UniverseOfDisneyAppTheme {

                var screen by remember { mutableStateOf("login") }

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

                    "home" -> {
                        Text("Bienvenue dans l'application Disney")
                    }
                }
            }
        }
    }
}