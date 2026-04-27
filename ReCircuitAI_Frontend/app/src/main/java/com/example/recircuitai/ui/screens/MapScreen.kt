package com.example.recircuitai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recircuitai.ui.theme.*
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.Marker
import android.preference.PreferenceManager

@Composable
fun MapScreen(
    onHomeClick: () -> Unit,
    onUploadClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val context = LocalContext.current
    
    // Chennai coordinates
    val chennaiPoint = GeoPoint(13.0827, 80.2707)

    val hubs = listOf(
        GeoPoint(12.9717, 80.2425) to "OMR Hub",
        GeoPoint(13.0067, 80.2578) to "Adyar Depot",
        GeoPoint(13.0850, 80.2700) to "Central Depot"
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(BackgroundGreen)
    ) {
        // Top Section: Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Recycling Map",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
        ) {
            // Free OpenStreetMap Implementation
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    // Important for Osmdroid
                    Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
                    
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)
                        controller.setZoom(12.0)
                        controller.setCenter(chennaiPoint)
                        
                        hubs.forEach { (point, title) ->
                            val marker = Marker(this)
                            marker.position = point
                            marker.title = title
                            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            overlays.add(marker)
                        }
                    }
                },
                update = { view ->
                    view.controller.setCenter(chennaiPoint)
                }
            )
            
            // Search overlay on map
            Surface(
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp).fillMaxWidth(0.9f),
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp
            ) {
                Row(
                   modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                   verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextGray)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Search nearby hubs...", color = TextGray, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Floating Filter on map
            Surface(
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 80.dp, end = 16.dp),
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 4.dp
            ) {
                IconButton(onClick = { /* Filter */ }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = EmeraldMedium)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Stats overlay at bottom of map
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MapStat(label = "Hubs", value = "12")
            MapStat(label = "Nearby", value = "1.2 km")
            MapStat(label = "Saved", value = "124 kg")
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom bar
    }
}

@Composable
fun MapPin(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            color = PrimaryGreen,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 4.dp
        ) {
            Text(
                text = title,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = PrimaryGreen,
            modifier = Modifier.size(32.dp).offset(y = (-4).dp)
        )
    }
}

@Composable
fun MapStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = TextBlack, fontWeight = FontWeight.ExtraBold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextGray)
    }
}
