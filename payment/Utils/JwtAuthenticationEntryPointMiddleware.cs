using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Logging;
using System;
using System.Text.Json;
using System.Threading.Tasks;
using payment.Utils;
using payment.Dto;

public class JwtAuthenticationEntryPointMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<JwtAuthenticationEntryPointMiddleware> _logger;

    public JwtAuthenticationEntryPointMiddleware(RequestDelegate next, ILogger<JwtAuthenticationEntryPointMiddleware> logger)
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
        catch (UnauthorizedAccessException ex)
        {
            await HandleUnauthorizedExceptionAsync(context, ex);
        }
    }

    private async Task HandleUnauthorizedExceptionAsync(HttpContext context, UnauthorizedAccessException ex)
    {
        var errorCode = ErrorCode.UNAUTHENTICATED;

        context.Response.StatusCode = StatusCodes.Status401Unauthorized;
        context.Response.ContentType = "application/json";

        _logger.LogError("🚨 Unauthorized access attempt! Path: {Path}, Method: {Method}, Error: {Error}",
            context.Request.Path, context.Request.Method, ex.Message);

        var response = new ApiResponse<object>
        {
            Code = errorCode.Code,
            Message = errorCode.Message
        };

        await context.Response.WriteAsync(JsonSerializer.Serialize(response));
    }
}
