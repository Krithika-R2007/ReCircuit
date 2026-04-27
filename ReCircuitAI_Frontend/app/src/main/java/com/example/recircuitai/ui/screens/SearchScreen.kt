package com.example.recircuitai.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.staggeredgrid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recircuitai.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onItemClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember { mutableStateListOf("Plastic Bottles", "Iron Rods", "Wooden Scraps", "Industrial Copper") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Discovery", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundGreen)
            )
        },
        containerColor = BackgroundGreen
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar with Mic & AI
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search for anything...", color = TextGray.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EmeraldMedium) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                            IconButton(onClick = { /* AI Chat */ }) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = "AI Search", tint = EmeraldMedium)
                            }
                            IconButton(onClick = { /* Mic */ }) {
                                Icon(Icons.Default.Mic, contentDescription = "Voice search", tint = EmeraldMedium)
                            }
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = EmeraldMedium
                    ),
                    singleLine = true
                )
            }

            // Recent Searches
            Text(
                text = "Recent Searches",
                style = MaterialTheme.typography.titleSmall,
                color = TextBlack,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )
            
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                items(recentSearches) { search ->
                    RecentSearchChip(text = search, onDelete = { recentSearches.remove(search) })
                }
            }

            // Mosaic Discovery Grid
            Text(
                text = "Trending in your area",
                style = MaterialTheme.typography.titleMedium,
                color = TextBlack,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
            )

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 12.dp
            ) {
                val discoveryItems = listOf(
                    Triple("d1", "Wooden Table", "Wood"),
                    Triple("d2", "Broken Laptop", "Electronic"),
                    Triple("d3", "Cotton Clothes", "Organic"),
                    Triple("d4", "Old Steel Bed", "Metal"),
                    Triple("d5", "Bike Cover Plastic", "Plastic"),
                    Triple("coconut", "Fresh Coconuts", "Organic"),
                    Triple("c1", "Banana Leaves", "Organic"),
                    Triple("c2", "Old Wooden Door", "Wood"),
                    Triple("c3", "Ceramic Dishes", "Organic"),
                    Triple("wooden_chair", "Teak Chair", "Wood"),
                    Triple("plastic", "Plastic Sheets", "Plastic"),
                    Triple("glass", "Window Glass", "Glass")
                )
                items(discoveryItems) { (drawable, title, category) ->
                    MosaicItem(
                        imageRes = "android.resource://com.example.recircuitai/drawable/$drawable",
                        title = title,
                        category = category,
                        onClick = { onItemClick(drawable) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecentSearchChip(text: String, onDelete: () -> Unit) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, TextGray.copy(alpha = 0.1f))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = text, style = MaterialTheme.typography.bodySmall, color = TextGray)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                Icons.Default.Close, 
                contentDescription = "Delete", 
                tint = TextGray, 
                modifier = Modifier.size(14.dp).clickable { onDelete() }
            )
        }
    }
}

@Composable
fun MosaicItem(imageRes: String, title: String, category: String, onClick: () -> Unit) {
    val height = remember { (170..260).random().dp }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 400f),
        label = "cardScale"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        pressed = true
                        tryAwaitRelease()
                        pressed = false
                    },
                    onTap = { onClick() }
                )
            },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = imageRes,
                contentDescription = title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
                            startY = 250f
                        )
                    )
            )
            Column(
                modifier = Modifier.align(Alignment.BottomStart).padding(14.dp)
            ) {
                Text(title, color = Color.White, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(category, color = Color.White.copy(alpha = 0.75f), style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
