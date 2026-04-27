package com.example.recircuitai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.recircuitai.data.MockData
import com.example.recircuitai.data.network.NetworkClient
import com.example.recircuitai.data.network.NetworkConfig
import com.example.recircuitai.data.RecycleItem
import com.example.recircuitai.ui.components.TagChip
import com.example.recircuitai.ui.theme.*
import com.example.recircuitai.data.GlobalAppState
import com.example.recircuitai.data.UserStats
import com.example.recircuitai.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onUploadClick: () -> Unit,
    onItemClick: (RecycleItem) -> Unit = {}
) {
    val isCompanyMode = GlobalAppState.isCompanyMode
    val stats = if (isCompanyMode) MockData.companyStats else MockData.userStats
    val context = LocalContext.current

    // Fetch live data for Profile
    var liveItems by remember { mutableStateOf<List<RecycleItem>>(emptyList()) }
    LaunchedEffect(Unit) {
        try {
            val response = NetworkClient.getApiService().getItems()
            if (response.isSuccessful && response.body()?.success == true) {
                val rawItems = response.body()?.data ?: emptyList()
                liveItems = rawItems.map { item ->
                    if (item.imageUrl.startsWith("/uploads")) {
                        item.copy(imageUrl = NetworkConfig.baseUrl + item.imageUrl.removePrefix("/"))
                    } else {
                        item
                    }
                }.filter { it.id.length > 5 }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(BackgroundGreen)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        ) {
            // ── Header ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextBlack
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Surface(
                        color = EmeraldMedium.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = EmeraldMedium)
                        }
                    }
                }
            }

            // ── Profile Card ──
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            AsyncImage(
                                model = stats.profileImage,
                                contentDescription = "Profile",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(3.dp, EmeraldMedium.copy(alpha = 0.3f), CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            if (isCompanyMode) {
                                Surface(
                                    modifier = Modifier.size(26.dp).offset(x = 2.dp, y = 2.dp),
                                    color = EmeraldMedium,
                                    shape = CircleShape
                                ) {
                                    Icon(
                                        Icons.Default.Verified,
                                        contentDescription = "Verified",
                                        tint = Color.White,
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = stats.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextBlack,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Text(
                            text = if (isCompanyMode) "Verified Business Partner" else "Eco-Conscious Individual",
                            style = MaterialTheme.typography.labelMedium,
                            color = EmeraldMedium
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        ContactRow(Icons.Default.Email, stats.email)
                        Spacer(modifier = Modifier.height(8.dp))
                        ContactRow(Icons.Default.Phone, stats.phoneNumber)
                        Spacer(modifier = Modifier.height(8.dp))
                        ContactRow(Icons.Default.LocationOn, stats.location)
                    }
                }
            }

            // ── Stats / Impact Section ──
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = if (isCompanyMode) "Company Impact" else "My Impact",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = TextBlack,
                    modifier = Modifier.padding(bottom = 12.dp, top = 12.dp)
                )
                if (isCompanyMode) {
                    // Company: 4-box grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Orders",
                            value = "128",
                            icon = Icons.Default.ShoppingBag
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Rating",
                            value = "4.8",
                            icon = Icons.Default.Star
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Matched",
                            value = "${stats.matchedItems ?: 45}",
                            icon = Icons.Default.Sync
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Waste Saved",
                            value = "${stats.wasteSavedKg.toInt()}kg",
                            icon = Icons.Default.Eco
                        )
                    }
                } else {
                    // User: 2-box row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Uploads",
                            value = stats.totalUploads.toString(),
                            icon = Icons.Default.CloudUpload
                        )
                        StatCard(
                            modifier = Modifier.weight(1f),
                            label = "Waste Saved",
                            value = "${stats.wasteSavedKg}kg",
                            icon = Icons.Default.Eco
                        )
                    }
                }
            }

            // ── Company: Products & Materials section ──
            if (isCompanyMode) {
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Organic Home Use Products",
                        style = MaterialTheme.typography.headlineSmall.copy(fontSize = 20.sp),
                        color = TextBlack,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Eco-friendly daily essentials crafted from natural, recirculated raw materials sourced locally.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        lineHeight = 19.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Materials We Need",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextBlack,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    val materialTags = listOf(
                        "Coconut Shell", "Teak Wood", "Banana Leaves",
                        "Cardboard", "Cotton Fibre", "Plant Husk", "Recycled Paper"
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        materialTags.forEach { tag -> MaterialTag(tag = tag) }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }

            // ── Listings section ──
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isCompanyMode) "Recent Procurement" else "My Listings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = TextBlack
                    )
                    Text(
                        text = "See all",
                        style = MaterialTheme.typography.labelMedium,
                        color = EmeraldMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val baseListings = if (isCompanyMode) {
                MockData.getCompanyProfileListings()
            } else {
                MockData.getCuratedUploads()
            }
            
            // Show real uploads at the top
            val listingItems = liveItems + baseListings

            items(listingItems) { item ->
                ProfileListingCard(
                    item = item,
                    isCompanyMode = isCompanyMode,
                    onClick = { onItemClick(item) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // ── Operating Mode Toggle (Moved to Bottom) ──
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = EmeraldMedium.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EmeraldMedium.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isCompanyMode) Icons.Default.Business else Icons.Default.Person,
                                contentDescription = null,
                                tint = EmeraldMedium,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Operating Mode",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextBlack,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isCompanyMode) "Business Procurement" else "Individual Recycling",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextGray
                                )
                            }
                        }
                        Switch(
                            checked = isCompanyMode,
                            onCheckedChange = { GlobalAppState.isCompanyMode = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = EmeraldMedium
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }

            item { Spacer(modifier = Modifier.height(120.dp)) }
        }
    }
}

@Composable
fun ProfileListingCard(
    item: RecycleItem,
    isCompanyMode: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextBlack,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EmeraldMedium.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isCompanyMode) "Procured" else "Available",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        color = SecondaryGreen.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.category,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = SecondaryGreen,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray.copy(alpha = 0.5f))
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, contentDescription = null, tint = EmeraldMedium, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = TextGray)
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, icon: ImageVector) {
    Surface(
        modifier = modifier,
        color = Color.White,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = EmeraldMedium,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 17.sp),
                color = TextBlack,
                fontWeight = FontWeight.ExtraBold
            )
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextGray)
        }
    }
}

@Composable
fun MyUploadItemCard(title: String, imageUrl: String, status: String, description: String = "") {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(68.dp).clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleSmall, color = TextBlack, fontWeight = FontWeight.Bold)
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextGray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(status, style = MaterialTheme.typography.labelSmall, color = EmeraldMedium, fontWeight = FontWeight.SemiBold)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextGray)
        }
    }
}

@Composable
fun MaterialTag(tag: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = EmeraldMedium.copy(alpha = 0.10f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = tag,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = PrimaryGreen
        )
    }
}


