package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.activity.Movie

@Composable
fun MovieScreen(
    universeId: String,
    onMovieClick: (Movie) -> Unit,
    onBackToUniverses: () -> Unit,
    onProfileClick: () -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val movies = remember { mutableStateListOf<Movie>() }

    LaunchedEffect(universeId) {
        database.child("movies")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    movies.clear()
                    for (child in snapshot.children) {
                        val movie = child.getValue(Movie::class.java)
                        if (movie != null && movie.universeId == universeId) {
                            movies.add(movie)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(title = "Movies")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Button(
                onClick = onBackToUniverses,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Return to the universes")
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onProfileClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View my profile")
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies) { movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMovieClick(movie) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(movie.title)
                            Text("Exit : ${movie.releaseDate}")
                            if (movie.category.isNotBlank()) {
                                Text("Category : ${movie.category}")
                            }
                        }
                    }
                }
            }
        }
    }
}