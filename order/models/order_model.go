package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

// OrderItem đại diện cho một mục trong đơn hàng
type OrderItem struct {
	ProductID   string  `json:"product_id" bson:"product_id"`
	Quantity    int     `json:"quantity" bson:"quantity"`
	SellerID    string  `json:"seller_id" bson:"seller_id"`
	Name        string  `json:"name" bson:"name"`
	Description string  `json:"description" bson:"description"`
	Price       float64 `json:"price" bson:"price"`
}

// Order đại diện cho đơn hàng
type Order struct {
	ID              primitive.ObjectID `json:"_id,omitempty" bson:"_id,omitempty"`
	UserID          string             `json:"user_id" bson:"user_id"`
	Items           []OrderItem        `json:"items" bson:"items"`
	DiscountId      string             `json:"discount_id,omitempty" bson:"discount_id,omitempty"`
	DiscountValue   float64            `json:"discount_value,omitempty" bson:"discount_value,omitempty"`
	OrderValue      float64            `json:"order_value" bson:"order_value"`
	FinalOrderValue float64            `json:"final_order_value" bson:"final_order_value"`
	PaymentStatus   string             `json:"payment_status" bson:"payment_status"`
	OrderStatus     string             `json:"order_status" bson:"order_status"`
	CreatedAt       time.Time          `json:"created_at" bson:"created_at"`
	UpdatedAt       time.Time          `json:"updated_at" bson:"updated_at"`
}
