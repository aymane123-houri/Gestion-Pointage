
from dotenv import load_dotenv

load_dotenv()

import requests
import logging

class AIAgent:
    def __init__(self):
        # Mettez directement votre clé ici (TEMPORAIREMENT pour test)
        self.api_key = "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"

        self.api_url = "https://openrouter.ai/api/v1/chat/completions"
        self.headers = {
            "Authorization": f"Bearer {self.api_key}",
            "Content-Type": "application/json",
            #"HTTP-Referer": "http://localhost:5000",
            "HTTP-Referer": "http://chatBot-Service:5000",
            "X-Title": "Gestion Pointage"
        }
        self.model = "deepseek/deepseek-r1-distill-llama-70b:free"

    def generate(self, query: str, context: str) -> str:
        prompt = f"""
        Vous êtes un assistant spécialisé dans le système de pointage des employés.
        Répondez à la question en français en utilisant des phrases complètes et claires.
        Utilisez uniquement les informations fournies dans le contexte.
        Structurez votre réponse de manière professionnelle et directe.
        Ne faites pas de calculs ou de déductions non mentionnées dans le contexte.
        
        Voici des exemples de réponses attendues:
        - "Aymane Hourie du département Marketing était présent le 12 février 2025 avec une entrée à 01:14 et une sortie à 13:18."
        - "Le 3 mars 2025, l'employé a effectué 12 heures de travail dont 4 heures supplémentaires."
        - "L'employé était absent le 2 mars 2025."
        
        Question: {query}
        Contexte: {context}
    
        Réponse:
        """

        payload = {
            "model": self.model,
            "messages": [{"role": "user", "content": prompt}],
            "max_tokens": 1000
        }

        try:
            response = requests.post(self.api_url, headers=self.headers, json=payload)
            response.raise_for_status()
            return response.json()["choices"][0]["message"]["content"]
        except requests.exceptions.HTTPError as e:
            logging.error(f"Erreur OpenRouter: {e.response.text}")
            return "Désolé, je ne peux pas répondre pour le moment."
        except Exception as e:
            logging.error(f"Erreur inattendue: {str(e)}")
            return "Erreur lors de la génération de la réponse."

    def _build_prompt(self, query: str, context: str) -> str:
        return f"""
        [Contexte] {context}
        [Question] {query}
        [Instructions] Répondez en français de manière concise et précise.
        """