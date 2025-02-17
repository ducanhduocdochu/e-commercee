using System.Text.Json.Serialization;

namespace payment.Utils
{
    public class ApiResponse<T>
    {
        [JsonPropertyName("code")]
        public int Code { get; set; } = 1000; // ✅ Mặc định là thành công (1000)

        [JsonPropertyName("message")]
        public string Message { get; set; }

        [JsonPropertyName("result")]
        [JsonIgnore(Condition = JsonIgnoreCondition.WhenWritingNull)] // ✅ Ẩn nếu null
        public T Result { get; set; }

        // ✅ Constructor mặc định (Fix lỗi CS1729)
        public ApiResponse()
        {
        }

        // ✅ Constructor có tham số
        public ApiResponse(int code, string message, T result = default)
        {
            Code = code;
            Message = message;
            Result = result;
        }

        // ✅ Static method cho success response
        public static ApiResponse<T> Success(T result, string message = "Success")
        {
            return new ApiResponse<T>(1000, message, result);
        }

        // ✅ Static method cho error response
        public static ApiResponse<T> Error(int code, string message)
        {
            return new ApiResponse<T>(code, message);
        }
    }
}
