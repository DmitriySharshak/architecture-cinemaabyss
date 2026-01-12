using events.Models;
using events.Services;
using Microsoft.AspNetCore.Mvc;

namespace events.Controllers
{
    [ApiController]
    [Produces("application/json")]
    public class EventsController : ControllerBase
    {
        private readonly ILogger<EventsController> _logger;
        private readonly PublishService _publishService;

        public EventsController(PublishService publishService, ILogger<EventsController> logger)
        {
            _logger = logger;
            _publishService = publishService;
        }

        [HttpPost("api/events/movie")]
        public IActionResult CreateMovie([FromBody] CreateMovieRequest request)
        {
            try
            {
                // Валидация модели (автоматически работает с [ApiController])
                if (!ModelState.IsValid)
                {
                    return BadRequest(new ApiResponse
                    {
                        Status     = "error",
                        Message    = "Invalid request data",
                        StatusCode = 400,
                        Errors = ModelState.Values
                            .SelectMany(v => v.Errors)
                            .Select(e => e.ErrorMessage)
                            .ToList()
                    });
                }

                _publishService.Send("movie-events", request);

                return StatusCode(StatusCodes.Status201Created, new ApiResponse<BaseResponse>
                {
                    Status     = "success",
                    Data       = null,
                    Message    = "Movie event created successfully",
                    StatusCode = 201
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating movie event");

                return StatusCode(StatusCodes.Status500InternalServerError, new ApiResponse
                {
                    Status     = "error",
                    Message    = "An error occurred while creating the movie event",
                    StatusCode = 500
                });
            }
        }

        [HttpPost("api/events/user")]
        public IActionResult CreateUser([FromBody] UserCreateRequest request)
        {
            try
            {
                // Валидация модели (автоматически работает с [ApiController])
                if (!ModelState.IsValid)
                {
                    return BadRequest(new ApiResponse
                    {
                        Status     = "error",
                        Message    = "Invalid request data",
                        StatusCode = 400,
                        Errors = ModelState.Values
                            .SelectMany(v => v.Errors)
                            .Select(e => e.ErrorMessage)
                            .ToList()
                    });
                }

                _publishService.Send("user-events", request);

                var response = new BaseResponse();

                return StatusCode(StatusCodes.Status201Created, new ApiResponse<BaseResponse>
                {
                    Status     = "success",
                    Data       = response,
                    Message    = "Movie event created successfully",
                    StatusCode = 201
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating movie event");

                return StatusCode(StatusCodes.Status500InternalServerError, new ApiResponse
                {
                    Status     = "error",
                    Message    = "An error occurred while creating the movie event",
                    StatusCode = 500
                });
            }
        }

        [HttpPost("api/events/payment")]
        public IActionResult CreatePayment([FromBody] PaymentCreateRequest request)
        {
            try
            {
                if (!ModelState.IsValid)
                {
                    // Логируем ошибки валидации
                    var errors = ModelState
                        .Where(ms => ms.Value.Errors.Any())
                        .ToDictionary(
                            kvp => kvp.Key,
                            kvp => kvp.Value.Errors.Select(e => e.ErrorMessage).ToArray()
                        );

                    _logger.LogWarning("BadRequest: {@Errors}", errors);

                    return BadRequest(new ApiResponse
                    {
                        Status     = "error",
                        Message    = "Invalid request data",
                        StatusCode = 400,
                        Errors = ModelState.Values
                            .SelectMany(v => v.Errors)
                            .Select(e => e.ErrorMessage)
                            .ToList()
                    });
                }

                _publishService.Send("payment-events", request);

                var response = new BaseResponse();

                return StatusCode(StatusCodes.Status201Created, new ApiResponse<BaseResponse>
                {
                    Status     = "success",
                    Data       = response,
                    Message    = "Movie event created successfully",
                    StatusCode = 201
                });
            }
            catch (Exception ex)
            {
                _logger.LogError(ex, "Error creating movie event");

                return StatusCode(StatusCodes.Status500InternalServerError, new ApiResponse
                {
                    Status     = "error",
                    Message    = "An error occurred while creating the movie event",
                    StatusCode = 500
                });
            }
        }
    }



    // Базовая модель API ответа
    public class ApiResponse
    {
        public string Status { get; set; } = "success";
        public string? Message { get; set; }
        public int StatusCode { get; set; } = 200;
        public DateTime Timestamp { get; set; } = DateTime.UtcNow;
        public List<string>? Errors { get; set; }
    }

    // Дженерик версия для типизированных данных
    public class ApiResponse<T> : ApiResponse
    {
        public T? Data { get; set; }
    }
}
