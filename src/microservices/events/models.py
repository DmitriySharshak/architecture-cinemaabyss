from pydantic import BaseModel, Field
from datetime import datetime
from typing import Optional, Any, Union, List


class MovieEventModel(BaseModel): 
    movie_id : int = Field()
    title : str = Field()
    action : str = Field()
    user_id : int | None = None
    rating : float | None = None
    genres : list | None = None
    description : str | None = None

class UserEventModel(BaseModel):
    user_id : int = Field()
    username : str | None = None
    email : str | None = None
    action : str | None = None
    timestamp : datetime = Field()

class PaymentEventModel(BaseModel):
    payment_id : int = Field()
    user_id : int = Field()
    amount : float = Field(ge=0)
    status : str = Field()
    timestamp : datetime = Field()
    method_type : Optional[str] = Field(None)

class Event(BaseModel):
    id: str
    type: str
    timestamp: datetime
    payload: Union[UserEventModel, MovieEventModel, PaymentEventModel]

class EventResponse(BaseModel):
    status: str
    partition: int
    offset: int
    event: Event
