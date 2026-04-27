package com.example.recircuitai.data

import androidx.compose.runtime.mutableStateListOf

object MockData {
    val curatedUploads = mutableStateListOf(
        RecycleItem("cables", "Copper Cables", "Industrial grade copper wiring from a server room upgrade.", "android.resource://com.example.recircuitai/drawable/d2", "Tech Hub OMR", "1 km", "Metal", owner = "Faaz_cafetech", aiData = AIData("Good", "₹450/kg", listOf("Metal", "Copper"), "Copper 98%")),
        RecycleItem("glass", "Glass Bottles", "Bulk collection of clear and green glass beverage bottles.", "android.resource://com.example.recircuitai/drawable/glass", "Adyar", "3 km", "Glass", owner = "HaveDrinks_restaurant", aiData = AIData("Clean", "₹12/kg", listOf("Glass", "Bottle"), "Glass 100%")),
        RecycleItem("wooden_chair", "Wooden Chair", "Solid wood chair with minor aesthetic wear.", "android.resource://com.example.recircuitai/drawable/wooden_chair", "Anna Nagar", "4 km", "Wood", owner = "Kumar_home", aiData = AIData("Used", "₹800", listOf("Wood", "Furniture"), "Wood 95%")),
        RecycleItem("plastic", "Plastic Storage", "Large plastic bins and storage containers, durable material.", "android.resource://com.example.recircuitai/drawable/plastic", "T. Nagar", "2 km", "Plastic", owner = "Usha_house", aiData = AIData("Good", "₹300", listOf("Plastic", "Storage"), "Plastic 100%")),
        RecycleItem("aluminum", "Aluminum Scraps", "High-quality aluminum window frames and siding leftovers.", "android.resource://com.example.recircuitai/drawable/d4", "Construction Site OMR", "5 km", "Metal", owner = "Renoworks_construction", aiData = AIData("Industrial", "₹120/kg", listOf("Metal", "Aluminum"), "Aluminum 95%")),
        RecycleItem("cpu", "Old CPU Case", "Vintage computer tower case, internal boards included.", "android.resource://com.example.recircuitai/drawable/d2", "Besant Nagar", "6 km", "Electronic", owner = "user_Shiv", aiData = AIData("Vintage", "₹600", listOf("Electronic", "Metal"), "Metal 70%, Plastic 30%")),
        RecycleItem("coconut", "Dry Coconuts", "Husks and shells from a large function, perfect for compost.", "android.resource://com.example.recircuitai/drawable/coconut", "Mylapore", "1 km", "Organic", owner = "cocunutshop", aiData = AIData("Organic", "₹2/unit", listOf("Organic", "Coconut"), "Fiber 100%")),
        RecycleItem("bag", "Plastic Carry Bags", "Bundled poly bags, sorted and cleaned for reprocessing.", "android.resource://com.example.recircuitai/drawable/d5", "Saidapet", "2 km", "Plastic", owner = "Priya_house", aiData = AIData("Clean", "₹15/kg", listOf("Plastic", "LDPE"), "Plastic 100%")),
        RecycleItem("iron", "Iron Rods", "Rusted iron rods from foundation work, structural grade.", "android.resource://com.example.recircuitai/drawable/d4", "Kotturpuram", "4 km", "Metal", owner = "buildsite_AZM", aiData = AIData("Heavy", "₹45/kg", listOf("Metal", "Iron"), "Iron 100%")),
        RecycleItem("window", "Window Glass Scraps", "Plate glass fragments from a window replacement project.", "android.resource://com.example.recircuitai/drawable/glass", "Shanti Colony", "3 km", "Glass", owner = "Shrely_house", aiData = AIData("Fragmented", "₹8/kg", listOf("Glass", "Plate"), "Glass 100%"))
    )

    val userStats = UserStats(
        name = "Amara",
        email = "amara.g@gmail.com",
        profileImage = "android.resource://com.example.recircuitai/drawable/profile",
        phoneNumber = "+91 98765 43210",
        location = "Velachery, Chennai",
        industryType = "Eco-Volunteer",
        registrationId = "U-2024-8891",
        sustainabilityRating = 4.9,
        requiredTags = emptyList(),
        totalUploads = 24,
        matchedItems = null,
        wasteSavedKg = 156.4,
        weeklyActivity = listOf(15f, 25f, 10f, 40f, 32f, 28f, 35f),
        materialMix = mapOf("Wood" to 0.5f, "Metal" to 0.2f, "Plastic" to 0.15f, "Glass" to 0.15f),
        goalProgress = 0.82f
    )

    val companyStats = UserStats(
        name = "EcoDynamics Solutions",
        email = "procurement@ecodynamics.com",
        profileImage = "android.resource://com.example.recircuitai/drawable/company",
        phoneNumber = "+91 44 2233 4455",
        location = "OMR IT Park, Chennai",
        industryType = "Nature-Based Procurement",
        registrationId = "CO-TN-4422-B",
        sustainabilityRating = 4.8,
        activeRequests = 12,
        requiredTags = listOf("Wood", "Organic", "Coconut", "Paper", "Banana Leaves"),
        totalUploads = 0,
        matchedItems = 45,
        wasteSavedKg = 1240.0,
        weeklyActivity = listOf(120f, 150f, 80f, 200f, 180f, 220f, 210f),
        materialMix = mapOf("Organic" to 0.7f, "Wood" to 0.3f),
        goalProgress = 0.94f
    )

    // Clean curated needs for companies - only relevant high-fidelity items
    fun getCuratedNeeds(): List<RecycleItem> {
        return listOf(
            RecycleItem("c1", "Banana Leaves", "Fresh large banana leaves — perfect for eco-friendly packaging and cooking.", "android.resource://com.example.recircuitai/drawable/c1", "Tambaram Market", "5 km", "Organic", owner = "Krishnan Farms", aiData = AIData("Fresh", "₹40/bunch", listOf("Organic", "Banana", "Biodegradable"), "100% Natural Leaf")),
            RecycleItem("c2", "Old Wooden Door", "Solid reclaimed teak wood door — ideal for upcycling and interior design.", "android.resource://com.example.recircuitai/drawable/c2", "Mylapore Depot", "4 km", "Wood", owner = "Rajan Home Store", aiData = AIData("Moderate", "₹1200/unit", listOf("Wood", "Teak", "Reclaimed"), "Teak 90%")),
            RecycleItem("c3", "Ceramic Dishes", "Set of organic ceramic dishes from a restaurant closure.", "android.resource://com.example.recircuitai/drawable/c3", "Hotel Hub OMR", "6 km", "Organic", owner = "Hotel Surplus Store", aiData = AIData("Good", "₹300/set", listOf("Ceramic", "Organic", "Kitchen"), "Clay 100%")),
            RecycleItem("coconut", "Fresh Coconuts", "Dry coconut shells and husks for organic fibre processing.", "android.resource://com.example.recircuitai/drawable/coconut", "EcoFarm Nungambakkam", "2 km", "Organic", owner = "EcoFarm", aiData = AIData("Good", "₹180/kg", listOf("Organic", "Coconut"), "Coconut 100%")),
            RecycleItem("wooden_chair", "Teak Wood Chair", "Sturdy teak wood chair base, perfect for restoration.", "android.resource://com.example.recircuitai/drawable/wooden_chair", "Velachery", "3 km", "Wood", owner = "Kumar_home", aiData = AIData("Good", "₹900/unit", listOf("Wood", "Teak"), "Teak 95%"))
        )
    }

    // d1=Wooden Table, d2=Broken Laptop, d3=Cotton Clothes, d4=Old Steel Bed, d5=Bike Cover Plastic
    fun getCuratedUploads(): List<RecycleItem> = curatedUploads


    fun getCompanyRecentShipments(): List<RecycleItem> {
        return listOf(
            RecycleItem("s1", "Coconut Shell Batch", "50 kg of dried coconut shells and husks for organic processing.", "android.resource://com.example.recircuitai/drawable/coconut", "Warehouse A, Guindy", "4 km", "Organic", aiData = AIData("Dry", "₹180/kg", listOf("Organic", "Coconut"), "Husk Fibre 100%")),
            RecycleItem("s2", "Banana Leaves Bundle", "80 bunches of fresh banana leaves for eco packaging.", "android.resource://com.example.recircuitai/drawable/c1", "Market Depot, Tambaram", "7 km", "Organic", aiData = AIData("Fresh", "₹40/bunch", listOf("Organic", "Banana"), "Leaf 100%")),
            RecycleItem("s3", "Cotton Fabric Scraps", "30 kg untreated cotton from textile surplus.", "android.resource://com.example.recircuitai/drawable/d3", "Textile Hub, Tirupur Road", "3 km", "Organic", aiData = AIData("Good", "₹200/kg", listOf("Cotton", "Textile"), "Cotton 100%"))
        )
    }
    fun getCompanyProfileListings(): List<RecycleItem> {
        return listOf(
            RecycleItem(
                "n1", "Fresh Coconuts",
                "Dry coconut shells and husks, ideal for organic fibre processing.",
                "android.resource://com.example.recircuitai/drawable/coconut",
                "EcoFarm Nungambakkam", "2 km", "Organic",
                owner = "EcoFarm",
                quantity = "50 kg batch",
                aiData = AIData("Good", "₹180/kg", listOf("Organic", "Coconut", "Natural Fibre"), "Coconut Husk 80%, Shell 20%")
            ),
            RecycleItem(
                "n2", "Banana Leaves",
                "Fresh large banana leaves — perfect for eco-friendly packaging.",
                "android.resource://com.example.recircuitai/drawable/c1",
                "Tambaram Market", "5 km", "Organic",
                owner = "Krishnan Farms",
                ownerImage = "android.resource://com.example.recircuitai/drawable/user_profile",
                quantity = "50 bundles",
                aiData = AIData("Fresh", "₹40/bunch", listOf("Organic", "Banana", "Biodegradable"), "100% Natural Leaf")
            ),
            RecycleItem(
                "n3", "Old Wooden Door",
                "Solid teak wood door from a heritage home renovation.",
                "android.resource://com.example.recircuitai/drawable/c2",
                "Mylapore Depot", "4 km", "Wood",
                owner = "Rajan Home Store",
                ownerImage = "android.resource://com.example.recircuitai/drawable/company",
                quantity = "1 unit",
                aiData = AIData("Moderate", "₹1200/unit", listOf("Wood", "Teak"), "Teak 90%")
            ),
            RecycleItem(
                "n4", "Ceramic Dishes",
                "Large set of organic ceramic plates and dishes.",
                "android.resource://com.example.recircuitai/drawable/c3",
                "Hotel Hub OMR", "6 km", "Organic",
                owner = "Hotel Surplus Store",
                ownerImage = "android.resource://com.example.recircuitai/drawable/company",
                quantity = "25 units",
                aiData = AIData("Good", "₹300/set", listOf("Ceramic", "Organic"), "Clay 100%")
            )
        )
    }

    fun getUserProfileListings(): List<RecycleItem> {
        return listOf(
            RecycleItem(
                "u1", "Wooden Table",
                "Solid 4-legged dining table, minor scratches.",
                "android.resource://com.example.recircuitai/drawable/d1",
                "My Home", "0 km", "Wood",
                quantity = "1 unit",
                aiData = AIData("Good", "₹1500", listOf("Wood", "Furniture"), "Wood 100%")
            ),
            RecycleItem(
                "u3", "Cotton Clothes",
                "Mixed cotton garments, washed and sorted.",
                "android.resource://com.example.recircuitai/drawable/d3",
                "My Home", "0 km", "Organic",
                quantity = "5 kg",
                aiData = AIData("Good", "₹200/kg", listOf("Cotton", "Organic"), "Cotton 100%")
            ),
            RecycleItem(
                "u2", "Broken Laptop",
                "Screen cracked, internals functional.",
                "android.resource://com.example.recircuitai/drawable/d2",
                "My Home", "0 km", "Electronic",
                quantity = "1 unit",
                aiData = AIData("Damaged", "₹500", listOf("Electronic", "Metal"), "Metals 40%")
            ),
            RecycleItem(
                "u4", "Old Steel Bed",
                "Sturdy metal frame, dismantling required.",
                "android.resource://com.example.recircuitai/drawable/d4",
                "My Home", "0 km", "Metal",
                quantity = "1 unit",
                aiData = AIData("Used", "₹800", listOf("Metal", "Steel"), "Steel 100%")
            ),
            RecycleItem(
                "u5", "Plastic Covers",
                "Bulk lot of high-density plastic bike covers.",
                "android.resource://com.example.recircuitai/drawable/d5",
                "My Home", "0 km", "Plastic",
                quantity = "10 units",
                aiData = AIData("Reusable", "₹50", listOf("Plastic", "HDPE"), "Plastic 100%")
            )
        )
    }
}
