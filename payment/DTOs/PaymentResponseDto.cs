using System;

namespace payment.Dto
{
    /// <summary>
    /// DTO phản hồi sau khi chọn phương thức thanh toán
    /// </summary>
    public class PaymentSelectionResponse
    {
        public string Message { get; set; }
        public string OrderId { get; set; }
        public string PaymentMethod { get; set; }
        public string Status { get; set; }
    }

    /// <summary>
    /// DTO phản hồi sau khi xác nhận thanh toán
    /// </summary>
    public class PaymentConfirmationResponse
    {
        public string Message { get; set; }
        public string OrderId { get; set; }
        public string Status { get; set; }
    }

    /// <summary>
    /// DTO phản hồi sau khi hủy thanh toán
    /// </summary>
    public class PaymentCancellationResponse
    {
        public string Message { get; set; }
        public string OrderId { get; set; }
        public string Status { get; set; }
    }

    /// <summary>
    /// DTO phản hồi khi redirect đến cổng thanh toán
    /// </summary>
    public class PaymentRedirectResponse
    {
        public string RedirectUrl { get; set; }
    }
}
