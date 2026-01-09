
using System.Text.Json;
using events.Services;
using Microsoft.AspNetCore.Diagnostics.HealthChecks;
using Microsoft.Extensions.Diagnostics.HealthChecks;

namespace events
{
    public class Program
    {
        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            builder.Services.AddControllers();
            builder.Services.AddEndpointsApiExplorer();
            builder.Services.AddSwaggerGen();
            builder.Services.AddHealthChecks();

            var kafkaConfig = new KafkaConfig()
            {
                BootstrapServer = Environment.GetEnvironmentVariable("KAFKA_BROKERS")
            };

            builder.Services.AddSingleton(kafkaConfig);
            builder.Services.AddSingleton<PublishService>();


            var app = builder.Build();

            app.UseAuthorization();
            app.MapControllers();

            // Настраиваем маршруты
            app.MapHealthChecks("api/events/health", new HealthCheckOptions
            {
                ResponseWriter = async (context, report) =>
                {
                    context.Response.ContentType = "application/json";
                    var result = JsonSerializer.Serialize(new
                    {
                        status = report.Status == HealthStatus.Healthy ? true : false,
                    });

                    await context.Response.WriteAsync(result);
                }
            });

            app.Run();
        }
    }
}
