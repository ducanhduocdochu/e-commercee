package configs

import (
	"context"
	"fmt"
	"log"
	"os"
	"time"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

// MongoDB Client toàn cục
var MongoClient *mongo.Client

// ConnectDatabase - Kết nối MongoDB với kiểm tra lỗi rõ ràng
func ConnectDatabase() {
	// Lấy URI từ biến môi trường
	mongoURI := os.Getenv("MONGO_URI")
	if mongoURI == "" {
		log.Fatal("❌ Lỗi: MONGO_URI không được thiết lập trong môi trường")
	}

	// Cấu hình kết nối MongoDB
	clientOptions := options.Client().ApplyURI(mongoURI)
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	// Kết nối MongoDB
	client, err := mongo.Connect(ctx, clientOptions)
	if err != nil {
		log.Fatalf("❌ Không thể kết nối MongoDB: %v", err)
	}

	// Kiểm tra kết nối bằng lệnh `ping`
	err = client.Ping(ctx, nil)
	if err != nil {
		log.Fatalf("❌ MongoDB không phản hồi: %v", err)
	}

	// Gán giá trị cho MongoClient (toàn cục)
	MongoClient = client

	fmt.Println("✅ Kết nối MongoDB thành công!")
}

// GetCollection - Hàm lấy Collection theo tên Database
func GetCollection(collectionName string) *mongo.Collection {
	if MongoClient == nil {
		log.Fatal("❌ Lỗi: MongoClient chưa được khởi tạo. Vui lòng gọi `ConnectDatabase()` trước")
	}
	return MongoClient.Database("order_service").Collection(collectionName)
}
