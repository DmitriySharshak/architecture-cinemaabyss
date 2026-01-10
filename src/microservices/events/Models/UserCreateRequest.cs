using System.Text.Json.Serialization;

namespace events.Models
{
    public class UserCreateRequest
    {

        [JsonPropertyName("user_id")]
        public long UserId { get; set; }

        public string UserName { get; set; }

        public string? Email { get; set; }

        public string Action { get; set; }

        public DateTimeOffset Timestamp { get; set; }

        public override string ToString()
        {
            return $"[user_id={this.UserId}, userName={this.UserName}, email={this.Email}, action={this.Action}, timestamp={this.Timestamp}]";
        }

    }
}
