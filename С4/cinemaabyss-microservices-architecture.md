```puml
@startuml CinemaAbyss Microservices Architecture
!include https://raw.githubusercontent.com/plantuml-stdlib/C4-PlantUML/master/C4_Container.puml

title CinemaAbyss - Microservices Architecture with Async Communication

Person(user, "Пользователь", "Клиент приложения")
Person(admin, "Администратор", "Управление контентом")

System_Boundary(cinemaabyss, "CinemaAbyss Platform") {
    
    Container_Boundary(api_gateway, "API Gateway Layer") {
        Container(api_gateway_service, "API Gateway", "Kong/Nginx", "Маршрутизация, аутентификация, агрегация данных, оптимизация под устройства")
        Container(device_detector, "Device Detector", "Go/Node.js", "Определение типа устройства и оптимизация контента")
    }
    
    Container_Boundary(microservices, "Microservices Layer") {
        Container(user_service, "User Service", "Go", "Аутентификация, авторизация, профили пользователей")
        Container(content_service, "Content Service", "Go", "Фильмы, метаданные, жанры, поиск")
        Container(payment_service, "Payment Service", "Go", "Подписки, скидки, транзакции, биллинг")
        Container(recommendation_service, "Recommendation Service", "Python/Go", "ML-алгоритмы, персонализация, аналитика")
        Container(event_service, "Event Service", "Go", "Обработка событий, логирование, мониторинг")
    }
    
    Container_Boundary(async_communication, "Async Communication Layer") {
        Container(kafka, "Apache Kafka", "Kafka", "Message Broker для асинхронной коммуникации")
        Container(zookeeper, "Zookeeper", "Zookeeper", "Координация Kafka кластера")
    }
    
    Container_Boundary(data_layer, "Data Layer") {
        Container(user_db, "User Database", "PostgreSQL", "Пользователи, аутентификация, сессии")
        Container(content_db, "Content Database", "PostgreSQL", "Фильмы, метаданные, жанры")
        Container(payment_db, "Payment Database", "PostgreSQL", "Платежи, подписки, транзакции")
        Container(recommendation_db, "Recommendation Database", "PostgreSQL", "Рекомендации, предпочтения")
        Container(elasticsearch, "Elasticsearch", "Elasticsearch", "Поиск и индексация контента")
        Container(redis, "Redis Cache", "Redis", "Кэширование, сессии, временные данные")
    }
    
    Container_Boundary(monitoring, "Monitoring & Observability") {
        Container(prometheus, "Prometheus", "Prometheus", "Метрики и мониторинг")
        Container(grafana, "Grafana", "Grafana", "Визуализация метрик")
        Container(jaeger, "Jaeger", "Jaeger", "Distributed tracing")
    }
}

' User interactions
user --> api_gateway_service : "HTTPS/REST/GraphQL"
admin --> api_gateway_service : "HTTPS/REST/GraphQL"

' API Gateway routing
api_gateway_service --> device_detector : "Device detection"
api_gateway_service --> user_service : "User operations"
api_gateway_service --> content_service : "Content operations"
api_gateway_service --> payment_service : "Payment operations"
api_gateway_service --> recommendation_service : "Recommendations"

' Synchronous communication
user_service --> user_db : "SQL queries"
content_service --> content_db : "SQL queries"
content_service --> elasticsearch : "Search queries"
payment_service --> payment_db : "SQL queries"
recommendation_service --> recommendation_db : "SQL queries"
recommendation_service --> redis : "Cache operations"

' Asynchronous communication via Kafka
user_service --> kafka : "Publish: user.created, user.updated, user.deleted"
content_service --> kafka : "Publish: movie.created, movie.viewed, movie.updated"
payment_service --> kafka : "Publish: payment.created, subscription.created"
recommendation_service --> kafka : "Publish: recommendation.generated"

kafka --> event_service : "Consume: all events"
kafka --> recommendation_service : "Consume: user.preference.updated, movie.viewed"
kafka --> content_service : "Consume: user.rating.created"

' Event processing
event_service --> kafka : "Event processing & routing"
event_service --> prometheus : "Metrics collection"

' Monitoring
prometheus --> grafana : "Metrics data"
jaeger --> user_service : "Tracing"
jaeger --> content_service : "Tracing"
jaeger --> payment_service : "Tracing"
jaeger --> recommendation_service : "Tracing"

' Infrastructure
kafka --> zookeeper : "Coordination"

' Device optimization
device_detector --> content_service : "Device-specific content requests"
device_detector --> recommendation_service : "Device-optimized recommendations"

note right of api_gateway_service
  **Device Optimization:**
  - Mobile: сжатые изображения, упрощенный UI
  - Desktop: полное качество, расширенный функционал
  - TV: большие элементы, простое управление
  - Tablet: адаптивный дизайн
end note

note right of kafka
  **Event Topics:**
  - user-events: user.created, user.updated, user.deleted
  - content-events: movie.created, movie.viewed, movie.rated
  - payment-events: payment.created, subscription.created
  - recommendation-events: recommendation.generated
end note

note right of recommendation_service
  **ML Features:**
  - Collaborative filtering
  - Content-based filtering
  - Hybrid recommendations
  - Real-time personalization
  - A/B testing
end note

@enduml
```
