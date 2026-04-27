package com.example.recircuitai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import com.example.recircuitai.data.RecycleItem
import com.example.recircuitai.data.GlobalAppState
import com.example.recircuitai.ui.components.EcoButton
import com.example.recircuitai.ui.components.TagChip
import com.example.recircuitai.ui.theme.*

import androidx.compose.runtime.*
import com.example.recircuitai.data.MockData
import com.example.recircuitai.data.network.NetworkClient
import com.example.recircuitai.data.network.NetworkConfig
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailsScreen(
    itemId: String,
    onBackClick: () -> Unit
) {
    var item by remember { mutableStateOf<RecycleItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(itemId) {
        isLoading = true
        errorMessage = null

        // Priority 1: Use the item passed directly from navigation (already has correct imageUrl)
        val passedItem = GlobalAppState.selectedItem
        if (passedItem != null && passedItem.id == itemId) {
            item = passedItem
            isLoading = false
            return@LaunchedEffect
        }

        // Priority 2: Local MockData
        val allItems = MockData.getCuratedNeeds() + MockData.getCuratedUploads() + MockData.getCompanyRecentShipments()
        val localItem = allItems.find { it.id == itemId }
        if (localItem != null) {
            item = localItem
            isLoading = false
            return@LaunchedEffect
        }

        // Priority 3: Network fetch (fallback)
        try {
            val response = NetworkClient.getApiService().getItem(itemId)
            if (response.isSuccessful) {
                val rawItem = response.body()?.data
                if (rawItem != null) {
                    val url = rawItem.imageUrl
                    item = rawItem.copy(
                        imageUrl = when {
                            url.startsWith("http") || url.startsWith("android.resource") -> url
                            else -> {
                                val base = NetworkConfig.baseUrl.trimEnd('/')
                                "$base/${url.removePrefix("/")}"
                            }
                        }
                    )
                } else {
                    errorMessage = "Item not found."
                }
            } else {
                errorMessage = "Error: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Network error. Please check your connection."
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryGreen)
        }
    } else if (errorMessage != null || item == null) {
        Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(errorMessage ?: "Item not found.", color = Color.Red)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackClick) { Text("Go Back") }
            }
        }
    } else {
        val safeItem = item!!
        Scaffold(
            bottomBar = {
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                        ) {
                            Text("Chat", style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp))
                        }
                        val context = LocalContext.current
                        EcoButton(
                            text = "Match Item",
                            onClick = { 
                                android.widget.Toast.makeText(context, "Item Matched Successfully!", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.weight(2f)
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // ── Top Image with Overlay ──
                Box(modifier = Modifier.fillMaxWidth().height(350.dp)) {
                    SubcomposeAsyncImage(
                        model = safeItem.imageUrl,
                        contentDescription = safeItem.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize().background(EmeraldMedium.copy(alpha = 0.10f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = EmeraldMedium, modifier = Modifier.size(40.dp))
                            }
                        },
                        error = {
                            Box(
                                modifier = Modifier.fillMaxSize().background(EmeraldMedium.copy(alpha = 0.08f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Image, contentDescription = null, tint = EmeraldMedium.copy(alpha = 0.4f), modifier = Modifier.size(64.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Image unavailable", color = TextGray, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    )

                    // Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)),
                                    startY = 600f
                                )
                            )
                    )

                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp)
                            .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextBlack)
                    }



                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        color = EmeraldMedium,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Available",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = safeItem.title,
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 28.sp),
                            color = TextBlack,
                            modifier = Modifier.weight(1f)
                        )

                        if (safeItem.quantity != null) {
                            Surface(
                                color = MintVibrant.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = safeItem.quantity,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PrimaryGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldMedium, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${safeItem.location} • ${safeItem.distance} away",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        color = TextBlack
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = safeItem.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextGray,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // AI Verified Info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EmeraldMedium.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = EmeraldMedium, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "AI Analyzed Details",
                                style = MaterialTheme.typography.labelSmall,
                                color = EmeraldMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Composition: ${safeItem.aiData?.materialContent ?: "Unknown"} • Condition: ${safeItem.aiData?.condition ?: "N/A"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextBlack
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Tags / Properties Row
                    Text(
                        text = "Properties",
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                        color = TextBlack
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        safeItem.aiData?.tags?.forEach { tag ->
                            Surface(
                                color = SecondaryGreen.copy(alpha = 0.14f),
                                shape = RoundedCornerShape(28.dp)
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                                    color = SecondaryGreen
                                )
                            }
                        }
                    }

                    if (safeItem.owner != null) {
                        Spacer(modifier = Modifier.height(32.dp))

                        Text(
                            text = "Listed by",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextBlack,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(1.dp, RoundedCornerShape(20.dp))
                                .background(Color.White, RoundedCornerShape(20.dp))
                                .padding(16.dp)
                        ) {
                        if (safeItem.ownerImage != null) {
                            AsyncImage(
                                model = safeItem.ownerImage,
                                contentDescription = "Owner",
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, EmeraldMedium.copy(alpha = 0.2f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = EmeraldMedium,
                                modifier = Modifier.size(40.dp)
                            )
                        }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = safeItem.owner,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextBlack,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (safeItem.owner?.lowercase()?.contains("home") == true || 
                                              safeItem.owner?.lowercase()?.contains("house") == true) 
                                              "Eco-Contributor" else "Verified Eco-Partner",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldMedium
                                )
                            }

                            if (safeItem.owner?.lowercase()?.contains("home") == false && 
                                safeItem.owner?.lowercase()?.contains("house") == false) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = EmeraldMedium,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}

@Composable
fun DetailChip(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium.copy(fontSize = 13.sp), color = TextGray)
            Text(value, style = MaterialTheme.typography.titleSmall.copy(fontSize = 16.sp), color = TextBlack, fontWeight = FontWeight.Bold)
        }
    }
}
