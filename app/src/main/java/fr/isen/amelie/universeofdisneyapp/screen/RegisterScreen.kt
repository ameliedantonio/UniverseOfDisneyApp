package fr.isen.amelie.universeofdisneyapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Créer un compte")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser

                                if (user != null) {
                                    val userData = hashMapOf(
                                        "uid" to user.uid,
                                        "email" to email
                                    )

                                    db.collection("users")
                                        .document(user.uid)
                                        .set(userData)
                                        .addOnSuccessListener {
                                            Toast.makeText(
                                                context,
                                                "Compte créé",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onRegisterSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(
                                                context,
                                                "Erreur Firestore : ${e.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    "Erreur : ${task.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(
                        context,
                        "Remplis tous les champs",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("S'inscrire")
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = onGoToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Déjà un compte ? Se connecter")
        }
    }
}