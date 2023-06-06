package com.example.eatit.ui

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.eatit.viewModel.RestaurantsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@Composable
fun MapScreen(
    startLocationUpdates: () -> Unit,
    restaurantsViewModel: RestaurantsViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var restaurants by remember { mutableStateOf<List<DocumentSnapshot>>(emptyList()) }
    val myPosition by rememberSaveable { restaurantsViewModel.restaurantFromGPS }
    val markers = remember { mutableStateListOf<MarkerInfo>() }
    var cameraPositionState = rememberCameraPositionState {
        CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }
    //startLocationUpdates()
    Log.d("currentLocation1", myPosition)
    val currentLocation = Geocoder(context).getFromLocationName("fano", 1)
    Log.d("currentLocation2", currentLocation.toString())
    if (currentLocation != null && currentLocation.size > 0) {
        cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(
                LatLng(currentLocation[0].latitude, currentLocation[0].longitude), 5f
            )
        }
    }
    LaunchedEffect(Unit) {
        restaurants = restaurantsViewModel.getRestaurants()
    }
    LaunchedEffect(restaurants) {
        markers.clear()
        for (restaurant in restaurants) {
            val position = withContext(Dispatchers.IO) {
                Geocoder(context).getFromLocationName(restaurant["city"].toString(), 1)
            }
            if (position != null && position.size > 0) {
                val latitude = position[0].latitude
                val longitude = position[0].longitude
                markers.add(
                    MarkerInfo(
                        restaurant["name"].toString(),
                        LatLng(latitude, longitude)
                    )
                )
            }
        }
    }
    Scaffold { innerPadding ->
        Column(modifier.padding(innerPadding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
            ) {
                markers.forEach { markerInfo ->
                    Marker(
                        state = rememberMarkerState(
                            position = markerInfo.position
                        ),
                        title = markerInfo.title
                    )
                }
            }
        }
    }
}

data class MarkerInfo(
    val title: String,
    val position: LatLng
)
