using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using System;
using System.Text.Json;
using System.Threading.Tasks;
using payment.Dto;
using payment.Utils;

public class GlobalExceptionMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<GlobalExceptionMiddleware> _logger;

    public GlobalExceptionMiddleware(RequestDelegate next, ILogger<GlobalExceptionMiddleware> logger)
    {
        _next = next;
        _logger = logger;
    }

    public async Task Invoke(HttpContext context)
    {
        try
        {
            await _next(context);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "❌ Unhandled exception");

            var response = context.Response;
            response.ContentType = "application/json";

            var errorResponse = ApiResponse<string>.Error(ErrorCode.UNCATEGORIZED_EXCEPTION.Code, ex.Message);
            response.StatusCode = (int)ErrorCode.UNCATEGORIZED_EXCEPTION.StatusCode;

            await response.WriteAsync(JsonSerializer.Serialize(errorResponse));
        }
    }
}
