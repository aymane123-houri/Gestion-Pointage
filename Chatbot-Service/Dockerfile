# Utiliser une image Python officielle comme base
FROM python:3.9-slim

# Installer les dépendances système nécessaires à dlib
RUN apt-get update && apt-get install -y --no-install-recommends \
    cmake \
    build-essential \
    libboost-all-dev \
    libopenblas-dev \
    libatlas-base-dev \
    libhdf5-dev \
    libhdf5-serial-dev \
    libhdf5-103 \
    python3-dev \
    && apt-get clean && rm -rf /var/lib/apt/lists/*

# Définir un répertoire de travail dans le conteneur
WORKDIR /app

# Copier d'abord requirements.txt pour bénéficier du cache Docker
COPY requirements.txt .

# Installer les dépendances Python (avec timeout et mirror plus rapide)
RUN pip install --no-cache-dir --timeout=100 -r requirements.txt -i https://pypi.org/simple

# Ensuite copier le reste du code
COPY . .

# Définir la commande par défaut pour exécuter l'application
CMD ["python", "app.py"]
