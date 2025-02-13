package services

import (
	"order-service/models"
	"order-service/repositories"
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

// Xử lý tạo đơn hàng
func CreateOrderService(userID, sellerID string, items []models.Item, discountCode *string) (*models.Order, error) {
	// Tính toán tổng giá trị đơn hàng
	var totalAmount float64
	for _, item := range items {
		totalAmount += item.Price * float64(item.Quantity)
	}

	// Kiểm tra giảm giá
	discountValue := 0.0
	if discountCode != nil {
		// Giả sử đã kiểm tra Redis và trừ mã giảm giá
		discountValue = 50.0 // Ví dụ giả lập giảm 50K
	}

	finalAmount := totalAmount - discountValue

	order := models.Order{
		ID:            primitive.NewObjectID(),
		UserID:        userID,
		SellerID:      sellerID,
		Items:         items,
		TotalAmount:   totalAmount,
		DiscountCode:  discountCode,
		DiscountValue: discountValue,
		FinalAmount:   finalAmount,
		PaymentStatus: "Pending",
		OrderStatus:   "Pending",
		CreatedAt:     time.Now(),
		UpdatedAt:     time.Now(),
	}

	_, err := repositories.CreateOrder(order)
	if err != nil {
		return nil, err
	}

	return &order, nil
}
