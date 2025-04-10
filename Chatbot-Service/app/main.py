from flask import Flask, request, jsonify
from app.services.rag_service import RAGSystem
from app.services.ai_service import AIAgent
import chromadb
from chromadb.utils import embedding_functions
from dotenv import load_dotenv
import os
from flask_cors import CORS

load_dotenv()

app = Flask(__name__)
CORS(app)
# Initialisation des services
def init_services():
    persist_dir = "C:/Users/ultra/Desktop/chroma_db"
    collection_name = os.getenv("CHROMA_COLLECTION", "Pointage_Data3_filtrage")

    client = chromadb.PersistentClient(path=persist_dir)
    embedding_fn = embedding_functions.SentenceTransformerEmbeddingFunction(
        model_name="sentence-transformers/all-mpnet-base-v2"
    )
    collection = client.get_collection(collection_name, embedding_function=embedding_fn)

    ai_agent = AIAgent()
    rag_system = RAGSystem(ai_agent=ai_agent, collection=collection)

    return rag_system

rag_system = init_services()

@app.route('/chat', methods=['POST'])
def chat():
    try:
        data = request.get_json()
        print("Received data:", data)

        # Utilisez 'query_type' au lieu de 'type_filter'
        response = rag_system.query(
            query=data['message'],
            query_type=data.get('query_type')
        )

        return jsonify({
            "success": True,
            "response": response,
            "query_type": data.get('query_type')
        })

    except Exception as e:
        print(f"Error: {str(e)}")
        return jsonify({
            "success": False,
            "error": "Internal server error"
        }), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)