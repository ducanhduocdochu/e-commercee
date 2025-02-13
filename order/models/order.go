package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Order struct {
	ID            primitive.ObjectID `bson:"_id,omitempty" json:"id"`                                // ID đơn hàng
	UserID        string             `bson:"user_id" json:"user_id"`                                 // ID Buyer (Người mua)
	SellerID      string             `bson:"seller_id" json:"seller_id"`                             // ID Seller (Người bán)
	Items         []Item             `bson:"items" json:"items"`                                     // Danh sách sản phẩm
	TotalAmount   float64            `bson:"total_amount" json:"total_amount"`                       // Tổng giá trị đơn hàng
	DiscountCode  *string            `bson:"discount_code,omitempty" json:"discount_code,omitempty"` // Mã giảm giá (nullable)
	DiscountValue float64            `bson:"discount_value" json:"discount_value"`                   // Giá trị giảm giá (nếu có)
	FinalAmount   float64            `bson:"final_amount" json:"final_amount"`                       // Tổng tiền sau khi áp mã giảm giá
	PaymentStatus string             `bson:"payment_status" json:"payment_status"`                   // Trạng thái thanh toán (Pending, Paid, Failed)
	OrderStatus   string             `bson:"order_status" json:"order_status"`                       // Trạng thái đơn hàng (Pending, Processing, Shipped, Completed, Cancelled)
	CreatedAt     time.Time          `bson:"created_at,omitempty" json:"created_at"`                 // Thời gian tạo đơn
	UpdatedAt     time.Time          `bson:"updated_at,omitempty" json:"updated_at"`                 // Thời gian cập nhật đơn
}
