package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.amelie.universeofdisneyapp.model.Movie

@Composable
fun HomeScreen(
    onMovieClick: (Movie) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    var randomMovie by remember { mutableStateOf<Movie?>(null) }

    LaunchedEffect(Unit) {
        db.collection("movies")
            .get()
            .addOnSuccessListener { result ->
                val movieList = result.documents.mapNotNull { document ->
                    document.toObject(Movie::class.java)?.copy(id = document.id)
                }

                if (movieList.isNotEmpty()) {
                    randomMovie = movieList.random()
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Home")

        Spacer(modifier = Modifier.height(16.dp))

        Text("Film aléatoire")

        Spacer(modifier = Modifier.height(12.dp))

        randomMovie?.let { movie ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(movie.title)
                    Text("Sortie : ${movie.releaseDate}")

                    if (movie.category.isNotBlank()) {
                        Text("Catégorie : ${movie.category}")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { onMovieClick(movie) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Voir le détail")
                    }
                }
            }
        } ?: Text("Aucun film trouvé")
    }
}