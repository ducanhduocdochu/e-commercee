package middleware

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

// RoleMiddleware - Kiểm tra quyền của người dùng
func RoleMiddleware(allowedRoles ...string) gin.HandlerFunc {
	return func(c *gin.Context) {
		user, exists := c.Get("user")

		if !exists {
			c.AbortWithStatusJSON(http.StatusForbidden, gin.H{"error": "User not authenticated"})
			return
		}

		userClaims, ok := user.(*UserClaims)
		if !ok {
			c.AbortWithStatusJSON(http.StatusForbidden, gin.H{"error": "Invalid user claims"})
			return
		}

		// Kiểm tra nếu user có ít nhất một role trong danh sách
		for _, role := range allowedRoles {
			for _, userRole := range userClaims.Permissions {
				if role == userRole {
					c.Next()
					return
				}
			}
		}

		c.AbortWithStatusJSON(http.StatusForbidden, gin.H{"error": "You do not have permission to access this resource"})
	}
}
