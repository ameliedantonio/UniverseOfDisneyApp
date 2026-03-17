package fr.isen.amelie.universeofdisneyapp.screen

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Email
import androidx.compose.ui.platform.LocalContext
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
    var searchQuery by remember { mutableStateOf("") }
    val context = LocalContext.current

    fun contactUserByEmail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$email")
        }
        context.startActivity(intent)
    }

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
    val filteredMovies = movies.filter {
        it.title.contains(searchQuery, ignoreCase = true)
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
                text = "Shared get rid",
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
            label = { Text("Search movies") },
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
        Spacer(modifier = Modifier.height(10.dp))
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
        }
        else if (filteredMovies.isEmpty() && searchQuery.isNotBlank()) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredMovies) { movie ->
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
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = user.userEmail,
                                            color = colorResource(id = R.color.blue_mid)
                                        )
                                        Card(
                                            modifier = Modifier
                                                .clickable {
                                                    contactUserByEmail(user.userEmail)
                                                },
                                            shape = RoundedCornerShape(50),
                                            colors = CardDefaults.cardColors(
                                                containerColor = colorResource(id = R.color.blue_light).copy(alpha = 0.25f)
                                            ),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Email,
                                                    contentDescription = "Contact",
                                                    tint = colorResource(id = R.color.blue_dark),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Contact",
                                                    color = colorResource(id = R.color.blue_dark),
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
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