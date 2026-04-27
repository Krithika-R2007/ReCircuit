const express = require('express');
const cors = require('cors');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

const app = express();
const PORT = 3000;

// Enable CORS and JSON parsing
app.use(cors());
app.use(express.json());

// Create uploads directory if it doesn't exist
const uploadDir = path.join(__dirname, 'uploads');
if (!fs.existsSync(uploadDir)) {
    fs.mkdirSync(uploadDir);
}

// Serve the uploads folder statically so images can be accessed directly
app.use('/uploads', express.static(uploadDir));

// Configure Multer for image uploads
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, uniqueSuffix + path.extname(file.originalname));
    }
});
const upload = multer({ storage: storage });

// In-memory data store (resets when server restarts)
// In-memory data store with Seed Data from listing.json
let items = [
    {
        id: "1",
        title: "Glass Bottles (1L)",
        description: "Set of 15 one-liter glass bottles collected from home use. Cleaned and suitable for reuse or recycling.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/glass",
        category: "Glass",
        location: "Velachery",
        distance: "1.2 km",
        owner: "HaveDrinks_restaurant",
        quantity: "15 bottles",
        timestamp: new Date().toISOString(),
        aiData: { condition: "Gently Used", estimatedValue: "₹120", tags: ["glass", "reuse", "eco"], materialContent: "Glass (100%)" }
    },
    {
        id: "2",
        title: "Wooden Chair (Damaged)",
        description: "Two wooden chairs with broken joints. Can be repaired or dismantled for wood reuse.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/wooden_chair",
        category: "Wood",
        location: "Tambaram",
        distance: "4.5 km",
        owner: "Kumar_home",
        quantity: "2 chairs",
        timestamp: new Date().toISOString(),
        aiData: { condition: "For Parts", estimatedValue: "₹150", tags: ["wood", "furniture", "scrap"], materialContent: "Wood (90%), Metal joints" }
    },
    {
        id: "3",
        title: "Plastic Storage Containers",
        description: "56 assorted plastic containers from kitchen storage. Some have minor cracks but usable for recycling.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/plastic",
        category: "Plastic",
        location: "Adyar",
        distance: "2.1 km",
        owner: "Usha_house",
        quantity: "56 containers",
        timestamp: new Date().toISOString(),
        aiData: { condition: "Well Used", estimatedValue: "₹200", tags: ["plastic", "kitchen", "recyclable"], materialContent: "Plastic (mixed)" }
    },
    {
        id: "4",
        title: "Aluminum Scrap Pieces",
        description: "Approximately 2.5 kg of aluminum scrap collected from minor renovation work.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/aluminum",
        category: "Metal",
        location: "Guindy",
        distance: "3.2 km",
        owner: "Renoworks_construction",
        quantity: "2.5 kg",
        timestamp: new Date().toISOString(),
        aiData: { condition: "Well Used", estimatedValue: "₹300", tags: ["metal", "aluminum", "scrap"], materialContent: "Aluminum (95%)" }
    },
    {
        id: "5",
        title: "Old Desktop CPU",
        description: "Single non-working CPU unit from early 2010s. Useful for parts or recycling.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/old_cpu",
        category: "Electronics",
        location: "T Nagar",
        distance: "5.0 km",
        owner: "user_Shiv",
        quantity: "1 unit",
        timestamp: new Date().toISOString(),
        aiData: { condition: "For Parts", estimatedValue: "₹250", tags: ["electronics", "cpu", "recycle"], materialContent: "Mixed Metals & Plastic" }
    },
    {
        id: "6",
        title: "Coconut Shell Waste",
        description: "45 coconut shells collected from a small coconut water shop. Ideal for composting or bio-use.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/coconut",
        category: "Organic",
        location: "Mylapore",
        distance: "0.8 km",
        owner: "coconutshop",
        quantity: "45 shells",
        timestamp: new Date().toISOString(),
        aiData: { condition: "New", estimatedValue: "Free", tags: ["organic", "compost", "eco"], materialContent: "Organic Fiber" }
    },
    {
        id: "7",
        title: "Plastic Carry Bags",
        description: "42 plastic bags collected from household and online deliveries. Suitable for recycling.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/plastic_bags",
        category: "Plastic",
        location: "OMR",
        distance: "6.7 km",
        owner: "Priya_house",
        quantity: "42 bags",
        timestamp: new Date().toISOString(),
        aiData: { condition: "Well Used", estimatedValue: "₹50", tags: ["plastic", "packaging", "waste"], materialContent: "LDPE Plastic" }
    },
    {
        id: "8",
        title: "Iron Rods",
        description: "30 rusted iron rods from construction site leftovers. Fully recyclable.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/iron_rod",
        category: "Metal",
        location: "Perungudi",
        distance: "4.1 km",
        owner: "buildsite_AZM",
        quantity: "30 rods",
        timestamp: new Date().toISOString(),
        aiData: { condition: "Well Used", estimatedValue: "₹500", tags: ["metal", "iron", "construction"], materialContent: "Wrought Iron" }
    },
    {
        id: "9",
        title: "Window Glass Panels",
        description: "12 glass panels removed from two house windows during renovation. No major cracks.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/window_glass",
        category: "Glass",
        location: "Anna Nagar",
        distance: "8.2 km",
        owner: "Shrely_house",
        quantity: "12 panels",
        timestamp: new Date().toISOString(),
        aiData: { condition: "Gently Used", estimatedValue: "₹400", tags: ["glass", "window", "reuse"], materialContent: "Sheet Glass" }
    },
    {
        id: "10",
        title: "Charging Cables Bundle",
        description: "26 mixed charging cables collected from a small computer service center. Some may still work.",
        imageUrl: "android.resource://com.example.recircuitai/drawable/cables",
        category: "Electronics",
        location: "Sholinganallur",
        distance: "9.5 km",
        owner: "Faaz_cafetech",
        quantity: "26 cables",
        timestamp: new Date().toISOString(),
        aiData: { condition: "For Parts", estimatedValue: "₹180", tags: ["electronics", "cables", "scrap"], materialContent: "Copper & PVC" }
    }
];

// ==========================================
// API ENDPOINTS
// ==========================================

// Root endpoint just to show the server is alive
app.get('/', (req, res) => {
    console.log('GET / request received');
    res.send('<h1>🚀 ReCircuit Backend Server</h1><p>Available endpoints: <ul><li><a href="/get-items">/get-items</a></li><li>/upload-item</li></ul></p>');
});

// Middleware to log all incoming requests
app.use((req, res, next) => {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);
    next();
});

// 1. GET /get-items
// Fetches all uploaded items
app.get('/get-items', (req, res) => {
    res.json({
        success: true,
        count: items.length,
        data: items
    });
});

const axios = require('axios');
const FormData = require('form-data');

// AI Service Configuration
const AI_SERVICE_URL = process.env.AI_SERVICE_URL || 'http://127.0.0.1:8000';

// 2. POST /analyze-image
// Analyzes an image using WitchHunt AI without saving it
app.post('/analyze-image', upload.single('image'), async (req, res) => {
    try {
        const file = req.file;
        if (!file) {
            return res.status(400).json({ success: false, message: 'Image file is required.' });
        }

        let aiResult = {
            object: 'Unknown Item',
            material: 'Unknown',
            confidence: 0,
            tags: [],
            industry: 'General Waste Facility',
            possible_products: []
        };

        const formData = new FormData();
        formData.append('image', fs.createReadStream(file.path));

        const response = await axios.post(`${AI_SERVICE_URL}/classify-material`, formData, {
            headers: {
                ...formData.getHeaders(),
            },
            timeout: 120000 
        });

        if (response.data) {
            aiResult = response.data;
        }

        res.json({
            success: true,
            data: aiResult
        });
    } catch (error) {
        console.error('Error analyzing image:', error.message);
        res.status(500).json({ success: false, message: 'AI Analysis failed.' });
    }
});

// 3. POST /upload-item
// Uploads a new item (image + text) and analyzes it using the WitchHunt AI service
app.post('/upload-item', upload.single('image'), async (req, res) => {
    try {
        const { title, description, quantity, phone, location } = req.body;
        const file = req.file;
        console.log(`INFO: Received upload-item request: ${title || 'Untitled'}`);

        if (!file) {
            return res.status(400).json({ success: false, message: 'Image file is required.' });
        }

        let aiResult = {
            object: 'Unknown Item',
            material: 'Unknown',
            confidence: 0,
            tags: [],
            industry: 'General Waste Facility',
            possible_products: []
        };

        // Call the WitchHunt AI service
        try {
            const formData = new FormData();
            formData.append('image', fs.createReadStream(file.path));

            const response = await axios.post(`${AI_SERVICE_URL}/classify-material`, formData, {
                headers: {
                    ...formData.getHeaders(),
                },
                timeout: 120000 // 120 seconds timeout for AI processing
            });

            if (response.data) {
                aiResult = response.data;
                console.log('AI Analysis successful:', aiResult.object);
            }
        } catch (aiError) {
            console.error('AI Service Error:', aiError.message);
            // Fallback: We still create the item but with friendly info
            aiResult.object = 'Item Awaiting Analysis';
            aiResult.material = 'Recyclable';
            aiResult.confidence = 0.1;
            aiResult.tags = ['Scanning...', 'Pending'];
        }

        const newItem = {
            id: Date.now().toString(),
            title: title || aiResult.object || 'Untitled Item',
            description: description || `A ${aiResult.material.toLowerCase()} item identified as ${aiResult.object}.`,
            imageUrl: `/uploads/${file.filename}`,
            location: location || 'Downtown Hub',
            quantity: quantity || 'Unknown quantity',
            phoneNumber: phone || 'Not provided',
            distance: (Math.random() * 5 + 0.5).toFixed(1) + ' km',
            category: aiResult.material,
            owner: 'Eco-Contributor', // Added for "Listed by" section in details
            ownerImage: 'android.resource://com.example.recircuitai/drawable/user_profile',
            timestamp: new Date().toISOString(),
            aiData: {
                condition: 'Fresh', // Matches high-fidelity screenshot
                estimatedValue: 'Calculating...',
                tags: aiResult.tags,
                materialContent: `${aiResult.material}`, // Cleaner "Composition" look
                industry: aiResult.industry,
                possible_products: aiResult.possible_products
            }
        };

        items.unshift(newItem); // Add to beginning of list

        res.status(201).json({
            success: true,
            message: 'Item uploaded and analyzed by WitchHunt AI!',
            data: newItem
        });
    } catch (error) {
        console.error('Error uploading item:', error);
        res.status(500).json({ success: false, message: 'Server error during upload.' });
    }
});

app.get('/get-item/:id', (req, res) => {
    const item = items.find(i => i.id === req.params.id);
    if (!item) {
        return res.status(404).json({ success: false, message: 'Item not found.' });
    }
    res.json({
        success: true,
        data: item
    });
});

app.listen(PORT, '0.0.0.0', () => {
    console.log(`🚀 ReCircuit Backend running at http://0.0.0.0:${PORT}`);
    console.log(`Endpoints available:`);
    console.log(`  - GET  /get-items`);
    console.log(`  - POST /upload-item`);
});
