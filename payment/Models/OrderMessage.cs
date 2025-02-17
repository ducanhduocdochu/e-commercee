namespace payment.Models
{
    /// <summary>
    /// Model đại diện cho message từ Order Service gửi qua RabbitMQ
    /// </summary>
    public class OrderMessage
    {
        public string OrderId { get; set; }
        public string UserId { get; set; }
        public decimal TotalAmount { get; set; }
    }
}
