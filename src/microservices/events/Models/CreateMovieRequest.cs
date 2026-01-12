using System.Text.Json.Serialization;

namespace events.Models
{
    public class CreateMovieRequest
    {
        [JsonPropertyName("movie_id")]
        public long MovieId { get; set; }
        public string Title { get; set; }
        //public string Description { get; set; }
        public string Action { get; set; }

        [JsonPropertyName("user_id")]
        public long UserId { get; set; }
        //public double Rating { get; set; }
        //public string[] Genres { get; set; }

        public override string ToString()
        {
            return $"[movie_id={this.MovieId}, title={this.Title}, action={this.Action}, user_id={this.UserId}]";
        }
    }
}
