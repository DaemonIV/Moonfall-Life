from dotenv import load_dotenv
import discord
import os
import openai

load_dotenv()

client = OpenAI(
    base_url="https://openrouter.ai/api/v1",
    api_key=os.getenv('OPENROUTER_API_KEY'),
)

def chatgpt_response(prompt):
    try:
        completion = client.chat.completions.create(
            model="google/gemini-2.0-flash-lite-001:free",
            messages=[
                {
                    "role": "user",
                    "content": prompt,
                }
            ]
        )
        return completion.choices[0].message.content
    except Exception as e:
        print(f"Error: {e}")
        return "Sorry, I ran into an error generating a response."
