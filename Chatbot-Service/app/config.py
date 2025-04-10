import os
from dotenv import load_dotenv

load_dotenv()

class Config:
    CHROMA_PERSIST_DIR = os.getenv("CHROMA_PERSIST_DIR", "chroma_db")
    CHROMA_COLLECTION = os.getenv("CHROMA_COLLECTION", "Pointage_Data3_filtrage")
    OPENROUTER_API_KEY = os.getenv("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")
    AI_MODEL = os.getenv("AI_MODEL", "deepseek/deepseek-r1-distill-llama-70b:free")