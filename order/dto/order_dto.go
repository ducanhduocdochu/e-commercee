package dto

import "github.com/go-playground/validator/v10"

// @Description Dữ liệu cần thiết để tạo đơn hàng đại diện cho một mục trong đơn hàng
type OrderItemRequest struct {
	Quantity  int     `json:"quantity" bson:"quantity" validate:"required,min=1"`     // ✅ Số lượng tối thiểu là 1
	ProductId string  `json:"product_id" bson:"product_id" validate:"required,uuid4"` // ✅ Phải có product_id hợp lệ (UUID)
	Price     float64 `json:"price" bson:"price" validate:"required,min=0"`           // ✅ Giá sản phẩm không được âm
}

// @Description Dữ liệu cần thiết để tạo đơn hàng
type CreateOrderRequest struct {
	Items         []OrderItemRequest `json:"items" bson:"items" validate:"required,dive"`                                         // ✅ Phải có ít nhất một sản phẩm
	DiscountId    string             `json:"discount_id,omitempty" bson:"discount_id,omitempty" validate:"omitempty,uuid4"`       // ✅ Nếu có thì phải là UUID
	DiscountValue float64            `json:"discount_value,omitempty" bson:"discount_value,omitempty" validate:"omitempty,min=0"` // ✅ Không được âm
	OrderValue    float64            `json:"order_value" bson:"order_value" validate:"required,min=0"`                            // ✅ Không được âm
}

// ✅ Struct chứa danh sách sản phẩm từ API
type ProductResponse struct {
	Code   int           `json:"code"`
	Result []ProductItem `json:"result"`
}

// ProductItem - Struct đại diện cho một sản phẩm trong hệ thống
type ProductItem struct {
	ID          string  `json:"id" validate:"required,uuid4"`
	Name        string  `json:"name" validate:"required"`
	Description string  `json:"description" validate:"required"`
	Price       float64 `json:"price" validate:"required,min=0"`
	Stock       int     `json:"stock" validate:"required,min=0"`
	CategoryID  string  `json:"categoryId" validate:"required,uuid4"`
	SellerID    string  `json:"sellerId" validate:"required,uuid4"`
}

// ✅ Hàm validate request sử dụng `validator`
var validate = validator.New()

func ValidateCreateOrderRequest(req CreateOrderRequest) error {
	return validate.Struct(req)
}
