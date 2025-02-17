package services

import (
	"bytes"
	"context"
	"encoding/json"
	"errors"
	"net/http"
	"order/dto"
	"order/models"
	"order/utils"
	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

// OrderService - Interface định nghĩa các phương thức
type OrderService interface {
	CreateOrder(userID string, req dto.CreateOrderRequest) (*models.Order, error)
	GetOrdersByBuyer(userID string) ([]models.Order, error)
	GetOrderDetail(orderID string) (*models.Order, error)
	CancelOrder(orderID string) error
	GetOrdersBySeller(sellerID string) ([]models.Order, error)
	ConfirmOrder(orderID string) error
}

// OrderServiceImpl - Triển khai OrderService
type OrderServiceImpl struct {
	OrderCollection *mongo.Collection
}

// NewOrderService - Khởi tạo OrderService
func NewOrderService(orderCollection *mongo.Collection) OrderService {
	return &OrderServiceImpl{
		OrderCollection: orderCollection,
	}
}

// ✅ **Tạo đơn hàng mới**
func (s *OrderServiceImpl) CreateOrder(userID string, req dto.CreateOrderRequest) (*models.Order, error) {
	// ✅ Chuẩn bị body gửi API lấy thông tin sản phẩm
	productRequest := map[string][]string{"productIds": {}}
	for _, item := range req.Items {
		productRequest["productIds"] = append(productRequest["productIds"], item.ProductId)
	}

	// ✅ Encode JSON
	jsonBody, err := json.Marshal(productRequest)
	if err != nil {
		return nil, errors.New(utils.InvalidKey.Message)
	}

	// ✅ Gửi request đến Product Service
	apiURL := "http://localhost:5000/product/internal/products/batch"
	resp, err := http.Post(apiURL, "application/json", bytes.NewBuffer(jsonBody))
	if err != nil {
		return nil, errors.New(utils.ProductAPIError.Message)
	}
	defer resp.Body.Close()

	// ✅ Kiểm tra response từ Product API
	if resp.StatusCode != http.StatusOK {
		return nil, errors.New(utils.ProductNotFound.Message)
	}

	// ✅ Decode dữ liệu từ Product Service
	var apiResponse struct {
		Code   int               `json:"code"`
		Result []dto.ProductItem `json:"result"`
	}
	if err := json.NewDecoder(resp.Body).Decode(&apiResponse); err != nil {
		return nil, errors.New(utils.ProductParseError.Message)
	}

	// ✅ Xử lý dữ liệu sản phẩm và tính tổng tiền đơn hàng
	orderItems := make([]models.OrderItem, 0)
	orderValue := 0.0

	for _, itemReq := range req.Items {
		for _, product := range apiResponse.Result {
			if itemReq.ProductId == product.ID {
				orderItem := models.OrderItem{
					ProductID:   product.ID,
					Quantity:    itemReq.Quantity,
					SellerID:    product.SellerID,
					Name:        product.Name,
					Description: product.Description,
					Price:       product.Price,
				}
				orderItems = append(orderItems, orderItem)
				orderValue += product.Price * float64(itemReq.Quantity)
			}
		}
	}

	// ✅ Xử lý lỗi khi giá trị đơn hàng không khớp
	if orderValue != req.OrderValue {
		return nil, errors.New(utils.OrderMismatchValue.Message)
	}

	// ✅ Kiểm tra Discount API
	discountValue := 0.0
	if req.DiscountId != "" {
		discountAPIURL := "http://localhost:7000/discount/internal/" + req.DiscountId
		discountResp, err := http.Get(discountAPIURL)
		if err != nil {
			return nil, errors.New(utils.DiscountAPIError.Message)
		}
		defer discountResp.Body.Close()

		if discountResp.StatusCode != http.StatusOK {
			return nil, errors.New(utils.DiscountInvalid.Message)
		}
	}

	// ✅ Lưu order vào MongoDB
	order := models.Order{
		ID:              primitive.NewObjectID(),
		UserID:          userID,
		Items:           orderItems,
		DiscountValue:   discountValue,
		OrderValue:      orderValue,
		FinalOrderValue: orderValue - discountValue,
		PaymentStatus:   "Pending",
		OrderStatus:     "Pending",
		CreatedAt:       time.Now(),
		UpdatedAt:       time.Now(),
	}
	_, err = s.OrderCollection.InsertOne(context.TODO(), order)
	if err != nil {
		return nil, errors.New(utils.InternalServerError.Message)
	}

	return &order, nil
}

// 2️⃣ **Lấy danh sách đơn hàng của Buyer**
func (s *OrderServiceImpl) GetOrdersByBuyer(userID string) ([]models.Order, error) {
	cursor, err := s.OrderCollection.Find(context.TODO(), bson.M{"user_id": userID})
	if err != nil {
		return nil, errors.New(utils.OrderNotFound.Message)
	}

	var orders []models.Order
	if err := cursor.All(context.TODO(), &orders); err != nil {
		return nil, errors.New(utils.InternalServerError.Message)
	}

	return orders, nil
}

// 3️⃣ **Xem chi tiết đơn hàng**
func (s *OrderServiceImpl) GetOrderDetail(orderID string) (*models.Order, error) {
	objID, err := primitive.ObjectIDFromHex(orderID)
	if err != nil {
		return nil, errors.New(utils.OrderNotFound.Message)
	}

	var order models.Order
	err = s.OrderCollection.FindOne(context.TODO(), bson.M{"_id": objID}).Decode(&order)
	if err != nil {
		return nil, errors.New(utils.OrderNotFound.Message)
	}

	return &order, nil
}

// 4️⃣ **Hủy đơn hàng (Chỉ khi `Pending`)**
func (s *OrderServiceImpl) CancelOrder(orderID string) error {
	objID, err := primitive.ObjectIDFromHex(orderID)
	if err != nil {
		return errors.New(utils.OrderNotFound.Message)
	}

	filter := bson.M{"_id": objID, "order_status": "Pending"}
	update := bson.M{"$set": bson.M{
		"order_status": "Cancelled",
		"updated_at":   time.Now(),
	}}

	result, err := s.OrderCollection.UpdateOne(context.TODO(), filter, update)
	if err != nil || result.MatchedCount == 0 {
		return errors.New(utils.OrderInvalidStatus.Message)
	}

	return nil
}

// 5️⃣ **Lấy danh sách đơn hàng của Seller**
func (s *OrderServiceImpl) GetOrdersBySeller(sellerID string) ([]models.Order, error) {
	cursor, err := s.OrderCollection.Find(context.TODO(), bson.M{"items.seller_id": sellerID})
	if err != nil {
		return nil, errors.New(utils.OrderNotFound.Message)
	}

	var orders []models.Order
	if err := cursor.All(context.TODO(), &orders); err != nil {
		return nil, errors.New(utils.InternalServerError.Message)
	}

	return orders, nil
}

// 6️⃣ **Xác nhận đơn hàng (Seller)**
func (s *OrderServiceImpl) ConfirmOrder(orderID string) error {
	objID, err := primitive.ObjectIDFromHex(orderID)
	if err != nil {
		return errors.New(utils.OrderNotFound.Message)
	}

	filter := bson.M{"_id": objID, "order_status": "Pending"}
	update := bson.M{"$set": bson.M{
		"order_status": "Confirmed",
		"updated_at":   time.Now(),
	}}

	result, err := s.OrderCollection.UpdateOne(context.TODO(), filter, update)
	if err != nil || result.MatchedCount == 0 {
		return errors.New(utils.OrderInvalidStatus.Message)
	}

	return nil
}
