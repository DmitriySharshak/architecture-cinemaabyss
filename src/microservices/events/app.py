import threading

from flask import Flask, request, jsonify
from confluent_kafka import Producer, Consumer
from pydantic import  ValidationError
from models import MovieEvent
import json

from models import UserEvent, PaymentEvent

app = Flask(__name__)

producer = Producer({'bootstrap.servers': 'kafka:9092'})

BASE = "/api/events"

@app.route(f"{BASE}/health", methods=['GET'])
def health():
    return jsonify({"status": True}), 200

@app.route(f"{BASE}/movie", methods=['POST'])
def create_movie_event():
    try:
        event = MovieEvent(**request.json)
    except ValidationError as e:
        return jsonify({"error": e.errors()}), 400
    except json.JSONDecodeError:
        return jsonify({"error": "Invalid JSON in 'raw' field"}), 400

    producer.produce('movie-events', event.json())
    producer.flush()

    return jsonify({"status": "success"}), 201

@app.route(f"{BASE}/user", methods=['POST'])
def create_user_event():
    try:
        event = UserEvent(**request.json)
    except ValidationError as e:
        return jsonify({"error": e.errors()}), 400
    except json.JSONDecodeError:
        return jsonify({"error": "Invalid JSON in 'raw' field"}), 400

    producer.produce('user-events', event.json())
    producer.flush()

    return jsonify({"status": "success"}), 201

@app.route(f"{BASE}/payment", methods=['POST'])
def create_payment_event():
    try:
        event = PaymentEvent(**request.json)
    except ValidationError as e:
        return jsonify({"error": e.errors()}), 400
    except json.JSONDecodeError:
        return jsonify({"error": "Invalid JSON in 'raw' field"}), 400

    producer.produce('payment-events', event.json())
    producer.flush()

    return jsonify({"status": "success"}), 201

# ---------------- Kafka Consumers ---------------- #

def consume_topic(topic_name: str):
    consumer = Consumer({
        'bootstrap.servers': 'kafka:9092',
        'group.id': f'{topic_name}-listener',
        'auto.offset.reset': 'earliest'
    })
    consumer.subscribe([topic_name])
    print(f"[*] Listening to topic '{topic_name}'")

    while True:
        msg = consumer.poll(1.0)
        if msg is None:
            continue
        if msg.error():
            print(f"Consumer error: {msg.error()}")
            continue

        print(f"[{topic_name}] Received message: {msg.value().decode('utf-8')}")

for topic in ['movie-events', 'user-events', 'payment-events']:
    thread = threading.Thread(target=consume_topic, args=(topic,), daemon=True)
    thread.start()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8082)
