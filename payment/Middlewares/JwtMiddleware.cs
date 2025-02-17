using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.Configuration;
using Microsoft.IdentityModel.Tokens;
using System;
using System.IdentityModel.Tokens.Jwt;  // ✅ Thư viện chứa JwtSecurityTokenHandler
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.Extensions.Logging;


public class JwtMiddleware
{
    private readonly RequestDelegate _next;
    private readonly IConfiguration _configuration;
    private readonly ILogger<JwtMiddleware> _logger;

    public JwtMiddleware(RequestDelegate next, IConfiguration configuration, ILogger<JwtMiddleware> logger)
    {
        _next = next;
        _configuration = configuration;
        _logger = logger;
    }

    public async Task Invoke(HttpContext context)
    {
        var authHeader = context.Request.Headers["Authorization"].FirstOrDefault();

        if (string.IsNullOrEmpty(authHeader) || !authHeader.StartsWith("Bearer "))
        {
            _logger.LogWarning("⚠️ Missing or invalid Authorization header");
            await _next(context);
            return;
        }

        var token = authHeader.Substring("Bearer ".Length).Trim();
        try
        {
            var tokenHandler = new JwtSecurityTokenHandler(); // ✅ Fix lỗi ở đây
            var key = Encoding.ASCII.GetBytes(_configuration["Jwt:Secret"]);

            var parameters = new TokenValidationParameters
            {
                ValidateIssuerSigningKey = true,
                IssuerSigningKey = new SymmetricSecurityKey(key),
                ValidateIssuer = false,
                ValidateAudience = false,
                ClockSkew = TimeSpan.Zero
            };

            var principal = tokenHandler.ValidateToken(token, parameters, out SecurityToken validatedToken);
            var jwtToken = (JwtSecurityToken)validatedToken;

            var userId = jwtToken.Claims.FirstOrDefault(x => x.Type == "sub")?.Value;
            var permissions = jwtToken.Claims.FirstOrDefault(x => x.Type == "scope")?.Value?.Split(' ');

            if (string.IsNullOrEmpty(userId))
            {
                throw new SecurityTokenValidationException("Invalid JWT: Missing 'sub' claim");
            }

            context.Items["UserId"] = userId;
            context.Items["Permissions"] = permissions;

            _logger.LogInformation("✅ Token verified: UserId={UserId}, Permissions={Permissions}", userId, permissions);
        }
        catch (SecurityTokenExpiredException)
        {
            _logger.LogError("❌ Token expired");
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.WriteAsync("Token expired");
            return;
        }
        catch (SecurityTokenValidationException ex)
        {
            _logger.LogError("❌ Token validation failed: {Message}", ex.Message);
            context.Response.StatusCode = StatusCodes.Status401Unauthorized;
            await context.Response.WriteAsync("Invalid token");
            return;
        }
        catch (Exception ex)
        {
            _logger.LogError("❌ Unexpected error during JWT validation: {Message}", ex.Message);
            context.Response.StatusCode = StatusCodes.Status500InternalServerError;
            await context.Response.WriteAsync("Internal Server Error");
            return;
        }

        await _next(context);
    }
}
