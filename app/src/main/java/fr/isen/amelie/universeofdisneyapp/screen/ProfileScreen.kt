package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.activity.MovieStatus

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val movieStatuses = remember { mutableStateListOf<MovieStatus>() }

    LaunchedEffect(Unit) {
        val user = auth.currentUser

        if (user != null) {
            db.collection("users")
                .document(user.uid)
                .collection("movieStatuses")
                .get()
                .addOnSuccessListener { result ->
                    movieStatuses.clear()
                    for (document in result.documents) {
                        val status = document.toObject(MovieStatus::class.java)
                        if (status != null) {
                            movieStatuses.add(status)
                        }
                    }
                }
        }
    }

    val watchedMovies = movieStatuses.filter { it.status == "watched" }
    val wantToWatchMovies = movieStatuses.filter { it.status == "want_to_watch" }
    val ownedMovies = movieStatuses.filter { it.status == "owned" }
    val wantToGetRidMovies = movieStatuses.filter { it.status == "want_to_get_rid" }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // TOP BAR
        AppTopBar(title = "Profile")

        // CONTENU
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            item {
                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Retour aux univers")
                }
            }

            item {
                Button(
                    onClick = {
                        auth.signOut()
                        onLogoutClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Se déconnecter")
                }
            }

            item { Text("Watched") }
            items(watchedMovies) { movie ->
                MovieStatusCard(movie.title, movie.releaseDate)
            }

            item { Text("Want to watch") }
            items(wantToWatchMovies) { movie ->
                MovieStatusCard(movie.title, movie.releaseDate)
            }

            item { Text("Owned") }
            items(ownedMovies) { movie ->
                MovieStatusCard(movie.title, movie.releaseDate)
            }

            item { Text("Want to get rid of") }
            items(wantToGetRidMovies) { movie ->
                MovieStatusCard(movie.title, movie.releaseDate)
            }
        }
    }
}

@Composable
fun MovieStatusCard(title: String, releaseDate: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title)
            Text(text = "Sortie : $releaseDate")
        }
    }
}