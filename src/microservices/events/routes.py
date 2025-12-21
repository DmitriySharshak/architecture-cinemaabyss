from fastapi import APIRouter, Request, status
from models import UserEventModel
import kafka_client
from models import UserEventModel, MovieEventModel, PaymentEventModel, EventResponse
import json
from pydantic import ValidationError


health_check_router = APIRouter()
users_router = APIRouter(tags=["user"])
payments_router = APIRouter(tags=["payment"])
movies_router = APIRouter(tags=["movie"])
routers = [health_check_router, users_router, payments_router, movies_router]


@health_check_router.get("/api/events/health", status_code=status.HTTP_200_OK)
async def check():
    return {"status": True}

@movies_router.api_route("/api/events/movie", methods=["POST"], status_code=status.HTTP_201_CREATED)
async def post_movie_event(req : Request, payload : MovieEventModel):
    await kafka_client.movie_producer.send_and_wait(
        topic="movie-events",
        value=payload.model_dump_json().encode()
    )
    print(f"Event sent for movie_id - {payload.movie_id}")
    return {"status": "success"}

async def consume_movie_event():
    if not kafka_client.movie_consumer:
        raise RuntimeError("Consumer is not initialized")
    try:
        async for msg in kafka_client.movie_consumer:
            event_data = msg.value.decode("utf-8")
            event_jsonify = json.loads(event_data)
            event = MovieEventModel.model_validate(event_jsonify)
            print("Received event:", msg.value)
            response = EventResponse(
                status="success",
                partition=msg.partition,
                offset=msg.offset,
                event=event
            )
            await kafka_client.movie_consumer.commit()
            return response
    except ValidationError as e:
        print(
            f"Validation error at offset {msg.offset}: "
            f"{e.errors()}"
        )
        await kafka_client.movie_consumer.commit()
    finally:
        await kafka_client.movie_consumer.stop()

@users_router.api_route("/api/events/user", methods=["POST"], status_code=status.HTTP_201_CREATED)
async def post_user_event(req : Request, payload : UserEventModel):
    await kafka_client.user_producer.send_and_wait(
        topic="user-events",
        value=payload.model_dump_json().encode()
    )
    print(f"Event sent for user_id - {payload.user_id}")
    return {"status": "success"}

async def consume_user_event():
    if not kafka_client.user_consumer:
        raise RuntimeError("Consumer is not initialized")
    try:
        async for msg in kafka_client.user_consumer:
            event_data = msg.value.decode("utf-8")
            event_jsonify = json.loads(event_data)
            event = UserEventModel.model_validate(event_jsonify)
            print("Received event:", msg.value)
            response = EventResponse(
                status="success",
                partition=msg.partition,
                offset=msg.offset,
                event=event
            )
            await kafka_client.user_consumer.commit()
            return response
    except ValidationError as e:
        print(
            f"Validation error at offset {msg.offset}: "
            f"{e.errors()}"
        )
        await kafka_client.user_consumer.commit()
    finally:
        await kafka_client.user_consumer.stop()


@payments_router.api_route("/api/events/payment", methods=["POST"], status_code=status.HTTP_201_CREATED)
async def post_payment_event(req : Request, payload : PaymentEventModel):
    await kafka_client.payment_producer.send_and_wait(
        topic="payment-events",
        value=payload.model_dump_json().encode()
    )
    print(f"Event sent for payment_id - {payload.payment_id}")
    return {"status": "success"}

async def consume_payment_event():
    if not kafka_client.payment_consumer:
        raise RuntimeError("Consumer is not initialized")
    try:
        async for msg in kafka_client.payment_consumer:
            event_data = msg.value.decode("utf-8")
            event_jsonify = json.loads(event_data)
            event = PaymentEventModel.model_validate(event_jsonify)
            print("Received event:", msg.value)
            response = EventResponse(
                status="success",
                partition=msg.partition,
                offset=msg.offset,
                event=event
            )
            await kafka_client.payment_consumer.commit()
            return response
    except ValidationError as e:
        print(
            f"Validation error at offset {msg.offset}: "
            f"{e.errors()}"
        )
        await kafka_client.payment_consumer.commit()
    finally:
        await kafka_client.payment_consumer.stop()
