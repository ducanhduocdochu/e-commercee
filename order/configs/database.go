package config

import (
	"context"
	"fmt"
	"log"
	"os"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

// MongoDB client
var MongoClient *mongo.Client

// Redis client
// var RedisClient *redis.Client

func ConnectDatabase() {
	// Kết nối MongoDB
	mongoURI := os.Getenv("MONGO_URI")
	clientOptions := options.Client().ApplyURI(mongoURI)

	client, err := mongo.Connect(context.TODO(), clientOptions)
	if err != nil {
		log.Fatal(err)
	}
	MongoClient = client

	fmt.Println("✅ Kết nối MongoDB thành công!")

	// Kết nối Redis
	// RedisClient = redis.NewClient(&redis.Options{
	// 	Addr: os.Getenv("REDIS_URI"),
	// 	DB:   0,
	// })

	// ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	// defer cancel()

	// _, err = RedisClient.Ping(ctx).Result()
	// if err != nil {
	// 	log.Fatal(err)
	// }
	// fmt.Println("✅ Kết nối Redis thành công!")
}
