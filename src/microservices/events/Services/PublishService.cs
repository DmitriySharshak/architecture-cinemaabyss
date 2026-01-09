using Confluent.Kafka;
using Newtonsoft.Json;
using System.Text;

namespace events.Services
{
    public class PublishService: IDisposable
    {
        
        private readonly ILogger<PublishService> _logger;
        private IProducer<byte[], byte[]> _producer;

        public PublishService(KafkaConfig kafkaConfig, ILogger<PublishService> logger)
        {
            _logger = logger;
            
            this._logger.LogInformation($"BootstrapServer={kafkaConfig.BootstrapServer}");

            var config = new ProducerConfig()
            {
                BootstrapServers  = kafkaConfig.BootstrapServer,
                ClientId          = "test",
                EnableIdempotence = false,
                Acks              = Acks.All,
                MessageTimeoutMs  = 15000,
            };

            var builder = new ProducerBuilder<byte[], byte[]>(config)
                .SetErrorHandler((prod, error) => _logger.LogError($"[KafkaError] Code: {error.Code}, Reason: {error.Reason}, Fatal: {error.IsFatal}"))
                .SetLogHandler((prod, message) => _logger.LogDebug($"[Name:{message.Name}], [Message:{message.Message}]"));

            _producer = builder.Build();
        }

        public DeliveryReport<byte[], byte[]> Send<T>(string channelName, T value)
        {
            string jsonOutput = JsonConvert.SerializeObject(value);
            var    result     = Encoding.UTF8.GetBytes(jsonOutput);

            var msg = new Message<byte[], byte[]>
            {
                Value   = result,
                Headers = new Headers()
            };

            return this._producer.ProduceAsync(channelName, msg).ConfigureAwait(false).GetAwaiter().GetResult() as DeliveryReport<byte[], byte[]>;
        }

        public void Dispose()
        {
            try
            {
                _producer?.Flush(TimeSpan.FromSeconds(5));
            }
            catch (Exception ex)
            {
                _logger.LogWarning($"Неудачная попытка очистки публикатора: {ex.Message}");
                //ignore
            }

            try
            {
                _producer?.Dispose();
            }
            catch (Exception ex)
            {
                _logger.LogWarning($"Неудачная попытка утилизации публикатора: {ex.Message}");
                //ignore
            }


            _producer = null;
        }
    }
}
