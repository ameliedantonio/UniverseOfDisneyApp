package fr.isen.amelie.universeofdisneyapp.screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.amelie.universeofdisneyapp.model.Movie

@Composable
fun MovieDetailScreen(
    movie: Movie,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    fun saveMovieStatus(status: String) {
        val user = auth.currentUser

        if (user != null) {
            val statusData = hashMapOf(
                "movieId" to movie.id,
                "title" to movie.title,
                "universeId" to movie.universeId,
                "category" to movie.category,
                "releaseDate" to movie.releaseDate,
                "status" to status
            )

            db.collection("users")
                .document(user.uid)
                .collection("movieStatuses")
                .document(movie.id)
                .set(statusData)
                .addOnSuccessListener {
                    Toast.makeText(
                        context,
                        "Statut enregistré : $status",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Erreur : ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
        } else {
            Toast.makeText(
                context,
                "Aucun utilisateur connecté",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

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
            onClick = { saveMovieStatus("watched") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Watched")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { saveMovieStatus("want_to_watch") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Want to watch")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { saveMovieStatus("owned") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Own on DVD / Blu-ray")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { saveMovieStatus("want_to_get_rid") },
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