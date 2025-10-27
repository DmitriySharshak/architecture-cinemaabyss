import os
import random
from itertools import cycle

from flask import Flask, jsonify
import requests

app = Flask(__name__)

MONOLITH_URL= os.getenv("MONOLITH_URL", "http://monolith:8080")
MOVIES_URL= os.getenv("MOVIES_SERVICE_URL","http://movies-service:8081")
MOVIES_MIGRATION_PERCENT= int(os.getenv("MOVIES_MIGRATION_PERCENT", 50))

# URL у нас работает по принципу Round Robin, значение берем из MOVIES_MIGRATION_PERCENT
urls = [MOVIES_URL] * MOVIES_MIGRATION_PERCENT + [MONOLITH_URL] * (100 - MOVIES_MIGRATION_PERCENT)
random.shuffle(urls)
round_robin = cycle(urls)

@app.route("/health", methods=['GET'])
def health():
    return jsonify({"status": True}), 200

@app.route('/api/movies', methods=['GET'])
def get_movies_via_proxy():
    try:
        host_url = next(round_robin)
        print(f'get movies from {host_url}', flush=True)
        r = requests.get(f"{host_url}/api/movies", timeout=5)
        response = r.json()
    except requests.RequestException as e:
        return jsonify({"error": str(e)}), 500

    return jsonify(response), 200

@app.route('/api/users', methods=['GET'])
def get_users_via_proxy():
    try:
        r = requests.get(f"{MONOLITH_URL}/api/users", timeout=5)
        response = r.json()
    except requests.RequestException as e:
        return jsonify({"error": str(e)}), 500

    return jsonify(response), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8000)