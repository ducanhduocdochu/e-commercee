using Microsoft.AspNetCore.Http;
using System;
using System.Linq;
using System.Threading.Tasks;

public class RoleMiddleware
{
    private readonly RequestDelegate _next;
    private readonly string[] _requiredRoles;

    public RoleMiddleware(RequestDelegate next, params string[] requiredRoles)
    {
        _next = next;
        _requiredRoles = requiredRoles;
    }

    public async Task Invoke(HttpContext context)
    {
        if (!context.Items.ContainsKey("Permissions"))
        {
            context.Response.StatusCode = StatusCodes.Status403Forbidden;
            await context.Response.WriteAsync("You do not have permission to access this resource");
            return;
        }

        var userPermissions = context.Items["Permissions"] as string[];
        if (userPermissions == null || !_requiredRoles.Any(role => userPermissions.Contains(role)))
        {
            context.Response.StatusCode = StatusCodes.Status403Forbidden;
            await context.Response.WriteAsync("You do not have permission to access this resource");
            return;
        }

        await _next(context);
    }
}
