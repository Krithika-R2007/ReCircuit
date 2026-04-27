package com.example.recircuitai.data.network

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Global configuration for networking.
 * Since testing on a physical phone, BASE_URL should be set to your computer's local IP.
 */
object NetworkConfig {
    // CHANGE THIS TO YOUR COMPUTER'S IP (e.g., "192.168.1.5")
    var ipAddress by mutableStateOf("192.168.1.37")
    
    val baseUrl: String
        get() {
            val formattedIp = if (ipAddress.contains(":")) "[$ipAddress]" else ipAddress
            return "http://$formattedIp:3000/"
        }
}

