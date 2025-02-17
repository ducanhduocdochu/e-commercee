using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using Microsoft.AspNetCore.Http;
using System.Collections.Generic;
using System.Linq;
using System.Text.Json;
using System.Threading.Tasks;
using Swashbuckle.AspNetCore.Annotations;
using payment.Dto;
using payment.Services;
using payment.Models;
using payment.Utils;


namespace payment.Controllers
{
    [Route("payment")]
    [ApiController]
    public class PaymentController : ControllerBase
    {
        private readonly PaymentService _paymentService;
        private readonly ILogger<PaymentController> _logger;

        public PaymentController(PaymentService paymentService, ILogger<PaymentController> logger)
        {
            _paymentService = paymentService;
            _logger = logger;
        }

        /// <summary>
        /// Chọn phương thức thanh toán cho đơn hàng
        /// </summary>
        /// <param name="orderId">Mã đơn hàng</param>
        /// <param name="request">Thông tin phương thức thanh toán</param>
        [HttpPost("select-payment/{orderId}")]
        [SwaggerOperation(Summary = "Chọn phương thức thanh toán", Description = "Chọn phương thức thanh toán cho đơn hàng cụ thể.")]
        [ProducesResponseType(typeof(ApiResponse<PaymentSelectionResponse>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status404NotFound)]
        public async Task<IActionResult> SelectPaymentMethod(string orderId, [FromBody] PaymentMethodRequest request)
        {
            var roles = HttpContext.Items["Permissions"] as string[];
            if (roles == null || !roles.Contains("ROLE_BUYER"))
            {
                _logger.LogWarning("❌ Unauthorized access: {Roles}", JsonSerializer.Serialize(roles));
                return StatusCode(StatusCodes.Status403Forbidden,
                    ApiResponse<object>.Error(ErrorCode.UNAUTHORIZED.Code, ErrorCode.UNAUTHORIZED.Message));
            }

            var paymentSelection = await _paymentService.SelectPaymentMethod(orderId, request.PaymentMethod);
            if (paymentSelection == null)
            {
                return NotFound(ApiResponse<object>.Error(ErrorCode.ORDER_NOT_FOUND.Code, ErrorCode.ORDER_NOT_FOUND.Message));
            }

            return Ok(ApiResponse<PaymentSelectionResponse>.Success(paymentSelection, "Payment method selected successfully"));
        }

        /// <summary>
        /// Redirect user đến cổng thanh toán online
        /// </summary>
        [HttpGet("redirect/{orderId}")]
        [SwaggerOperation(Summary = "Redirect đến cổng thanh toán", Description = "Người dùng được điều hướng đến trang thanh toán trực tuyến.")]
        [ProducesResponseType(typeof(ApiResponse<PaymentRedirectResponse>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status404NotFound)]
        public async Task<IActionResult> RedirectToPaymentGateway(string orderId)
        {
            var redirectResponse = await _paymentService.RedirectToPaymentGateway(orderId);
            if (redirectResponse == null)
            {
                return NotFound(ApiResponse<object>.Error(ErrorCode.ORDER_NOT_FOUND.Code, ErrorCode.ORDER_NOT_FOUND.Message));
            }

            return Ok(ApiResponse<PaymentRedirectResponse>.Success(redirectResponse, "Redirect to payment gateway successful"));
        }

        /// <summary>
        /// Webhook nhận phản hồi từ cổng thanh toán
        /// </summary>
        [HttpPost("webhook")]
        [SwaggerOperation(Summary = "Webhook thanh toán", Description = "Hệ thống thanh toán gọi đến webhook này khi có cập nhật.")]
        [ProducesResponseType(typeof(ApiResponse<PaymentConfirmationResponse>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status404NotFound)]
        public async Task<IActionResult> PaymentWebhook([FromBody] PaymentWebhookRequest request)
        {
            var confirmationResponse = await _paymentService.UpdatePaymentStatus(request.OrderId, request.Status);
            if (confirmationResponse == null)
            {
                return NotFound(ApiResponse<object>.Error(ErrorCode.ORDER_NOT_FOUND.Code, ErrorCode.ORDER_NOT_FOUND.Message));
            }

            return Ok(ApiResponse<PaymentConfirmationResponse>.Success(confirmationResponse, "Payment status updated successfully"));
        }

        /// <summary>
        /// Admin xác nhận thanh toán cho COD
        /// </summary>
        [HttpPost("confirm/{orderId}")]
        [SwaggerOperation(Summary = "Xác nhận thanh toán COD", Description = "Chỉ dành cho ADMIN để xác nhận thanh toán khi khách chọn COD.")]
        [ProducesResponseType(typeof(ApiResponse<PaymentConfirmationResponse>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status404NotFound)]
        public async Task<IActionResult> ConfirmPayment(string orderId)
        {
            var roles = HttpContext.Items["Permissions"] as string[];
            if (roles == null || !roles.Contains("ROLE_ADMIN"))
            {
                _logger.LogWarning("❌ Unauthorized access: {Roles}", JsonSerializer.Serialize(roles));
                return StatusCode(StatusCodes.Status403Forbidden,
                    ApiResponse<object>.Error(ErrorCode.UNAUTHORIZED.Code, ErrorCode.UNAUTHORIZED.Message));
            }

            var confirmationResponse = await _paymentService.ConfirmCODPayment(orderId);
            if (confirmationResponse == null)
            {
                return NotFound(ApiResponse<object>.Error(ErrorCode.ORDER_NOT_FOUND.Code, ErrorCode.ORDER_NOT_FOUND.Message));
            }

            return Ok(ApiResponse<PaymentConfirmationResponse>.Success(confirmationResponse, "COD payment confirmed successfully"));
        }

        /// <summary>
        /// Hủy thanh toán cho đơn hàng
        /// </summary>
        [HttpPost("cancel/{orderId}")]
        [SwaggerOperation(Summary = "Hủy thanh toán", Description = "Người dùng hoặc hệ thống có thể hủy giao dịch thanh toán.")]
        [ProducesResponseType(typeof(ApiResponse<PaymentCancellationResponse>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status404NotFound)]
        public async Task<IActionResult> CancelPayment(string orderId)
        {
            var cancellationResponse = await _paymentService.CancelPayment(orderId);
            if (cancellationResponse == null)
            {
                return NotFound(ApiResponse<object>.Error(ErrorCode.ORDER_NOT_FOUND.Code, ErrorCode.ORDER_NOT_FOUND.Message));
            }

            return Ok(ApiResponse<PaymentCancellationResponse>.Success(cancellationResponse, "Payment cancelled successfully"));
        }

        /// <summary>
        /// Kiểm tra trạng thái thanh toán của một đơn hàng
        /// </summary>
        [HttpGet("status/{orderId}")]
        [SwaggerOperation(Summary = "Trạng thái thanh toán", Description = "Truy vấn trạng thái thanh toán của đơn hàng.")]
        [ProducesResponseType(typeof(ApiResponse<PaymentStatusResponse>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status404NotFound)]
        public async Task<IActionResult> GetPaymentStatus(string orderId)
        {
            var statusResponse = await _paymentService.GetPaymentStatus(orderId);
            if (statusResponse == null)
            {
                return NotFound(ApiResponse<object>.Error(ErrorCode.ORDER_NOT_FOUND.Code, ErrorCode.ORDER_NOT_FOUND.Message));
            }

            return Ok(ApiResponse<PaymentStatusResponse>.Success(statusResponse, "Payment status retrieved successfully"));
        }

        /// <summary>
        /// Xem lịch sử thanh toán của một user
        /// </summary>
        [HttpGet("history/{userId}")]
        [SwaggerOperation(Summary = "Lịch sử thanh toán", Description = "Truy xuất lịch sử thanh toán của một người dùng.")]
        [ProducesResponseType(typeof(ApiResponse<List<PaymentHistoryResponse>>), StatusCodes.Status200OK)]
        [ProducesResponseType(typeof(ApiResponse<object>), StatusCodes.Status404NotFound)]
        public async Task<IActionResult> GetPaymentHistory(string userId)
        {
            var historyList = await _paymentService.GetPaymentHistory(userId);
            if (historyList == null || historyList.Count == 0)
            {
                return NotFound(ApiResponse<object>.Error(ErrorCode.ORDER_NOT_FOUND.Code, "No payment history found"));
            }

            return Ok(ApiResponse<List<PaymentHistoryResponse>>.Success(historyList, "Payment history retrieved successfully"));
        }
    }
}
