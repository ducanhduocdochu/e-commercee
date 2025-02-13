package routes

import (
	"order-service/controllers"

	"github.com/gin-gonic/gin"
)

// Khởi tạo route cho Order API
func OrderRoutes(router *gin.Engine) {
	orderGroup := router.Group("/orders")
	{
		orderGroup.POST("/", controllers.CreateOrder)                  // Buyer tạo đơn hàng
		orderGroup.GET("/", controllers.GetOrders)                     // Buyer lấy danh sách đơn
		orderGroup.GET("/:order_id", controllers.GetOrderDetail)       // Buyer xem chi tiết đơn hàng
		orderGroup.PATCH("/:order_id/cancel", controllers.CancelOrder) // Buyer hủy đơn hàng
	}
}
