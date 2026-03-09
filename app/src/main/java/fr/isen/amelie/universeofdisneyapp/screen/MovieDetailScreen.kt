package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import fr.isen.amelie.universeofdisneyapp.model.Movie

@Composable
fun MovieDetailScreen(
    movie: Movie,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = movie.title)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Date de sortie : ${movie.releaseDate}")
        Text(text = "Univers : ${movie.universeId}")
        Text(text = "Catégorie : ${movie.category}")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Watched")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Want to watch")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Own on DVD / Blu-ray")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Want to get rid of")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Retour")
        }
    }
}