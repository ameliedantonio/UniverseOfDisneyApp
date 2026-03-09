package fr.isen.amelie.universeofdisneyapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = FirebaseFirestore.getInstance()

        val data = hashMapOf(
            "name" to "test",
            "type" to "test"
        )

        db.collection("characters")
            .add(data)
            .addOnSuccessListener { documentReference ->
                Log.e("FIREBASE_TEST", "Document ajouté : ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("FIREBASE_TEST", "Erreur Firestore : ${e.message}", e)
            }

        setContent {
        }
    }
}