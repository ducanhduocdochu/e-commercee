package routes

import (
	"order/controllers"
	middleware "order/middlewares"

	"github.com/gin-gonic/gin"
)

// OrderRoutes - Định nghĩa API routes cho Order
func OrderRoutes(router *gin.Engine, orderController *controllers.OrderController) {
	// Middleware xác thực JWT
	auth := middleware.AuthMiddleware()

	// 🛒 Buyer APIs
	buyerGroup := router.Group("/orders")
	buyerGroup.Use(auth, middleware.RoleMiddleware("ROLE_BUYER"))
	{
		buyerGroup.POST("", orderController.CreateOrder)                   // Tạo đơn hàng
		buyerGroup.GET("", orderController.GetOrdersByBuyer)               // Lấy danh sách đơn hàng
		buyerGroup.GET("/:order_id", orderController.GetOrderDetail)       // Xem chi tiết đơn hàng
		buyerGroup.PATCH("/:order_id/cancel", orderController.CancelOrder) // Hủy đơn hàng
	}

	// 🏪 Seller APIs
	sellerGroup := router.Group("/seller/orders")
	sellerGroup.Use(auth, middleware.RoleMiddleware("ROLE_SELLER"))
	{
		sellerGroup.GET("", orderController.GetOrdersBySeller)                // Lấy danh sách đơn hàng của Seller
		sellerGroup.PATCH("/:order_id/confirm", orderController.ConfirmOrder) // Xác nhận đơn hàng
	}
}
