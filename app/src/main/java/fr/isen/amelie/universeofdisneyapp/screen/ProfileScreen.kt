package fr.isen.amelie.universeofdisneyapp.screen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.amelie.universeofdisneyapp.activity.MovieStatus
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.colorResource
import fr.isen.amelie.universeofdisneyapp.R

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val movieStatuses = remember { mutableStateListOf<MovieStatus>() }

    val user = auth.currentUser
    val email = user?.email.orEmpty()
    val displayName = email.substringBefore("@").ifBlank { "User" }

    LaunchedEffect(Unit) {
        if (user != null) {
            database
                .child("users")
                .child(user.uid)
                .child("movieStatuses")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        movieStatuses.clear()

                        for (child in snapshot.children) {
                            val movieId = child.child("movieId").getValue(String::class.java) ?: ""
                            val title = child.child("title").getValue(String::class.java) ?: ""
                            val releaseDate = child.child("releaseDate").getValue(String::class.java) ?: ""
                            val status = child.child("status").getValue(String::class.java) ?: ""

                            movieStatuses.add(
                                MovieStatus(
                                    movieId = movieId,
                                    title = title,
                                    releaseDate = releaseDate,
                                    status = status
                                )
                            )
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }

    val watchedMovies = movieStatuses.filter { it.status == "watched" }
    val wantToWatchMovies = movieStatuses.filter { it.status == "want_to_watch" }
    val ownedMovies = movieStatuses.filter { it.status == "owned" }
    val wantToGetRidMovies = movieStatuses.filter { it.status == "want_to_get_rid" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ProfileHeaderCard(
            displayName = displayName,
            email = email,
            onEditProfileClick = onEditProfileClick
        )
        Spacer(modifier = Modifier.height(20.dp))

        ProfileMenuGrid(
            watchedCount = watchedMovies.size,
            wantToWatchCount = wantToWatchMovies.size,
            ownedCount = ownedMovies.size,
            getRidCount = wantToGetRidMovies.size
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                auth.signOut()
                onLogoutClick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.blue_dark),
                contentColor = Color.White
            )
        ) {
            Text("Log out")
        }
    }
}

@Composable
fun ProfileHeaderCard(
    displayName: String,
    email: String,
    onEditProfileClick: () -> Unit = {}
)
{
    val gradient = Brush.linearGradient(
        colors = listOf(
            colorResource(id = R.color.blue_soft_white),
            colorResource(id = R.color.blue_light)
        )
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(28.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradient)
                .padding(vertical = 28.dp, horizontal = 20.dp)
        ) {
            IconButton(
                onClick = onEditProfileClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(44.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.2f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit profile",
                    tint = Color.White
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(18.dp))

                Surface(
                    modifier = Modifier.size(88.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.95f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.take(1).uppercase(),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = colorResource(id = R.color.blue_dark)
                            )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = displayName.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = email.ifBlank { "No email" },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun ProfileMenuGrid(
    watchedCount: Int,
    wantToWatchCount: Int,
    ownedCount: Int,
    getRidCount: Int
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileMenuCard(
                title = "Watched",
                count = watchedCount,
                modifier = Modifier.weight(1f)
            )
            ProfileMenuCard(
                title = "Want to watch",
                count = wantToWatchCount,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileMenuCard(
                title = "Owned",
                count = ownedCount,
                modifier = Modifier.weight(1f)
            )
            ProfileMenuCard(
                title = "Want to get rid",
                count = getRidCount,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ProfileMenuCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue_dark)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$count movies",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.grey)
            )
        }
    }
}