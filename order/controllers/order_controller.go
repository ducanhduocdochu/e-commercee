// Package controllers - Quản lý API cho đơn hàng
// @title Order Service API
// @version 1.0
// @description API cho phép tạo, quản lý đơn hàng trong hệ thống
// @termsOfService http://swagger.io/terms/
// @contact.name Hỗ trợ kỹ thuật
// @contact.email support@ecommerce.com
// @license.name Apache 2.0
// @license.url http://www.apache.org/licenses/LICENSE-2.0.html
// @host localhost:8000
// @BasePath /
package controllers

import (
	"net/http"
	"order/dto"
	middleware "order/middlewares"
	"order/services"
	"order/utils"

	"github.com/gin-gonic/gin"
)

// OrderController - Chứa các API liên quan đến Order
type OrderController struct {
	OrderService services.OrderService
}

// NewOrderController - Hàm tạo mới OrderController
func NewOrderController(orderService services.OrderService) *OrderController {
	return &OrderController{
		OrderService: orderService,
	}
}

// CreateOrder - Tạo đơn hàng mới
// @Summary Tạo đơn hàng mới
// @Description API tạo đơn hàng với danh sách sản phẩm và thông tin giảm giá (nếu có).
// @Tags Order
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param request body dto.CreateOrderRequest true "Dữ liệu đơn hàng"
// @Success 201 {object} utils.ApiResponse "Đơn hàng được tạo thành công"
// @Failure 400 {object} utils.ApiResponse "Lỗi request không hợp lệ"
// @Failure 401 {object} utils.ApiResponse "Người dùng chưa xác thực"
// @Failure 500 {object} utils.ApiResponse "Lỗi hệ thống"
// @Router /orders [post]
func (oc *OrderController) CreateOrder(c *gin.Context) {
	var req dto.CreateOrderRequest
	if err := c.ShouldBindJSON(&req); err != nil {
		utils.HandleErrorResponse(c, utils.InvalidKey, err)
		return
	}

	// ✅ Kiểm tra validate request
	if err := dto.ValidateCreateOrderRequest(req); err != nil {
		utils.HandleErrorResponse(c, utils.InvalidRequest, err)
		return
	}

	// ✅ Lấy thông tin user từ JWT middleware
	claims, exists := c.Get("user")
	if !exists {
		utils.HandleErrorResponse(c, utils.Unauthenticated, nil)
		return
	}
	userClaims := claims.(*middleware.UserClaims)

	// ✅ Gọi service để xử lý
	createdOrder, err := oc.OrderService.CreateOrder(userClaims.UserID, req)
	if err != nil {
		utils.HandleErrorResponse(c, utils.InternalServerError, err)
		return
	}

	c.JSON(http.StatusCreated, utils.NewSuccessResponse(createdOrder))
}

// GetOrdersByBuyer - Lấy danh sách đơn hàng của Buyer
// @Summary Lấy danh sách đơn hàng của người mua
// @Description Trả về danh sách đơn hàng của người mua hiện tại.
// @Tags Order
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} utils.ApiResponse "Danh sách đơn hàng"
// @Failure 401 {object} utils.ApiResponse "Người dùng chưa xác thực"
// @Failure 404 {object} utils.ApiResponse "Không tìm thấy đơn hàng"
// @Router /orders [get]
func (oc *OrderController) GetOrdersByBuyer(c *gin.Context) {
	claims, exists := c.Get("user")
	if !exists {
		utils.HandleErrorResponse(c, utils.Unauthenticated, nil)
		return
	}
	userClaims := claims.(*middleware.UserClaims)

	orders, err := oc.OrderService.GetOrdersByBuyer(userClaims.UserID)
	if err != nil {
		utils.HandleErrorResponse(c, utils.OrderNotFound, err)
		return
	}

	c.JSON(http.StatusOK, utils.NewSuccessResponse(orders))
}

// GetOrderDetail - Xem chi tiết đơn hàng
// @Summary Xem chi tiết đơn hàng
// @Description Lấy thông tin chi tiết về một đơn hàng cụ thể.
// @Tags Order
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param order_id path string true "ID của đơn hàng"
// @Success 200 {object} utils.ApiResponse "Thông tin chi tiết đơn hàng"
// @Failure 401 {object} utils.ApiResponse "Người dùng chưa xác thực"
// @Failure 404 {object} utils.ApiResponse "Không tìm thấy đơn hàng"
// @Router /orders/detail/{order_id} [get]
func (oc *OrderController) GetOrderDetail(c *gin.Context) {
	orderID := c.Param("order_id")

	order, err := oc.OrderService.GetOrderDetail(orderID)
	if err != nil {
		utils.HandleErrorResponse(c, utils.OrderNotFound, err)
		return
	}

	c.JSON(http.StatusOK, utils.NewSuccessResponse(order))
}

// CancelOrder - Hủy đơn hàng
// @Summary Hủy đơn hàng
// @Description Chỉ có thể hủy đơn hàng nếu nó đang ở trạng thái `Pending`.
// @Tags Order
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param order_id path string true "ID của đơn hàng"
// @Success 200 {object} utils.ApiResponse "Đơn hàng đã bị hủy"
// @Failure 401 {object} utils.ApiResponse "Người dùng chưa xác thực"
// @Failure 400 {object} utils.ApiResponse "Trạng thái đơn hàng không hợp lệ"
// @Router /orders/cancel/{order_id} [patch]
func (oc *OrderController) CancelOrder(c *gin.Context) {
	orderID := c.Param("order_id")

	err := oc.OrderService.CancelOrder(orderID)
	if err != nil {
		utils.HandleErrorResponse(c, utils.OrderInvalidStatus, err)
		return
	}

	c.JSON(http.StatusOK, utils.NewSuccessResponse("Đơn hàng đã bị hủy"))
}

// GetOrdersBySeller - Lấy danh sách đơn hàng của Seller
// @Summary Lấy danh sách đơn hàng của người bán
// @Description Trả về danh sách đơn hàng do người bán quản lý.
// @Tags Order
// @Accept json
// @Produce json
// @Security BearerAuth
// @Success 200 {object} utils.ApiResponse "Danh sách đơn hàng của Seller"
// @Failure 401 {object} utils.ApiResponse "Người dùng chưa xác thực"
// @Failure 404 {object} utils.ApiResponse "Không tìm thấy đơn hàng"
// @Router /seller/orders [get]
func (oc *OrderController) GetOrdersBySeller(c *gin.Context) {
	claims, exists := c.Get("user")
	if !exists {
		utils.HandleErrorResponse(c, utils.Unauthenticated, nil)
		return
	}
	userClaims := claims.(*middleware.UserClaims)

	orders, err := oc.OrderService.GetOrdersBySeller(userClaims.UserID)
	if err != nil {
		utils.HandleErrorResponse(c, utils.OrderNotFound, err)
		return
	}

	c.JSON(http.StatusOK, utils.NewSuccessResponse(orders))
}

// ConfirmOrder - Xác nhận đơn hàng
// @Summary Xác nhận đơn hàng
// @Description Người bán xác nhận đơn hàng để tiến hành giao hàng.
// @Tags Order
// @Accept json
// @Produce json
// @Security BearerAuth
// @Param order_id path string true "ID của đơn hàng"
// @Success 200 {object} utils.ApiResponse "Đơn hàng đã được xác nhận"
// @Failure 401 {object} utils.ApiResponse "Người dùng chưa xác thực"
// @Failure 400 {object} utils.ApiResponse "Trạng thái đơn hàng không hợp lệ"
// @Router /seller/orders/confirm/{order_id} [patch]
func (oc *OrderController) ConfirmOrder(c *gin.Context) {
	orderID := c.Param("order_id")

	err := oc.OrderService.ConfirmOrder(orderID)
	if err != nil {
		utils.HandleErrorResponse(c, utils.OrderInvalidStatus, err)
		return
	}

	c.JSON(http.StatusOK, utils.NewSuccessResponse("Đơn hàng đã được xác nhận"))
}
