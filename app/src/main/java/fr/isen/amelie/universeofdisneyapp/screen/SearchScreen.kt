package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import fr.isen.amelie.universeofdisneyapp.R

@Composable
fun SearchScreen(
    onMovieClick: (Movie) -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val allMovies = remember { mutableStateListOf<Movie>() }
    val averageRatings = remember { mutableStateMapOf<String, Float>() }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        database.child("movies")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val tempMovies = mutableListOf<Movie>()

                    for (child in snapshot.children) {
                        val movie = child.getValue(Movie::class.java)
                        if (movie != null) {
                            tempMovies.add(movie)
                        }
                    }

                    allMovies.clear()
                    allMovies.addAll(
                        tempMovies.distinctBy { it.title.trim().lowercase() }
                    )
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
        database.child("movie_ratings")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    averageRatings.clear()
                    for (movieSnapshot in snapshot.children) {
                        val movieId = movieSnapshot.key ?: continue
                        var sum = 0
                        var count = 0
                        for (userSnapshot in movieSnapshot.children) {
                            val rating = userSnapshot.child("rating").getValue(Int::class.java) ?: 0
                            if (rating in 1..5) {
                                sum += rating
                                count++
                            }
                        }
                        averageRatings[movieId] =
                            if (count > 0) sum.toFloat() / count else 0f
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
    val filteredMovies = if (searchText.isBlank()) {
        emptyList()
    } else {
        allMovies.filter {
            it.title.trim().contains(searchText.trim(), ignoreCase = true)
        }
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
                onValueChange = { input ->
                    searchText = input.filter { char ->
                        char != '\n' && char != '\r' && char != '\t'
                    }
                },
                placeholder = { Text("Search for a movie...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = colorResource(id = R.color.blue_dark)
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search",
                                tint = colorResource(id = R.color.blue_dark)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.92f),
                        shape = RoundedCornerShape(28.dp)
                    )
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (searchText.isNotBlank() && filteredMovies.isNotEmpty()) {
                Text(
                    text = "${filteredMovies.size} result${if (filteredMovies.size > 1) "s" else ""}",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (searchText.isBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App logo",
                            modifier = Modifier
                                .size(220.dp)
                                .alpha(0.20f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Find your favorite movie",
                            color = colorResource(id = R.color.blue_mid).copy(alpha = 0.70f),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else if (filteredMovies.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = colorResource(id = R.color.blue_dark),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No movies found for \"$searchText\"",
                            color = colorResource(id = R.color.blue_dark),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = filteredMovies,
                        key = { movie -> movie.title }
                    ) { movie ->
                        MovieHorizontalCard(
                            movie = movie,
                            averageRating = averageRatings[movie.id] ?: 0f,
                            onClick = { onMovieClick(movie) }
                        )
                    }
                }
            }
        }
    }
}