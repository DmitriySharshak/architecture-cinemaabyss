namespace events
{
    public class BadRequestLoggingMiddleware
    {
        private readonly RequestDelegate _next;
        private readonly ILogger<BadRequestLoggingMiddleware> _logger;

        public BadRequestLoggingMiddleware(
            RequestDelegate next,
            ILogger<BadRequestLoggingMiddleware> logger)
        {
            _next = next;
            _logger = logger;
        }

        public async Task InvokeAsync(HttpContext context)
        {
            // Перехватываем поток ответа
            var originalBodyStream = context.Response.Body;

            using var responseBody = new MemoryStream();
            context.Response.Body = responseBody;

            await _next(context);

            // Проверяем статус 400
            if (context.Response.StatusCode == 400)
            {
                // Получаем тело ответа
                responseBody.Seek(0, SeekOrigin.Begin);
                var responseText = await new StreamReader(responseBody).ReadToEndAsync();
                responseBody.Seek(0, SeekOrigin.Begin);

                // Логируем с контекстом
                _logger.LogWarning(
                    "BadRequest 400: Path={Path}, Method={Method}, Status={StatusCode}, " +
                    "Response={Response}, User={User}, IP={IP}",
                    context.Request.Path,
                    context.Request.Method,
                    context.Response.StatusCode,
                    responseText,
                    context.User?.Identity?.Name ?? "Anonymous",
                    context.Connection.RemoteIpAddress
                );

                // Логируем тело запроса (если есть)
                if (context.Request.ContentLength > 0 &&
                    context.Request.ContentType?.Contains("json") == true)
                {
                    context.Request.EnableBuffering();
                    var requestBody = await new StreamReader(context.Request.Body).ReadToEndAsync();
                    context.Request.Body.Position = 0;

                    if (requestBody.Length < 5000) // Не логируем большие тела
                    {
                        _logger.LogWarning("Request body: {RequestBody}", requestBody);
                    }
                }
            }

            // Копируем обратно в оригинальный поток
            await responseBody.CopyToAsync(originalBodyStream);
        }
    }
}
