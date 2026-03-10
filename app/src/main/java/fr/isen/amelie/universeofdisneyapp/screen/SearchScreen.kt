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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.activity.Movie

@Composable
fun SearchScreen(
    onMovieClick: (Movie) -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val allMovies = remember { mutableStateListOf<Movie>() }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        database.child("movies")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    allMovies.clear()
                    for (child in snapshot.children) {
                        val movie = child.getValue(Movie::class.java)
                        if (movie != null) {
                            allMovies.add(movie)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    val filteredMovies = allMovies.filter {
        it.title.contains(searchText, ignoreCase = true)
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(title = "Search")
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search for a movie") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMovies) { movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMovieClick(movie) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(movie.title)
                            Text("Exit : ${movie.releaseDate}")
                        }
                    }
                }
            }
        }
    }
}