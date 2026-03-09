package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.model.Movie

@Composable
fun MovieScreen(
    universeId: String,
    onMovieClick: (Movie) -> Unit,
    onBackToUniverses: () -> Unit,
    onProfileClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val movies = remember { mutableStateListOf<Movie>() }

    LaunchedEffect(universeId) {
        db.collection("movies")
            .whereEqualTo("universeId", universeId)
            .get()
            .addOnSuccessListener { result ->
                movies.clear()
                for (document in result.documents) {
                    val movie = document.toObject(Movie::class.java)
                    if (movie != null) {
                        movies.add(movie.copy(id = document.id))
                    }
                }
            }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        // TOP BAR
        AppTopBar(title = "Movies")

        // CONTENU
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Button(
                onClick = onBackToUniverses,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retour aux univers")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onProfileClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voir mon profil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(movies) { movie ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMovieClick(movie) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(text = movie.title)

                            Text(text = "Sortie : ${movie.releaseDate}")

                            if (movie.category.isNotBlank()) {
                                Text(text = "Catégorie : ${movie.category}")
                            }
                        }
                    }
                }
            }
        }
    }
}