package main

import (
	"fmt"
	"log"
	"order/configs"
	"order/controllers"
	"order/routes"
	"order/services"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
	swaggerFiles "github.com/swaggo/files" // 📌 Thêm import này
	ginSwagger "github.com/swaggo/gin-swagger"

	_ "order/docs" // 📌 Import tài liệu Swagger
)

func main() {
	// Load environment variables từ .env
	err := godotenv.Load()
	if err != nil {
		log.Fatal("❌ Lỗi khi tải tệp .env")
	}

	// Kết nối MongoDB
	configs.ConnectDatabase()

	// ✅ Khởi tạo Order Service
	orderService := services.NewOrderService(configs.MongoClient.Database("order_service").Collection("orders"))

	// ✅ Khởi tạo Order Controller
	orderController := controllers.NewOrderController(orderService)

	// ✅ Khởi tạo Router
	r := gin.Default()

	// ✅ Cấu hình Swagger
	r.GET("/swagger/*any", ginSwagger.WrapHandler(swaggerFiles.Handler)) // 📌 Fix lỗi ở đây

	// ✅ Định nghĩa các routes
	routes.OrderRoutes(r, orderController)

	// ✅ Chạy server trên cổng 8000
	port := ":8000"
	fmt.Println("🚀 Server đang chạy trên cổng", port)
	r.Run(port)
}
