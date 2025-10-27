from datetime import datetime

from pydantic import BaseModel

class MovieEvent(BaseModel):
    movie_id: int
    title: str
    action: str
    user_id: int

class UserEvent(BaseModel):
    user_id: int
    username: str
    action: str
    timestamp: datetime

class PaymentEvent(BaseModel):
    payment_id: int
    user_id: int
    amount: float
    status: str
    timestamp: datetime
    method_type: str