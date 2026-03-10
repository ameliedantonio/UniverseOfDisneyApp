package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.activity.Movie

import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.style.TextAlign

import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    onMovieClick: (Movie) -> Unit
) {
    val db = FirebaseDatabase.getInstance()
    val moviesRef = db.getReference("movies")
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid

    var randomMovie by remember { mutableStateOf<Movie?>(null) }
    var movieList by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var firstName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (userId != null) {
            db.getReference("users").child(userId).child("firstName")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        firstName = snapshot.getValue(String::class.java) ?: ""
                    }
                    override fun onCancelled(error: DatabaseError) {
                        println("User firstname error : ${error.message}")
                    }
                })
        }

        moviesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movies = mutableListOf<Movie>()

                for (movieSnapshot in snapshot.children) {
                    val movie = movieSnapshot.getValue(Movie::class.java)
                    if (movie != null) {
                        movies.add(movie.copy(id = movieSnapshot.key ?: ""))
                    }
                }
                movieList = movies

                if (movies.isNotEmpty()) {
                    randomMovie = movies.random()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                println("Realtime Database error : ${error.message}")
            }
        })
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(title = if (firstName.isNotBlank()) "Hi $firstName" else "Hi")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            randomMovie?.let { movie ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column {
                        AsyncImage(
                            model = movie.imageUrl,
                            contentDescription = movie.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                                .background(Color.LightGray),
                            contentScale = ContentScale.Crop,
                            onSuccess = {
                                println("Random image ok = ${movie.imageUrl}")
                            },
                            onError = {
                                println("Random image error = ${movie.imageUrl}")
                                println("Cause = ${it.result.throwable}")
                            }
                        )

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = { onMovieClick(movie) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("See detail")
                            }
                        }
                    }
                }
            } ?: Text(
                text = "No movies found",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Recommended for You",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movieList) { movie ->
                    MovieCarouselItem(
                        movie = movie,
                        onClick = { onMovieClick(movie) }
                    )
                }
            }
        }
    }
}

@Composable
fun MovieCarouselItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column {
            AsyncImage(
                model = movie.imageUrl,
                contentDescription = movie.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    println("Image ok = ${movie.imageUrl}")
                },
                onError = {
                    println("image error = ${movie.imageUrl}")
                    println("cause = ${it.result.throwable}")
                }
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Exit : ${movie.releaseDate}",
                    style = MaterialTheme.typography.bodySmall
                )

                if (movie.category.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = movie.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }
        }
    }
}