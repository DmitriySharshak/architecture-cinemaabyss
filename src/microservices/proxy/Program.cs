
using Microsoft.Extensions.DependencyInjection;
using Yarp.ReverseProxy.Configuration;
using Yarp.ReverseProxy.LoadBalancing;

namespace proxy
{

    public class Program
    {

        public static void Main(string[] args)
        {
            var builder = WebApplication.CreateBuilder(args);

            Console.WriteLine("=== Environment Variables ===");
            foreach (var env in Environment.GetEnvironmentVariables().Keys)
            {
                if (env.ToString().Contains("Cluster", StringComparison.OrdinalIgnoreCase))
                {
                    Console.WriteLine($"{env} = {Environment.GetEnvironmentVariable(env.ToString())}");
                }
            }

            // Вывести загруженную конфигурацию
            Console.WriteLine("=== Configuration ===");
            var config = builder.Configuration.GetSection("Clusters");
            foreach (var cluster in config.GetChildren())
            {
                Console.WriteLine($"Cluster: {cluster.Key}");
                foreach (var dest in cluster.GetSection("Destinations").GetChildren())
                {
                    Console.WriteLine($"  Destination: {dest.Key}");
                    Console.WriteLine($"    Address: {dest["Address"]}");
                }
            }


            // Добавляем health checks
            builder.Services.AddHealthChecks();

            // Создаем конфигурацию в памяти
            var routes = new[]
            {
                new RouteConfig
                {
                    RouteId   = "movie-route",
                    ClusterId = "movies-cluster",
                    Match = new RouteMatch
                    {
                        Path = "/api/movies/{**catch-all}"
                    }
                },
                new RouteConfig
                {
                    RouteId   = "monolit-route",
                    ClusterId = "monolit-cluster",
                    Match = new RouteMatch
                    {
                        Path = "/api/users/{**catch-all}"
                    }
                }
            };

            var movieServiceAddress = Environment.GetEnvironmentVariable("MOVIES_SERVICE_URL");
            var monolitAddress      = Environment.GetEnvironmentVariable("MONOLITH_URL");

            var isGradualMigration = bool.Parse(Environment.GetEnvironmentVariable("GRADUAL_MIGRATION"));
            var percent            = int.Parse(Environment.GetEnvironmentVariable("MOVIES_MIGRATION_PERCENT"));

            var destinations = new Dictionary<string, DestinationConfig>();
            if (isGradualMigration)
            {
                destinations.Add(
                    "movie-service", new DestinationConfig
                    {
                        Address = movieServiceAddress,
                        Metadata = new Dictionary<string, string>
                        {
                            ["Weight"] = percent.ToString()
                        }
                    });

                destinations.Add(
                    "monolite-service", new DestinationConfig
                    {
                        Address = monolitAddress,
                        Metadata = new Dictionary<string, string>
                        {
                            ["Weight"] = (100 - percent).ToString()
                        }
                    });
            }
            else
            {
                destinations.Add(
                    "monolite-service", new DestinationConfig
                    {
                        Address = monolitAddress,
                    });
            }

            var clusters = new[]
            {
                new ClusterConfig
                {
                    ClusterId           = "movies-cluster",
                    LoadBalancingPolicy = LoadBalancingPolicies.PowerOfTwoChoices,
                    Destinations        = destinations
                },
                new ClusterConfig
                {
                    ClusterId           = "monolit-cluster",
                    Destinations        = new Dictionary<string, DestinationConfig>
                    {
                        ["monolite-service"] = new()
                        {
                            Address = monolitAddress
                        }
                    }
                }
            };

            builder.Services.AddReverseProxy().LoadFromMemory(routes, clusters);

            builder.Services.AddControllers();

            var app = builder.Build();

            // Настраиваем маршруты
            app.MapHealthChecks("/health");
            app.MapGet("/", () => "Service is running!");

            app.UseAuthorization();

            app.MapControllers();

            app.MapReverseProxy(proxyPipeline =>
            {
                proxyPipeline.Use((context, next) =>
                {
                    Console.WriteLine($"Proxying to: {context.Request.Path}");
                    return next();
                });
            });

            app.Run();


        }

    }

}
