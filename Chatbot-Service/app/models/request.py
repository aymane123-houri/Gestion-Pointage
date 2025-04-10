from pydantic import BaseModel

class ChatRequest(BaseModel):
    message: str
    query_type: str = "ANOMALIE"  # Valeur par défaut