import re
import re
import chromadb
from chromadb.utils import embedding_functions
import time
from datetime import datetime
import locale
import requests
from fuzzywuzzy import fuzz
import logging

# Configuration du logger simplifiée comme dans le notebook
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)
EMPLOYEE_SERVICE_URL = "http://employe-service:8081/Employes"


def extract_date_from_query(query):
    """Convertit les dates françaises en format ISO (YYYY-MM-DD)"""
    mois_fr_to_num = {
        'janvier': '01', 'février': '02', 'mars': '03', 'avril': '04',
        'mai': '05', 'juin': '06', 'juillet': '07', 'août': '08',
        'septembre': '09', 'octobre': '10', 'novembre': '11', 'décembre': '12'
    }

    # Format français (13 février 2025)
    match_fr = re.search(r'(\d{1,2})\s+([a-zéû]+)\s+(\d{4})', query.lower())
    if match_fr:
        jour, mois, annee = match_fr.groups()
        if mois in mois_fr_to_num:
            return f"{annee}-{mois_fr_to_num[mois]}-{int(jour):02d}"

    # Format ISO (2025-02-13)
    match_iso = re.search(r'(\d{4})-(\d{2})-(\d{2})', query)
    if match_iso:
        return match_iso.group(0)

    # Format court (13/02/2025)
    match_short = re.search(r'(\d{2})/(\d{2})/(\d{4})', query)
    if match_short:
        return f"{match_short.group(3)}-{match_short.group(2)}-{match_short.group(1)}"

    return None

class EmployeeService:
    def __init__(self):
        self.employees = self._fetch_employees()

    def _fetch_employees(self):
        try:
            response = requests.get(EMPLOYEE_SERVICE_URL, timeout=3)
            response.raise_for_status()
            employees_data = response.json()
            return [f"{emp['prenom']} {emp['nom']}" for emp in employees_data]
        except Exception as e:
            print(f"⚠️ Erreur de récupération des employés: {e}")
            return []  # Retourne une liste vide si échec

class RAGSystem:
    def __init__(self, ai_agent, collection, default_num_retrieved_docs=2):
        self.default_num_docs = default_num_retrieved_docs
        self.collection = collection
        self.ai_agent = ai_agent
        self.employee_service = EmployeeService()





    def _is_listing_query(self, query):
        """Détecte si la requête demande une liste/rapport complet"""
        query_lower = query.lower()
        return any(kw in query_lower for kw in [
            "liste", "tous", "anomalies", "rapports",
            "lister", "donner les", "afficher les", "historique"
        ])

    def _needs_employee_filter(self, query):
        """Détermine si la requête nécessite un filtrage par employé"""
        return self._is_listing_query(query) and any(
            kw in query.lower() for kw in ["employé", "de ", "pour ", "du "]
        )

    def _extract_employee(self, query):
        """Extrait le nom de l'employé avec matching flou"""
        if not self._needs_employee_filter(query):
            return None

        query_clean = re.sub(r'[^\wéèêûîïç]', ' ', query.lower()).strip()

        # 1. Matching exact
        for emp in self.employee_service.employees:
            if emp.lower() in query.lower():
                return emp

        # 2. Matching par prénom
        first_names = {e.split()[0].lower(): e for e in self.employee_service.employees}
        for word in query_clean.split():
            if word in first_names:
                return first_names[word]

        # 3. Fuzzy matching
        best_match, best_score = None, 0
        for emp in self.employee_service.employees:
            score = fuzz.token_set_ratio(emp.lower(), query_clean)
            if score > best_score and score > 75:
                best_match, best_score = emp, score

        return best_match

    def determine_num_docs(self, query):
        """Détermine le nombre de documents à récupérer"""
        return 15 if self._is_listing_query(query) else self.default_num_docs

    def build_filters(self, type_filter, date, employe=None):
        """Construit les filtres pour ChromaDB"""
        filters = {}
        if type_filter:
            filters["type"] = type_filter
        if date:
            filters["date"] = date
        if employe:
            filters["employe"] = employe

        if not filters:
            return None

        return (
            filters if len(filters) == 1
            else {"$and": [{k: {"$eq": v}} for k, v in filters.items()]}
        )

    def retrieve(self, query, type_filter=None):
        """Récupère les documents avec filtres intelligents"""
        num_docs = self.determine_num_docs(query)
        date = extract_date_from_query(query)
        employe = self._extract_employee(query)

        print(f"\n🔍 Paramètres de recherche:")
        print(f"- Type: {type_filter or 'Aucun'}")
        print(f"- Date: {date or 'Aucune'}")
        print(f"- Employé: {employe or 'Aucun'}")
        print(f"- Nombre docs: {num_docs}")

        results = self.collection.query(
            query_texts=[query],
            n_results=num_docs,
            where=self.build_filters(type_filter, date, employe)
        )
        docs = results['documents'][0] if results['documents'] else ["Aucune donnée correspondante trouvée"]
        print(f"📄 Documents récupérés ({len(docs)}):")
        for i, doc in enumerate(docs, 1):
            print(f"{i}. {doc[:100]}...")


        return " ".join(results['documents'][0]) if results['documents'] else "Aucune donnée trouvée"

    def query(self, query, type_filter=None, query_type=None):
        """Exécute une requête complète"""
        # Utilise query_type si type_filter n'est pas spécifié
        effective_type_filter = type_filter if type_filter is not None else query_type
        context = self.retrieve(query, effective_type_filter)
        return self.ai_agent.generate(query, context)