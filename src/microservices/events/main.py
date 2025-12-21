from fastapi import FastAPI
import uvicorn
from routes import routers, consume_user_event, consume_movie_event, consume_payment_event
from aiokafka import AIOKafkaProducer, AIOKafkaConsumer
from aiokafka.errors import KafkaConnectionError
import kafka_client
from contextlib import asynccontextmanager
import os
from dotenv import load_dotenv
from pathlib import Path
import asyncio


load_dotenv('architecture-cinemaabyss/.env')
KAFKA_SERVICE= os.getenv('KAFKA_SERVICE')
KAFKA_PORT = os.getenv('KAFKA_PORT')

@asynccontextmanager
async def lifespan(app: FastAPI):
    # =========================
    # STARTUP
    # =========================
    try:
        # USER
        kafka_client.user_producer = AIOKafkaProducer(bootstrap_servers="kafka:9092")
        await kafka_client.user_producer.start()

        kafka_client.user_consumer = AIOKafkaConsumer(
            "user-events",
            bootstrap_servers="kafka:9092",
            group_id="user-service",
            auto_offset_reset="earliest",
        )
        await kafka_client.user_consumer.start()
        asyncio.create_task(consume_user_event())

        # MOVIE
        kafka_client.movie_producer = AIOKafkaProducer(bootstrap_servers="kafka:9092")
        await kafka_client.movie_producer.start()

        kafka_client.movie_consumer = AIOKafkaConsumer(
            "movie-events",
            bootstrap_servers="kafka:9092",
            group_id="movie-service",
            auto_offset_reset="earliest",
        )
        await kafka_client.movie_consumer.start()
        asyncio.create_task(consume_movie_event())

        # PAYMENT
        kafka_client.payment_producer = AIOKafkaProducer(bootstrap_servers="kafka:9092")
        await kafka_client.payment_producer.start()

        kafka_client.payment_consumer = AIOKafkaConsumer(
            "payment-events",
            bootstrap_servers="kafka:9092",
            group_id="payment-service",
            auto_offset_reset="earliest",
        )
        await kafka_client.payment_consumer.start()
        asyncio.create_task(consume_payment_event())

        print("Kafka connected successfully")

    except KafkaConnectionError as e:
        print(f"Kafka connection failed: {e}")
        raise

    # ⬅️ РОВНО ОДИН yield
    yield

    # =========================
    # SHUTDOWN
    # =========================
    await kafka_client.user_consumer.stop()
    await kafka_client.user_producer.stop()

    await kafka_client.movie_consumer.stop()
    await kafka_client.movie_producer.stop()

    await kafka_client.payment_consumer.stop()
    await kafka_client.payment_producer.stop()

    print("Kafka disconnected")


app = FastAPI(lifespan=lifespan)
for router in routers:
    app.include_router(router)

# if __name__ == "__main__":
#     uvicorn.run(app, host="0.0.0.0", port=8082)