using System;
using System.ComponentModel.DataAnnotations;

namespace payment.Dto
{
    /// <summary>
    /// DTO chọn phương thức thanh toán
    /// </summary>
    public class PaymentMethodRequest
    {
        [Required]
        [RegularExpression("^(COD|Online)$", ErrorMessage = "Phương thức thanh toán phải là 'COD' hoặc 'Online'")]
        public string PaymentMethod { get; set; }
    }

    /// <summary>
    /// DTO đại diện cho phản hồi từ Webhook của cổng thanh toán
    /// </summary>
    public class PaymentWebhookRequest
    {
        [Required]
        public string OrderId { get; set; }

        [Required]
        [RegularExpression("^(SUCCESS|FAILED)$", ErrorMessage = "Trạng thái phải là 'SUCCESS' hoặc 'FAILED'")]
        public string Status { get; set; }
    }

    /// <summary>
    /// DTO phản hồi cho trạng thái thanh toán
    /// </summary>
    public class PaymentStatusResponse
    {
        public string OrderId { get; set; }
        public string Status { get; set; }
    }

    /// <summary>
    /// DTO phản hồi lịch sử thanh toán của user
    /// </summary>
    public class PaymentHistoryResponse
    {
        public string OrderId { get; set; }
        public decimal Amount { get; set; }
        public string PaymentMethod { get; set; }
        public string Status { get; set; }
        public DateTime CreatedAt { get; set; }
    }
}
