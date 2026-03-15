package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.R
import fr.isen.amelie.universeofdisneyapp.activity.Movie

@Composable
fun MovieScreen(
    universeId: String = "",
    genre: String = "",
    universeName: String,
    onMovieClick: (Movie) -> Unit,
    onBackToUniverses: () -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val movies = remember { mutableStateListOf<Movie>() }
    var selectedCategory by remember { mutableStateOf("All") }
    LaunchedEffect(universeId, genre) {
        database.child("movies")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    movies.clear()
                    for (data in snapshot.children) {
                        val movie = data.getValue(Movie::class.java)
                        if (movie != null) {
                            val matchesUniverse =
                                universeId.isNotBlank() && movie.universeId == universeId
                            val matchesGenre =
                                genre.isNotBlank() && movie.genre.equals(genre, ignoreCase = true)
                            if (matchesUniverse || matchesGenre) {
                                movies.add(movie)
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    val categories = listOf("All") + movies
        .map { it.category }
        .filter { it.isNotBlank() }
        .distinct()
        .sorted()
    val filteredMovies = movies
        .filter {
            selectedCategory == "All" || it.category == selectedCategory
        }
        .sortedBy { it.releaseDate }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        AppTopBar(title = "Movies")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.blue_soft_white))
                    .clickable { onBackToUniverses() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorResource(id = R.color.blue_dark)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Discover movies : $universeName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue_soft_white)
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "${movies.size} movies",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.blue_soft_white)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        if (categories.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    CategoryChip(
                        title = category,
                        isSelected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredMovies) { movie ->
                MovieHorizontalCard(
                    movie = movie,
                    onClick = { onMovieClick(movie) }
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun CategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(50.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                colorResource(id = R.color.blue_soft_white)
            } else {
                colorResource(id = R.color.blue_soft_white).copy(alpha = 0.22f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (isSelected) {
                colorResource(id = R.color.blue_dark)
            } else {
                colorResource(id = R.color.blue_soft_white)
            },
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun MovieHorizontalCard(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            AsyncImage(
                model = movie.imageUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(92.dp)
                    .fillMaxHeight()
            )
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(14.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.blue_dark),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Release: ${movie.releaseDate}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.blue_dark),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (movie.category.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = movie.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorResource(id = R.color.blue_mid),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}