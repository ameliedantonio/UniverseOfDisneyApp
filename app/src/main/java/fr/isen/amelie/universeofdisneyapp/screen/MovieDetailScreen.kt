package fr.isen.amelie.universeofdisneyapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.activity.MovieOwnerInfo
import fr.isen.amelie.universeofdisneyapp.activity.Movie

@Composable
fun MovieDetailScreen(
    movie: Movie,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val sharedUsers = remember { mutableStateListOf<MovieOwnerInfo>() }

    fun loadSharedGetRidUsers() {
        database.child("shared_get_rid")
            .child(movie.id)
            .child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    sharedUsers.clear()

                    for (child in snapshot.children) {
                        val userEmail = child.child("userEmail").getValue(String::class.java) ?: ""
                        val status = child.child("status").getValue(String::class.java) ?: ""

                        sharedUsers.add(
                            MovieOwnerInfo(
                                userEmail = userEmail,
                                status = status
                            )
                        )
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
    fun saveMovieStatus(status: String) {
        val user = auth.currentUser

        if (user != null) {
            val statusData = mapOf(
                "movieId" to movie.id,
                "title" to movie.title,
                "universeId" to movie.universeId,
                "category" to movie.category,
                "releaseDate" to movie.releaseDate,
                "status" to status,
                "userEmail" to (user.email ?: "")
            )
            database.child("users")
                .child(user.uid)
                .child("movieStatuses")
                .child(movie.id)
                .setValue(statusData)
                .addOnSuccessListener {
                    if (status == "want_to_get_rid") {
                        val sharedData = mapOf(
                            "userEmail" to (user.email ?: ""),
                            "status" to status
                        )
                        database.child("shared_get_rid")
                            .child(movie.id)
                            .child("users")
                            .child(user.uid)
                            .setValue(sharedData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Saved status : $status",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadSharedGetRidUsers()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Sharing error : ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        database.child("shared_get_rid")
                            .child(movie.id)
                            .child("users")
                            .child(user.uid)
                            .removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Saved status : $status",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadSharedGetRidUsers()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Share deletion error : ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Error : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            Toast.makeText(
                context,
                "No users logged in",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    LaunchedEffect(movie.id) {
        loadSharedGetRidUsers()
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(title = movie.title)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = "Release date : ${movie.releaseDate}")
            Text(text = "Universe : ${movie.universeId}")
            Text(text = "Category : ${movie.category}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { saveMovieStatus("watched") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Watched")
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { saveMovieStatus("want_to_watch") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Want to watch")
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { saveMovieStatus("owned") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Own on DVD / Blu-ray")
            }
            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { saveMovieStatus("want_to_get_rid") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Want to get rid of")
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Users who want to get rid of this movie")
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                items(sharedUsers) { userInfo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = userInfo.userEmail)
                            Text(text = "Wants to get rid of it")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }
        }
    }
}