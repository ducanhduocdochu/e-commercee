// using System.Text;
// using Microsoft.Extensions.DependencyInjection;
// using Microsoft.Extensions.Hosting;
// using Newtonsoft.Json;
// using payment.Models;
// using payment.Repositories;
// using RabbitMQ.Client;
// using RabbitMQ.Client.Events;

// public class PaymentConsumer : BackgroundService
// {
//     private readonly IServiceProvider _serviceProvider;
//     private readonly IModel _channel;

//     public PaymentConsumer(IServiceProvider serviceProvider)
//     {
//         _serviceProvider = serviceProvider;

//         var factory = new ConnectionFactory() { HostName = "localhost" };
//         var connection = factory.CreateConnection();
//         _channel = connection.CreateModel();
//         _channel.QueueDeclare(queue: "payment_queue",
//                               durable: true,
//                               exclusive: false,
//                               autoDelete: false,
//                               arguments: null);
//     }

//     protected override async Task ExecuteAsync(CancellationToken stoppingToken)
//     {
//         var consumer = new EventingBasicConsumer(_channel);
//         consumer.Received += async (model, ea) =>
//         {
//             var body = ea.Body.ToArray();
//             var message = Encoding.UTF8.GetString(body);
//             var order = JsonConvert.DeserializeObject<OrderMessage>(message);

//             using (var scope = _serviceProvider.CreateScope())
//             {
//                 var paymentRepository = scope.ServiceProvider.GetRequiredService<PaymentRepository>();

//                 var payment = new Payment
//                 {
//                     OrderId = order.OrderId,
//                     UserId = order.UserId,
//                     Amount = order.TotalAmount,
//                     Status = "Pending",
//                     CreatedAt = DateTime.UtcNow
//                 };

//                 await paymentRepository.CreatePaymentAsync(payment);
//             }
//         };

//         _channel.BasicConsume(queue: "payment_queue", autoAck: true, consumer: consumer);
//     }
// }
