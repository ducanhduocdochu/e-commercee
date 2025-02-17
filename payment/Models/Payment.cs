using System;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace payment.Models
{
    /// <summary>
    /// Model đại diện cho giao dịch thanh toán
    /// </summary>
    public class Payment
    {
        [BsonId]
        [BsonRepresentation(BsonType.ObjectId)]
        public string Id { get; set; }

        [BsonElement("order_id")]
        public string OrderId { get; set; }

        [BsonElement("user_id")]
        public string UserId { get; set; }

        [BsonElement("amount")]
        public decimal Amount { get; set; }

        [BsonElement("payment_method")]
        public string PaymentMethod { get; set; } // "COD" hoặc "Online"

        [BsonElement("status")]
        public string Status { get; set; } // "Pending", "Completed", "Failed", "Cancelled"

        [BsonElement("created_at")]
        public DateTime CreatedAt { get; set; } = DateTime.UtcNow;

        [BsonElement("updated_at")]
        public DateTime UpdatedAt { get; set; } = DateTime.UtcNow;
    }
}
