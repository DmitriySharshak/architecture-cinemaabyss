using System.Text.Json.Serialization;

namespace events.Models
{
    public class PaymentCreateRequest
    {

        [JsonPropertyName("payment_id")]
        public long? PaymentId { get; set; }

        [JsonPropertyName("user_id")]
        public long? UserId { get; set; }

        public double? Amount { get; set; }

        public string? Status { get; set; }

        [JsonPropertyName("method_type")]
        public string? MethodType { get; set; }

        public DateTimeOffset? Timestamp { get; set; }

        public override string ToString()
        {
            return $"[payment_id={this.PaymentId}, user_id={this.UserId}, amount={this.Amount}, status={this.Status}, method_type={this.MethodType}, timestamp={this.Timestamp}]";
        }
    }
}
