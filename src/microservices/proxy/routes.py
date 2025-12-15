import random
from fastapi import Request, APIRouter, Response, status
import httpx
import os
from dotenv import load_dotenv


load_dotenv('architecture-cinemaabyss/.env')
MONOLITH_URL = os.getenv('MONOLITH_URL')
MOVIES_SERVICE_URL= os.getenv('MOVIES_SERVICE_URL')
MOVIES_MIGRATION_PERCENT = os.getenv('MOVIES_MIGRATION_PERCENT')

movies_proxy_router = APIRouter()
users_proxy_router = APIRouter()
health_check_router = APIRouter()
routers = [movies_proxy_router, users_proxy_router, health_check_router]


@health_check_router.api_route("/health", methods=["GET"], status_code=status.HTTP_200_OK)
async def health_check():
    return {"status": True}

@movies_proxy_router.api_route(
        "/api/movies/{path:path}", 
        methods=["GET", "POST", "PUT", "DELETE", "PATCH"],
        status_code=status.HTTP_200_OK
    )
async def proxy_movies(path: str, request: Request):
    if MOVIES_MIGRATION_PERCENT and random.randint(1, 100) <= int(MOVIES_MIGRATION_PERCENT):
        base_url = MOVIES_SERVICE_URL
    else:
        base_url = MONOLITH_URL
    
    if path:
        target_url = f"{base_url}/api/movies/{path}"
    else:
        target_url = f"{base_url}/api/movies"
    
    print(f"Target URL: {target_url}")
    
    async with httpx.AsyncClient() as client:
        response = await client.request(
            method=request.method,
            url=target_url,
            params=request.query_params,
            content=await request.body()
        )

        return Response(
            content=response.content,
            status_code=response.status_code,
            headers=dict(response.headers)
        )
    
@users_proxy_router.api_route(
        "/api/users/{path:path}", 
        methods=["GET", "POST", "PUT", "DELETE", "PATCH"],
        status_code=status.HTTP_200_OK
    )
async def proxy_users(path: str, request: Request):
    if path:
        target_url = f"{MONOLITH_URL}/api/users/{path}"
    else:
        target_url = f"{MONOLITH_URL}/api/users"
    
    print(f"Target URL: {target_url}")
    
    async with httpx.AsyncClient() as client:
        response = await client.request(
            method=request.method,
            url=target_url,
            params=request.query_params,
            content=await request.body()
        )

        return Response(
            content=response.content,
            status_code=response.status_code,
            headers=dict(response.headers)
        )
    