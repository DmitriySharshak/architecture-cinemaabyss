import random
from fastapi import Request, APIRouter, Response
import httpx
import os
from dotenv import load_dotenv


load_dotenv('architecture-cinemaabyss/.env')
MONOLITH_URL = os.getenv('MONOLITH_URL')
MOVIES_SERVICE_URL= os.getenv('MOVIES_SERVICE_URL')
MOVIES_MIGRATION_PERCENT = os.getenv('MOVIES_MIGRATION_PERCENT')

router = APIRouter()
monolith_requests_count = 0
mircoservice_requests_count = 0

@router.api_route("/movies/{path:path}", methods=["GET", "POST", "PUT", "DELETE", "PATCH"])
async def proxy_movies(path: str, request: Request):
    global monolith_requests_count, mircoservice_requests_count
    if settings.gradual_migration and random.randint(1, 100) <= int(MOVIES_MIGRATION_PERCENT):
        base_url = settings.movies_service_url
        mircoservice_requests_count +=1
    else:
        base_url = settings.monolith_url
        monolith_requests_count += 1
    
    if path:
        target_url = f"{base_url}/api/movies/{path}"
    else:
        target_url = f"{base_url}/api/movies"
    
    print(f"Target URL: {target_url}")
    with open('/app/logs/requests.log', mode='a') as f:
        f.write(f"[Total: monolith={monolith_requests_count}, microservice={mircoservice_requests_count}\n")
    
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