package middleware

import (
	"fmt"
	"log"
	"net/http"
	"os"
	"strings"

	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt/v5"
)

// UserClaims - cấu trúc lưu thông tin từ JWT
type UserClaims struct {
	UserID      string   `json:"sub"`
	Permissions []string `json:"scope"`
	jwt.RegisteredClaims
}

// AuthMiddleware - Middleware xác thực JWT
func AuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		authHeader := c.GetHeader("Authorization")

		if authHeader == "" || !strings.HasPrefix(authHeader, "Bearer ") {
			log.Println("❌ Token missing or invalid format")
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Token is missing or invalid"})
			return
		}

		tokenString := strings.TrimPrefix(authHeader, "Bearer ")
		secretKey := os.Getenv("JWT_SECRET_KEY")
		if secretKey == "" {
			log.Fatal("❌ MISSING ENV: JWT_SECRET_KEY")
		}

		// 🔥 **Giải mã token với thuật toán HS512 (hoặc tùy service)**
		token, err := jwt.Parse(tokenString, func(token *jwt.Token) (interface{}, error) {
			// Kiểm tra thuật toán ký
			if _, ok := token.Method.(*jwt.SigningMethodHMAC); !ok {
				return nil, fmt.Errorf("unexpected signing method: %v", token.Header["alg"])
			}
			return []byte(secretKey), nil
		})

		// ❌ Nếu token không hợp lệ, trả về lỗi
		if err != nil || !token.Valid {
			log.Printf("❌ JWT Error: %v", err)
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Invalid token"})
			return
		}

		// ✅ Giải mã payload token
		claims, ok := token.Claims.(jwt.MapClaims)
		if !ok {
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Invalid token payload"})
			return
		}

		// ✅ Lấy UserID từ `sub`
		userID, ok := claims["sub"].(string)
		if !ok {
			log.Println("❌ Missing 'sub' in JWT")
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "Invalid token: missing user_id"})
			return
		}

		// ✅ Chuyển đổi quyền `scope` từ string → array
		var permissions []string
		if scope, exists := claims["scope"].(string); exists {
			permissions = strings.Split(scope, " ")
		}

		// ✅ Lưu thông tin user vào context Gin
		c.Set("user", &UserClaims{
			UserID:      userID,
			Permissions: permissions,
		})

		log.Printf("✅ Token Decoded: UserID=%s, Permissions=%v", userID, permissions)
		c.Next()
	}
}
