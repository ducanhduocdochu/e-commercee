package repositories

import (
	"context"
	"order-service/config"
	"order-service/models"
	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
)

var orderCollection *mongo.Collection = config.MongoClient.Database("ecommerce").Collection("orders")

// Lưu đơn hàng
func CreateOrder(order models.Order) (*mongo.InsertOneResult, error) {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	return orderCollection.InsertOne(ctx, order)
}

// Tìm đơn hàng theo ID
func GetOrderByID(orderID string) (*models.Order, error) {
	var order models.Order
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	err := orderCollection.FindOne(ctx, bson.M{"_id": orderID}).Decode(&order)
	return &order, err
}
