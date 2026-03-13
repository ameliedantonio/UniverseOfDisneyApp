package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.amelie.universeofdisneyapp.R
import fr.isen.amelie.universeofdisneyapp.activity.MovieStatus
import fr.isen.amelie.universeofdisneyapp.activity.Movie

@Composable
fun MyMoviesScreen(
    onMovieClick: (Movie) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val movieStatuses = remember { mutableStateListOf<MovieStatus>() }

    val user = auth.currentUser
    val email = user?.email.orEmpty()
    val displayName = email.substringBefore("@").ifBlank { "User" }

    var selectedCategory by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (user != null) {
            database
                .child("users")
                .child(user.uid)
                .child("movieStatuses")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        movieStatuses.clear()

                        for (movieData in snapshot.children) {
                            val movieStatus = movieData.getValue(MovieStatus::class.java)
                            if (movieStatus != null) {
                                movieStatuses.add(movieStatus)
                            }
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                })
        }
    }
    val watchedMovies = movieStatuses.filter { it.status == "watched" }
    val wantToWatchMovies = movieStatuses.filter { it.status == "want_to_watch" }
    val ownedMovies = movieStatuses.filter { it.status == "owned" }
    val wantToGetRidMovies = movieStatuses.filter { it.status == "want_to_get_rid" }

    val displayedMovies = when (selectedCategory) {
        "watched" -> watchedMovies
        "want_to_watch" -> wantToWatchMovies
        "owned" -> ownedMovies
        "want_to_get_rid" -> wantToGetRidMovies
        else -> emptyList()
    }
    val displayedTitle = when (selectedCategory) {
        "watched" -> "Watched"
        "want_to_watch" -> "Want to watch"
        "owned" -> "Owned"
        "want_to_get_rid" -> "Want to get rid of"
        else -> "Select a category"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        MyMoviesHeaderCard(
            displayName = displayName
        )
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MyMoviesCategoryCard(
                    title = "Watched",
                    count = watchedMovies.size,
                    isSelected = selectedCategory == "watched",
                    onClick = { selectedCategory = "watched" },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle
                )
                MyMoviesCategoryCard(
                    title = "Want to watch",
                    count = wantToWatchMovies.size,
                    isSelected = selectedCategory == "want_to_watch",
                    onClick = { selectedCategory = "want_to_watch" },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MyMoviesCategoryCard(
                    title = "Owned",
                    count = ownedMovies.size,
                    isSelected = selectedCategory == "owned",
                    onClick = { selectedCategory = "owned" },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Bookmark
                )
                MyMoviesCategoryCard(
                    title = "Get rid",
                    count = wantToGetRidMovies.size,
                    isSelected = selectedCategory == "want_to_get_rid",
                    onClick = { selectedCategory = "want_to_get_rid" },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Delete
                )
            }
        }
        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = displayedTitle,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.blue_soft_white)
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (selectedCategory == null) {
                item {
                    GlassInfoCard(
                        text = "Choose a category to see your movies"
                    )
                }
            } else if (displayedMovies.isEmpty()) {
                item {
                    GlassInfoCard(
                        text = "No movies in this category"
                    )
                }
            } else {
                items(displayedMovies) { movieStatus ->
                    MyMovieItemCard(
                        title = movieStatus.title,
                        releaseDate = movieStatus.releaseDate,
                        onClick = {
                            database
                                .child("movies")
                                .child(movieStatus.movieId)
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        val movie = snapshot.getValue(Movie::class.java)
                                        if (movie != null) {
                                            onMovieClick(movie)
                                        }
                                    }
                                    override fun onCancelled(error: DatabaseError) {
                                    }
                                })
                        }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun MyMoviesHeaderCard(
    displayName: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(
                    color = colorResource(id = R.color.blue_soft_white).copy(alpha = 0.95f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayName.take(1).uppercase(),
                color = colorResource(id = R.color.blue_dark),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "My Movies",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue_soft_white)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Organize your personal collection",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.blue_soft_white)
            )
        }
    }
}

@Composable
fun MyMoviesCategoryCard(
    title: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        label = "cardScale"
    )
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorResource(id = R.color.blue_dark)
        } else {
            colorResource(id = R.color.blue_soft_white).copy(alpha = 0.90f)
        },
        label = "cardColor"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorResource(id = R.color.blue_soft_white)
        } else {
            colorResource(id = R.color.blue_dark)
        },
        label = "textColor"
    )
    Card(
        modifier = modifier
            .height(120.dp)
            .scale(animatedScale)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 10.dp else 5.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = animatedContainerColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = animatedTextColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = animatedTextColor
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$count movies",
                style = MaterialTheme.typography.bodyMedium,
                color = animatedTextColor
            )
        }
    }
}

@Composable
fun MyMovieItemCard(
    title: String,
    releaseDate: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.blue_soft_white).copy(alpha = 0.94f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.blue_dark)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Release: $releaseDate",
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(id = R.color.blue_dark)
            )
        }
    }
}

@Composable
fun GlassInfoCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.blue_soft_white).copy(alpha = 0.75f)
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            color = colorResource(id = R.color.blue_dark),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}