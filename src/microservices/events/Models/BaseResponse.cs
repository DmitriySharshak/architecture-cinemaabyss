using System.Text.Json.Serialization;

namespace events.Models
{
    public class BaseResponse
    {
        [JsonPropertyName("status")]
        public string Status { get; set; } = "success";
        public long Partition {get; set; }
        public long Offset { get; set; }
        public Event Event { get; set; }
    }

    public class Event
    {
        public string Id { get; set; }
        public string Type { get; set; }
        public DateTimeOffset Timestamp { get; set; }
        public string Payload { get; set; }
    }
}
