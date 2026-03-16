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
import androidx.compose.ui.res.colorResource
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import fr.isen.amelie.universeofdisneyapp.R
import androidx.compose.foundation.background

import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.IconButton
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
    }

    val filteredMovies = if (searchText.isBlank()) {
        emptyList()
    } else {
        allMovies.filter {
            it.title.trim().startsWith(searchText.trim(), ignoreCase = true)
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
                onValueChange = { input -> searchText = input },
                placeholder = { Text("Search for a movie...") },
                singleLine = true,
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear search"
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
                        color = colorResource(id = R.color.blue_soft_white).copy(alpha = 0.92f),
                        shape = RoundedCornerShape(28.dp)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredMovies,
                    key = { movie -> movie.title }
                ) { movie ->
                    MovieHorizontalCard(
                        movie = movie,
                        onClick = { onMovieClick(movie) }
                    )
                }
            }
        }
    }
}