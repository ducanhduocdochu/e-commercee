using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Hosting;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Hosting;
using Microsoft.OpenApi.Models;
using MongoDB.Driver;
using payment.Repositories;
using payment.Services;

var builder = WebApplication.CreateBuilder(args);

// ✅ Load cấu hình từ appsettings.json
var configuration = builder.Configuration;

// ✅ Thêm dịch vụ Controllers
builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();

// ✅ Cấu hình Swagger để hỗ trợ Authorization Bearer Token
builder.Services.AddSwaggerGen(c =>
{
    c.SwaggerDoc("v1", new OpenApiInfo { Title = "Payment Service API", Version = "v1" });
    c.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.Http,
        Scheme = "Bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header,
        Description = "Nhập JWT Token dạng: Bearer {token}"
    });
    c.EnableAnnotations();
    c.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            Array.Empty<string>()
        }
    });
});

// ✅ Cấu hình MongoDB từ appsettings.json
builder.Services.AddSingleton<IMongoClient, MongoClient>(s =>
    new MongoClient(configuration.GetConnectionString("MongoDb")));

builder.Services.AddSingleton(s => s.GetRequiredService<IMongoClient>().GetDatabase("PaymentDB"));

// ✅ Đăng ký Repositories & Services
builder.Services.AddSingleton<PaymentRepository>();
builder.Services.AddSingleton<PaymentService>();

// ✅ Thêm CORS hỗ trợ API gọi từ frontend (Tùy chỉnh nếu cần)
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll",
        policy => policy.AllowAnyOrigin()
                        .AllowAnyMethod()
                        .AllowAnyHeader());
});

var app = builder.Build();

// ✅ Kích hoạt Swagger trong môi trường phát triển
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// ✅ Sử dụng CORS
app.UseCors("AllowAll");

// ✅ Thêm Middleware Xác thực JWT
app.UseMiddleware<JwtMiddleware>();
app.UseMiddleware<JwtAuthenticationEntryPointMiddleware>();

// ✅ Xác thực & Ủy quyền
app.UseAuthentication();
app.UseAuthorization();

// ✅ Ánh xạ Controllers
app.MapControllers();

// ✅ Chạy ứng dụng
app.Run();
