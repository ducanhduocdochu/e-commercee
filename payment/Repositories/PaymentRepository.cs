using MongoDB.Driver;
using System.Collections.Generic;
using System.Threading.Tasks;
using payment.Models;

namespace payment.Repositories
{
    public class PaymentRepository
    {
        private readonly IMongoCollection<Payment> _paymentCollection;

        public PaymentRepository(IMongoDatabase database)
        {
            _paymentCollection = database.GetCollection<Payment>("payments");
        }

        /// <summary>
        /// Lưu thanh toán mới vào MongoDB
        /// </summary>
        public async Task CreatePaymentAsync(Payment payment)
        {
            await _paymentCollection.InsertOneAsync(payment);
        }

        /// <summary>
        /// Cập nhật thông tin thanh toán
        /// </summary>
        public async Task UpdatePaymentAsync(Payment payment)
        {
            var filter = Builders<Payment>.Filter.Eq(p => p.OrderId, payment.OrderId);
            await _paymentCollection.ReplaceOneAsync(filter, payment);
        }

        /// <summary>
        /// Lấy thông tin thanh toán theo OrderId
        /// </summary>
        public async Task<Payment> GetPaymentByOrderIdAsync(string orderId)
        {
            return await _paymentCollection.Find(p => p.OrderId == orderId).FirstOrDefaultAsync();
        }

        /// <summary>
        /// Lấy tất cả thanh toán theo UserId
        /// </summary>
        public async Task<List<Payment>> GetPaymentsByUserIdAsync(string userId)
        {
            return await _paymentCollection.Find(p => p.UserId == userId).ToListAsync();
        }

        /// <summary>
        /// Huỷ thanh toán theo OrderId
        /// </summary>
        public async Task<bool> CancelPaymentAsync(string orderId)
        {
            var filter = Builders<Payment>.Filter.Eq(p => p.OrderId, orderId);
            var update = Builders<Payment>.Update.Set(p => p.Status, "Cancelled");
            var result = await _paymentCollection.UpdateOneAsync(filter, update);
            return result.ModifiedCount > 0;
        }
    }
}
