# Utiliser une image Python officielle comme base
FROM python:3.9-slim

# Installer les dépendances système nécessaires à dlib et OpenCV
RUN apt-get update && apt-get install -y \
    cmake \
    build-essential \
    libboost-all-dev \
    libopenblas-dev \
    libatlas-base-dev \
    libhdf5-dev \
    libhdf5-serial-dev \
    libhdf5-103 \
    python3-dev \
    libgl1 \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Définir un répertoire de travail dans le conteneur
WORKDIR /app

# Copier les fichiers de votre application dans le conteneur
COPY . /app

# Installer les dépendances Python
RUN pip install --no-cache-dir -r requirements.txt

# Définir la commande par défaut
CMD ["python", "app.py"]
