package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.colorResource
import fr.isen.amelie.universeofdisneyapp.R

@Composable
fun EditProfileScreen(
    onBackClick: () -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val user = FirebaseAuth.getInstance().currentUser
    val email = user?.email ?: "No email"

    var name by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(user?.uid) {
        val currentUser = user ?: return@LaunchedEffect

        database.child("users")
            .child(currentUser.uid)
            .child("firstName")
            .get()
            .addOnSuccessListener { snapshot ->
                name = snapshot.getValue(String::class.java)
                    ?: email.substringBefore("@").ifBlank { "User" }
            }
            .addOnFailureListener {
                name = email.substringBefore("@").ifBlank { "User" }
            }
    }

    val isSuccess = message.contains("success", ignoreCase = true)

    val blueGradient = Brush.linearGradient(
        colors = listOf(
            colorResource(id = R.color.blue_soft_white),
            colorResource(id = R.color.blue_light)
        )
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Edit profile",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Update your personal information and security settings",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(blueGradient)
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name.take(1).uppercase().ifBlank { "U" },
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.blue_dark)
                            )
                        }
                        Spacer(modifier = Modifier.size(16.dp))

                        Column {
                            Text(
                                text = name.ifBlank { "User" },
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.blue_dark)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = email,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(blueGradient)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Profile information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.blue_dark)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            placeholder = { Text("Enter your name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {},
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(blueGradient)
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Security",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(id = R.color.blue_dark)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Leave these fields empty if you do not want to change your password.",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorResource(id = R.color.blue_dark)

                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(16.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccess) colorResource(id = R.color.success_bg)
                        else colorResource(id = R.color.error_bg)
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(16.dp),
                        color = if (isSuccess) colorResource(id = R.color.green) else colorResource(id = R.color.error_text),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    message = ""
                    val currentUser = user

                    if (currentUser == null) {
                        message = "User not connected"
                        return@Button
                    }

                    if (name.isBlank()) {
                        message = "Name cannot be empty"
                        return@Button
                    }

                    if (newPassword.isNotEmpty() || confirmPassword.isNotEmpty()) {
                        if (newPassword.length < 6) {
                            message = "Password must contain at least 6 characters"
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            message = "Passwords do not match"
                            return@Button
                        }
                    }

                    isSaving = true

                    database.child("users")
                        .child(currentUser.uid)
                        .child("firstName")
                        .setValue(name)
                        .addOnCompleteListener { nameTask ->

                            if (!nameTask.isSuccessful) {
                                isSaving = false
                                message = "Error while updating first name"
                                return@addOnCompleteListener
                            }

                            if (newPassword.isNotEmpty()) {
                                currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener { passwordTask ->
                                        isSaving = false
                                        message = if (passwordTask.isSuccessful) {
                                            newPassword = ""
                                            confirmPassword = ""
                                            "First name and password updated successfully"
                                        } else {
                                            "First name updated, but password change failed"
                                        }
                                    }
                            } else {
                                isSaving = false
                                message = "First name updated successfully"
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = R.color.blue_dark),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isSaving) "Saving..." else "Save changes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(2.dp, colorResource(id = R.color.blue_dark)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = colorResource(id = R.color.blue_dark)
                )
            ) {
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(id = R.color.blue_dark)
                )
            }

        }
    }
}