#!/bin/bash

echo "==================================="
echo "Waiting for Kafka to be ready..."
echo "==================================="

# Ждём пока Kafka станет доступен
until kafka-broker-api-versions --bootstrap-server kafka:9092 &> /dev/null; do
  echo "Kafka is unavailable - sleeping"
  sleep 2
done

echo "==================================="
echo "Kafka is ready! Creating topics..."
echo "==================================="

# Создаём топики
kafka-topics --create --if-not-exists \
  --topic movie-events \
  --bootstrap-server kafka:9092 \
  --partitions 1 \
  --replication-factor 1

kafka-topics --create --if-not-exists \
  --topic user-events \
  --bootstrap-server kafka:9092 \
  --partitions 1 \
  --replication-factor 1

kafka-topics --create --if-not-exists \
  --topic payment-events \
  --bootstrap-server kafka:9092 \
  --partitions 1 \
  --replication-factor 1

echo "==================================="
echo "Topics created successfully!"
echo "==================================="

# Показываем список топиков
kafka-topics --list --bootstrap-server kafka:9092

echo "==================================="
echo "Init script completed!"
echo "==================================="