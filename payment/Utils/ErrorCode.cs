using System.Net;

namespace payment.Utils
{
    public class ErrorCode
    {
        public int Code { get; }
        public string Message { get; }
        public HttpStatusCode StatusCode { get; }

        private ErrorCode(int code, string message, HttpStatusCode statusCode)
        {
            Code = code;
            Message = message;
            StatusCode = statusCode;
        }

        // ✅ General Errors
        public static readonly ErrorCode UNCATEGORIZED_EXCEPTION = new(9999, "Uncategorized error", HttpStatusCode.InternalServerError);
        public static readonly ErrorCode INVALID_KEY = new(1001, "Invalid key", HttpStatusCode.BadRequest);

        // ✅ User Errors
        public static readonly ErrorCode USER_EXISTED = new(1002, "User already exists", HttpStatusCode.BadRequest);
        public static readonly ErrorCode USERNAME_INVALID = new(1003, "Username must be at least {min} characters", HttpStatusCode.BadRequest);
        public static readonly ErrorCode INVALID_PASSWORD = new(1004, "Password must be at least {min} characters", HttpStatusCode.BadRequest);
        public static readonly ErrorCode USER_NOT_EXISTED = new(1005, "User does not exist", HttpStatusCode.NotFound);
        public static readonly ErrorCode UNAUTHENTICATED = new(1006, "Unauthenticated", HttpStatusCode.Unauthorized);
        public static readonly ErrorCode UNAUTHORIZED = new(1007, "You do not have permission", HttpStatusCode.Forbidden);
        public static readonly ErrorCode INVALID_DOB = new(1008, "Your age must be at least {min}", HttpStatusCode.BadRequest);
        public static readonly ErrorCode INVALID_EMAIL = new(1010, "Invalid email format", HttpStatusCode.BadRequest);

        // ✅ Product Errors
        public static readonly ErrorCode PRODUCT_NOT_FOUND = new(2001, "Product not found", HttpStatusCode.NotFound);
        public static readonly ErrorCode PRODUCT_EXISTED = new(2002, "Product already exists", HttpStatusCode.BadRequest);
        public static readonly ErrorCode PRODUCT_INVALID_PRICE = new(2003, "Product price must be greater than zero", HttpStatusCode.BadRequest);
        public static readonly ErrorCode PRODUCT_INVALID_STOCK = new(2004, "Stock quantity must be non-negative", HttpStatusCode.BadRequest);
        public static readonly ErrorCode PRODUCT_CATEGORY_NOT_FOUND = new(2005, "Category associated with the product not found", HttpStatusCode.NotFound);
        public static readonly ErrorCode PRODUCT_UNAUTHORIZED = new(2006, "You do not have permission to modify this product", HttpStatusCode.Forbidden);
        public static readonly ErrorCode PRODUCT_INVALID_ID = new(2007, "Product ID cannot be null", HttpStatusCode.BadRequest);
        public static readonly ErrorCode PRODUCT_INVALID_QUANTITY = new(2008, "Quantity must be at least {min}", HttpStatusCode.BadRequest);
        public static readonly ErrorCode PRODUCT_INVALID_NAME = new(2009, "Product ID cannot be empty", HttpStatusCode.BadRequest);

        // ✅ Category Errors
        public static readonly ErrorCode CATEGORY_NOT_FOUND = new(3001, "Category not found", HttpStatusCode.NotFound);
        public static readonly ErrorCode CATEGORY_EXISTED = new(3002, "Category already exists", HttpStatusCode.BadRequest);
        public static readonly ErrorCode CATEGORY_DELETE_FAILED = new(3003, "Cannot delete category with existing products", HttpStatusCode.BadRequest);
        public static readonly ErrorCode CATEGORY_INVALID_NAME = new(3004, "Category cannot be blank", HttpStatusCode.BadRequest);
        public static readonly ErrorCode CATEGORY_INVALID_ID = new(3005, "Category ID cannot be null", HttpStatusCode.BadRequest);

        // ✅ Order Errors
        public static readonly ErrorCode ORDER_NOT_FOUND = new(4001, "Order not found", HttpStatusCode.NotFound);
        public static readonly ErrorCode ORDER_INVALID_STATUS = new(4002, "Invalid order status", HttpStatusCode.BadRequest);
        public static readonly ErrorCode ORDER_PAYMENT_FAILED = new(4003, "Order payment failed", HttpStatusCode.BadRequest);

        // ✅ Discount Errors
        public static readonly ErrorCode DISCOUNT_NOT_FOUND = new(5001, "Discount code not found", HttpStatusCode.NotFound);
        public static readonly ErrorCode DISCOUNT_EXPIRED = new(5002, "Discount code has expired", HttpStatusCode.BadRequest);
        public static readonly ErrorCode DISCOUNT_INVALID = new(5003, "Invalid discount code", HttpStatusCode.BadRequest);

        // ✅ Cart Errors
        public static readonly ErrorCode CART_NOT_FOUND = new(6001, "Cart not found", HttpStatusCode.NotFound);
        public static readonly ErrorCode CART_EMPTY = new(6002, "Cart is empty", HttpStatusCode.BadRequest);
        public static readonly ErrorCode CART_ITEM_NOT_FOUND = new(6003, "Cart item not found", HttpStatusCode.NotFound);
        public static readonly ErrorCode CART_ITEM_ALREADY_EXISTS = new(6004, "Item already exists in cart", HttpStatusCode.BadRequest);
        public static readonly ErrorCode CART_INVALID_QUANTITY = new(6005, "Invalid quantity for cart item", HttpStatusCode.BadRequest);
        public static readonly ErrorCode CART_PRODUCT_OUT_OF_STOCK = new(6006, "Product is out of stock", HttpStatusCode.BadRequest);
        public static readonly ErrorCode CART_UNAUTHORIZED_ACCESS = new(6007, "You do not have permission to modify this cart", HttpStatusCode.Forbidden);

        // ✅ Payment Errors (BỔ SUNG CHO `Payment Service`)
        public static readonly ErrorCode PAYMENT_NOT_FOUND = new(7001, "Payment record not found", HttpStatusCode.NotFound);
        public static readonly ErrorCode PAYMENT_FAILED = new(7002, "Payment transaction failed", HttpStatusCode.BadRequest);
        public static readonly ErrorCode PAYMENT_PENDING = new(7003, "Payment is still pending", HttpStatusCode.BadRequest);
        public static readonly ErrorCode PAYMENT_METHOD_NOT_SUPPORTED = new(7004, "Payment method not supported", HttpStatusCode.BadRequest);
        public static readonly ErrorCode PAYMENT_UNAUTHORIZED = new(7005, "You do not have permission to process this payment", HttpStatusCode.Forbidden);
    }
}
