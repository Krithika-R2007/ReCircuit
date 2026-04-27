package com.example.recircuitai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recircuitai.data.MockData
import com.example.recircuitai.ui.components.UserImpactDashboard
import com.example.recircuitai.ui.theme.*

@Composable
fun ImpactScreen() {
    val stats = MockData.userStats
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(BackgroundGreen)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Impact Dashboard",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = TextBlack
                )
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            item {
                Surface(
                    color = EmeraldMedium.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = EmeraldMedium)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Sustainability Report", style = MaterialTheme.typography.titleMedium, color = EmeraldMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Your monthly recycling habits saved 156.4kg of waste from landfills. You are in the top 5% of active recyclers in Chennai!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextGray,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Analytics Overview",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextBlack,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            item {
                UserImpactDashboard(stats)
            }

            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
