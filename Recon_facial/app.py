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









"""
import mysql.connector

import face_recognition_models
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









import cv2
import numpy as np
import time
from datetime import datetime, timezone
import mysql.connector
import requests
from PIL import Image
from io import BytesIO
import base64
import threading
import queue
import logging
import os
import json
import mediapipe as mp
from facenet_pytorch import MTCNN, InceptionResnetV1
import torch

# Configuration du logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('face_recognition.log'),
        logging.StreamHandler()
    ]
)

# Configuration de la caméra Hikvision
CAMERA_IP = "192.168.11.189"
CAMERA_USER = "admin"
CAMERA_PASS = "aymaneLarache1@"
RTSP_URL = f"rtsp://{CAMERA_USER}:{CAMERA_PASS}@{CAMERA_IP}:554/Streaming/Channels/101"

# Paramètres d'affichage
DISPLAY_WIDTH = 1280
DISPLAY_HEIGHT = 720
ZOOM_FACTOR = 1.0
ZOOM_STEP = 0.1

# Paramètres de détection
FACE_DETECTION_INTERVAL = 0.2
FACE_MATCH_THRESHOLD = 0.9
MIN_TIME_BETWEEN_POINTAGES = 180
MIN_TIME_BETWEEN_ENTRY_AND_EXIT = 300
MIN_FACE_SIZE = 15

# Configuration base de données
DB_CONFIG = {
    'host': 'mysql-service',
    'user': 'root',
    'password': '',
    'database': 'employe_db'
}

# Configuration API
API_BASE_URL = "http://pointage-service:8083/Pointages"
API_TIMEOUT = 3

class FaceRecognitionSystem:
    def __init__(self):
        self.employe_encodings = {}
        self.recognition_results = {}
        self.last_pointage_times = {}
        self.recent_encodings = []
        self.face_encodings_history = {}
        self.zoom_factor = 1.0
        self.running = False
        self.camera_connected = False
        self.last_frame_time = 0
        self.fps = 0

        # Initialisation de MediaPipe pour la détection
        self.mp_face_detection = mp.solutions.face_detection
        self.face_detection = self.mp_face_detection.FaceDetection(min_detection_confidence=0.6, model_selection=1)

        # Initialisation de Facenet pour la reconnaissance
        self.device = 'cuda' if torch.cuda.is_available() else 'cpu'
        self.mtcnn = MTCNN(image_size=160, margin=10, device=self.device)
        self.resnet = InceptionResnetV1(pretrained='vggface2').eval().to(self.device)

        # Queues pour le traitement multithread
        self.frame_queue = queue.Queue(maxsize=2)
        self.detection_queue = queue.Queue(maxsize=1)

        # Cache pour les requêtes API
        self.pointage_cache = {}

        # Initialisation des composants
        self.init_database()
        self.init_camera()

        # Chargement de la configuration
        self.load_config()

    def load_config(self):
        config_file = "config.json"
        if os.path.exists(config_file):
            try:
                with open(config_file, 'r') as f:
                    config = json.load(f)
                    self.last_pointage_times = config.get('last_pointage_times', {})
                    logging.info("Configuration chargée")
            except Exception as e:
                logging.error(f"Erreur chargement config: {str(e)}")

    def save_config(self):
        config = {
            'last_pointage_times': self.last_pointage_times
        }
        try:
            with open("config.json", 'w') as f:
                json.dump(config, f)
        except Exception as e:
            logging.error(f"Erreur sauvegarde config: {str(e)}")

    def init_database(self):
        try:
            conn = mysql.connector.connect(**DB_CONFIG)
            cursor = conn.cursor(dictionary=True)
            cursor.execute("SELECT id, nom, prenom, photo_profil FROM employe")

            loaded_count = 0
            for employe in cursor:
                try:
                    if not employe['photo_profil']:
                        logging.warning(f"Pas de photo pour l'employé {employe['id']}")
                        continue

                    img = self.decode_image(employe['photo_profil'])
                    if not img:
                        logging.warning(f"Échec décodage photo employé {employe['id']}")
                        continue

                    img_aligned, prob = self.mtcnn(img, return_prob=True)
                    if img_aligned is None or prob < 0.9:
                        logging.warning(f"Aucun visage détecté pour l'employé {employe['id']}")
                        continue

                    img_aligned = img_aligned.unsqueeze(0).to(self.device)
                    encoding = self.resnet(img_aligned).detach().cpu().numpy()

                    self.employe_encodings[employe['id']] = {
                        'encoding': encoding[0],
                        'nom': employe['nom'],
                        'prenom': employe['prenom'],
                        'last_seen': 0,
                        'photo': img
                    }
                    loaded_count += 1

                except Exception as e:
                    logging.error(f"Erreur traitement employé {employe['id']}: {str(e)}")

            logging.info(f"{loaded_count} encodages chargés avec succès")
        except Exception as e:
            logging.error(f"Erreur connexion BD: {str(e)}")
        finally:
            if conn:
                conn.close()

    def decode_image(self, image_data):
        try:
            if isinstance(image_data, bytes):
                if image_data.startswith(b'\xFF\xD8') or image_data.startswith(b'\x89PNG'):
                    return Image.open(BytesIO(image_data)).convert('RGB')
                try:
                    return Image.open(BytesIO(base64.b64decode(image_data))).convert('RGB')
                except:
                    pass

            if isinstance(image_data, str):
                if image_data.startswith('data:image'):
                    image_data = image_data.split(';base64,')[1]
                return Image.open(BytesIO(base64.b64decode(image_data))).convert('RGB')

        except Exception as e:
            logging.error(f"Erreur décodage image: {str(e)}")
            return None

    def init_camera(self):
        self.cap = cv2.VideoCapture()
        self.cap.set(cv2.CAP_PROP_BUFFERSIZE, 1)
        self.cap.set(cv2.CAP_PROP_FPS, 12.5)
        self.cap.set(cv2.CAP_PROP_FOURCC, cv2.VideoWriter_fourcc(*'H264'))
        self.cap.set(cv2.CAP_PROP_FRAME_WIDTH, 3840)
        self.cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 2160)
        self.connect_camera()

    def connect_camera(self):
        if not self.camera_connected:
            try:
                if not self.cap.open(RTSP_URL):
                    raise ConnectionError("Échec de l'ouverture du flux RTSP")

                for _ in range(5):
                    ret, _ = self.cap.read()
                    if ret:
                        self.camera_connected = True
                        logging.info("Caméra connectée avec succès")
                        return
                    time.sleep(0.1)

                raise ConnectionError("Flux vidéo non valide")

            except Exception as e:
                self.camera_connected = False
                logging.error(f"Erreur connexion caméra: {str(e)}")
                self.cap.release()
                raise

    def video_capture_thread(self):
        reconnect_attempts = 0
        max_reconnect_attempts = 5
        reconnect_delay = 2

        while self.running:
            try:
                if not self.camera_connected:
                    if reconnect_attempts < max_reconnect_attempts:
                        try:
                            self.connect_camera()
                            reconnect_attempts = 0
                        except Exception as e:
                            reconnect_attempts += 1
                            logging.warning(f"Tentative de reconnexion {reconnect_attempts}/{max_reconnect_attempts}")
                            time.sleep(reconnect_delay)
                            continue
                    else:
                        logging.error("Nombre maximum de tentatives de reconnexion atteint")
                        self.running = False
                        break

                ret, frame = self.cap.read()
                if not ret:
                    logging.warning("Problème de lecture du flux")
                    self.camera_connected = False
                    continue

                current_time = time.time()
                if hasattr(self, 'last_frame_time'):
                    self.fps = 0.9 * self.fps + 0.1 * (1 / (current_time - self.last_frame_time))
                self.last_frame_time = current_time

                if frame.shape[0] > 720:
                    scale_factor = 720 / frame.shape[0]
                    new_width = int(frame.shape[1] * scale_factor)
                    new_height = int(frame.shape[0] * scale_factor)
                    frame = cv2.resize(frame, (new_width, new_height), interpolation=cv2.INTER_AREA)

                if not self.frame_queue.full():
                    self.frame_queue.put(frame)
                else:
                    try:
                        self.frame_queue.get_nowait()
                    except queue.Empty:
                        pass
                    self.frame_queue.put(frame)

            except Exception as e:
                logging.error(f"Erreur capture vidéo: {str(e)}")
                self.camera_connected = False
                time.sleep(1)

    def preprocess_frame(self, frame):
        gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
        brightness = np.mean(gray)
        if brightness < 50:
            alpha = 1.5
            beta = 50
            frame = cv2.convertScaleAbs(frame, alpha=alpha, beta=beta)
        return frame

    def check_image_quality(self, face_img):
        gray = cv2.cvtColor(face_img, cv2.COLOR_BGR2GRAY)
        laplacian_var = cv2.Laplacian(gray, cv2.CV_64F).var()
        brightness = np.mean(gray)

        MIN_LAPLACIAN_VAR = 50
        MIN_BRIGHTNESS = 40
        MAX_BRIGHTNESS = 200

        if laplacian_var < MIN_LAPLACIAN_VAR:
            logging.debug(f"Image floue: variance Laplacienne = {laplacian_var:.2f}")
            return False

        if brightness < MIN_BRIGHTNESS or brightness > MAX_BRIGHTNESS:
            logging.debug(f"Luminosité hors limites: {brightness:.2f}")
            return False

        return True

    def detection_thread(self):
        while self.running:
            try:
                frame = self.detection_queue.get(timeout=1)
                if frame is None:
                    break

                frame_enhanced = self.preprocess_frame(frame)
                rgb_frame = cv2.cvtColor(frame_enhanced, cv2.COLOR_BGR2RGB)
                results = self.face_detection.process(rgb_frame)

                face_locations = []
                if results.detections:
                    for detection in results.detections:
                        bbox = detection.location_data.relative_bounding_box
                        h, w, _ = frame.shape
                        x_min = int(bbox.xmin * w)
                        y_min = int(bbox.ymin * h)
                        width = int(bbox.width * w)
                        height = int(bbox.height * h)
                        x_max = x_min + width
                        y_max = y_min + height

                        if width >= MIN_FACE_SIZE and height >= MIN_FACE_SIZE:
                            face_locations.append((y_min, x_max, y_max, x_min))

                current_results = {}
                if face_locations:
                    if not hasattr(self, 'face_encodings_history'):
                        self.face_encodings_history = {}

                    for idx, (top, right, bottom, left) in enumerate(face_locations):
                        face_width = right - left
                        face_height = bottom - top
                        zoom_factor = max(1.5, 150 / max(face_width, face_height))
                        new_width = int(face_width * zoom_factor)
                        new_height = int(face_height * zoom_factor)

                        center_x = (left + right) // 2
                        center_y = (top + bottom) // 2
                        new_left = max(0, center_x - new_width // 2)
                        new_right = min(frame.shape[1], center_x + new_width // 2)
                        new_top = max(0, center_y - new_height // 2)
                        new_bottom = min(frame.shape[0], center_y + new_height // 2)

                        face_img = frame[new_top:new_bottom, new_left:new_right]
                        if face_img.size == 0:
                            continue

                        if not self.check_image_quality(face_img):
                            logging.info("Image de mauvaise qualité, détection ignorée")
                            current_results[idx] = {
                                'emp_id': None,
                                'name': "Inconnu",
                                'status': "Mauvaise qualité",
                                'face_location': (top, right, bottom, left),
                                'last_seen': time.time(),
                                'distance': 1.0
                            }
                            continue

                        face_img_resized = cv2.resize(face_img, (160, 160), interpolation=cv2.INTER_CUBIC)

                        face_key = f"{center_x}_{center_y}"
                        encoding = None
                        if face_key in self.face_encodings_history and self.face_encodings_history[face_key]:
                            recent_encodings = [(enc, t) for enc, t in self.face_encodings_history[face_key] if time.time() - t < 5]
                            if recent_encodings:
                                encodings = [enc for enc, t in recent_encodings]
                                encoding = np.mean(encodings, axis=0)

                        if encoding is None:
                            face_img_pil = Image.fromarray(cv2.cvtColor(face_img_resized, cv2.COLOR_BGR2RGB))
                            face_aligned, prob = self.mtcnn(face_img_pil, return_prob=True)
                            if face_aligned is None or prob < 0.9:
                                continue

                            face_aligned = face_aligned.unsqueeze(0).to(self.device)
                            encoding = self.resnet(face_aligned).detach().cpu().numpy()[0]

                            if face_key not in self.face_encodings_history:
                                self.face_encodings_history[face_key] = []
                            self.face_encodings_history[face_key].append((encoding, time.time()))

                        self.face_encodings_history[face_key] = [(enc, t) for enc, t in self.face_encodings_history[face_key] if time.time() - t < 5][-3:]

                        if not hasattr(self, 'recent_encodings'):
                            self.recent_encodings = []
                        self.recent_encodings.append((encoding, time.time(), (top, right, bottom, left)))
                        self.recent_encodings = [(enc, t, loc) for enc, t, loc in self.recent_encodings if time.time() - t < 15]

                        matches, emp_id, distance = self.match_with_known_faces(encoding)

                        if matches:
                            emp_data = self.employe_encodings[emp_id]
                            if emp_id in self.last_pointage_times:
                                time_since_last = time.time() - self.last_pointage_times[emp_id]
                                if time_since_last < MIN_TIME_BETWEEN_POINTAGES:
                                    logging.info(f"Employé {emp_id} détecté récemment ({time_since_last:.2f}s), pointage ignoré")
                                    current_results[idx] = {
                                        'emp_id': emp_id,
                                        'name': f"{emp_data['prenom']} {emp_data['nom']}",
                                        'status': "Déjà pointé",
                                        'face_location': (top, right, bottom, left),
                                        'last_seen': time.time(),
                                        'distance': distance,
                                        'photo': emp_data['photo']
                                    }
                                    continue

                            pointage_type = self.check_pointage(emp_id)
                            if pointage_type:
                                threading.Thread(
                                    target=self.register_pointage,
                                    args=(emp_id, pointage_type, frame.copy()),
                                    daemon=True
                                ).start()
                                status = "Entrée" if pointage_type == 'ENTRY' else "Sortie"
                            else:
                                today = datetime.now().strftime('%Y-%m-%d')
                                cache_key = f"{emp_id}_{today}"
                                if cache_key in self.pointage_cache:
                                    last_pointage = self.pointage_cache[cache_key]
                                    if last_pointage['entries']:
                                        last_entry = last_pointage['entries'][-1]
                                        last_exit = last_pointage['exits'][-1] if last_pointage['exits'] else None
                                        if not last_exit or last_exit < last_entry:
                                            time_since_entry = time.time() - last_entry.timestamp()
                                            remaining_time = max(0, MIN_TIME_BETWEEN_ENTRY_AND_EXIT - time_since_entry)
                                            status = f"Présent (sortie dans {int(remaining_time)}s)"
                                        else:
                                            status = "Reconnu"
                                    else:
                                        status = "Reconnu"
                                else:
                                    status = "Reconnu"
                            current_results[idx] = {
                                'emp_id': emp_id,
                                'name': f"{emp_data['prenom']} {emp_data['nom']}",
                                'status': status,
                                'face_location': (top, right, bottom, left),
                                'last_seen': time.time(),
                                'distance': distance,
                                'photo': emp_data['photo']
                            }
                        else:
                            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
                            #cv2.imwrite(f"inconnu_{timestamp}.jpg", frame)
                            #logging.info(f"Capture enregistrée pour inconnu: inconnu_{timestamp}.jpg")
                            current_results[idx] = {
                                'emp_id': None,
                                'name': "Inconnu",
                                'status': "Non reconnu",
                                'face_location': (top, right, bottom, left),
                                'last_seen': time.time(),
                                'distance': 1.0
                            }

                self.recognition_results = current_results
                self.detection_queue.task_done()

            except queue.Empty:
                continue
            except Exception as e:
                logging.error(f"Erreur détection: {e}", exc_info=True)

    def match_with_known_faces(self, encoding):
        if not self.employe_encodings:
            return False, None, 1.0

        best_distance = 1.0
        best_emp_id = None

        for emp_id, emp_data in self.employe_encodings.items():
            distance = np.linalg.norm(emp_data['encoding'] - encoding)
            if distance < best_distance:
                best_distance = distance
                best_emp_id = emp_id

        all_distances = []
        for emp_data in self.employe_encodings.values():
            distance = np.linalg.norm(emp_data['encoding'] - encoding)
            all_distances.append(distance)

        distances_sorted = np.sort(all_distances)
        if len(distances_sorted) > 1 and distances_sorted[1] - best_distance < 0.05:
            logging.warning(f"Correspondance ambiguë: {best_distance:.2f} vs {distances_sorted[1]:.2f}")
            return False, None, best_distance

        if best_distance < FACE_MATCH_THRESHOLD:
            logging.info(f"Identité confirmée pour employé {best_emp_id}, distance: {best_distance:.2f}")

            if hasattr(self, 'recent_encodings'):
                for enc, t, loc in self.recent_encodings:
                    retro_distance = np.linalg.norm(enc - self.employe_encodings[best_emp_id]['encoding'])
                    if retro_distance < FACE_MATCH_THRESHOLD:
                        logging.info(f"Correction rétroactive: visage à {loc} reconnu comme employé {best_emp_id}, distance: {retro_distance:.2f}")
                        for idx, result in self.recognition_results.items():
                            if result['face_location'] == loc and result['emp_id'] is None:
                                result['emp_id'] = best_emp_id
                                result['name'] = f"{self.employe_encodings[best_emp_id]['prenom']} {self.employe_encodings[best_emp_id]['nom']}"
                                result['status'] = "Reconnu (rétroactif)"
                                result['distance'] = retro_distance
                                result['photo'] = self.employe_encodings[best_emp_id]['photo']
                                break

            return True, best_emp_id, best_distance

        return False, None, best_distance

    def check_pointage(self, employe_id):
        now = time.time()
        if employe_id in self.last_pointage_times:
            if now - self.last_pointage_times[employe_id] < MIN_TIME_BETWEEN_POINTAGES:
                logging.info(f"Cooldown actif pour employé {employe_id}, dernier pointage il y a {now - self.last_pointage_times[employe_id]:.2f} secondes")
                return None

        try:
            today = datetime.now().strftime('%Y-%m-%d')
            cache_key = f"{employe_id}_{today}"

            if cache_key in self.pointage_cache:
                last_pointage = self.pointage_cache[cache_key]
            else:
                response = requests.get(
                    f'{API_BASE_URL}/aujourdhui/{employe_id}?date={today}',
                    timeout=API_TIMEOUT
                )

                if response.status_code != 200:
                    logging.error(f"Erreur API lors de la vérification des pointages: {response.status_code}")
                    return None

                pointages = response.json()
                if not pointages:
                    self.pointage_cache[cache_key] = {'entries': [], 'exits': []}
                    logging.info(f"Aucun pointage trouvé pour {employe_id} aujourd'hui, nouvelle entrée autorisée")
                    return 'ENTRY'

                last_pointage = self.process_pointages(pointages)
                self.pointage_cache[cache_key] = last_pointage

            if last_pointage['entries']:
                last_entry = last_pointage['entries'][-1]
                last_exit = last_pointage['exits'][-1] if last_pointage['exits'] else None

                if not last_exit or last_exit < last_entry:
                    time_since_entry = now - last_entry.timestamp()
                    if time_since_entry >= MIN_TIME_BETWEEN_ENTRY_AND_EXIT:
                        logging.info(f"Sortie autorisée pour {employe_id}, dernière entrée à {last_entry} (il y a {time_since_entry:.2f} secondes)")
                        return 'EXIT'
                    else:
                        logging.info(f"Employé {employe_id} déjà présent (entrée à {last_entry}), sortie non autorisée (délai insuffisant: {time_since_entry:.2f}s, minimum: {MIN_TIME_BETWEEN_ENTRY_AND_EXIT}s)")
                        return None

                if last_exit and (now - last_exit.timestamp()) < 3600:
                    logging.info(f"Trop tôt pour une nouvelle entrée pour {employe_id}, dernière sortie à {last_exit}")
                    return None

                logging.info(f"Nouvelle entrée autorisée pour {employe_id} après sortie à {last_exit}")
                return 'ENTRY'

            logging.info(f"Aucune entrée trouvée pour {employe_id}, nouvelle entrée autorisée")
            return 'ENTRY'

        except Exception as e:
            logging.error(f"Erreur vérification pointage: {str(e)}")
            return None

    def process_pointages(self, pointages):
        entries = []
        exits = []

        for p in pointages:
            try:
                if p.get('dateHeureEntree'):
                    entry_time = self.parse_datetime(p['dateHeureEntree'])
                    if entry_time:
                        entries.append(entry_time)

                if p.get('dateHeureSortie'):
                    exit_time = self.parse_datetime(p['dateHeureSortie'])
                    if exit_time:
                        exits.append(exit_time)
            except Exception as e:
                logging.warning(f"Erreur traitement pointage {p}: {str(e)}")
                continue

        return {'entries': sorted(entries), 'exits': sorted(exits)}

    def parse_datetime(self, dt_str):
        if not dt_str:
            return None

        try:
            if 'T' in dt_str:
                if 'Z' in dt_str:
                    return datetime.fromisoformat(dt_str.replace('Z', '+00:00')).astimezone()
                else:
                    return datetime.fromisoformat(dt_str).astimezone()

            try:
                return datetime.strptime(dt_str, '%Y-%m-%d %H:%M:%S').replace(tzinfo=timezone.utc)
            except ValueError:
                pass

            for fmt in ('%Y-%m-%d %H:%M:%S.%f', '%Y-%m-%d %H:%M:%S%z'):
                try:
                    return datetime.strptime(dt_str, fmt).astimezone()
                except ValueError:
                    continue

            logging.warning(f"Format de date non reconnu: {dt_str}")
            return None

        except Exception as e:
            logging.error(f"Erreur parsing date {dt_str}: {str(e)}")
            return None

    def register_pointage(self, employe_id, pointage_type, frame=None):
        self.last_pointage_times[employe_id] = time.time()

        if frame is not None:
            timestamp = datetime.now().strftime('%Y%m%d_%H%M%S')
            #cv2.imwrite(f"pointage_{employe_id}_{timestamp}.jpg", frame)
            #logging.info(f"Capture enregistrée pour {employe_id}: pointage_{employe_id}_{timestamp}.jpg")

        try:
            data = {
                'employeId': employe_id,
                'type': pointage_type,
                'timestamp': datetime.now(timezone.utc).isoformat()
            }

            response = requests.post(
                f'{API_BASE_URL}/enregistrer/{employe_id}',
                json=data,
                timeout=API_TIMEOUT
            )

            if response.status_code == 200:
                logging.info(f"Pointage enregistré: {pointage_type} pour {employe_id}")
                today = datetime.now().strftime('%Y-%m-%d')
                cache_key = f"{employe_id}_{today}"
                timestamp = datetime.now(timezone.utc)

                if cache_key not in self.pointage_cache:
                    self.pointage_cache[cache_key] = {'entries': [], 'exits': []}

                if pointage_type == 'ENTRY':
                    self.pointage_cache[cache_key]['entries'].append(timestamp)
                else:
                    self.pointage_cache[cache_key]['exits'].append(timestamp)

                self.pointage_cache[cache_key]['entries'] = sorted(self.pointage_cache[cache_key]['entries'])
                self.pointage_cache[cache_key]['exits'] = sorted(self.pointage_cache[cache_key]['exits'])

                return True
            else:
                logging.error(f"Erreur API: {response.status_code} - {response.text}")
                return False

        except requests.exceptions.Timeout:
            logging.error("Timeout lors de l'enregistrement du pointage")
            return False
        except Exception as e:
            logging.error(f"Erreur enregistrement: {e}")
            return False

    def apply_zoom(self, frame, zoom_factor):
        if zoom_factor == 1.0:
            return frame, (0, 0, frame.shape[1], frame.shape[0])

        h, w = frame.shape[:2]
        center_x, center_y = w // 2, h // 2

        new_w = int(w / zoom_factor)
        new_h = int(h / zoom_factor)

        x1 = max(0, center_x - new_w // 2)
        y1 = max(0, center_y - new_h // 2)
        x2 = min(w, center_x + new_w // 2)
        y2 = min(h, center_y + new_h // 2)

        cropped = frame[y1:y2, x1:x2]

        if cropped.shape[0] < h or cropped.shape[1] < w:
            zoomed_frame = np.zeros((h, w, 3), dtype=np.uint8)
            y_offset = (h - cropped.shape[0]) // 2
            x_offset = (w - cropped.shape[1]) // 2
            zoomed_frame[y_offset:y_offset+cropped.shape[0], x_offset:x_offset+cropped.shape[1]] = cropped
        else:
            zoomed_frame = cv2.resize(cropped, (w, h), interpolation=cv2.INTER_LINEAR)

        return zoomed_frame, (x1, y1, x2, y2)

    def draw_face_info(self, frame, face_info, zoom_coords=None):
        top, right, bottom, left = face_info['face_location']

        offset_h = 80
        offset_w = 60

        top = max(0, top - offset_h)
        left = max(0, left - offset_w)
        bottom = min(frame.shape[0], bottom + offset_h)
        right = min(frame.shape[1], right + offset_w)

        if zoom_coords:
            x1, y1, x2, y2 = zoom_coords
            top += y1
            bottom += y1
            left += x1
            right += x1

        color = (0, 255, 0) if face_info['emp_id'] else (0, 0, 255)
        thickness = 4
        cv2.rectangle(frame, (left, top), (right, bottom), color, thickness)

        text_height = 50
        cv2.rectangle(frame, (left, bottom - text_height), (right, bottom), color, cv2.FILLED)

        font = cv2.FONT_HERSHEY_SIMPLEX
        font_scale = 2.0
        font_thickness = 3

        text_bottom_margin = 15
        line_spacing = 30

        text_size = cv2.getTextSize(face_info['name'], font, font_scale, font_thickness)[0]
        text_x = left + (right - left - text_size[0]) // 2
        cv2.putText(frame, face_info['name'],
                    (text_x, bottom - text_bottom_margin - line_spacing),
                    font, font_scale, (0, 0, 0), font_thickness)

        status_text = face_info['status']
        if 'distance' in face_info and face_info['emp_id']:
            status_text += f" ({face_info['distance']:.2f})"

        status_size = cv2.getTextSize(status_text, font, font_scale, font_thickness)[0]
        status_x = left + (right - left - status_size[0]) // 2
        cv2.putText(frame, status_text,
                    (status_x, bottom - text_bottom_margin),
                    font, font_scale, (0, 0, 0), font_thickness)

        if 'photo' in face_info and face_info['photo']:
            try:
                photo_size = 100
                photo = np.array(face_info['photo'].resize((photo_size, photo_size)))
                photo = cv2.cvtColor(photo, cv2.COLOR_RGB2BGR)
                frame[top:top+photo_size, left:left+photo_size] = photo
                cv2.rectangle(frame, (left, top), (left+photo_size, top+photo_size), color, thickness)
            except Exception as e:
                logging.warning(f"Erreur affichage photo: {str(e)}")

    def run(self):
        self.running = True

        video_thread = threading.Thread(target=self.video_capture_thread, daemon=True)
        video_thread.start()

        detect_thread = threading.Thread(target=self.detection_thread, daemon=True)
        detect_thread.start()

        cv2.namedWindow('Reconnaissance Faciale Hikvision', cv2.WINDOW_NORMAL)
        cv2.resizeWindow('Reconnaissance Faciale Hikvision', DISPLAY_WIDTH, DISPLAY_HEIGHT)

        last_detection_time = 0
        last_status_update = 0

        try:
            while self.running:
                start_time = time.time()

                try:
                    frame = self.frame_queue.get(timeout=1)
                except queue.Empty:
                    if not self.camera_connected:
                        logging.warning("En attente de connexion caméra...")
                        time.sleep(1)
                    continue

                zoomed_frame, zoom_coords = self.apply_zoom(frame, self.zoom_factor)

                current_time = time.time()
                if current_time - last_detection_time > FACE_DETECTION_INTERVAL:
                    if not self.detection_queue.full():
                        self.detection_queue.put(zoomed_frame.copy())
                    last_detection_time = current_time

                display_frame = zoomed_frame.copy()

                for idx, info in self.recognition_results.items():
                    if current_time - info['last_seen'] < FACE_DETECTION_INTERVAL * 3:
                        self.draw_face_info(display_frame, info, zoom_coords)

                if current_time - last_status_update > 1:
                    status_text = (f"Personnes: {len(self.recognition_results)} | "
                                   f"FPS: {self.fps:.1f} | "
                                   f"Zoom: {self.zoom_factor:.1f}x | "
                                   f"Caméra: {'OK' if self.camera_connected else 'ERREUR'}")
                    last_status_update = current_time

                cv2.putText(display_frame, status_text, (20, 30),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)

                cv2.putText(display_frame, "Zoom: +/- | Quitter: q", (20, DISPLAY_HEIGHT - 20),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 1)

                cv2.imshow('Reconnaissance Faciale Hikvision', display_frame)

                key = cv2.waitKey(1) & 0xFF
                if key == ord('q'):
                    break
                elif key == ord('+'):
                    self.zoom_factor = min(self.zoom_factor + ZOOM_STEP, 3.0)
                elif key == ord('-'):
                    self.zoom_factor = max(self.zoom_factor - ZOOM_STEP, 1.0)

        except KeyboardInterrupt:
            logging.info("Arrêt demandé par l'utilisateur")
        except Exception as e:
            logging.error(f"Erreur principale: {str(e)}", exc_info=True)
        finally:
            self.running = False
            self.save_config()

            if hasattr(self, 'cap'):
                self.cap.release()
            self.detection_queue.put(None)

            video_thread.join(timeout=2)
            detect_thread.join(timeout=2)

            cv2.destroyAllWindows()
            logging.info("Système arrêté proprement")

if __name__ == '__main__':
    logging.info("Démarrage du système de reconnaissance faciale avec caméra Hikvision")
    system = FaceRecognitionSystem()
    system.run()