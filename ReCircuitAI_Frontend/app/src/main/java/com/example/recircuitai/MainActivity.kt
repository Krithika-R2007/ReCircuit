package com.example.recircuitai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import coil.compose.AsyncImage
import com.example.recircuitai.data.MockData
import com.example.recircuitai.data.RecycleItem
import com.example.recircuitai.data.GlobalAppState
import com.example.recircuitai.ui.screens.*
import com.example.recircuitai.ui.components.NavBarItem
import com.example.recircuitai.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ReCircuitAITheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "splash"
    // Read reactively so FAB & navbar recompose immediately when mode is toggled
    val isCompanyMode = GlobalAppState.isCompanyMode


    val scaffoldRoutes = listOf("home", "needs", "map", "profile", "impact")
    val showScaffold = currentRoute in scaffoldRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundGreen,
        bottomBar = {
            if (showScaffold) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onHomeClick = { navController.navigate("home") },
                    onMapClick = { navController.navigate("map") },
                    onCenterClick = { 
                        navController.navigate(if (isCompanyMode) "needs" else "upload")
                    },
                    onRightActionClick = { 
                        navController.navigate(if (isCompanyMode) "upload" else "impact")
                    },
                    onProfileClick = { navController.navigate("profile") },
                    isCompanyMode = isCompanyMode
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            NavHost(
                navController = navController,
                startDestination = "splash",
                enterTransition = { fadeIn(animationSpec = tween(400)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) },
                exitTransition = { fadeOut(animationSpec = tween(400)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400)) },
                popEnterTransition = { fadeIn(animationSpec = tween(400)) + slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) },
                popExitTransition = { fadeOut(animationSpec = tween(400)) + slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400)) }
            ) {
                composable("splash") {
                    SplashScreen(onAnimationFinished = {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    })
                }
                
                composable("home") {
                    HomeScreen(
                        onItemClick = { item ->
                            GlobalAppState.selectedItem = item
                            navController.navigate("details/${item.id}")
                        },
                        onUploadClick = { navController.navigate("upload") },
                        onMapClick = { navController.navigate("map") },
                        onProfileClick = { navController.navigate("profile") },
                        onSearchClick = { navController.navigate("search") }
                    )
                }
                
                composable("search") {
                    SearchScreen(
                        onBackClick = { navController.popBackStack() },
                        onItemClick = { id -> navController.navigate("details/$id") }
                    )
                }
                
                composable("details/{itemId}") { backStackEntry ->
                    val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
                    DetailsScreen(
                        itemId = itemId,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                
                composable("upload") {
                    UploadScreen(
                        onBackClick = { navController.popBackStack() },
                        onUploadSuccess = {
                            navController.navigate("home") {
                                popUpTo("upload") { inclusive = true }
                            }
                        }
                    )
                }

                composable("needs") {
                    NeedsScreen(
                        onItemClick = { item ->
                            GlobalAppState.selectedItem = item
                            navController.navigate("details/${item.id}")
                        },
                        onMapClick = { navController.navigate("map") },
                        onProfileClick = { navController.navigate("profile") }
                    )
                }
                
                composable("map") {
                    MapScreen(
                        onHomeClick = { navController.navigate("home") },
                        onUploadClick = { navController.navigate("upload") },
                        onProfileClick = { navController.navigate("profile") }
                    )
                }
                composable("profile") {
                    ProfileScreen(
                        onHomeClick = { navController.navigate("home") },
                        onMapClick = { navController.navigate("map") },
                        onUploadClick = { navController.navigate("upload") },
                        onItemClick = { item ->
                            GlobalAppState.selectedItem = item
                            navController.navigate("details/${item.id}")
                        }
                    )
                }

                composable("impact") {
                    ImpactScreen()
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onMapClick: () -> Unit,
    onCenterClick: () -> Unit,
    onRightActionClick: () -> Unit,
    onProfileClick: () -> Unit,
    isCompanyMode: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 45.dp), // System clearance
        contentAlignment = Alignment.BottomCenter
    ) {
        // Main Bar Surface
        Surface(
            color = TextBlack,
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NavBarItem(selected = currentRoute == "home", onClick = onHomeClick, icon = Icons.Default.Home, label = "Home")
                NavBarItem(selected = currentRoute == "map", onClick = onMapClick, icon = Icons.Default.Map, label = "Map")
                
                // Gap for the floating button
                Spacer(modifier = Modifier.width(72.dp))

                NavBarItem(
                    selected = currentRoute == (if (isCompanyMode) "upload" else "impact"),
                    onClick = onRightActionClick,
                    icon = if (isCompanyMode) Icons.Default.CloudUpload else Icons.Default.BarChart,
                    label = if (isCompanyMode) "Upload" else "Impact"
                )

                NavBarItem(selected = currentRoute == "profile", onClick = onProfileClick, icon = Icons.Default.Person, label = "Profile")
            }
        }

        // Floating Center Button (The Arc)
        Surface(
            modifier = Modifier
                .size(68.dp)
                .offset(y = (-30).dp), // Half-in, Half-out logic
            color = EmeraldMedium,
            shape = CircleShape,
            shadowElevation = 12.dp,
            onClick = onCenterClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isCompanyMode) Icons.Default.AutoAwesome else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.White
                )
            }
        }
    }
}