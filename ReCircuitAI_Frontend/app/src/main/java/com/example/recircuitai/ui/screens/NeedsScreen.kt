package com.example.recircuitai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recircuitai.ui.theme.*
import com.example.recircuitai.data.GlobalAppState
import com.example.recircuitai.data.MockData
import com.example.recircuitai.data.RecycleItem
import com.example.recircuitai.ui.components.ReCard
import com.example.recircuitai.data.network.NetworkClient
import com.example.recircuitai.data.network.NetworkConfig


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NeedsScreen(
    onItemClick: (RecycleItem) -> Unit,
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    // 1. Get curated needs (mock data for high-fidelity look)
    val curatedNeeds = remember { MockData.getCuratedNeeds() }
    
    // 2. State for live items from backend
    var liveItems by remember { mutableStateOf<List<RecycleItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 3. Fetch from backend on launch
    LaunchedEffect(Unit) {
        try {
            val response = NetworkClient.getApiService().getItems()
            if (response.isSuccessful && response.body()?.success == true) {
                val rawItems = response.body()?.data ?: emptyList()
                
                // Process URLs: convert server paths like "/uploads/..." to full URLs
                val processedItems = rawItems.map { item ->
                    if (item.imageUrl.startsWith("/uploads")) {
                        // Ensure no double slashes between baseUrl (http://...:3000/) and path
                        val cleanPath = item.imageUrl.removePrefix("/")
                        item.copy(imageUrl = NetworkConfig.baseUrl + cleanPath)
                    } else {
                        item
                    }
                }
                
                // Filter for "Eco" items (Nature-made / Decomposable)
                liveItems = processedItems.filter { item ->
                    val cat = item.category.lowercase()
                    val tags = item.aiData?.tags?.map { it.lowercase() } ?: emptyList()
                    
                    // Direct category match
                    cat == "organic" || cat == "wood" || cat == "paper" || cat == "stationary" || cat == "calendar" ||
                    // Keyword match in tags or title (ensure notebooks/calendars appear)
                    tags.any { it.contains("paper") || it.contains("notebook") || it.contains("calendar") || it.contains("stationary") } ||
                    item.title.lowercase().let { it.contains("notebook") || it.contains("paper") || it.contains("calendar") }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    // 4. Combine: Real uploads at the top + Curated High-Fidelity items below
    val displayItems = remember(liveItems) {
        liveItems + curatedNeeds
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(BackgroundGreen)
    ) {
        // Top Section: Logo & Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), 
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (GlobalAppState.isCompanyMode) "Procurement Needs" else "Recycling Needs",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { /* Order All */ },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldMedium),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Order All", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    color = EmeraldMedium.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(24.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldMedium.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldMedium)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("AI Matching Active", style = MaterialTheme.typography.titleMedium, color = EmeraldMedium, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.weight(1f))
                            // Connection Status Badge
                            Surface(
                                color = if (isLoading) Color.LightGray else if (liveItems.isNotEmpty()) EmeraldMedium else Color(0xFFFFA500),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (isLoading) "Syncing..." else if (liveItems.isNotEmpty()) "Live Sync" else "Offline Mode",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            if (liveItems.isNotEmpty()) "We found ${liveItems.size} high-fidelity matches from local contributors!" 
                            else "Displaying curated needs. Connect to backend to see live matches.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }
                }
            }

            if (isLoading && liveItems.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EmeraldMedium)
                    }
                }
            }

            // Display live uploads + curated items
            items(displayItems) { item ->
                // Mark live items as High-Fidelity Matches
                val isLiveMatch = liveItems.contains(item)
                ReCard(
                    item = item,
                    isAiMatch = isLiveMatch,
                    onClick = { onItemClick(item) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
