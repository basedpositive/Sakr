package com.example.sakr.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.GeoPoint


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SacredPlacesScreen(navController: NavHostController) {
    val db = FirebaseFirestore.getInstance()
    var places by remember { mutableStateOf(listOf<SacredPlace>()) }

    LaunchedEffect(Unit) {
        val snapshot = db.collection("sacred_places").get().await()
        places = snapshot.documents.map { doc ->
            SacredPlace(
                name = doc.getString("name") ?: "",
                description = doc.getString("description") ?: "",
                imageUrl = doc.getString("imageUrl") ?: "",
                location = doc.getGeoPoint("location") ?: GeoPoint(0.0, 0.0)
            )
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Список сакральных мест") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            if (places.isEmpty()) {
                Text("Загрузка данных...")
            } else {
                SacredPlacesList(places)

            }
        }
    }
}

data class SacredPlace(
    val name: String,
    val description: String,
    val imageUrl: String,
    val location: GeoPoint
)

@Composable
fun SacredPlacesList(places: List<SacredPlace>) {
    LazyColumn {
        items(places.size) { index ->
            val place = places[index]
            SacredPlaceItem(place)
            Text(text = "Координаты: ${place.location.latitude}, ${place.location.longitude}")
        }
    }
}

@Composable
fun SacredPlaceItem(place: SacredPlace) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = place.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = place.description)
            Spacer(modifier = Modifier.height(8.dp))
            Image(
                painter = rememberAsyncImagePainter(place.imageUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
    }
}

