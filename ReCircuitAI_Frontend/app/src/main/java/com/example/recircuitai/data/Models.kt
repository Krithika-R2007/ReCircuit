package com.example.recircuitai.data

data class RecycleItem(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val location: String = "Unknown",
    val distance: String = "0.0 km",
    val category: String = "All",
    val timestamp: String = "",
    val owner: String? = null,
    val ownerImage: String? = null,
    val quantity: String? = null,
    val industry: String? = null,
    val aiData: AIData? = null
)


data class AIData(
    val condition: String,
    val estimatedValue: String,
    val tags: List<String>,
    val materialContent: String,
    val confidence: Double = 1.0,
    val industry: String? = null,
    val possibleProducts: List<String>? = null
)

data class UserStats(
    val name: String,
    val email: String,
    val profileImage: String,
    val phoneNumber: String = "+1 234 567 890",
    val location: String = "Chennai, India",
    val industryType: String = "Individual",
    val registrationId: String = "N/A",
    val sustainabilityRating: Double = 5.0,
    val activeRequests: Int = 0,
    val requiredTags: List<String> = emptyList(),
    val totalUploads: Int,
    val matchedItems: Int?,
    val wasteSavedKg: Double,
    val weeklyActivity: List<Float> = listOf(2f, 5f, 3f, 8f, 4f, 6f, 7f),
    val materialMix: Map<String, Float> = mapOf("Glass" to 0.4f, "Metal" to 0.3f, "Plastic" to 0.2f, "Other" to 0.1f),
    val goalProgress: Float = 0.75f
)
