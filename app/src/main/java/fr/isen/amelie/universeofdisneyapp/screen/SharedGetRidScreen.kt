package fr.isen.amelie.universeofdisneyapp.screen

import android.R.attr.maxLines
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
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
import coil3.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.amelie.universeofdisneyapp.R
import fr.isen.amelie.universeofdisneyapp.activity.Movie
import fr.isen.amelie.universeofdisneyapp.activity.MovieOwnerInfo

@Composable
fun SharedGetRidScreen(
    onBackClick: () -> Unit,
    onMovieClick: (Movie) -> Unit
) {
    val database = FirebaseDatabase.getInstance().reference
    val movies = remember { mutableStateListOf<Movie>() }
    val movieUsers = remember { mutableStateMapOf<String, List<MovieOwnerInfo>>() }

    LaunchedEffect(Unit) {
        database.child("shared_get_rid")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    movies.clear()
                    movieUsers.clear()
                    for (movieSnapshot in snapshot.children) {
                        val movieId = movieSnapshot.key ?: continue
                        val usersList = mutableListOf<MovieOwnerInfo>()
                        val usersSnapshot = movieSnapshot.child("users")
                        for (userSnapshot in usersSnapshot.children) {
                            val userEmail =
                                userSnapshot.child("userEmail").getValue(String::class.java) ?: ""
                            val status =
                                userSnapshot.child("status").getValue(String::class.java) ?: ""
                            usersList.add(
                                MovieOwnerInfo(
                                    userEmail = userEmail,
                                    status = status
                                )
                            )
                        }
                        movieUsers[movieId] = usersList
                        database.child("movies")
                            .child(movieId)
                            .get()
                            .addOnSuccessListener { movieData ->
                                val movie = movieData.getValue(Movie::class.java)
                                if (movie != null) {
                                    movies.add(movie)
                                }
                            }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
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
                        color = colorResource(id = R.color.blue_soft_white),
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
                text = "Shared get rid",
                modifier = Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colorResource(id = R.color.blue_soft_white),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (movies.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Text(
                    text = "No shared movies for the moment",
                    modifier = Modifier.padding(16.dp),
                    color = colorResource(id = R.color.blue_dark)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies) { movie ->
                    val users = movieUsers[movie.id] ?: emptyList()
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMovieClick(movie) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = movie.imageUrl,
                                contentDescription = movie.title,
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
                                    text = movie.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorResource(id = R.color.blue_dark),
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                users.forEach { user ->
                                    Text(
                                        text = user.userEmail,
                                        color = colorResource(id = R.color.grey)
                                    )
                                }
                            }
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}