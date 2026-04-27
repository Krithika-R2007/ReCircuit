"""
ReCircuit AI — FastAPI service: Gemini Vision classify + rule-based match/cluster.
"""

from __future__ import annotations

import json
import os
import re
from typing import Any, List, Optional
from collections import Counter

from dotenv import load_dotenv
load_dotenv()

import google.generativeai as genai
from fastapi import FastAPI, UploadFile, File
from fastapi import HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

app = FastAPI(title="ReCircuit AI Service", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- Fallback Logic ---
INDUSTRY_FALLBACKS = {
    "plastic": {
        "industry": "Plastic Recycling Plant",
        "possible_products": ["Containers", "Packaging", "Mugs"]
    },
    "wood": {
        "industry": "Wood Recycling Facility",
        "possible_products": ["Particle Board", "Furniture", "Mulch"]
    },
    "metal": {
        "industry": "Metal Recovery Plant",
        "possible_products": ["Steel Feedstock", "Parts"]
    },
    "glass": {
        "industry": "Glass Recycling Plant",
        "possible_products": ["Bottles", "Fiberglass"]
    },
    "organic": {
        "industry": "Compost Facility",
        "possible_products": ["Compost", "Biofertilizer"]
    },
    "paper": {
        "industry": "Paper Recycling Plant",
        "possible_products": ["Cardboard", "Tissue Paper", "Newsprint"]
    },
    "stationary": {
        "industry": "Paper Recycling Plant",
        "possible_products": ["Recycled Paper", "Notebooks"]
    }
}

def get_industry_fallback(material: str) -> dict[str, Any]:
    mat_lower = material.lower()
    for key, val in INDUSTRY_FALLBACKS.items():
        if key in mat_lower:
            return val
    return {
        "industry": "Mixed Waste Sorting Facility",
        "possible_products": ["Sorted fractions", "Energy recovery feed"]
    }

# --- Request/Response Models ---

class MatchIndustryRequest(BaseModel):
    material: str

class MatchIndustryResponse(BaseModel):
    industry: str
    possible_products: list[str]

class ClusterItemInput(BaseModel):
    material: Optional[str] = None
    id: Optional[str] = None
    title: Optional[str] = None
    description: Optional[str] = None
    image_url: Optional[str] = None
    weight_kg: Optional[float] = None

class ClusterItemsRequest(BaseModel):
    items: list[ClusterItemInput] = Field(..., min_length=1)

class ClusterItemsResponse(BaseModel):
    cluster: str
    estimated_bulk: str
    matched_industry: str
    bulk_summary: str

# --- Gemini (Vision) ---

GEMINI_MODEL = os.environ.get("GEMINI_MODEL", "gemini-1.5-flash")

def _gemini_model() -> Any:
    key = os.environ.get("GEMINI_API_KEY", "").strip()
    if not key:
        raise HTTPException(
            status_code=503,
            detail="GEMINI_API_KEY is not set. Add it to the environment to use /classify-material.",
        )
    genai.configure(api_key=key)
    generation_config = {
        "temperature": 0.1,
        "top_p": 0.95,
        "top_k": 40,
        "max_output_tokens": 1024,
        "response_mime_type": "application/json",
    }
    return genai.GenerativeModel(
        model_name=GEMINI_MODEL,
        generation_config=generation_config,
    )

def _extract_json_object(text: str) -> dict[str, Any]:
    t = (text or "").strip()
    fence = re.search(r"```(?:json)?\s*([\s\S]*?)\s*```", t, re.IGNORECASE)
    if fence:
        t = fence.group(1).strip()
    return json.loads(t)

def _gemini_response_text(response: Any) -> str:
    try:
        return (response.text or "").strip()
    except Exception:
        pass
    parts: list[str] = []
    for cand in getattr(response, "candidates", None) or []:
        content = getattr(cand, "content", None)
        for p in getattr(content, "parts", None) or []:
            if hasattr(p, "text") and p.text:
                parts.append(p.text)
    return "".join(parts).strip()


@app.get("/health")
async def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/classify-material")
async def classify_material(image: UploadFile = File(...)):
    """
    Multipart file upload only (Swagger: multipart/form-data, file field `image`).
    Classifies waste using Gemini Vision on raw image bytes.
    """
    contents = await image.read()
    if not contents:
        raise HTTPException(status_code=400, detail="Empty file.")

    mime = (image.content_type or "").strip().lower()
    if mime and not mime.startswith("image/"):
        raise HTTPException(status_code=400, detail="File must be an image (image/*).")

    mime_for_gemini = image.content_type or "image/jpeg"

    try:
        model = _gemini_model()
        prompt = """
        Analyze this image of waste/material. 
        Identify the primary object, its material type (e.g., Plastic, Wood, Metal, Organic, Electronic, Glass, Paper), a confidence score (0.0 to 1.0), and some relevant tags.
        Also suggest the best recycling industry for it and some possible products it can be recycled into.
        Return ONLY a JSON object with this exact structure:
        {
         "object": "Plastic Bottle",
         "material": "Plastic",
         "confidence": 0.95,
         "tags": ["PET", "recyclable"],
         "industry": "Plastic Recycling Plant",
         "possible_products": ["Containers", "Mugs", "Packaging"]
        }
        """
    except Exception as e:
        print(f"CRITICAL: Failed to initialize Gemini model: {e}")
        return {
            "object": "AI Config Error",
            "material": "Check API Key",
            "confidence": 0.0,
            "tags": ["Error"],
            "industry": "Service Hub",
            "possible_products": [str(e)[:50]]
        }

    import time
    max_retries = 2
    for attempt in range(max_retries):
        try:
            print(f"INFO: Attempting Gemini classification (attempt {attempt+1})...")
            response = model.generate_content(
                [
                    {"mime_type": mime_for_gemini, "data": contents},
                    prompt,
                ]
            )
            raw = _gemini_response_text(response)
            print(f"DEBUG: Raw response from Gemini: {raw}")
            data = _extract_json_object(raw)

            # Fallback logic ensuring all fields exist
            material = str(data.get("material", "Mixed")).strip() or "Mixed"
            conf = data.get("confidence", 0.75)
            try:
                confidence = float(conf)
            except (TypeError, ValueError):
                confidence = 0.75
            confidence = max(0.0, min(1.0, round(confidence, 2)))

            tags = data.get("tags")
            if not isinstance(tags, list):
                tags = []
            tags = [str(t).strip() for t in tags if str(t).strip()]
            
            object_name = str(data.get("object", "Unknown Object")).strip()

            fallback = get_industry_fallback(material)
            industry = data.get("industry")
            if not industry or not isinstance(industry, str):
                industry = fallback["industry"]
                
            possible_products = data.get("possible_products")
            if not possible_products or not isinstance(possible_products, list) or len(possible_products) == 0:
                possible_products = fallback["possible_products"]

            return {
                "object": object_name,
                "material": material,
                "confidence": confidence,
                "tags": tags,
                "industry": industry,
                "possible_products": possible_products
            }
        except Exception as e:
            err_str = str(e).lower()
            if ("429" in err_str or "quota" in err_str) and attempt < max_retries - 1:
                print(f"Rate limited (429). Attempt {attempt + 1} failed. Retrying in 3 seconds...")
                time.sleep(3)
                continue
            
            # SMART FALLBACK: Show the actual error on the phone so we can debug
            print(f"ERROR during AI classification: {e!s}")
            error_msg = str(e)[:30] # Keep it short for the title
            fallback_obj = f"AI Error: {error_msg}"
            fallback_mat = "Check Logs"
            
            return {
                "object": fallback_obj,
                "material": fallback_mat,
                "confidence": 1.0,
                "tags": ["Error", "Offline Mode"],
                "industry": "Service Hub",
                "possible_products": ["Please restart server"]
            }


@app.post("/match-industry", response_model=MatchIndustryResponse)
async def match_industry(body: MatchIndustryRequest) -> MatchIndustryResponse:
    fallback = get_industry_fallback(body.material)
    return MatchIndustryResponse(
        industry=fallback["industry"],
        possible_products=fallback["possible_products"]
    )


@app.post("/cluster-items", response_model=ClusterItemsResponse)
async def cluster_items(body: ClusterItemsRequest) -> ClusterItemsResponse:
    if not body.items:
        fallback = get_industry_fallback("Mixed")
        return ClusterItemsResponse(
            cluster="Mixed Feedstock",
            estimated_bulk="0 items",
            matched_industry=fallback["industry"],
            bulk_summary="0 items grouped"
        )

    # Gather materials
    materials = [item.material for item in body.items if item.material]
    if not materials:
        majority_material = "Mixed"
        count = 0
    else:
        counts = Counter(materials)
        most_common = counts.most_common()
        
        # Check for tie
        if len(most_common) > 1 and most_common[0][1] == most_common[1][1]:
            majority_material = "Mixed"
            count = len(materials)
        else:
            majority_material = most_common[0][0]
            count = most_common[0][1]
            
    num_items = len(body.items)
    
    if majority_material.lower() == "mixed":
        cluster_name = "Mixed Feedstock"
        fallback = get_industry_fallback("Mixed")
        bulk_summary = f"{num_items} mixed items grouped"
        estimated_bulk = f"{num_items} mixed items"
    else:
        cluster_name = f"{majority_material} Bulk Cluster"
        fallback = get_industry_fallback(majority_material)
        bulk_summary = f"{count} {majority_material.lower()} items grouped"
        estimated_bulk = f"{count} similar items"

    return ClusterItemsResponse(
        cluster=cluster_name,
        estimated_bulk=estimated_bulk,
        matched_industry=fallback["industry"],
        bulk_summary=bulk_summary
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
