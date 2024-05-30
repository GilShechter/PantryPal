from urllib.parse import unquote

from fastapi import FastAPI, HTTPException
from openai import OpenAI
import os
from dotenv import load_dotenv
import requests
from pydantic import BaseModel

load_dotenv()
API_KEY = os.getenv("API_KEY")
app = FastAPI()
client = OpenAI(api_key=API_KEY)


class ImageRequest(BaseModel):
    image_url: str


@app.post('/analyze_image/')
async def analyze_image(request: ImageRequest):
    image_url = request.image_url
    if not image_url or image_url == "":
        raise HTTPException(status_code=400, detail="Missing image_url parameter")

    try:
        await check_image_url(image_url)
        print("Image URL: ", image_url)
        response = client.chat.completions.create(
            model="gpt-4o",
            messages=[
                {
                    "role": "user",
                    "content": [
                        {"type": "text", "text": "List all the food ingredients in the image, just the name of the "
                                                 "ingredient, separated by space. No extra details needed."},
                        {"type": "image_url", "image_url": {"url": image_url}},
                    ],
                }
            ],
            max_tokens=300,
        )
        return response.choices[0]
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


async def check_image_url(image_url: str):
    response = requests.head(image_url)
    print("Response: ", response.status_code, "For URL: ", image_url)
    if response.status_code != 200:
        raise HTTPException(status_code=500, detail="Invalid image URL")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
