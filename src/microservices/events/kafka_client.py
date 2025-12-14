from aiokafka import AIOKafkaProducer, AIOKafkaConsumer


movie_producer: AIOKafkaProducer | None = None
movie_consumer: AIOKafkaConsumer | None = None

user_producer: AIOKafkaProducer | None = None
user_consumer: AIOKafkaConsumer | None = None

payment_producer: AIOKafkaProducer | None = None
payment_consumer: AIOKafkaConsumer | None = None
