package fr.isen.amelie.universeofdisneyapp.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.amelie.universeofdisneyapp.model.Movie
import fr.isen.amelie.universeofdisneyapp.model.MovieOwnerInfo

@Composable
fun MovieDetailScreen(
    movie: Movie,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val sharedUsers = remember { mutableStateListOf<MovieOwnerInfo>() }

    fun loadSharedGetRidUsers() {
        db.collection("shared_get_rid")
            .document(movie.id)
            .collection("users")
            .get()
            .addOnSuccessListener { result ->
                sharedUsers.clear()
                for (document in result.documents) {
                    val userEmail = document.getString("userEmail") ?: ""
                    val status = document.getString("status") ?: ""
                    sharedUsers.add(
                        MovieOwnerInfo(
                            userEmail = userEmail,
                            status = status
                        )
                    )
                }
            }
    }

    fun saveMovieStatus(status: String) {
        val user = auth.currentUser

        if (user != null) {
            val statusData = hashMapOf(
                "movieId" to movie.id,
                "title" to movie.title,
                "universeId" to movie.universeId,
                "category" to movie.category,
                "releaseDate" to movie.releaseDate,
                "status" to status,
                "userEmail" to (user.email ?: "")
            )

            // 1) Sauvegarde perso
            db.collection("users")
                .document(user.uid)
                .collection("movieStatuses")
                .document(movie.id)
                .set(statusData)
                .addOnSuccessListener {
                    // 2) Gestion partie partagée
                    if (status == "want_to_get_rid") {
                        val sharedData = hashMapOf(
                            "userEmail" to (user.email ?: ""),
                            "status" to status
                        )

                        db.collection("shared_get_rid")
                            .document(movie.id)
                            .collection("users")
                            .document(user.uid)
                            .set(sharedData)
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Statut enregistré : $status",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadSharedGetRidUsers()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Erreur partage : ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    } else {
                        // Si le statut n'est plus "want_to_get_rid", on retire du partagé
                        db.collection("shared_get_rid")
                            .document(movie.id)
                            .collection("users")
                            .document(user.uid)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(
                                    context,
                                    "Statut enregistré : $status",
                                    Toast.LENGTH_SHORT
                                ).show()
                                loadSharedGetRidUsers()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Erreur suppression partage : ${e.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
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

    LaunchedEffect(movie.id) {
        loadSharedGetRidUsers()
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

        Text("Utilisateurs qui veulent se débarrasser de ce film")

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            items(sharedUsers) { userInfo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = userInfo.userEmail)
                        Text(text = "Veut s'en débarrasser")
                    }
                }
            }
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