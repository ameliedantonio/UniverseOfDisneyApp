package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.activity.MovieStatus

@Composable
fun MyMoviesScreen() {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val movieStatuses = remember { mutableStateListOf<MovieStatus>() }

    LaunchedEffect(Unit) {
        val user = auth.currentUser
        if (user != null) {
            database.child("users")
                .child(user.uid)
                .child("movieStatuses")
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {
                        movieStatuses.clear()
                        snapshot.children.forEach { dataSnapshot ->
                            val movieStatus = dataSnapshot.getValue(MovieStatus::class.java)
                            if (movieStatus != null) {
                                movieStatuses.add(movieStatus)
                            }
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
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(title = "My movies")
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Text("Watched") }
            items(watchedMovies) { movie ->
                MyMovieCard(movie.title, movie.releaseDate)
            }
            item { Text("Want to watch") }
            items(wantToWatchMovies) { movie ->
                MyMovieCard(movie.title, movie.releaseDate)
            }
            item { Text("Owned") }
            items(ownedMovies) { movie ->
                MyMovieCard(movie.title, movie.releaseDate)
            }
            item { Text("Want to get rid of") }
            items(wantToGetRidMovies) { movie ->
                MyMovieCard(movie.title, movie.releaseDate)
            }
        }
    }
}

@Composable
fun MyMovieCard(title: String, releaseDate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title)
            Text(text = "Exit : $releaseDate")
        }
    }
}