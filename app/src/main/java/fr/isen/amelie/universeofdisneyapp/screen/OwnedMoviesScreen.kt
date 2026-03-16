package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.style.TextAlign
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.amelie.universeofdisneyapp.R
import fr.isen.amelie.universeofdisneyapp.activity.Movie
import fr.isen.amelie.universeofdisneyapp.activity.MovieStatus

@Composable
fun OwnedMoviesScreen(
    onBackClick: () -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val movieStatuses = remember { mutableStateListOf<MovieStatus>() }
    val user = auth.currentUser
    var movieToDelete by remember { mutableStateOf<MovieStatus?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf("A-Z") }
    fun deleteMovieStatus(movieId: String) {
        if (user != null) {
            database.child("users")
                .child(user.uid)
                .child("movieStatuses")
                .child(movieId)
                .removeValue()
                .addOnSuccessListener {
                    movieStatuses.removeAll { it.movieId == movieId }
                    database.child("shared_get_rid")
                        .child(movieId)
                        .child("users")
                        .child(user.uid)
                        .removeValue()
                }
        }
    }
    LaunchedEffect(Unit) {
        if (user != null) {
            database.child("users")
                .child(user.uid)
                .child("movieStatuses")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        movieStatuses.clear()
                        for (child in snapshot.children) {
                            val movieId = child.child("movieId").getValue(String::class.java) ?: ""
                            val title = child.child("title").getValue(String::class.java) ?: ""
                            val releaseDate = child.child("releaseDate").getValue(String::class.java) ?: ""
                            val userEmail = child.child("userEmail").getValue(String::class.java) ?: ""
                            val posterPath = child.child("posterPath").getValue(String::class.java) ?: ""
                            val viewStatus = child.child("viewStatus").getValue(String::class.java) ?: ""
                            val ownershipStatus = child.child("ownershipStatus").getValue(String::class.java) ?: ""
                            movieStatuses.add(
                                MovieStatus(
                                    movieId = movieId,
                                    title = title,
                                    releaseDate = releaseDate,
                                    userEmail = userEmail,
                                    posterPath = posterPath,
                                    viewStatus = viewStatus,
                                    ownershipStatus = ownershipStatus
                                )
                            )
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }
    val ownedMoviesAll = movieStatuses.filter {
            it.ownershipStatus == "owned" || it.ownershipStatus == "want_to_get_rid"
    }
    val ownedMovies = ownedMoviesAll
        .filter {
            it.title.contains(searchQuery, ignoreCase = true )
        }
        .let { movies ->
            when (selectedSort) {
                "Newest" -> movies.sortedByDescending {
                    it.releaseDate.trim().take(4).toIntOrNull() ?: 0
                }

                "Oldest" -> movies.sortedBy { it.releaseDate.trim().take(4).toIntOrNull() ?: 0 }
                else -> movies.sortedBy { it.title }
            }
        }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = Color.White,
                        shape = CircleShape
                    )
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
                text = "Owned",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { newValue ->
                searchQuery = newValue
                    .replace("\n", "")
                    .replace("\r", "")
                    .replace("\t", "")
            },
            label = { Text("Search owned movies") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = colorResource(id = R.color.blue_dark),
                unfocusedTextColor = colorResource(id = R.color.blue_dark),
                focusedBorderColor = colorResource(id = R.color.blue_dark),
                unfocusedBorderColor = colorResource(id = R.color.blue_dark),
                focusedLabelColor = Color.White,
                unfocusedLabelColor = colorResource(id = R.color.blue_dark),
                cursorColor = colorResource(id = R.color.blue_dark)
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SortChip(
                title = "A-Z",
                isSelected = selectedSort == "A-Z",
                onClick = { selectedSort = "A-Z" },
                modifier = Modifier.weight(1f)
            )
            SortChip(
                title = "Newest",
                isSelected = selectedSort == "Newest",
                onClick = { selectedSort = "Newest" },
                modifier = Modifier.weight(1f)
            )
            SortChip(
                title = "Oldest",
                isSelected = selectedSort == "Oldest",
                onClick = { selectedSort = "Oldest" },
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (ownedMoviesAll.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(
                    1.dp,
                    colorResource(id = R.color.blue_dark)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = "No movies",
                        tint = colorResource(id = R.color.blue_dark),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No owned movies",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(id = R.color.blue_dark),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Add movies to your collection",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(id = R.color.blue_dark),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        else if (ownedMovies.isEmpty() && searchQuery.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "No movies found for \"$searchQuery\"",
                    modifier = Modifier.padding(16.dp),
                    color = colorResource(id = R.color.blue_dark)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ownedMovies) { movieStatus ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                database.child("movies")
                                    .child(movieStatus.movieId)
                                    .get()
                                    .addOnSuccessListener { snapshot ->
                                        val movie = snapshot.getValue(Movie::class.java)
                                        if (movie != null) {
                                            onMovieClick(movie)
                                        }
                                    }
                            },
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = movieStatus.imageUrl,
                                    contentDescription = movieStatus.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .width(62.dp)
                                        .height(86.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = movieStatus.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = colorResource(id = R.color.blue_dark),
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = movieStatus.releaseDate,
                                        color = colorResource(id = R.color.blue_mid)
                                    )
                                }
                            }
                            IconButton(
                                onClick = { movieToDelete = movieStatus }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = colorResource(id = R.color.blue_dark)
                                )
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
        if (movieToDelete != null) {
            AlertDialog(
                onDismissRequest = { movieToDelete = null },
                confirmButton = {
                    TextButton(
                        onClick = {
                            deleteMovieStatus(movieToDelete!!.movieId)
                            movieToDelete = null
                        }
                    ) {
                        Text(
                            text = "Delete",
                            color = colorResource(id = R.color.blue_dark)
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { movieToDelete = null }
                    ) {
                        Text(
                            text = "Cancel",
                            color = colorResource(id = R.color.blue_dark)
                        )
                    }
                },
                title = {
                    Text("Delete movie")
                },
                text = {
                    Text("Are you sure you want to remove this movie?")
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun SortChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(42.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                colorResource(id = R.color.blue_light)
            } else {
                Color.White
            }
        ),
        border = BorderStroke(
            1.dp,
            colorResource(id = R.color.blue_dark)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = if (isSelected) {
                    colorResource(id = R.color.blue_dark)
                } else {
                    colorResource(id = R.color.blue_dark)
                },
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}