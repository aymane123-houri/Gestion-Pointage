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
    eureka_server="http://localhost:8761/eureka/",  # URL du serveur Eureka
    #eureka_server=eureka_server_url,
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
        host="localhost",
        #host="mysql-service",
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
    url = f'http://localhost:8083/Pointages/enregistrer/{employe_id}'  # Mise à jour du port
    #url = f'http://pointage-service:8083/Pointages/enregistrer/{employe_id}'
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
"""

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
from concurrent.futures import ThreadPoolExecutor
from datetime import datetime, timezone
import threading
import time

app = Flask(__name__)
CORS(app)
executor = ThreadPoolExecutor(4)

# Configuration Eureka
eureka_server_url = os.getenv('DISCOVERY_SERVICE_URL', 'http://localhost:8761/eureka/')
eureka_client = EurekaClient(
    eureka_server=eureka_server_url,
    app_name="reconnaissance-service",
    instance_port=5000,
    instance_ip="reconnaissance-service.default.svc.cluster.local",
    region="default",
)

async def start_eureka_client():
    await eureka_client.start()

# Cache pour les encodages des visages
employe_encodings_cache = {}
cache_loaded = False
active_detections = {}  # Pour éviter les détections multiples

# Configuration de la base de données
def get_db_connection():
    return mysql.connector.connect(
        #host="localhost",
        host="mysql-service",
        user="root",
        password="",
        database="employe_db"
    )

# Précharger les encodages des employés
def preload_employe_encodings():
    global cache_loaded
    try:
        connection = get_db_connection()
        cursor = connection.cursor()
        cursor.execute("SELECT id, nom, prenom, photo_profil FROM employe")
        employes = cursor.fetchall()

        for employe in employes:
            photo_blob = employe[3]
            if isinstance(photo_blob, bytes):
                photo_base64 = base64.b64encode(photo_blob).decode('utf-8')
            else:
                photo_base64 = photo_blob

            photo_recue = decode_image(photo_base64)
            if photo_recue:
                photo_recue_np = np.array(photo_recue.convert('RGB'))
                encodages = face_recognition.face_encodings(photo_recue_np)
                if len(encodages) > 0:
                    employe_encodings_cache[employe[0]] = {
                        'encoding': encodages[0],
                        'nom': employe[1],
                        'prenom': employe[2]
                    }

        cache_loaded = True
        print(f"Cache chargé avec {len(employe_encodings_cache)} encodages")
    except Exception as e:
        print(f"Erreur lors du préchargement des encodages: {e}")
    finally:
        if connection:
            connection.close()

# Décoder une image base64
def decode_image(image_base64):
    try:
        if isinstance(image_base64, bytes):
            image_base64 = image_base64.decode('utf-8')

        if image_base64.startswith('data:image'):
            image_base64 = image_base64.split(';base64,')[1]

        image_data = base64.b64decode(image_base64)
        image = Image.open(BytesIO(image_data))

        if image is None:
            return None

        return image.convert('RGB')
    except Exception as e:
        print(f"Erreur lors du décodage de l'image: {e}")
        return None

# Reconnaissance faciale optimisée
def recognize_face(frame_base64):
    try:
        if not cache_loaded:
            return "Cache non chargé", "Erreur", None

        image_recue = decode_image(frame_base64)
        if image_recue is None:
            return "Image invalide", "Erreur", None

        image_recue_np = np.array(image_recue)
        face_locations = face_recognition.face_locations(image_recue_np)

        if len(face_locations) == 0:
            return "Aucun visage détecté", "Aucun visage", None

        # Encodage du visage détecté
        face_encoding = face_recognition.face_encodings(image_recue_np, face_locations)[0]

        # Comparaison avec le cache
        for emp_id, emp_data in employe_encodings_cache.items():
            matches = face_recognition.compare_faces([emp_data['encoding']], face_encoding, tolerance=0.6)
            if matches[0]:
                distances = face_recognition.face_distance([emp_data['encoding']], face_encoding)
                if distances[0] < 0.6:  # Seuil de confiance
                    return f"{emp_data['prenom']} {emp_data['nom']}", "Reconnu", emp_id

        return "Visage non reconnu", "Inconnu", None

    except Exception as e:
        print(f"Erreur de reconnaissance: {e}")
        return "Erreur", "Erreur", None

# Vérifier les pointages existants
def get_today_pointages(employe_id):
    try:
        today = datetime.now().strftime('%Y-%m-%d')
        #url = f'http://localhost:8083/Pointages/aujourdhui/{employe_id}?date={today}'
        url = f'http://pointage-service:8083/Pointages/aujourdhui/{employe_id}?date={today}'
        response = requests.get(url)
        if response.status_code == 200:
            return response.json()
        return []
    except Exception as e:
        print(f"Erreur récupération pointages: {e}")
        return []

# Déterminer l'action à effectuer
def should_register_pointage(employe_id):
    today_pointages = get_today_pointages(employe_id)

    # Si aucun pointage aujourd'hui -> entrée
    if not today_pointages:
        return 'ENTRY'

    # Prendre le dernier pointage
    last_pointage = today_pointages[-1]

    # Si pas de sortie -> sortie
    if not last_pointage.get('dateHeureSortie'):
        return 'EXIT'

    # Gestion des différents formats de date
    last_exit_str = last_pointage['dateHeureSortie']
    try:
        # Essayer le format avec timezone
        last_exit = datetime.strptime(last_exit_str, '%Y-%m-%dT%H:%M:%S.%f%z')
    except ValueError:
        try:
            # Essayer le format sans timezone
            last_exit = datetime.strptime(last_exit_str, '%Y-%m-%dT%H:%M:%S.%f')
            # Ajouter la timezone UTC si elle est absente
            last_exit = last_exit.replace(tzinfo=timezone.utc)
        except ValueError:
            try:
                # Essayer le format sans millisecondes
                last_exit = datetime.strptime(last_exit_str, '%Y-%m-%dT%H:%M:%S')
                last_exit = last_exit.replace(tzinfo=timezone.utc)
            except ValueError as e:
                print(f"Format de date non reconnu: {last_exit_str}")
                return None

    # Si sortie il y a plus d'1 heure -> nouvelle entrée
    if (datetime.now(timezone.utc) - last_exit).total_seconds() > 3600:
        return 'ENTRY'

    return None

# Enregistrer le pointage
def enregistrer_pointage(employe_id, pointage_type):
    try:
        url = f'http://pointage-service:8083/Pointages/enregistrer/{employe_id}'
        response = requests.post(url, json={
            'employeId': employe_id,
            'type': pointage_type
        })
        return response.status_code == 200
    except Exception as e:
        print(f"Erreur pointage: {e}")
        return False

# Route pour le streaming en temps réel
@app.route('/api/stream_recognize', methods=['POST'])
def stream_recognize():
    try:
        data = request.get_json()
        frame_base64 = data.get('frame')

        if not frame_base64:
            return jsonify({'error': 'Aucune frame fournie'}), 400

        # Vérifier si une détection est déjà en cours pour éviter les doublons
        current_time = time.time()
        if 'last_detection_time' in active_detections and (current_time - active_detections['last_detection_time']) < 30:
            return jsonify({'message': 'Détection déjà en cours', 'status': 'En cours'})

        # Marquer la détection comme active
        active_detections['last_detection_time'] = current_time

        # Détection dans un thread séparé
        future = executor.submit(recognize_face, frame_base64)
        nom, status, emp_id = future.result()

        if status == "Reconnu" and emp_id:
            action = should_register_pointage(emp_id)

            if not action:
                return jsonify({
                    'message': f"{nom} a déjà pointé récemment",
                    'status': "Déjà pointé",
                    'recognized': True,
                    'employee_id': emp_id
                })

            pointage_ok = enregistrer_pointage(emp_id, action)

            if pointage_ok:
                status = "Entrée pointée" if action == 'ENTRY' else "Sortie pointée"
            else:
                status = "Reconnu mais pointage échoué"

        # Nettoyer la détection active
        if 'last_detection_time' in active_detections:
            del active_detections['last_detection_time']

        return jsonify({
            'message': nom,
            'status': status,
            'recognized': status in ["Entrée pointée", "Sortie pointée", "Reconnu"],
            'employee_id': emp_id,
            'pointage_type': action if status.startswith(('Entrée', 'Sortie')) else None
        })

    except Exception as e:
        print(f"Erreur endpoint: {e}")
        if 'last_detection_time' in active_detections:
            del active_detections['last_detection_time']
        return jsonify({'error': str(e)}), 500

# Route pour rafraîchir le cache
@app.route('/api/refresh_cache', methods=['POST'])
def refresh_cache():
    try:
        preload_employe_encodings()
        return jsonify({
            'status': 'success',
            'cache_size': len(employe_encodings_cache)
        })
    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 500

if __name__ == '__main__':
    # Initialiser le cache
    preload_employe_encodings()

    # Démarrer Eureka
    asyncio.run(start_eureka_client())

    # Démarrer le serveur
    app.run(host="0.0.0.0", port=5000, threaded=True)