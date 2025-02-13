package main

import (
	"fmt"
	"log"
	config "order/configs"
	"order/routes"
	"os"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
)

func main() {
	// Load biến môi trường từ .env
	if err := godotenv.Load(); err != nil {
		fmt.Println("⚠️ Không tìm thấy file .env, sử dụng giá trị mặc định")
	}

	// Kết nối database
	config.ConnectDatabase()

	// Khởi tạo Gin router
	router := gin.Default()

	// Đăng ký routes cho Order
	routes.OrderRoutes(router)

	// Khởi động server
	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}
	log.Printf("🚀 Server đang chạy tại http://localhost:%s", port)
	router.Run(":" + port)
}
