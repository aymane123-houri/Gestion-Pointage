
import requests
import pandas as pd
import chromadb
import json
import uuid
import re
from datetime import datetime, date
from chromadb.utils import embedding_functions
import dateparser
import locale
import numpy as np
import ast
import logging
from apscheduler.schedulers.blocking import BlockingScheduler
from tenacity import retry, stop_after_attempt, wait_fixed

# Configuration des logs
logging.basicConfig(
    filename="C:/Users/ultra/Desktop/Projets/Test-FSTT_chatbot-main/Data/chromadb_update.log",
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s"
)

# Configuration
EMPLOYE_SERVICE_URL = "http://localhost:8081/Employes"
POINTAGE_SERVICE_URL = "http://localhost:8083/Pointages"
HORAIRE_SERVICE_URL = "http://localhost:8084/Horaires"
RAPPORT_SERVICE_URL = "http://localhost:8085/Rapports"
ANOMALIE_SERVICE_URL = "http://localhost:8086/Anomalie"
CHROMA_PATH = "C:/Users/ultra/Desktop/chroma_db"
COLLECTION_NAME = "Pointage_Data3_filtrage"
EMBEDDING_MODEL = "sentence-transformers/all-mpnet-base-v2"

# Configurer la locale française
try:
    locale.setlocale(locale.LC_TIME, "fr_FR.UTF-8")
except locale.Error:
    logging.warning("Locale française non disponible, utilisation par défaut")

@retry(stop=stop_after_attempt(3), wait=wait_fixed(2))
def fetch_data_from_microservice(url, date_filter=None):
    """Récupère les données depuis un microservice avec un filtre de date optionnel"""
    try:
        if date_filter:
            response = requests.get(f"{url}?date={date_filter}", timeout=10)
        else:
            response = requests.get(url, timeout=10)
        response.raise_for_status()
        data = response.json()
        logging.info(f"Données récupérées depuis {url}: {len(data)} éléments")
        return data
    except Exception as e:
        logging.error(f"Erreur récupération données {url}: {e}")
        return []

def preprocessing(text):
    """Nettoie le texte et vérifie s'il est en français"""
    if pd.isna(text) or text is None:
        return ''
    text = str(text).lower()
    text = re.sub(r'@\w+', ' ', text)
    text = re.sub(r'\xa0', ' ', text)
    text = re.sub(r'\n', ' ', text)
    text = re.sub(r'«|»|“|”|’|‘', ' ', text)
    try:
        from langdetect import detect
        return text if detect(text) == 'fr' else ''
    except Exception as e:
        logging.warning(f"Erreur détection langue: {e}")
        return text

def clean_encoding_issues(df, columns):
    """Corrige les problèmes d'encodage dans les colonnes spécifiées"""
    replacements = {
        'Ã‰': 'É', 'Ã©': 'é', 'Ã¨': 'è', 'Ãª': 'ê', 'Ã«': 'ë', 'Ã€': 'À', 'Ã¡': 'á',
        'Ã¢': 'â', 'Ã£': 'ã', 'Ã¤': 'ä', 'Ã§': 'ç', 'Ã¹': 'ù', 'Ã»': 'û', 'Ã¼': 'ü',
        'Ã´': 'ô', 'Ã¶': 'ö', 'Ã¯': 'ï', 'Ã¬': 'ì', 'Ã­': 'í', 'Ã®': 'î', 'â€™': "'",
        'â€“': '–', 'â€”': '—', 'â€¦': '…', 'â€˜': '‘', 'â€™': '’', 'â€œ': '“', 'â€': '”',
        'ï¿½': 'é', 'fÃ©vrier': 'février', 'aoÃ»t': 'août', 'dÃ©cembre': 'décembre'
    }
    for column in columns:
        if column in df.columns:
            for wrong, correct in replacements.items():
                df[column] = df[column].str.replace(wrong, correct, regex=False)
    return df

def safe_convert(value):
    """Convertit les valeurs en types compatibles avec ChromaDB"""
    if pd.isna(value) or value is None:
        return "Inconnu"
    if isinstance(value, (dict, list)):
        try:
            return json.dumps(value, ensure_ascii=False)
        except:
            return str(value)
    if isinstance(value, pd.Timestamp):
        return value.strftime('%Y-%m-%d')
    if isinstance(value, float) and np.isnan(value):
        return "Inconnu"
    return str(value)

def format_date(date_str):
    """Formatte une date en 'jour mois année'"""
    try:
        return datetime.strptime(date_str, "%Y-%m-%d").strftime("%d %B %Y")
    except:
        return "Date non spécifiée"

def process_pointages(df_pointages, collection):
    """Traite et stocke les nouveaux pointages dans ChromaDB"""
    ids = []
    metadatas = []
    documents = []

    for idx, row in df_pointages.iterrows():
        try:
            employe_data = json.loads(row['employe']) if isinstance(row['employe'], str) else row.get('employe', {})
            date_entree = pd.to_datetime(row['dateHeureEntree'])
            date_sortie = pd.to_datetime(row['dateHeureSortie'], errors='coerce')
            duree = (date_sortie - date_entree).total_seconds() / 60 if pd.notna(date_sortie) else 0

            metadata = {
                "type": "POINTAGE",
                "pointage_id": safe_convert(row.get('id')),
                "employe_id": safe_convert(row.get('employeId')),
                "employe": f"{employe_data.get('prenom', '')} {employe_data.get('nom', '')}",
                "date": date_entree.strftime('%Y-%m-%d'),
                "heure_entree": date_entree.strftime('%H:%M'),
                "heure_sortie": date_sortie.strftime('%H:%M') if pd.notna(date_sortie) else "Inconnu",
                "duree_minutes": int(duree)
            }

            for column in ["merged_content1"]:
                if column in row and row[column].strip():
                    doc_id = f"pointage_{row['id']}_{column}"
                    content = row[column].strip()
                    ids.append(doc_id)
                    metadatas.append(metadata)
                    documents.append(content)

                    logging.info(f"Pointage ID: {doc_id}, Employé: {metadata['employe']}")
                    print(f"Document ID: {doc_id}")
                    print(f"Metadata: {json.dumps(metadata, indent=2, ensure_ascii=False)}")
                    print(f"Document Content: {content[:200]}...")
        except Exception as e:
            logging.error(f"Erreur ligne {idx} (pointages): {e}")
            continue

    if ids:
        collection.upsert(ids=ids, metadatas=metadatas, documents=documents)
        logging.info(f"{len(ids)} pointages ajoutés à ChromaDB")
        print(f"✅ {len(ids)} pointages ajoutés")
    else:
        logging.info("Aucun pointage à ajouter")
        print("⏭ Aucun pointage valide")

def process_anomalies(df_anomalies, collection):
    """Traite et stocke les nouvelles anomalies dans ChromaDB"""
    ids = []
    metadatas = []
    documents = []

    for idx, row in df_anomalies.iterrows():
        try:
            employe_data = json.loads(row['employe']) if isinstance(row['employe'], str) else row.get('employe', {})
            date_validation = pd.to_datetime(row['dateValidation'], errors='coerce')

            metadata = {
                "type": "ANOMALIE",
                "anomalie_type": row.get('type', '').upper(),
                "employe": f"{employe_data.get('prenom', '')} {employe_data.get('nom', '')}",
                "date": date_validation.strftime('%Y-%m-%d') if pd.notna(date_validation) else "Inconnu",
                "description": row.get('description', '')[:200]
            }

            for column in ["merged_content1"]:
                if column in row and row[column].strip():
                    doc_id = f"anomalie_{row['id']}_{column}"
                    content = row[column].strip()
                    ids.append(doc_id)
                    metadatas.append(metadata)
                    documents.append(content)

                    logging.info(f"Anomalie ID: {doc_id}, Employé: {metadata['employe']}")
                    print(f"Document ID: {doc_id}")
                    print(f"Metadata: {json.dumps(metadata, indent=2, ensure_ascii=False)}")
                    print(f"Document Content: {content[:200]}...")
        except Exception as e:
            logging.error(f"Erreur ligne {idx} (anomalies): {e}")
            continue

    if ids:
        collection.upsert(ids=ids, metadatas=metadatas, documents=documents)
        logging.info(f"{len(ids)} anomalies ajoutées à ChromaDB")
        print(f"✅ {len(ids)} anomalies ajoutées")
    else:
        logging.info("Aucune anomalie à ajouter")
        print("⏭ Aucune anomalie valide")

def process_rapports(df_rapports, collection):
    """Traite et stocke les nouveaux rapports quotidiens dans ChromaDB"""
    ids = []
    metadatas = []
    documents = []

    for idx, row in df_rapports.iterrows():
        try:
            employe = json.loads(row['employe']) if isinstance(row['employe'], str) else row.get('employe', {})
            daily_reports = [r.strip() for r in str(row['merged_content5']).split("|") if r.strip()]

            for daily_report in daily_reports:
                details = extract_daily_details(daily_report)
                date_iso = details['date_iso'] or row.get('periode', '')

                metadata = {
                    "type": "Rapport_Quotidien",
                    "employe": f"{employe.get('prenom', '')} {employe.get('nom', '')}",
                    "date": date_iso,
                    "heures_travaillees": details['heures_travaillees'],
                    "heures_supplementaires": details['heures_supplementaires'],
                    "en_retard": details['en_retard'],
                    "absent": details['absent']
                }

                doc_id = f"rapport_{row['id']}_{date_iso}"
                ids.append(doc_id)
                metadatas.append(metadata)
                documents.append(daily_report)

                logging.info(f"Rapport ID: {doc_id}, Employé: {metadata['employe']}")
                print(f"Document ID: {doc_id}")
                print(f"Metadata: {json.dumps(metadata, indent=2, ensure_ascii=False)}")
                print(f"Document Content: {daily_report[:200]}...")
        except Exception as e:
            logging.error(f"Erreur ligne {idx} (rapports): {e}")
            continue

    if ids:
        collection.upsert(ids=ids, metadatas=metadatas, documents=documents)
        logging.info(f"{len(ids)} rapports quotidiens ajoutés à ChromaDB")
        print(f"✅ {len(ids)} rapports quotidiens ajoutés")
    else:
        logging.info("Aucun rapport à ajouter")
        print("⏭ Aucun rapport valide")

def extract_daily_details(daily_report):
    """Extrait les détails du rapport quotidien"""
    date_complete = None
    if 'rapport du' in daily_report.lower():
        date_part = daily_report.lower().split('rapport du')[-1].split('pour')[0].strip()
        date_complete = ' '.join(date_part.split()[:3])

    details = {
        'heures_travaillees': 0.0,
        'heures_supplementaires': 0.0,
        'en_retard': 'retard' in daily_report.lower(),
        'absent': 'absent' in daily_report.lower(),
        'date_complete': date_complete,
        'date_iso': convert_to_iso_date(date_complete) if date_complete else None
    }

    try:
        if 'travaillé' in daily_report:
            parts = daily_report.split('travaillé')[-1].split('heures')[0].strip()
            details['heures_travaillees'] = float(parts.split()[0])
        if 'supplémentaires' in daily_report:
            parts = daily_report.split('supplémentaires')[-1].split('heures')[0].strip()
            details['heures_supplementaires'] = float(parts.split()[-1])
    except Exception as e:
        logging.warning(f"Erreur extraction détails rapport: {e}")

    return details

def convert_to_iso_date(date_str):
    """Convertit une date en français en format ISO"""
    try:
        date_obj = dateparser.parse(date_str, languages=['fr'])
        return date_obj.strftime('%Y-%m-%d') if date_obj else None
    except Exception as e:
        logging.warning(f"Erreur conversion date: {e}")
        return None

def main():
    """Pipeline de mise à jour quotidienne"""
    logging.info(f"Début mise à jour ChromaDB: {datetime.now()}")
    today = date.today().strftime('%Y-%m-%d')

    # Initialiser ChromaDB
    try:
        chroma_client = chromadb.PersistentClient(path=CHROMA_PATH)
        collection = chroma_client.get_or_create_collection(
            name=COLLECTION_NAME,
            embedding_function=embedding_functions.SentenceTransformerEmbeddingFunction(model_name=EMBEDDING_MODEL)
        )
    except Exception as e:
        logging.error(f"Erreur initialisation ChromaDB: {e}")
        return

    # Récupérer les données du jour
    pointages = fetch_data_from_microservice(POINTAGE_SERVICE_URL, date_filter=today)
    anomalies = fetch_data_from_microservice(ANOMALIE_SERVICE_URL, date_filter=today)
    rapports = fetch_data_from_microservice(RAPPORT_SERVICE_URL, date_filter=today)

    # Convertir en DataFrames
    df_pointages = pd.DataFrame(pointages)
    df_anomalies = pd.DataFrame(anomalies)
    df_rapports = pd.DataFrame(rapports)

    # Prétraitement des pointages
    if not df_pointages.empty:
        df_pointages['dateHeureEntree'] = pd.to_datetime(df_pointages['dateHeureEntree'], errors='coerce')
        df_pointages['dateHeureSortie'] = pd.to_datetime(df_pointages['dateHeureSortie'], errors='coerce')
        df_pointages['merged_content1'] = df_pointages.apply(
            lambda row: (
                f"Le {datetime.strptime(str(row.get('dateHeureEntree', 'Non spécifié'))[:10], '%Y-%m-%d').strftime('%d %B %Y')}, "
                f"l'employé {row.get('employe', {}).get('nom', 'Inconnu')} {row.get('employe', {}).get('prenom', 'Inconnu')} "
                f"a pointé l'entrée à {str(row.get('dateHeureEntree', 'Non spécifié'))[11:]} "
                f"et il est sorti à {str(row.get('dateHeureSortie', 'Non spécifié'))[11:] if row.get('dateHeureSortie') else 'Non spécifié'}."
            ),
            axis=1
        ).apply(preprocessing)
        df_pointages = clean_encoding_issues(df_pointages, ['merged_content1'])
        process_pointages(df_pointages, collection)

    # Prétraitement des anomalies
    if not df_anomalies.empty:
        df_anomalies['merged_content1'] = df_anomalies.apply(
            lambda row: (
                f"Le {datetime.strptime(str(row.get('dateValidation', 'Non spécifié'))[:10], '%Y-%m-%d').strftime('%d %B %Y')}, "
                f"une anomalie de type '{row.get('type', 'Non spécifié')}' a été détectée "
                f"pour l'employé {row.get('employe', {}).get('nom', 'Inconnu')} {row.get('employe', {}).get('prenom', 'Inconnu')}. "
                f"Détails : {row.get('description', 'Aucune description fournie')}."
            ),
            axis=1
        ).apply(preprocessing)
        df_anomalies = clean_encoding_issues(df_anomalies, ['merged_content1'])
        process_anomalies(df_anomalies, collection)

    # Prétraitement des rapports
    if not df_rapports.empty:
        df_rapports['merged_content5'] = df_rapports.apply(
            lambda row: (
                    " " +
                    " | ".join([
                        f"Le rapport du {format_date(detail.get('jour', 'Non spécifié'))} "
                        f"pour l'employé {row.get('employe', {}).get('nom', 'Inconnu')} "
                        f"{row.get('employe', {}).get('prenom', 'Inconnu')} "
                        f"a travaillé {detail.get('heuresTravaillees', 0.0)} heures, "
                        f"avec {detail.get('heuresSupplementaires', 0.0)} heures supplémentaires. "
                        f"{'Il a eu un retard.' if detail.get('enRetard', False) else ''} "
                        f"{'Il est absent ce jour-là.' if detail.get('heuresTravaillees', 0.0) == 0.0 else ''}"
                        for detail in row.get('details', [])
                    ])
            ),
            axis=1
        ).apply(preprocessing)
        df_rapports = clean_encoding_issues(df_rapports, ['merged_content5'])
        process_rapports(df_rapports, collection)

    logging.info(f"Fin mise à jour ChromaDB: {datetime.now()}")

if __name__ == "__main__":
    # Exécuter une fois immédiatement pour tester
    main()

    # Planifier l'exécution quotidienne à minuit
    scheduler = BlockingScheduler()
    scheduler.add_job(main, 'cron', hour=0, minute=0)
    try:
        logging.info("Démarrage du planificateur APScheduler")
        scheduler.start()
    except (KeyboardInterrupt, SystemExit):
        logging.info("Arrêt du planificateur")
