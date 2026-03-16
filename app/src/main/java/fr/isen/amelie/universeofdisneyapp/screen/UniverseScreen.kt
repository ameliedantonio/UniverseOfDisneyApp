package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.LocalMovies
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
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
import fr.isen.amelie.universeofdisneyapp.activity.Universe

@Composable
fun UniverseScreen(
    onUniverseClick: (Universe) -> Unit,
    onGenreClick: (String) -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val universes = remember { mutableStateListOf<Universe>() }
    val genres = remember { mutableStateListOf<String>() }
    val universeCounts = remember { mutableStateMapOf<String, Int>() }
    val genreCounts = remember { mutableStateMapOf<String, Int>() }
    var selectedTab by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        database.child("universes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    universes.clear()
                    for (data in snapshot.children) {
                        val universe = data.getValue(Universe::class.java)
                        if (universe != null) {
                            universes.add(universe)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
        database.child("movies")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    genres.clear()
                    universeCounts.clear()
                    genreCounts.clear()
                    val genreSet = mutableSetOf<String>()
                    for (data in snapshot.children) {
                        val movie = data.getValue(Movie::class.java)
                        if (movie != null) {
                            if (movie.genre.isNotBlank()) {
                                genreSet.add(movie.genre)
                                genreCounts[movie.genre] = (genreCounts[movie.genre] ?: 0) + 1
                            }
                            if (movie.universeId.isNotBlank()) {
                                universeCounts[movie.universeId] =
                                    (universeCounts[movie.universeId] ?: 0) + 1
                            }
                        }
                    }
                    genres.addAll(genreSet.sorted())
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(title = "Explore")
        UniverseGenreSelector(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedTab == 0) {
                items(universes) { universe ->
                    UniverseCard(
                        universe = universe,
                        count = universeCounts[universe.id] ?: 0,
                        onClick = { onUniverseClick(universe) }
                    )
                }
            } else {
                items(genres) { genre ->
                    GenreCard(
                        genre = genre,
                        count = genreCounts[genre] ?: 0,
                        onClick = { onGenreClick(genre) }
                    )
                }
            }
        }
    }
}

@Composable
fun UniverseGenreSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val selectorWidth = (screenWidth - 32.dp - 8.dp) / 2
    val animatedOffset = animateDpAsState(
        targetValue = if (selectedTab == 0) 0.dp else selectorWidth,
        animationSpec = tween(250)
    ).value
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .height(52.dp)
            .background(
                color = Color.White.copy(alpha = 0.22f),
                shape = RoundedCornerShape(50)
            )
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .offset(x = animatedOffset)
                .width(selectorWidth)
                .fillMaxHeight()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(50)
                )
        )
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(selectorWidth)
                    .fillMaxHeight()
                    .clickable { onTabSelected(0) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Universes",
                    color = if (selectedTab == 0)
                        colorResource(id = R.color.blue_dark)
                    else
                        Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .width(selectorWidth)
                    .fillMaxHeight()
                    .clickable { onTabSelected(1) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Genres",
                    color = if (selectedTab == 1)
                        colorResource(id = R.color.blue_dark)
                    else
                        Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun UniverseCard(
    universe: Universe,
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (universe.logoUrl.isNotBlank()) {
                AsyncImage(
                    model = universe.logoUrl,
                    contentDescription = universe.name,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = colorResource(id = R.color.blue_light).copy(alpha = 0.18f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalMovies,
                        contentDescription = universe.name,
                        tint = colorResource(id = R.color.blue_dark),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = universe.name,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.blue_dark)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$count movies",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.blue_mid)
                )
            }
        }
    }
}

@Composable
fun GenreCard(
    genre: String,
    count: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = colorResource(id = R.color.blue_light).copy(alpha = 0.18f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MovieFilter,
                    contentDescription = genre,
                    tint = colorResource(id = R.color.blue_dark),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = genre,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(id = R.color.blue_dark)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$count movies",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorResource(id = R.color.blue_dark)
                )
            }
        }
    }
}