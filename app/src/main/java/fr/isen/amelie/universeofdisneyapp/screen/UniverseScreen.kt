package fr.isen.amelie.universeofdisneyapp.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import fr.isen.amelie.universeofdisneyapp.AppTopBar
import fr.isen.amelie.universeofdisneyapp.activity.Universe

@Composable
fun UniverseScreen(
    onUniverseClick: (Universe) -> Unit
) {

    val database = FirebaseDatabase.getInstance().reference
    val universes = remember { mutableStateListOf<Universe>() }

    LaunchedEffect(Unit) {
        database.child("universes")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    universes.clear()
                    for (child in snapshot.children) {
                        val universe = child.getValue(Universe::class.java)
                        if (universe != null) {
                            universes.add(universe)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AppTopBar(title = "Universes")
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
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
