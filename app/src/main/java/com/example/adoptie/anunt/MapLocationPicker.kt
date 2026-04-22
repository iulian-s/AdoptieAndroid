package com.example.adoptie.anunt

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapLocationPicker(
    onLocationSelected: (LatLng) -> Unit
) {
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(44.4268, 26.1025), 10f) // Default București
    }

    Column(modifier = Modifier.fillMaxWidth().height(300.dp)) {
        Text("Selectează locația pe hartă:", style = MaterialTheme.typography.bodyMedium)
        GoogleMap(
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                markerPosition = latLng
                onLocationSelected(latLng)
            }
        ) {
            markerPosition?.let {
                Marker(state = MarkerState(position = it))
            }
        }
    }
}