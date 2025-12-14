from fastapi import FastAPI
import uvicorn
from routes import movies_proxy_router, users_proxy_router, health_check_router


app = FastAPI()
app.include_router(movies_proxy_router)
app.include_router(users_proxy_router)
app.include_router(health_check_router)

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
    