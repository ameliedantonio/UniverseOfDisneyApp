package fr.isen.amelie.universeofdisneyapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import fr.isen.amelie.universeofdisneyapp.R

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onGoToLogin: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val context = LocalContext.current

    var firstName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 70.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = R.color.blue_soft_white)
                    )
                ) {
                    Box(
                        modifier = Modifier.size(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "App icon",
                            tint = colorResource(id = R.color.blue_dark), //icon temporaire, on mettra notre futur logo
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "CREATE AN ACCOUNT",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = colorResource(id = R.color.blue_soft_white)
            )
            Spacer(modifier = Modifier.height(8.dp)) //TODO

            Text(
                text = "Sign up to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(id = R.color.blue_soft_white)
            )
            Spacer(modifier = Modifier.height(30.dp)) //TODO
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter) //TODO
                .padding(top = 220.dp), //TODO
            shape = RoundedCornerShape(28.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.blue_soft_white)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.blue_soft_white),
                        unfocusedContainerColor = colorResource(id = R.color.blue_soft_white),
                        focusedTextColor = colorResource(id = R.color.blue_dark),
                        unfocusedTextColor = colorResource(id = R.color.blue_mid),
                        focusedBorderColor = colorResource(id = R.color.blue_dark),
                        unfocusedBorderColor = colorResource(id = R.color.blue_dark),
                        focusedLabelColor = colorResource(id = R.color.blue_dark),
                        unfocusedLabelColor = colorResource(id = R.color.blue_dark),
                        cursorColor = colorResource(id = R.color.blue_mid)
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.blue_soft_white),
                        unfocusedContainerColor = colorResource(id = R.color.blue_soft_white),
                        focusedTextColor = colorResource(id = R.color.blue_dark),
                        unfocusedTextColor = colorResource(id = R.color.blue_mid),
                        focusedBorderColor = colorResource(id = R.color.blue_dark),
                        unfocusedBorderColor = colorResource(id = R.color.blue_dark),
                        focusedLabelColor = colorResource(id = R.color.blue_dark),
                        unfocusedLabelColor = colorResource(id = R.color.blue_dark),
                        cursorColor = colorResource(id = R.color.blue_mid)
                    )
                )
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorResource(id = R.color.blue_soft_white),
                        unfocusedContainerColor = colorResource(id = R.color.blue_soft_white),
                        focusedTextColor = colorResource(id = R.color.blue_dark),
                        unfocusedTextColor = colorResource(id = R.color.blue_mid),
                        focusedBorderColor = colorResource(id = R.color.blue_dark),
                        unfocusedBorderColor = colorResource(id = R.color.blue_dark),
                        focusedLabelColor = colorResource(id = R.color.blue_dark),
                        unfocusedLabelColor = colorResource(id = R.color.blue_dark),
                        cursorColor = colorResource(id = R.color.blue_mid)
                    )
                )
                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        if (firstName.isBlank() || email.isBlank() || password.isBlank()) {
                            Toast.makeText(
                                context,
                                "Please fill in all fields",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser

                                        if (user != null) {
                                            val userData = mapOf(
                                                "uid" to user.uid,
                                                "firstName" to firstName,
                                                "email" to email
                                            )

                                            database.child("users")
                                                .child(user.uid)
                                                .setValue(userData)
                                                .addOnSuccessListener {
                                                    Toast.makeText(
                                                        context,
                                                        "Account created successfully",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    onRegisterSuccess()
                                                }
                                                .addOnFailureListener { e ->
                                                    Toast.makeText(
                                                        context,
                                                        e.message ?: "An error occurred",
                                                        Toast.LENGTH_LONG
                                                    ).show()
                                                }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            task.exception?.message ?: "An error occurred",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.blue_mid),
                        contentColor = colorResource(id = R.color.blue_soft_white)
                    )
                ) {
                    Text(
                        text = "Sign up",
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                TextButton(
                    onClick = onGoToLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = colorResource(id = R.color.blue_dark)
                    )
                ) {
                    Text("Already have an account? Log in")
                }
            }
        }
    }
}