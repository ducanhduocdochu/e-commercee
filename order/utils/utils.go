package utils

import (
	"log"
	"net/http"

	"github.com/gin-gonic/gin"
)

// ✅ ErrorCode đại diện cho lỗi hệ thống giống Java Enum
type ErrorCode struct {
	Code       int    `json:"code"`
	Message    string `json:"message"`
	StatusCode int    `json:"status_code"`
}

// ✅ Danh sách các lỗi hệ thống
var (
	// General Errors
	InternalServerError = ErrorCode{9999, "Internal server error", http.StatusInternalServerError}
	InvalidKey          = ErrorCode{1001, "Invalid key", http.StatusBadRequest}
	InvalidRequest      = ErrorCode{9998, "Invalid body", http.StatusBadRequest}

	// User Errors
	UserExisted     = ErrorCode{1002, "User already exists", http.StatusBadRequest}
	UsernameInvalid = ErrorCode{1003, "Username must be at least X characters", http.StatusBadRequest}
	InvalidPassword = ErrorCode{1004, "Password must be at least X characters", http.StatusBadRequest}
	UserNotExisted  = ErrorCode{1005, "User does not exist", http.StatusNotFound}
	Unauthenticated = ErrorCode{1006, "Unauthenticated", http.StatusUnauthorized}
	Unauthorized    = ErrorCode{1007, "You do not have permission", http.StatusForbidden}
	InvalidDOB      = ErrorCode{1008, "Your age must be at least X", http.StatusBadRequest}
	InvalidEmail    = ErrorCode{1010, "Invalid email format", http.StatusBadRequest}

	// Product Errors
	ProductNotFound     = ErrorCode{2001, "Product not found", http.StatusNotFound}
	ProductAPIError     = ErrorCode{2002, "Error when calling Product API", http.StatusInternalServerError}
	ProductInvalidPrice = ErrorCode{2003, "Product price must be greater than zero", http.StatusBadRequest}
	ProductInvalidStock = ErrorCode{2004, "Stock quantity must be non-negative", http.StatusBadRequest}
	ProductParseError   = ErrorCode{2005, "Failed to parse Product API response", http.StatusInternalServerError}

	// Order Errors
	OrderNotFound      = ErrorCode{4001, "Order not found", http.StatusNotFound}
	OrderInvalidStatus = ErrorCode{4002, "Invalid order status", http.StatusBadRequest}
	OrderPaymentFailed = ErrorCode{4003, "Order payment failed", http.StatusBadRequest}
	OrderMismatchValue = ErrorCode{4004, "Order value does not match the provided value", http.StatusBadRequest}

	// Discount Errors
	DiscountNotFound    = ErrorCode{5001, "Discount code not found", http.StatusNotFound}
	DiscountExpired     = ErrorCode{5002, "Discount code has expired", http.StatusBadRequest}
	DiscountInvalid     = ErrorCode{5003, "Invalid discount code", http.StatusBadRequest}
	DiscountAPIError    = ErrorCode{5004, "Error when calling Discount API", http.StatusInternalServerError}
	DiscountParseError  = ErrorCode{5005, "Failed to parse Discount API response", http.StatusInternalServerError}
	DiscountMinNotMet   = ErrorCode{5006, "Order does not meet the minimum amount for discount", http.StatusBadRequest}
	DiscountTypeInvalid = ErrorCode{5007, "Invalid discount type", http.StatusBadRequest}
)

// ✅ ApiResponse giúp tạo response chuẩn
// @Description ApiResponse giúp tạo response chuẩn
type ApiResponse struct {
	Code    int         `json:"code"`
	Message string      `json:"message"`
	Result  interface{} `json:"result,omitempty"`
}

// ✅ NewSuccessResponse trả về response thành công
func NewSuccessResponse(data interface{}) ApiResponse {
	return ApiResponse{
		Code:    1000,
		Message: "Success",
		Result:  data,
	}
}

// ✅ NewErrorResponse trả về response lỗi dựa trên ErrorCode và lỗi chi tiết
func NewErrorResponse(errCode ErrorCode, err error) ApiResponse {
	if err != nil {
		log.Printf("❌ Lỗi xảy ra: Code=%d, Message=%s, Error=%v", errCode.Code, errCode.Message, err)
	} else {
		log.Printf("❌ Lỗi xảy ra: Code=%d, Message=%s", errCode.Code, errCode.Message)
	}

	return ApiResponse{
		Code:    errCode.Code,
		Message: errCode.Message,
	}
}

// ✅ Gin Middleware xử lý lỗi và response
func HandleErrorResponse(c *gin.Context, errCode ErrorCode, err error) {
	log.Printf("err: ", err)
	c.JSON(errCode.StatusCode, NewErrorResponse(errCode, err))
}
