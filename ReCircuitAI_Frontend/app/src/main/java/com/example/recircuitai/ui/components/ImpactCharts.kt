package com.example.recircuitai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recircuitai.ui.theme.EmeraldMedium
import com.example.recircuitai.ui.theme.PrimaryGreen
import com.example.recircuitai.ui.theme.SecondaryGreen
import com.example.recircuitai.ui.theme.TextBlack
import com.example.recircuitai.ui.theme.TextGray

@Composable
fun LineSparkline(data: List<Float>, modifier: Modifier = Modifier) {
    val maxValue = data.maxOrNull() ?: 1f
    
    Canvas(modifier = modifier.height(50.dp).fillMaxWidth()) {
        val width = size.width
        val height = size.height
        val stepX = width / (data.size - 1)
        
        val points = data.mapIndexed { index, value ->
            Offset(index * stepX, height - (value / maxValue * height))
        }
        
        val path = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                cubicTo(
                    (points[i - 1].x + points[i].x) / 2, points[i - 1].y,
                    (points[i - 1].x + points[i].x) / 2, points[i].y,
                    points[i].x, points[i].y
                )
            }
        }
        
        drawPath(
            path = path,
            color = EmeraldMedium,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        
        // Gradient fill
        val fillPath = Path().apply {
            addPath(path)
            lineTo(points.last().x, height)
            lineTo(points.first().x, height)
            close()
        }
        
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(EmeraldMedium.copy(alpha = 0.2f), Color.Transparent)
            )
        )
    }
}

@Composable
fun UserImpactDashboard(stats: com.example.recircuitai.data.UserStats) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChartCard(
                title = "Contribution Trend",
                subtitle = "Last 7 days",
                modifier = Modifier.weight(1f)
            ) {
                LineSparkline(data = stats.weeklyActivity)
            }
            ChartCard(
                title = "Material Mix",
                subtitle = "Weight distribution",
                modifier = Modifier.weight(1f)
            ) {
                SimpleDonutChart(data = stats.materialMix)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ChartCard(
                title = "Weekly Volume",
                subtitle = "kg recycled",
                modifier = Modifier.weight(1f)
            ) {
                MiniBarChart(data = stats.weeklyActivity)
            }
            ChartCard(
                title = "Monthly Goal",
                subtitle = "${(stats.goalProgress * 100).toInt()}% achieved",
                modifier = Modifier.weight(1f)
            ) {
                CircularGoalGraph(progress = stats.goalProgress)
            }
        }
    }
}

@Composable
fun SimpleDonutChart(data: Map<String, Float>, modifier: Modifier = Modifier) {
    val colors = listOf(EmeraldMedium, PrimaryGreen, SecondaryGreen, Color(0xFF81C784))
    
    Box(modifier = modifier.size(80.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            data.values.forEachIndexed { index, value ->
                val sweepAngle = value * 360f
                drawArc(
                    color = colors[index % colors.size],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                )
                startAngle += sweepAngle
            }
        }
        Text(
            text = "${(data.values.first() * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )
    }
}

@Composable
fun MiniBarChart(data: List<Float>, modifier: Modifier = Modifier) {
    val maxValue = data.maxOrNull() ?: 1f
    
    Row(
        modifier = modifier.height(60.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { value ->
            val barHeight = (value / maxValue) * 50
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(barHeight.dp)
                    .background(EmeraldMedium, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
            )
        }
    }
}

@Composable
fun CircularGoalGraph(progress: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(80.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Track
            drawArc(
                color = EmeraldMedium.copy(alpha = 0.1f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx())
            )
            // Progress
            drawArc(
                color = EmeraldMedium,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
            )
        }
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen
        )
    }
}

@Composable
fun ChartCard(title: String, subtitle: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth().height(160.dp),
        color = Color.White,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextBlack)
            Text(subtitle, style = MaterialTheme.typography.labelSmall, color = TextGray)
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                content()
            }
        }
    }
}
