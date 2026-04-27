package com.example.recircuitai.ui.screens

import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recircuitai.data.RecycleItem
import com.example.recircuitai.data.network.NetworkClient
import com.example.recircuitai.data.network.NetworkConfig
import com.example.recircuitai.data.GlobalAppState
import com.example.recircuitai.ui.components.ReCard
import com.example.recircuitai.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onItemClick: (RecycleItem) -> Unit,
    onUploadClick: () -> Unit,
    onMapClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Plastic", "Wood", "Organic", "Metal", "Glass")
    var selectedCategory by remember { mutableStateOf("All") }

    var items by remember { mutableStateOf<List<RecycleItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showIpDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Animated search placeholder
    val placeholders = listOf("Search for materials...", "Search near you...", "Find wood, organic...", "Search for products...")
    var placeholderIndex by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(2500)
            placeholderIndex = (placeholderIndex + 1) % placeholders.size
        }
    }

    fun fetchItems() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = NetworkClient.getApiService().getItems()
                if (response.isSuccessful) {
                    val fullItems = response.body()?.data ?: emptyList()
                    items = fullItems.map {
                        val url = it.imageUrl
                        it.copy(imageUrl = if (url.startsWith("http") || url.startsWith("android.resource")) url else {
                            val base = NetworkConfig.baseUrl.trimEnd('/')
                            val path = url.removePrefix("/")
                            "$base/$path"
                        })
                    }
                } else {
                    errorMessage = "Server Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = "Network Error: ${e.localizedMessage ?: "Unknown Error"}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(NetworkConfig.baseUrl) { fetchItems() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(BackgroundGreen)
    ) {
        // ── Pinned Logo / Settings Row (does NOT scroll) ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "android.resource://com.example.recircuitai/drawable/logo",
                contentDescription = "Logo",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (GlobalAppState.isCompanyMode) "Procurement Hub" else "ReCircuit",
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = PrimaryGreen,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                onClick = { showIpDialog = true },
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(40.dp),
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = PrimaryGreen, modifier = Modifier.size(20.dp))
                }
            }
        }

        // ── Main scrollable area ──
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryGreen)
            }
        } else if (errorMessage != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.WifiOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.Red.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage!!, color = Color.Red, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { fetchItems() }) { Text("Retry") }
            }
        } else {
            val filteredItems = items.filter {
                (selectedCategory == "All" || it.category == selectedCategory) &&
                (it.title.contains(searchQuery, ignoreCase = true) || it.description.contains(searchQuery, ignoreCase = true))
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // Search bar scrolls with content
                item(key = "searchBar") {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { onSearchClick() },
                        color = Color.White,
                        shape = RoundedCornerShape(28.dp),
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TextGray.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            androidx.compose.animation.AnimatedContent(
                                targetState = placeholders[placeholderIndex],
                                transitionSpec = {
                                    (androidx.compose.animation.fadeIn() + androidx.compose.animation.slideInVertically { it })
                                        .togetherWith(androidx.compose.animation.fadeOut() + androidx.compose.animation.slideOutVertically { -it })
                                },
                                label = "placeholderAnim"
                            ) { text ->
                                Text(text, color = TextGray.copy(alpha = 0.5f), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                // Category chips scroll with content
                item(key = "categories") {
                    Text(
                        text = "Categories",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextBlack,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldMedium,
                                    selectedLabelColor = Color.White,
                                    containerColor = Color.White.copy(alpha = 0.5f),
                                ),
                                shape = RoundedCornerShape(12.dp),
                                border = null
                            )
                        }
                    }
                }

                if (filteredItems.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No items found.", color = TextGray)
                        }
                    }
                } else {
                    items(filteredItems, key = { it.id }) { item ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                            ReCard(item = item, onClick = { onItemClick(item) })
                        }
                    }
                }
            }
        }
    }

    if (showIpDialog) {
        var tempIp by remember { mutableStateOf(NetworkConfig.ipAddress) }
        AlertDialog(
            onDismissRequest = { showIpDialog = false },
            title = { Text("Server IP") },
            text = {
                OutlinedTextField(
                    value = tempIp,
                    onValueChange = { tempIp = it },
                    label = { Text("IP Address") }
                )
            },
            confirmButton = {
                Button(onClick = {
                    NetworkConfig.ipAddress = tempIp
                    showIpDialog = false
                }) { Text("Save") }
            }
        )
    }
}
