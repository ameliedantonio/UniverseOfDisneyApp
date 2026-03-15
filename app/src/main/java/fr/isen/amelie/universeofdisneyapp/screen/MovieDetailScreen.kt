package fr.isen.amelie.universeofdisneyapp.screen

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.isen.amelie.universeofdisneyapp.R
import fr.isen.amelie.universeofdisneyapp.activity.Movie
import fr.isen.amelie.universeofdisneyapp.activity.MovieOwnerInfo

@Composable
fun MovieDetailScreen(
    movie: Movie,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().reference
    val sharedUsers = remember { mutableStateListOf<MovieOwnerInfo>() }
    var universeName by remember { mutableStateOf(movie.universeId) }
    var currentViewStatus by remember { mutableStateOf("") }
    var currentOwnershipStatus by remember { mutableStateOf("") }

    fun loadSharedGetRidUsers() {
        database.child("shared_get_rid")
            .child(movie.id)
            .child("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    sharedUsers.clear()
                    for (data in snapshot.children) {
                        val userEmail = data.child("userEmail").getValue(String::class.java) ?: ""
                        val status = data.child("status").getValue(String::class.java) ?: ""
                        sharedUsers.add(
                            MovieOwnerInfo(
                                userEmail = userEmail,
                                status = status
                            )
                        )
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
    fun saveMovieStatuses(
        viewStatus: String = currentViewStatus,
        ownershipStatus: String = currentOwnershipStatus
    ) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(
                context,
                "No user logged in",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        val statusData = mapOf(
            "movieId" to movie.id,
            "title" to movie.title,
            "universeId" to movie.universeId,
            "category" to movie.category,
            "releaseDate" to movie.releaseDate,
            "userEmail" to (user.email ?: ""),
            "posterPath" to movie.posterPath,
            "viewStatus" to viewStatus,
            "ownershipStatus" to ownershipStatus
        )
        database.child("users")
            .child(user.uid)
            .child("movieStatuses")
            .child(movie.id)
            .setValue(statusData)
            .addOnSuccessListener {
                currentViewStatus = viewStatus
                currentOwnershipStatus = ownershipStatus
                if (ownershipStatus == "want_to_get_rid") {
                    val sharedData = mapOf(
                        "userEmail" to (user.email ?: ""),
                        "status" to ownershipStatus
                    )
                    database.child("shared_get_rid")
                        .child(movie.id)
                        .child("users")
                        .child(user.uid)
                        .setValue(sharedData)
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Statuses saved",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadSharedGetRidUsers()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Sharing error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                } else {
                    database.child("shared_get_rid")
                        .child(movie.id)
                        .child("users")
                        .child(user.uid)
                        .removeValue()
                        .addOnSuccessListener {
                            Toast.makeText(
                                context,
                                "Statuses saved",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadSharedGetRidUsers()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Share deletion error: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(
                    context,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
    LaunchedEffect(movie.id) {
        loadSharedGetRidUsers()
        database.child("universes")
            .child(movie.universeId)
            .get()
            .addOnSuccessListener { snapshot ->
                universeName =
                    snapshot.child("name").getValue(String::class.java) ?: movie.universeId
            }
        val user = auth.currentUser
        if (user != null) {
            database.child("users")
                .child(user.uid)
                .child("movieStatuses")
                .child(movie.id)
                .get()
                .addOnSuccessListener { snapshot ->
                    currentViewStatus = snapshot.child("viewStatus").getValue(String::class.java) ?: ""
                    currentOwnershipStatus = snapshot.child("ownershipStatus").getValue(String::class.java) ?: ""                }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = colorResource(id = R.color.blue_soft_white),
                        shape = CircleShape
                    )
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = colorResource(id = R.color.blue_dark)
                )
            }
            Text(
                text = movie.title,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 56.dp),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = colorResource(id = R.color.blue_soft_white),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.imageUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(150.dp)
                    .height(220.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 2.dp,
                        color = colorResource(id = R.color.blue_soft_white).copy(alpha = 0.75f),
                        shape = RoundedCornerShape(20.dp)
                    )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.height(220.dp),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MovieMiniInfoCard(movie.releaseDate)
                MovieMiniInfoCard(universeName)
                if (movie.category.isNotBlank()) {
                    MovieMiniInfoCard(movie.category)
                }
                if (movie.genre.isNotBlank()) {
                    MovieMiniInfoCard(movie.genre)
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Synopsis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.blue_soft_white)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = R.color.blue_soft_white).copy(alpha = 0.92f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Text(
                text = if (movie.overview.isNotBlank()) movie.overview else "Synopsis coming soon...",
                modifier = Modifier.padding(16.dp),
                color = colorResource(id = R.color.blue_dark),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Your status",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.blue_soft_white)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                title = "Watched",
                icon = Icons.Default.CheckCircle,
                isSelected = currentViewStatus == "watched",
                modifier = Modifier.weight(1f),
                onClick = {
                    saveMovieStatuses(
                        viewStatus = "watched",
                        ownershipStatus = currentOwnershipStatus
                    )
                }
            )
            StatusCard(
                title = "Want to watch",
                icon = Icons.Default.Schedule,
                isSelected = currentViewStatus == "want_to_watch",
                modifier = Modifier.weight(1f),
                onClick = {
                    saveMovieStatuses(
                        viewStatus = "want_to_watch",
                        ownershipStatus = currentOwnershipStatus
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatusCard(
                title = "Own",
                icon = Icons.Default.Bookmark,
                isSelected = currentOwnershipStatus == "owned" || currentOwnershipStatus == "want_to_get_rid",
                modifier = Modifier.weight(1f),
                onClick = {
                    saveMovieStatuses(
                        viewStatus = currentViewStatus,
                        ownershipStatus = "owned"
                    )
                }
            )
            StatusCard(
                title = "Get rid",
                icon = Icons.Default.Delete,
                isSelected = currentOwnershipStatus == "want_to_get_rid",
                modifier = Modifier.weight(1f),
                onClick = {
                    saveMovieStatuses(
                        viewStatus = currentViewStatus,
                        ownershipStatus = "want_to_get_rid"
                    )
                }
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = "Users who want to get rid of this movie",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.blue_soft_white)
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (sharedUsers.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorResource(id = R.color.blue_soft_white).copy(alpha = 0.90f)
                )
            ) {
                Text(
                    text = "No users for the moment",
                    modifier = Modifier.padding(16.dp),
                    color = colorResource(id = R.color.blue_dark)
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                sharedUsers.forEach { userInfo ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = userInfo.userEmail,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(id = R.color.blue_dark)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Wants to get rid of it",
                                color = colorResource(id = R.color.blue_mid)
                            )
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun StatusCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val animatedContainerColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorResource(id = R.color.blue_mid)
        } else {
            Color.White
        },
        label = "statusCardColor"
    )
    val animatedTextColor by animateColorAsState(
        targetValue = if (isSelected) {
            colorResource(id = R.color.blue_soft_white)
        } else {
            colorResource(id = R.color.blue_dark)
        },
        label = "statusCardTextColor"
    )
    Card(
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = animatedContainerColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = animatedTextColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                color = animatedTextColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MovieMiniInfoCard(
    text: String
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(40.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        border = BorderStroke(
            1.5.dp,
            colorResource(id = R.color.blue_soft_white).copy(alpha = 0.9f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = colorResource(id = R.color.blue_soft_white),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}