"""from flask import Flask, request, jsonify
from PIL import Image
from io import BytesIO
import base64
from flask_cors import CORS
app = Flask(__name__)
CORS(app)

def base64_to_image(base64_data):

    try:
        # Si l'image est envoyée avec un préfixe comme "data:image/jpeg;base64,", il faut le retirer
        if base64_data.startswith('data:image'):
            base64_data = base64_data.split(',')[1]

        # Décodage de l'image à partir du base64
        img_data = base64.b64decode(base64_data)
        # Création d'un objet Image à partir des données
        image = Image.open(BytesIO(img_data))
        return image
    except Exception as e:
        print(f"Erreur de décodage ou d'ouverture de l'image: {e}")
        return None

def recognize_face(image_data):

    try:
        # Convertir l'image base64 en objet Image
        image = base64_to_image(image_data)

        if image:
            # Affichage de l'image pour vérifier visuellement qu'elle est bien reçue
            image.show()

            # Logique de reconnaissance faciale (utilisez face_recognition ou une autre bibliothèque ici)
            # Exemple avec face_recognition (à adapter selon votre implémentation):
            # import face_recognition
            # faces = face_recognition.face_locations(image)

            # Ici, vous pouvez intégrer votre propre logique de reconnaissance faciale.
            # Pour cet exemple, on suppose que l'image est valide.

            return {"message": "Reconnaissance faciale réussie", "status": "success"}
        else:
            return {"error": "Image invalide", "status": "failed"}
    except Exception as e:
        print(f"Erreur dans la fonction recognize_face: {e}")
        return {"error": "Erreur interne", "status": "failed"}

@app.route('/api/recognize', methods=['POST'])
def recognize_endpoint():

    try:
        # Récupérer les données de l'image en base64 envoyées dans le corps de la requête
        data = request.json
        if 'image' not in data:
            return jsonify({"error": "Aucune image fournie", "status": "failed"}), 400

        image_data = data['image']

        # Appeler la fonction de reconnaissance faciale
        result = recognize_face(image_data)

        return jsonify(result)
    except Exception as e:
        print(f"Erreur dans le point d'API: {e}")
        return jsonify({"error": "Erreur interne", "status": "failed"}), 500

if __name__ == '__main__':
    app.run(debug=True)
"""
import mysql


import base64
import face_recognition
from io import BytesIO
from PIL import Image, UnidentifiedImageError
import numpy as np
import mysql.connector
from flask import Flask, request, jsonify
from flask_cors import CORS
import requests
from py_eureka_client.eureka_client import EurekaClient
import asyncio
import os

app = Flask(__name__)
CORS(app)
# Enregistrer les blueprints

eureka_server_url = os.getenv('DISCOVERY_SERVICE_URL', 'http://localhost:8761/eureka/')
eureka_client = EurekaClient(
    #eureka_server="http://localhost:8761/eureka/",  # URL du serveur Eureka
    eureka_server=eureka_server_url,
    app_name="reconnaissance-service",  # Nom de votre microservice
    instance_port=5000,  # Port de votre service
    #instance_ip="127.0.0.1",  # Adresse IP de votre instance
    #instance_ip="0.0.0.0",
    instance_ip="reconnaissance-service.default.svc.cluster.local",
    region="default",  # Région (facultatif)
)

async def start_eureka_client():
    await eureka_client.start()
# Configuration de la connexion à la base de données


def get_db_connection():
    return mysql.connector.connect(
        #host="localhost",
        #host="employe-service",
        host="mysql-service",
        user="root",
        password="",
        database="employe_db"
    )

# Fonction pour décoder l'image base64 et la convertir en RGB
def decode_image(image_base64):
    try:
        # Vérifier si l'entrée est en bytes et la convertir en string
        if isinstance(image_base64, bytes):
            image_base64 = image_base64.decode('utf-8')

            # Supprimer le préfixe 'data:image/jpeg;base64,' si présent
        if image_base64.startswith('data:image'):
            image_base64 = image_base64.split(';base64,')[1]

        print(f"Base64 après découpage (premiers 100 caractères) : {image_base64[:100]}")

        # Décoder l'image base64
        image_data = base64.b64decode(image_base64)
        image = Image.open(BytesIO(image_data))

        if image is None:
            print("Erreur : Impossible d'ouvrir l'image.")
            return None

        print(f"Mode de l'image avant conversion : {image.mode}")

        # Convertir l'image en RGB (3 canaux) pour éviter les erreurs
        image = image.convert('RGB')

        print(f"Mode de l'image après conversion en RGB : {image.mode}")

        return image
    except (UnidentifiedImageError, Exception) as e:
        print(f"Erreur lors du décodage de l'image: {e}")
        return None

# Fonction de reconnaissance faciale
def comparer_visages(image_encodée):
    try:
        # Décoder l'image reçue
        image_recue = decode_image(image_encodée)
        if image_recue is None:
            return "Erreur : L'image reçue est invalide", "Échec de la reconnaissance"

        # Convertir en numpy array et extraire les encodages
        image_recue_np = np.array(image_recue)
        inconnus_encodages = face_recognition.face_encodings(image_recue_np)

        if len(inconnus_encodages) == 0:
            return "Aucun visage trouvé", "Échec de la reconnaissance"

        # Connexion à la base de données pour récupérer les visages stockés
        connection = get_db_connection()
        cursor = connection.cursor()
        cursor.execute("SELECT id, nom, prenom, photo_profil FROM employe")
        employes = cursor.fetchall()

        # Comparaison des visages
        # Comparaison des visages
        for employe in employes:
            photo_blob = employe[3]  # Récupérer l'image stockée en BLOB

            # Vérifier si l'image est un BLOB (bytes) et la convertir en base64
            if isinstance(photo_blob, bytes):
                photo_base64 = base64.b64encode(photo_blob).decode('utf-8')  # Convertir BLOB → base64
            else:
                photo_base64 = photo_blob  # Si c'est déjà en base64, pas besoin de conversion

            photo_recue = decode_image(photo_base64)
            if photo_recue is None:
                continue


        # Convertir la photo de l'employé en RGB puis en numpy array
            photo_recue = photo_recue.convert('RGB')
            photo_recue_np = np.array(photo_recue)

            # Extraire les encodages du visage de l'employé
            employe_encodages = face_recognition.face_encodings(photo_recue_np)

            if len(employe_encodages) > 0:
                result = face_recognition.compare_faces([employe_encodages[0]], inconnus_encodages[0])
                if result[0]:
                    if enregistrer_pointage(employe[0]):
                        connection.close()
                        return f"Bonjour {employe[1]} {employe[2]}", "Reconnaissance réussie"

        connection.close()
        return "Visage non reconnu", "Échec de la reconnaissance"

    except Exception as e:
        print(f"Erreur lors de la comparaison des visages : {e}")
        return "Erreur inconnue", "Échec de la reconnaissance"




# Fonction pour enregistrer le pointage via le microservice
def enregistrer_pointage(employe_id):
    #url = f'http://localhost:8083/Pointages/enregistrer/{employe_id}'  # Mise à jour du port
    url = f'http://pointage-service:8083/Pointages/enregistrer/{employe_id}'
    data = {'employeId': employe_id}

    response = requests.post(url, json=data)

    if response.status_code == 200:
        print(f"Pointage effectué avec succès pour l'employé {employe_id}.")
        return True
    else:
        print(f"Erreur lors de l'enregistrement du pointage pour l'employé {employe_id}.")
        print(f"Code de réponse: {response.status_code}, Message: {response.text}")
        return False


# Route API pour la reconnaissance faciale
@app.route('/api/recognize', methods=['POST'])
def recognize_face():
    try:
        data = request.get_json()
        image_encodée = data['image']
        print(f"Image base64 reçue (premiers 100 caractères) : {image_encodée[:100]}")

        employe, message = comparer_visages(image_encodée)

        return jsonify({'message': employe, 'status': message})

    except Exception as e:
        print(f"Erreur lors de la reconnaissance faciale: {e}")
        return jsonify({'message': 'Erreur lors de la reconnaissance faciale', 'status': 'Échec'}), 500

if __name__ == '__main__':
    asyncio.run(start_eureka_client())
    #app.run(debug=True)
    app.run(host="0.0.0.0", port=5000)
