package com.example.recircuitai.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.example.recircuitai.data.MockData
import com.example.recircuitai.data.network.NetworkClient
import com.example.recircuitai.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    onBackClick: () -> Unit,
    onUploadSuccess: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    
    var quantity by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf(MockData.userStats.phoneNumber) }
    var isPhoneEditable by remember { mutableStateOf(false) }
    var showConditionMenu by remember { mutableStateOf(false) }
    var condition by remember { mutableStateOf("Reusable") }

    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var cameraTmpUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }
    
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            selectedImageUri = cameraTmpUri
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val uri = createCameraUri(context)
            cameraTmpUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Camera permission is required to snap photos.", Toast.LENGTH_SHORT).show()
        }
    }

    var isAnalyzing by remember { mutableStateOf(false) }
    var lowConfidenceWarning by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedImageUri) {
        if (selectedImageUri != null && title.isEmpty()) {
            isAnalyzing = true
            lowConfidenceWarning = null
            try {
                val inputStream = context.contentResolver.openInputStream(selectedImageUri!!)
                val file = File(context.cacheDir, "analyze_tmp.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                
                val response = NetworkClient.getApiService().analyzeImage(body)
                if (response.isSuccessful && response.body() != null) {
                    val aiData = response.body()!!.data
                    
                    if (aiData.confidence < 0.3) {
                        lowConfidenceWarning = "Picture is a bit blurry or unclear. AI isn't sure! Please try retaking for better results."
                        title = "Unclear Item"
                        description = "Please retake the photo for better identification."
                    } else {
                        title = aiData.identifiedItem
                        description = "Material: ${aiData.material}\nSuggested Industry: ${aiData.industry}\n" +
                                     "Note: ${if (aiData.possible_products.isNotEmpty()) "Can be made into ${aiData.possible_products.first()}" else "Great for recycling"}"
                        condition = if (aiData.confidence > 0.8) "Reusable" else "Scrap"
                    }
                }
            } catch (e: Exception) {
                scope.launch {
                    Toast.makeText(context, "AI Service unreachable. Check server connection.", Toast.LENGTH_LONG).show()
                }
                e.printStackTrace()
            } finally {
                isAnalyzing = false
            }
        }
    }

    fun handleCameraClick() {
        val permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val uri = createCameraUri(context)
            cameraTmpUri = uri
            cameraLauncher.launch(uri)
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    fun uploadItem() {
        val uri = selectedImageUri ?: return
        isUploading = true
        uploadError = null

        scope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "upload_image.jpg")
                val outputStream = FileOutputStream(file)
                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                val quantityBody = quantity.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneBody = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                val locationBody = location.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = NetworkClient.getApiService().uploadItem(
                    body, titleBody, descBody, quantityBody, phoneBody, locationBody
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val newItem = response.body()!!.data
                    // Add to local mock list for immediate feedback in high-fidelity demo
                    MockData.curatedUploads.add(0, newItem)
                    onUploadSuccess()
                } else {
                    uploadError = "Upload failed: ${response.message()}"
                    isUploading = false
                }
            } catch (e: Exception) {
                uploadError = "Error: ${e.localizedMessage}"
                isUploading = false
            }
        }
    }

    if (isUploading) {
        AILoadingScreen(status = if (uploadError != null) "Error: $uploadError" else "Analyzing with AI...") {
            isUploading = false
            onBackClick()
        }
    } else {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text("Upload an Item", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = PrimaryGreen))
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = PrimaryGreen)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = BackgroundGreen)
                )
            },
            containerColor = BackgroundGreen
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .shadow(4.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                        Surface(
                            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape,
                            onClick = { galleryLauncher.launch("image/*") }
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.padding(8.dp))
                        }
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { galleryLauncher.launch("image/*") }.padding(12.dp)
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(48.dp), tint = EmeraldMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Gallery", style = MaterialTheme.typography.labelSmall, color = EmeraldMedium)
                                }
                                
                                Spacer(modifier = Modifier.width(40.dp))
                                
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.clickable { handleCameraClick() }.padding(12.dp)
                                ) {
                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(48.dp), tint = EmeraldMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Camera", style = MaterialTheme.typography.labelSmall, color = EmeraldMedium)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(20.dp))
                            
                            Text(
                                "Snap or Select Photo",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextBlack,
                                fontWeight = FontWeight.Bold
                            )
                            if (lowConfidenceWarning != null) {
                                Card(
                                    modifier = Modifier.padding(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
                                ) {
                                    Text(
                                        lowConfidenceWarning!!,
                                        modifier = Modifier.padding(8.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFF57F17),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                "AI will scan your material details",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Form Fields (Linearized)
                UploadSectionTitle("About Item")
                
                if (isAnalyzing) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = EmeraldMedium)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI is scanning details...", style = MaterialTheme.typography.labelSmall, color = EmeraldMedium)
                    }
                }

                UploadTextField(value = title, onValueChange = { title = it }, label = "Item Name", icon = Icons.Default.Inventory2)
                Spacer(modifier = Modifier.height(16.dp))
                UploadTextField(value = description, onValueChange = { description = it }, label = "Material Details", icon = Icons.Default.Description, singleLine = false, minLines = 3)
                
                Spacer(modifier = Modifier.height(28.dp))
                
                UploadSectionTitle("Specifications")
                UploadTextField(value = quantity, onValueChange = { quantity = it }, label = "Quantity/Weight", icon = Icons.Default.Scale)
                
                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    ExposedDropdownMenuBox(
                        expanded = showConditionMenu,
                        onExpandedChange = { showConditionMenu = !showConditionMenu }
                    ) {
                        OutlinedTextField(
                            value = condition,
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            label = { Text("Quality") },
                            leadingIcon = { Icon(Icons.Default.StarHalf, contentDescription = null, tint = PrimaryGreen) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showConditionMenu) },
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = PrimaryGreen.copy(alpha = 0.3f),
                                focusedBorderColor = PrimaryGreen,
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = showConditionMenu,
                            onDismissRequest = { showConditionMenu = false }
                        ) {
                            listOf("Unused", "Reusable", "Refurbished", "Scrap").forEach { selection ->
                                DropdownMenuItem(
                                    text = { Text(selection) },
                                    onClick = {
                                        condition = selection
                                        showConditionMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                UploadSectionTitle("Contact & Location")
                UploadTextField(value = location, onValueChange = { location = it }, label = "Pickup Point", icon = Icons.Default.Map)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { if (isPhoneEditable) phoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isPhoneEditable,
                    label = { Text("Contact Number") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = PrimaryGreen) },
                    trailingIcon = {
                        IconButton(onClick = { isPhoneEditable = !isPhoneEditable }) {
                            Icon(
                                imageVector = if (isPhoneEditable) Icons.Default.Check else Icons.Default.Edit,
                                contentDescription = "Edit Phone",
                                tint = EmeraldMedium
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = PrimaryGreen.copy(alpha = 0.3f),
                        focusedBorderColor = PrimaryGreen,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = { uploadItem() },
                    enabled = selectedImageUri != null && title.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldMedium,
                        disabledContainerColor = EmeraldMedium.copy(alpha = 0.3f)
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("List Material with AI", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
                
                if (uploadError != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(uploadError!!, color = Color.Red, style = MaterialTheme.typography.labelSmall)
                }
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

fun createCameraUri(context: android.content.Context): android.net.Uri {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val storageDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
    val file = File.createTempFile("RECIRC_PNG_${timeStamp}_", ".png", storageDir)
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

@Composable
fun UploadSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = EmeraldMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp, top = 4.dp)
    )
}

@Composable
fun UploadTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(24.dp)) },
        shape = RoundedCornerShape(16.dp),
        singleLine = singleLine,
        minLines = minLines,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = PrimaryGreen.copy(alpha = 0.3f),
            focusedBorderColor = PrimaryGreen,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}

@Composable
fun AILoadingScreen(status: String, onFinished: () -> Unit) {
    var statusText by remember { mutableStateOf(status) }
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanPosition = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanPosition"
    )

    LaunchedEffect(status) {
        statusText = status
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .border(2.dp, PrimaryGreen, RoundedCornerShape(24.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawWithContent {
                            val scanY = size.height * scanPosition.value
                            drawContent()
                            drawLine(
                                color = SecondaryGreen,
                                start = Offset(0f, scanY),
                                end = Offset(size.width, scanY),
                                strokeWidth = 4f
                            )
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, SecondaryGreen.copy(alpha = 0.3f)),
                                    startY = scanY - 40f,
                                    endY = scanY
                                ),
                                topLeft = Offset(0f, scanY - 40f),
                                size = size.copy(height = 40f)
                            )
                        }
                ) {
                    Icon(
                        imageVector = Icons.Default.Camera,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(64.dp).alpha(0.2f),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = statusText,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            CircularProgressIndicator(color = SecondaryGreen)
        }
    }
}
