using MongoDB.Driver;
using System.Threading.Tasks;
using System.Collections.Generic;
using System.Linq;
using payment.Repositories;
using payment.Models;
using payment.Dto;

namespace payment.Services
{
    public class PaymentService
    {
        private readonly PaymentRepository _paymentRepository;

        public PaymentService(PaymentRepository paymentRepository)
        {
            _paymentRepository = paymentRepository;
        }

        public async Task<PaymentSelectionResponse?> SelectPaymentMethod(string orderId, string paymentMethod)
        {
            var payment = await _paymentRepository.GetPaymentByOrderIdAsync(orderId);
            if (payment == null) return null;

            payment.PaymentMethod = paymentMethod;
            payment.Status = paymentMethod == "COD" ? "Awaiting Confirmation" : "Redirecting";
            await _paymentRepository.UpdatePaymentAsync(payment);

            return new PaymentSelectionResponse
            {
                OrderId = orderId,
                PaymentMethod = paymentMethod,
                Status = payment.Status
            };
        }

        public async Task<PaymentRedirectResponse?> RedirectToPaymentGateway(string orderId)
        {
            var payment = await _paymentRepository.GetPaymentByOrderIdAsync(orderId);
            if (payment == null || payment.PaymentMethod != "Online") return null;

            var redirectUrl = $"https://fakepayment.com/pay?orderId={orderId}&amount={payment.Amount}";
            return new PaymentRedirectResponse { RedirectUrl = redirectUrl };
        }

        public async Task<PaymentConfirmationResponse?> UpdatePaymentStatus(string orderId, string status)
        {
            var payment = await _paymentRepository.GetPaymentByOrderIdAsync(orderId);
            if (payment == null) return null;

            payment.Status = status == "SUCCESS" ? "Completed" : "Failed";
            await _paymentRepository.UpdatePaymentAsync(payment);

            return new PaymentConfirmationResponse
            {
                OrderId = orderId,
                Status = payment.Status
            };
        }

        public async Task<PaymentConfirmationResponse?> ConfirmCODPayment(string orderId)
        {
            var payment = await _paymentRepository.GetPaymentByOrderIdAsync(orderId);
            if (payment == null || payment.PaymentMethod != "COD") return null;

            payment.Status = "Completed";
            await _paymentRepository.UpdatePaymentAsync(payment);

            return new PaymentConfirmationResponse
            {
                OrderId = orderId,
                Status = payment.Status
            };
        }

        public async Task<PaymentCancellationResponse?> CancelPayment(string orderId)
        {
            var payment = await _paymentRepository.GetPaymentByOrderIdAsync(orderId);
            if (payment == null) return null;

            payment.Status = "Cancelled";
            await _paymentRepository.UpdatePaymentAsync(payment);

            return new PaymentCancellationResponse
            {
                OrderId = orderId,
                Status = payment.Status
            };
        }

        public async Task<PaymentStatusResponse?> GetPaymentStatus(string orderId)
        {
            var payment = await _paymentRepository.GetPaymentByOrderIdAsync(orderId);
            if (payment == null) return null;

            return new PaymentStatusResponse
            {
                OrderId = orderId,
                Status = payment.Status
            };
        }

        public async Task<List<PaymentHistoryResponse>> GetPaymentHistory(string userId)
        {
            var payments = await _paymentRepository.GetPaymentsByUserIdAsync(userId);
            if (payments == null || payments.Count == 0) return new List<PaymentHistoryResponse>();

            return payments.Select(p => new PaymentHistoryResponse
            {
                OrderId = p.OrderId,
                Amount = p.Amount,
                PaymentMethod = p.PaymentMethod,
                Status = p.Status,
                CreatedAt = p.CreatedAt
            }).ToList();
        }
    }
}
