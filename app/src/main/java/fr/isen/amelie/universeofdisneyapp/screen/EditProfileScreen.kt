package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import fr.isen.amelie.universeofdisneyapp.R
import androidx.compose.foundation.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalLayoutApi::class)
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
    var showAvatarPicker by remember { mutableStateOf(false) }
    var savedAvatar by remember { mutableStateOf<String?>(null) }
    var tempSelectedAvatar by remember { mutableStateOf<String?>(null) }
    val avatarMap = mapOf(
        "avatar_ironman" to R.drawable.pdp_avengers,
        "avatar_spiderman" to R.drawable.pdp_marvel,
        "avatar_elise" to R.drawable.pdp_elise,
        "avatar_emeric" to R.drawable.pdp_emeric,
        "avatar_amelie" to R.drawable.pdp_amelie,
        "avatar_lucas" to R.drawable.pdp_lucas,
        )

    LaunchedEffect(user?.uid) {
        val currentUser = user ?: return@LaunchedEffect

        database.child("users")
            .child(currentUser.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                name = snapshot.child("firstName").getValue(String::class.java)
                    ?: email.substringBefore("@").ifBlank { "User" }

                savedAvatar = snapshot.child("avatar").getValue(String::class.java)
                tempSelectedAvatar = savedAvatar
            }
            .addOnFailureListener {
                name = email.substringBefore("@").ifBlank { "User" }
                savedAvatar = null
                tempSelectedAvatar = null
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White, CircleShape)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colorResource(id = R.color.blue_dark)
                    )
                }
                Text(
                    text = "Edit profile",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "Update your personal information and security settings",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.CenterHorizontally),
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
                                .clip(CircleShape)
                                .background(Color.White, CircleShape)
                                .clickable { showAvatarPicker = true },
                            contentAlignment = Alignment.Center
                        ) {
                            val avatarRes = tempSelectedAvatar?.let { avatarMap[it] }
                            if (avatarRes != null) {
                                Image(
                                    painter = painterResource(id = avatarRes),
                                    contentDescription = "Profile picture",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                )
                            } else {
                                Text(
                                    text = name.take(1).uppercase().ifBlank { "U" },
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.blue_dark)
                                )
                            }
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
                            shape = RoundedCornerShape(16.dp),
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

                    val updates = mapOf(
                        "firstName" to name,
                        "avatar" to tempSelectedAvatar
                    )
                    database.child("users")
                        .child(currentUser.uid)
                        .updateChildren(updates)
                        .addOnCompleteListener { nameTask ->

                            if (!nameTask.isSuccessful) {
                                isSaving = false
                                message = "Error while updating profile"
                                return@addOnCompleteListener
                            }

                            savedAvatar = tempSelectedAvatar

                            if (newPassword.isNotEmpty()) {
                                currentUser.updatePassword(newPassword)
                                    .addOnCompleteListener { passwordTask ->
                                        isSaving = false
                                        message = if (passwordTask.isSuccessful) {
                                            newPassword = ""
                                            confirmPassword = ""
                                            "Profile updated successfully"
                                        } else {
                                            "Profile updated, but password change failed"
                                        }
                                    }
                            } else {
                                isSaving = false
                                   message = "Profile updated successfully"
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
        }
        if (showAvatarPicker) {
            AlertDialog(
                onDismissRequest = { showAvatarPicker = false },
                title = { Text("Choose a profile picture") },
                text = {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        maxItemsInEachRow = 3
                    ) {
                        avatarMap.forEach { (avatarKey, avatarRes) ->
                            Image(
                                painter = painterResource(id = avatarRes),
                                contentDescription = avatarKey,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .clickable {
                                        tempSelectedAvatar = avatarKey
                                        showAvatarPicker = false
                                    }
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showAvatarPicker = false }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}