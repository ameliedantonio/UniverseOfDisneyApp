package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.clickable
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
import com.google.firebase.firestore.FirebaseFirestore
import fr.isen.amelie.universeofdisneyapp.model.Universe

@Composable
fun UniverseScreen(
    onUniverseClick: (Universe) -> Unit,
    onProfileClick: () -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val universes = remember { mutableStateListOf<Universe>() }

    LaunchedEffect(Unit) {
        db.collection("universes")
            .get()
            .addOnSuccessListener { result ->
                universes.clear()
                for (document in result.documents) {
                    val universe = document.toObject(Universe::class.java)
                    if (universe != null) {
                        universes.add(universe)
                    }
                }
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Univers Disney")

        Button(
            onClick = onProfileClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        ) {
            Text("Voir mon profil")
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 16.dp)
        ) {
            items(universes) { universe ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUniverseClick(universe) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = universe.name,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }
    }
}