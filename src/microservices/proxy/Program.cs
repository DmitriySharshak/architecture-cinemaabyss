
using Microsoft.Extensions.DependencyInjection;
using Yarp.ReverseProxy;
using Yarp.ReverseProxy.Configuration;
using Yarp.ReverseProxy.LoadBalancing;
using Yarp.ReverseProxy.Model;

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
                            ["Percentage"] = percent.ToString()
                        }
                    });

                if (percent < 100)
                {
                    destinations.Add(
                        "monolite-service", new DestinationConfig
                        {
                            Address = monolitAddress,
                            Metadata = new Dictionary<string, string>
                            {
                                ["Percentage"] = (100 - percent).ToString()
                            }
                        });
                }
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
                    LoadBalancingPolicy = "Percentage",
                    Destinations        = destinations
                },
                new ClusterConfig
                {
                    ClusterId = "monolit-cluster",
                    Destinations = new Dictionary<string, DestinationConfig>
                    {
                        ["monolite-service"] = new()
                        {
                            Address = monolitAddress
                        }
                    }
                }
            };

            // 1. Регистрируем кастомную политику
            builder.Services.AddSingleton<ILoadBalancingPolicy, PercentagePolicy>();

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
                proxyPipeline.UseLoadBalancing();

                proxyPipeline.Use((context, next) =>
                {
                    Console.WriteLine($"Proxying to: {context.Request.Path}");
                    return next();
                });
            });

            // Статистика распределения
            app.MapGet("/api/proxy/stats", async (IProxyStateLookup proxyState) =>
            {
                var stats = new Dictionary<string, object>();

                foreach (var cluster in proxyState.GetClusters())
                {
                    var clusterStats = new
                    {
                        LoadBalancingPolicy = cluster.Model.Config.LoadBalancingPolicy ?? "PowerOfTwoChoices",
                        HealthCheckEnabled  = cluster.Model.Config.HealthCheck?.Active?.Enabled ?? false,

                        TotalRequests = cluster.DestinationsState.AvailableDestinations.Sum(q => q.ConcurrentRequestCount),
                        Destinations = cluster.DestinationsState.AllDestinations.Select(d => new
                        {
                            DestinationId = d.DestinationId,
                            Address       = d.Model.Config.Address,
                            Requests      = d.ConcurrentRequestCount,
                            Percentage    = GetPercentageFromMetadata(d)
                        })
                    };

                    stats[cluster.ClusterId] = clusterStats;

                }

                return Results.Json(stats);
            });

            app.Run();
        }


        private static int GetPercentageFromMetadata(DestinationState destination)
        {
            if (destination.Model.Config.Metadata?.TryGetValue("Percentage", out var percentObj) == true)
            {
                return percentObj switch
                {
                    //int percent => percent,
                    string percentStr when int.TryParse(percentStr, out var parsed) => parsed,
                    _ => 0
                };
            }

            return 0;
        }
    }

    public class PercentagePolicy : ILoadBalancingPolicy
    {

        private readonly Random _random = new();
        public string Name => "Percentage";
        private readonly ILogger<PercentagePolicy> logger;

        public PercentagePolicy(ILogger<PercentagePolicy> logger)
        {
            this.logger = logger;
        }

        public DestinationState? PickDestination(HttpContext context, ClusterState cluster, IReadOnlyList<DestinationState> availableDestinations)
        {
            this.logger.LogInformation("===========================PickDestination is OK=================================================");

            if (availableDestinations.Count == 0)
                return null;

            // Собираем destinations с процентами
            var dests = availableDestinations
                .Select(d => new
                {
                    Destination = d,
                    Percent = GetPercent(d)
                })
                .ToList();

            // Если проценты не заданы → равномерное распределение
            var totalPercent = dests.Sum(d => d.Percent);

            this.logger.LogInformation($"TotalPercent:{totalPercent}");

            if (totalPercent == 0)
            {
                var equalPercent = 100 / availableDestinations.Count;
                dests = availableDestinations
                    .Select(d => new { Destination = d, Percent = equalPercent })
                    .ToList();
                totalPercent = 100;
            }

            // Нормализуем до 100%
            if (totalPercent != 100)
            {
                dests = dests.Select(d => new
                {
                    d.Destination,
                    Percent = (int)Math.Round(d.Percent * 100.0 / totalPercent)
                }).ToList();
                totalPercent = 100;
            }

            // Выбираем по проценту
            var randomValue = _random.Next(totalPercent);

            this.logger.LogInformation($"randomValue:{randomValue}");
            int cumulative = 0;

            foreach (var item in dests)
            {
                cumulative += item.Percent;
                if (randomValue < cumulative)
                {
                    this.logger.LogInformation($"Destination:{item.Destination.DestinationId}");
                    return item.Destination;
                }
                    
            }

            return dests.Last().Destination;
        }

        private int GetPercent(DestinationState destination)
        {
            if (destination.Model.Config.Metadata?.TryGetValue("Percentage", out var percentObj) == true)
            {
                return percentObj switch
                {
                    string percentStr when int.TryParse(percentStr, out var parsed) => parsed,
                    _ => 0
                };
            }

            return 0;
        }

    }

}
