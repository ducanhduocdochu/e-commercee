# 📦 Microservice Ecommerce

## 🏗️ Tổng quan kiến trúc

Dự án này là một **nền tảng thương mại điện tử được xây dựng dựa trên kiến trúc microservices**, giúp tối ưu hóa **hiệu suất và khả năng mở rộng** của hệ thống. Thay vì sử dụng mô hình nguyên khối (monolithic), hệ thống được chia thành nhiều dịch vụ nhỏ (microservices), mỗi dịch vụ đảm nhận một nhiệm vụ cụ thể như quản lý người dùng, quản lý sản phẩm, xử lý đơn hàng, thanh toán, giảm giá, giỏ hàng và nhiều tính năng khác. Điều này giúp cải thiện tính linh hoạt, khả năng mở rộng và bảo trì của hệ thống.

---

## 📌 Mục Lục

1. [Giới thiệu dự án](#1️⃣-giới-thiệu-dự-án)
2. [Thiết kế hệ thống](#2️⃣-thiết-kế-hệ-thống)
3. [Xây dựng API](#3️⃣-xây-dựng-api)
4. [Xây dựng kiểm thử](#4️⃣-xây-dựng-kiểm-thử)
5. [Xây dựng hệ thống](#5️⃣-xây-dựng-hệ-thống)
6. [Các bài kiểm thử với hệ thống](#6️⃣-các-bài-kiểm-thử-với-hệ-thống)
7. [Tổng kết và cải tiến](#7️⃣-tổng-kết-và-cải-tiến)

---

## 1️⃣ Giới thiệu dự án  

### 🛒 Nền tảng thương mại điện tử với kiến trúc Microservices  

Dự án này là một **hệ thống thương mại điện tử** được xây dựng theo **kiến trúc microservices**, nhằm đảm bảo **hiệu suất cao, khả năng mở rộng linh hoạt và dễ bảo trì**.  

Hệ thống cho phép người dùng **tìm kiếm, đặt hàng, thanh toán** với nhiều phương thức khác nhau, đồng thời hỗ trợ quản trị viên **quản lý sản phẩm, đơn hàng, giảm giá và người dùng** một cách hiệu quả.  

---

### 📌 **Mục tiêu của dự án**  

✅ **Tách biệt các dịch vụ** theo mô hình microservices để đảm bảo tính linh hoạt.  
✅ **Cải thiện hiệu suất** bằng caching, load balancing và message queue.  
✅ **Hỗ trợ đa nền tảng**, dễ dàng tích hợp với hệ thống bên thứ ba.  
✅ **Bảo mật cao**, sử dụng JWT và OAuth2 để xác thực và phân quyền.  
✅ **Triển khai linh hoạt** trên Docker, Kubernetes, hỗ trợ cloud-native.

---

### 🏗 **Tổng quan hệ thống**  

Kiến trúc hệ thống bao gồm các **microservices** riêng biệt, giao tiếp thông qua **REST API, gRPC (updating...) và Message Queue**:  

1️⃣ **API Gateway** – Quản lý traffic, xác thực JWT, rate-limiting.  
2️⃣ **Identity Service** – Xác thực, phân quyền, quản lý tài khoản.  
3️⃣ **Product Service** – Quản lý sản phẩm, danh mục, tìm kiếm.  
4️⃣ **Cart Service** – Lưu trạng thái giỏ hàng.  
5️⃣ **Order Service** – Xử lý đơn hàng, cập nhật trạng thái.  
6️⃣ **Payment Service** – Tích hợp thanh toán (COD, Online).  
7️⃣ **Discount Service** – Quản lý mã giảm giá, chương trình khuyến mãi.  
7️⃣ **Scheduled Discount Service**: Hệ thống lập lịch mã giảm giá, khuyến mãi và các ưu đãi khách hàng.
8️⃣ **Notification Service** – Gửi email, SMS, thông báo real-time.  (updating...)
8️⃣ **Review & Rating Service**: Hỗ trợ đánh giá sản phẩm, phản hồi từ khách hàng. (...updating)

---

🚀 **Dự án hướng đến việc triển khai cloud-native, đóng gói với docker, triển khai với kubernetes**, hỗ trợ **AWS**, đảm bảo **mở rộng linh hoạt và vận hành hiệu quả** trong môi trường **production**.  (updating...)

🚀 Dự án hướng đến việc phát triển với các **công cụ CICD, công cụ theo dõi moniroting**, sử dụng **github** làm nơi lưu trữ source code, kết nối với **jenkins** để triển khai CICD, một số service theo dõi khác có thể triển khai với **prometheus, grafana**.  (updating...)

🚀 Dự án hướng đến việc vận hàng với lượng người dùng cao, tập trung xây dựng các kịch bản tiếp cận **concurrency**  (updating...)

📌 **Tiếp tục đọc phần tiếp theo để tìm hiểu chi tiết về từng thành phần!** 🔥  

---

## 2️⃣ Thiết kế hệ thống

### 🏗️ Kiến trúc tổng quan

Hệ thống được chia thành nhiều microservices, mỗi dịch vụ đảm nhiệm một chức năng cụ thể:

- **API Gateway**: Quản lý và định tuyến các yêu cầu từ người dùng đến các microservices phù hợp. Hỗ trợ xác thực và cân bằng tải.
- **Identity Service**: Xác thực, quản lý tài khoản người dùng và quyền truy cập (JWT, OAuth2).
- **Product Service**: Quản lý danh mục sản phẩm, thông tin sản phẩm, tồn kho.
- **Cart Service**: Xử lý giỏ hàng, cho phép người dùng thêm/xóa sản phẩm trước khi đặt hàng.
- **Order Service**: Quản lý đơn hàng, xác nhận và xử lý trạng thái đơn hàng.
- **Payment Service**: Xử lý thanh toán (COD, thanh toán trực tuyến, webhook từ cổng thanh toán).
- **Discount Service**: Quản lý mã giảm giá, khuyến mãi và các ưu đãi khách hàng.
- **Scheduled Discount Service**: Hệ thống lập lịch mã giảm giá, khuyến mãi và các ưu đãi khách hàng.
- **Notification Service**: Gửi thông báo qua email, SMS, hoặc đẩy thông báo (push notification). (...updating)
- **Review & Rating Service**: Hỗ trợ đánh giá sản phẩm, phản hồi từ khách hàng. (...updating)

---
### 🏗️ Sơ đồ hệ thống

![image](https://github.com/user-attachments/assets/c4bf38dc-b9b4-48ab-9c70-870a80c1570a)

Hệ thống thương mại điện tử này được thiết kế theo kiến trúc microservices, giúp đảm bảo tính mở rộng, linh hoạt và dễ bảo trì. Các dịch vụ giao tiếp với nhau thông qua API Gateway, RabbitMQ, gRPC, và Redis Stream. API Gateway đóng vai trò trung gian, xử lý xác thực JWT, phân quyền và định tuyến request đến các dịch vụ như Identity Service, Cart Service, Product Service, Order Service, Payment Service, Discount Service, v.v. Hệ thống sử dụng Spring Boot cho Identity, Cart, Product và API Gateway, trong khi Order Service được xây dựng bằng Golang (Gin) và gRPC để xử lý hiệu quả dữ liệu phi cấu trúc trên MongoDB. Payment Service được phát triển bằng .NET Core, tích hợp RabbitMQ để xử lý giao dịch, còn Discount Service sử dụng NestJS với MongoDB để quản lý giảm giá. Bên cạnh đó, Schedule Discount Service chạy trên ExpressJS, sử dụng Redis Stream để cập nhật giảm giá theo thời gian thực. Cơ sở dữ liệu được phân bổ hợp lý, với MySQL cho dữ liệu quan hệ, MongoDB cho dữ liệu phi cấu trúc, Redis để caching và RabbitMQ để giao tiếp bất đồng bộ. Kiến trúc này giúp hệ thống có khả năng mở rộng theo chiều ngang, dễ dàng tích hợp với các dịch vụ bên ngoài, đồng thời tối ưu hiệu suất và bảo mật trong môi trường phân tán.

---

### 🔗 Luồng hoạt động chính

#### 🚀 1️⃣ Flow Đăng ký & Đăng nhập Người Dùng

📌 **Mô tả**:
- Người dùng mới tạo tài khoản.
- Hệ thống xác thực tài khoản qua email hoặc OTP.
- Người dùng đăng nhập và nhận JWT Token.

🔗 **Các dịch vụ liên quan**:
- Identity Service (Xác thực người dùng)
- API Gateway (Trung gian xử lý request)
- Notification Service (Gửi email/SMS OTP)

🔄 **Luồng hoạt động**:
- User gửi yêu cầu đăng ký (POST /api/auth/register).
- Identity Service kiểm tra thông tin hợp lệ.
- Gửi email xác nhận hoặc OTP qua Notification Service.
- User nhập OTP để kích hoạt tài khoản (POST /api/auth/verify). (...updating)
- User đăng nhập (POST /api/auth/login).
- Identity Service cấp JWT Token cho user.
- JWT Token được gửi lại cho user, sử dụng cho các request sau.

#### 🛍 2️⃣ Flow Duyệt & Tìm Kiếm Sản Phẩm

📌 **Mô tả**:
- Người dùng duyệt danh sách sản phẩm theo danh mục.
- Tìm kiếm sản phẩm bằng từ khóa.
- Bộ lọc giúp tìm sản phẩm nhanh hơn.

🔗 **Các dịch vụ liên quan**:
- Product Service (Quản lý sản phẩm)
- API Gateway (Định tuyến request)

🔄 **Luồng hoạt động**:
- User truy cập danh sách sản phẩm (GET /api/products).
- Product Service truy vấn danh mục & sản phẩm từ DB.
- Gửi kết quả về cho user.
- Nếu tìm kiếm, Product Service thực hiện tìm kiếm nâng cao với Elasticsearch (GET /api/products/search?q=...). (...updating)

#### 🛒 3️⃣ Flow Thêm & Xóa Sản Phẩm Khỏi Giỏ Hàng

📌 **Mô tả**:
- Người dùng thêm sản phẩm vào giỏ hàng.
- Xóa hoặc cập nhật số lượng sản phẩm trong giỏ hàng.

🔗 **Các dịch vụ liên quan**:
- Cart Service (Quản lý giỏ hàng)
- API Gateway
- Redis (Lưu cache giỏ hàng) (...updating)

🔄 **Luồng hoạt động**:
- User thêm sản phẩm vào giỏ (POST /api/cart/add).
- Cart Service lưu thông tin vào Redis.
- User kiểm tra giỏ hàng (GET /api/cart).
- Xóa sản phẩm khỏi giỏ (DELETE /api/cart/remove/{productId}).

#### 📦 4️⃣ Flow Tạo Đơn Hàng

📌 **Mô tả**:
- Người dùng tiến hành đặt hàng từ giỏ hàng.
- Hệ thống xác nhận đơn hàng và xử lý thanh toán.

🔗 **Các dịch vụ liên quan**:
- Order Service (Tạo đơn hàng)
- Cart Service (Lấy thông tin giỏ hàng)
- Discount Service (Áp dụng mã giảm giá)
- Payment Service (Xử lý thanh toán)
- RabbitMQ (Xử lý bất đồng bộ)

🔄 **Luồng hoạt động**:
- User xác nhận giỏ hàng để tạo đơn (POST /api/orders).
- Order Service lấy thông tin giỏ hàng từ Cart Service.
- Kiểm tra mã giảm giá với Discount Service.
- Tạo đơn hàng và lưu vào MySQL.
- Gửi event "order.created" đến RabbitMQ để Payment Service xử lý thanh toán.

#### 💳 5️⃣ Flow Thanh Toán Đơn Hàng

📌 **Mô tả**:
- Người dùng chọn phương thức thanh toán.
- Hệ thống xử lý thanh toán online hoặc COD.

🔗 **Các dịch vụ liên quan**:
- Payment Service
- Order Service
- External Payment Gateway (VNPay, PayPal, Stripe)
- RabbitMQ (Xử lý sự kiện)

🔄 **Luồng hoạt động**:
- User chọn phương thức thanh toán (POST /api/payments/select-payment/{orderId}).
- Nếu COD → Đơn hàng chờ xác nhận.
- Nếu Online → Redirect user đến cổng thanh toán (GET /api/payments/redirect/{orderId}).
- Cổng thanh toán gửi webhook sau khi hoàn thành giao dịch (POST /api/payments/webhook).
- Payment Service cập nhật trạng thái thanh toán.
- Order Service nhận thông báo từ RabbitMQ để cập nhật trạng thái đơn hàng.

#### 🚚 6️⃣ Flow Xử Lý Vận Chuyển (...updating)

📌 **Mô tả**:
- Sau khi thanh toán thành công, đơn hàng được giao cho nhà vận chuyển.
- Người dùng theo dõi trạng thái giao hàng.

🔗 **Các dịch vụ liên quan**:
- Order Service
- Shipping Service (Tích hợp với bên thứ ba như GHN, GHTK)
- Notification Service

🔄 **Luồng hoạt động**:
- Order Service gửi request đến Shipping Service (POST /api/shipping/create).
- Shipping Service cập nhật tracking number & trạng thái vận chuyển.
- Người dùng kiểm tra trạng thái đơn hàng (GET /api/orders/{orderId}/tracking).
- Shipping Service gửi event "order.delivered" đến Notification Service.
- Notification Service gửi email/SMS thông báo giao hàng thành công.

#### ❌ 7️⃣ Flow Hủy Đơn Hàng & Hoàn Tiền

📌 **Mô tả**:
- Người dùng hủy đơn khi đơn chưa giao.
- Hệ thống hoàn tiền nếu thanh toán trước.

🔗 **Các dịch vụ liên quan**:
- Order Service
- Payment Service
- RabbitMQ

🔄 **Luồng hoạt động**:
- User yêu cầu hủy đơn hàng (POST /api/orders/cancel/{orderId}).
- Order Service kiểm tra trạng thái đơn hàng.
- Nếu đơn chưa giao, cập nhật trạng thái là "Cancelled".
- Gửi event "order.cancelled" đến RabbitMQ.
- Payment Service hoàn tiền nếu user đã thanh toán trước.

#### ⭐ 8️⃣ Flow Đánh Giá Sản Phẩm (...updating)

📌 **Mô tả**:
- Người dùng đánh giá và bình luận sản phẩm sau khi nhận hàng.

🔗 **Các dịch vụ liên quan**:
- Product Service
- Review Service

🔄 **Luồng hoạt động**:
- User viết đánh giá (POST /api/reviews/{productId}).
- Review Service kiểm tra và lưu đánh giá vào DB.
- Tính điểm đánh giá trung bình cho sản phẩm (GET /api/products/{productId}/ratings).

#### 🔔 🔟 Flow Gửi Thông Báo (...updating)

📌 **Mô tả**:
- Hệ thống gửi email/SMS khi có sự kiện quan trọng.

🔗 **Các dịch vụ liên quan**:
- Notification Service
- RabbitMQ

🔄 **Luồng hoạt động**:
- Order Service gửi event "order.created" đến RabbitMQ.
- Notification Service lắng nghe và gửi email xác nhận đơn hàng.
- Khi đơn hàng được giao → gửi thông báo "Đơn hàng đã đến nơi".

---

### 🛠️ Công nghệ sử dụng

Hệ thống thương mại điện tử này áp dụng nhiều công nghệ hiện đại để đảm bảo hiệu suất, bảo mật và khả năng mở rộng. Dưới đây là các công nghệ quan trọng được sử dụng trong từng thành phần của hệ thống.

#### 1️⃣ Ngôn ngữ lập trình & Frameworks
Hệ thống sử dụng nhiều ngôn ngữ lập trình và framework khác nhau, phù hợp với từng dịch vụ cụ thể:

**Backend Services**:
- Spring Boot (Java) → Được sử dụng cho Identity, Cart, Product, API Gateway do tính ổn định và hệ sinh thái mạnh mẽ.
- Golang (Gin + gRPC) → Xây dựng Order Service, sử dụng RabbitMQ để xử lý sự kiện.
- .NET Core → Xây dựng Payment Service, tích hợp nhiều phương thức thanh toán khác nhau.
- NestJS (Node.js) → Dùng cho Discount Service, dễ dàng tích hợp với hệ sinh thái JavaScript.
- Express.js (Node.js) → Dùng cho Schedule Discount, xử lý các tác vụ tự động, sử dụng Redis Stream để kết nối với Discount Service.

**Frontend**: (...updating)
- React.js (Next.js) → Dùng cho website người dùng, tối ưu SEO và trải nghiệm tốt hơn.
- Flutter → Dùng cho ứng dụng di động, hỗ trợ đa nền tảng Android/iOS.

#### 2️⃣ Cơ sở dữ liệu & Bộ nhớ đệm
![image](https://github.com/user-attachments/assets/16b9595b-0956-42ea-af8f-3b2089760b54)

#### 3️⃣ API Gateway & Giao tiếp giữa các dịch vụ

- API Gateway (Spring Boot): Định tuyến request, xác thực JWT, load balancing, rate limiting.
- Giao tiếp đồng bộ (REST API): Được sử dụng khi cần phản hồi ngay lập tức.
- Giao tiếp bất đồng bộ (RabbitMQ, Redis Stream): Xử lý các tác vụ nền và sự kiện.
- gRPC (Golang): Giúp tối ưu hiệu suất cho Order Service.

#### 4️⃣ Xử lý thanh toán (Payment Service - .NET Core)

- Hỗ trợ nhiều phương thức thanh toán: VNPay, MoMo, PayPal, Stripe, COD.
- Webhook nhận phản hồi từ cổng thanh toán và cập nhật trạng thái đơn hàng.

#### 5️⃣ Bảo mật hệ thống

**Xác thực & Phân quyền**
- OAuth2 + JWT → Quản lý phiên đăng nhập. (Dùng Spring Security)
- Role-Based Access Control (RBAC) → Phân quyền người dùng.
- API Gateway Security → Kiểm soát traffic, rate limiting.

**Bảo vệ dữ liệu**
- AES-256 Encryption → Mã hóa dữ liệu nhạy cảm.
- TLS/SSL → Bảo mật truyền dữ liệu.
- Audit Logging → Theo dõi lịch sử truy cập API.

#### 6️⃣ Triển khai & CI/CD (...Updating)

- Docker & Kubernetes → Chạy các service độc lập, dễ mở rộng.
- GitHub Actions / GitLab CI → Tự động hóa build & deploy.
- Helm Charts → Quản lý triển khai Kubernetes.
- ArgoCD → Hỗ trợ GitOps.

#### 7️⃣ Monitoring & Logging (...Updating)

- Prometheus + Grafana → Giám sát tài nguyên hệ thống.
- Jaeger → Theo dõi request giữa các service (Distributed Tracing).
- ELK Stack (Elasticsearch, Logstash, Kibana) → Thu thập & phân tích logs.

---

### 🛠️ Trình bày cụ thể từng service
#### 1️⃣ Identity Service (Quản lý Người Dùng & Xác Thực)

📌 **Chức năng chính**:
- Đăng ký, đăng nhập và quản lý thông tin người dùng.
- Xác thực và phân quyền người dùng bằng JWT & OAuth2.
- Hỗ trợ Refresh Token để cấp lại token mà không cần đăng nhập lại.
- Cung cấp API nội bộ để các service khác có thể kiểm tra quyền truy cập của user.

🛠 **Công nghệ sử dụng**:

- Ngôn ngữ: Java + Spring Boot
- Cơ sở dữ liệu: MySQL (lưu thông tin người dùng)
- Bảo mật: OAuth2, JWT, BCrypt (hash mật khẩu)
- Giao tiếp: REST API
- API Gateway: Kết nối với API Gateway để xác thực request

#### 2️⃣ Cart Service (Xử lý Giỏ Hàng)

📌 **Chức năng chính**:
- Quản lý giỏ hàng của người dùng (thêm, xóa, cập nhật sản phẩm).
- Kiểm tra tồn kho trước khi tạo đơn hàng.
- Lưu giỏ hàng tạm thời để giảm tải lên database.

🛠 **Công nghệ sử dụng**:
- Ngôn ngữ: Java + Spring Boot
- Cơ sở dữ liệu: MySQL (lưu giỏ hàng)
- Cache: Redis (lưu giỏ hàng tạm thời)
- Giao tiếp: REST API
- API Gateway: Định tuyến request từ client

#### 3️⃣ Product Service (Quản lý Sản Phẩm & Danh Mục)

📌 **Chức năng chính**:
- CRUD sản phẩm, danh mục.
- Hỗ trợ tìm kiếm sản phẩm theo tên, danh mục, giá.
- Quản lý hình ảnh sản phẩm (tích hợp với hệ thống lưu trữ file như MinIO/S3).

🛠 **Công nghệ sử dụng**:
- Ngôn ngữ: Java + Spring Boot
- Cơ sở dữ liệu: MySQL (lưu thông tin sản phẩm)
- Tìm kiếm: Elasticsearch (tăng tốc tìm kiếm)
- API nội bộ: Để Order Service lấy thông tin sản phẩm
- Giao tiếp: REST API

#### 4️⃣ Order Service (Xử lý Đơn Hàng)

📌 **Chức năng chính**:
- Người dùng tạo đơn hàng từ giỏ hàng.
- Kiểm tra tồn kho và tính tổng giá trị đơn hàng.
- Gửi sự kiện order.created đến RabbitMQ để Payment Service xử lý thanh toán.

🛠 **Công nghệ sử dụng**:
- Ngôn ngữ: Golang + Gin Framework
- Giao tiếp: gRPC (giao tiếp nội bộ với Payment Service)
- Cơ sở dữ liệu: MongoDB (lưu đơn hàng)
- Message Queue: RabbitMQ (gửi sự kiện thanh toán)
- API nội bộ: Kết nối với Product & Cart Service để lấy thông tin sản phẩm

#### 5️⃣ Payment Service (Xử lý Thanh Toán)

📌 **Chức năng chính**:
- Người dùng chọn phương thức thanh toán (COD, Online).
- Nếu chọn thanh toán online, hệ thống redirect đến cổng thanh toán (VNPay, PayPal, Stripe).
- Nhận phản hồi từ cổng thanh toán qua webhook.
- Gửi sự kiện payment.success đến RabbitMQ nếu thanh toán thành công.

🛠 **Công nghệ sử dụng**:
- Ngôn ngữ: .NET Core
- Giao tiếp: REST API + Webhook (tích hợp cổng thanh toán)
- Cơ sở dữ liệu: MySQL (lưu giao dịch thanh toán)
- Message Queue: RabbitMQ (nhận sự kiện từ Order Service)
- Bảo mật: Xác thực request từ API Gateway

#### 6️⃣ Discount Service (Quản lý Mã Giảm Giá)

📌 **Chức năng chính:**
- Người dùng nhập mã giảm giá trước khi thanh toán.
- Kiểm tra điều kiện áp dụng mã giảm giá.
- Lưu trữ mã giảm giá và quản lý hiệu lực.

🛠 **Công nghệ sử dụng**:
- Ngôn ngữ: NestJS
- Cơ sở dữ liệu: MongoDB (lưu mã giảm giá)
- Giao tiếp: REST API
- API nội bộ: Liên kết với Order Service để áp dụng giảm giá
- Redis Stream: Kết nối với Schedule Discount Service để cập nhật mã giảm giá theo thời gian thực

#### 7️⃣ Schedule Discount Service (Tự động Cập Nhật Giảm Giá)

📌 **Chức năng chính**:
- Tự động kích hoạt hoặc vô hiệu hóa mã giảm giá theo thời gian.
- Scale theo mức độ tải của hệ thống.
- Gửi sự kiện đến Discount Service để cập nhật giảm giá.

🛠 **Công nghệ sử dụng**:
- Ngôn ngữ: ExpressJS
- Giao tiếp: Redis Stream (gửi thông báo đến Discount Service)
- Lập lịch: Node.js Cron Jobs
- API nội bộ: Cập nhật giảm giá theo thời gian thực

#### 8️⃣ API Gateway (Cổng Giao Tiếp Chính)

📌 **Chức năng chính**:
- Nhận request từ client và chuyển tiếp đến service tương ứng.
- Xác thực JWT để kiểm soát quyền truy cập.
- Hỗ trợ Rate Limiting, Load Balancing, Logging.

🛠 **Công nghệ sử dụng**:
- Ngôn ngữ: Java + Spring Cloud Gateway
- Bảo mật: OAuth2 + JWT Authentication
- Giao tiếp: REST API (chuyển tiếp request đến các service)
- Cơ sở dữ liệu: Không cần database (chỉ là proxy)

#### 9️⃣ Notification Service (Gửi Thông Báo)

📌 **Chức năng chính**:
- Gửi thông báo qua email, SMS, Firebase khi đơn hàng thay đổi trạng thái.
- Hỗ trợ gửi thông báo real-time bằng WebSocket.

🛠 **Công nghệ sử dụng**:
- Ngôn ngữ: Node.js (NestJS)
- Giao tiếp: WebSocket + REST API
- Message Queue: RabbitMQ (nhận sự kiện từ Order & Payment Service)

---

### 🛠️ Cơ sở dự liệu cần xây dựng

#### 1️⃣ Cơ sở dữ liệu của Identity, Cart, Product, API Gateway (MySQL)
Các dịch vụ này sử dụng MySQL để lưu trữ dữ liệu quan hệ, đảm bảo tính toàn vẹn và truy vấn hiệu quả.

1.1 **Bảng users (Quản lý người dùng)**
```sql
CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    username VARCHAR(255) UNIQUE NOT NULL COLLATE utf8mb4_unicode_ci,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    dob DATE,
    is_verified BOOLEAN DEFAULT FALSE
);
```
id: UUID của người dùng.
username, email: Duy nhất.
password: Lưu trữ mật khẩu mã hóa.
is_verified: Xác thực người dùng.

1.2 **Bảng roles (Vai trò)**
```sql
CREATE TABLE roles (
    name VARCHAR(50) PRIMARY KEY,
    description TEXT
);
```

name: Vai trò của người dùng (ADMIN, USER, SELLER).
description: Mô tả vai trò.

1.3 **Bảng permissions (Quyền)**
```sql
CREATE TABLE permissions (
    name VARCHAR(50) PRIMARY KEY,
    description TEXT
);
```

1.4 **Bảng role_permissions (Liên kết Role với Permissions)**
```sql
CREATE TABLE role_permissions (
    role_name VARCHAR(50),
    permission_name VARCHAR(50),
    PRIMARY KEY (role_name, permission_name),
    FOREIGN KEY (role_name) REFERENCES roles(name),
    FOREIGN KEY (permission_name) REFERENCES permissions(name)
);
```

1.5 **Bảng user_roles (Liên kết User với Role)**
```sql
CREATE TABLE user_roles (
    user_id VARCHAR(36),
    role_name VARCHAR(50),
    PRIMARY KEY (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (role_name) REFERENCES roles(name)
);
```

1.6 **Bảng categories (Danh mục sản phẩm)**
```sql
CREATE TABLE categories (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

1.7 **Bảng products (Sản phẩm)**
```sql
CREATE TABLE products (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    seller_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);
```

1.8 **Bảng carts (Giỏ hàng)**
```sql
CREATE TABLE carts (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    buyer_id VARCHAR(36) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

1.9 **Bảng cart_items (Sản phẩm trong giỏ hàng)**
```sql
CREATE TABLE cart_items (
    id VARCHAR(36) PRIMARY KEY DEFAULT (UUID()),
    cart_id VARCHAR(36) NOT NULL,
    product_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cart_id) REFERENCES carts(id)
);
```

#### 2️⃣ Cơ sở dữ liệu của Order Service (MongoDB)
Dữ liệu đơn hàng có tính linh hoạt cao, do đó sử dụng MongoDB với mô hình NoSQL.

**Cấu trúc Collection orders**
```json
{
  "_id": ObjectId("64f12b3b9a3f5b0014a7c0b2"),
  "user_id": "123456",
  "items": [
    {
      "product_id": "abc123",
      "quantity": 2,
      "seller_id": "seller01",
      "name": "Laptop",
      "description": "Dell XPS 15",
      "price": 1500.00
    }
  ],
  "discount_id": "DISCOUNT50",
  "discount_value": 50.00,
  "order_value": 3000.00,
  "final_order_value": 2950.00,
  "payment_status": "Pending",
  "order_status": "Processing",
  "created_at": "2025-02-16T12:00:00Z",
  "updated_at": "2025-02-16T12:00:00Z"
}
```
items: Mảng chứa danh sách sản phẩm trong đơn hàng.
discount_value: Giá trị giảm giá.
payment_status: Trạng thái thanh toán (Pending, Completed).
order_status: Trạng thái đơn hàng (Processing, Shipped).

#### 3️⃣ Cơ sở dữ liệu của Payment Service (MongoDB)
Thanh toán có thể thay đổi linh hoạt nên sử dụng MongoDB.

**Cấu trúc Collection payments**
```json
{
  "_id": ObjectId("64f12b3b9a3f5b0014a7c0c3"),
  "order_id": "order123",
  "user_id": "user001",
  "amount": 2950.00,
  "payment_method": "Online",
  "status": "Completed",
  "created_at": "2025-02-16T12:30:00Z",
  "updated_at": "2025-02-16T12:30:00Z"
}
```

payment_method: COD, Online.
status: Pending, Completed, Failed, Cancelled.

#### 4️⃣ Cơ sở dữ liệu của Discount Service (MongoDB)
Giảm giá không cần tính toàn vẹn dữ liệu mạnh mẽ nên sử dụng MongoDB.

**Cấu trúc Collection discounts**
```json
{
  "_id": ObjectId("64f12b3b9a3f5b0014a7c0e4"),
  "discount_name": "Black Friday Sale",
  "discount_code": "BLACKFRIDAY50",
  "discount_type": "Percent",
  "discount_value": 50,
  "discount_start_date": "2025-11-25T00:00:00Z",
  "discount_end_date": "2025-11-30T23:59:59Z",
  "discount_max_uses": 1000,
  "discount_use_count": 0,
  "discount_invisable": false
}
```

#### 5️⃣ Cơ sở dữ liệu của Scheduled Discount (MongoDB)
Sử dụng Redis Stream để xử lý scheduled discount.

Scheduled Discounts lưu trong MongoDB.
Khi đến thời gian kích hoạt, Redis Stream sẽ phát tín hiệu để áp dụng discount.

## 3️⃣ Xây dựng API
Dưới đây là các link api và docs api của từng service (updating):

**Identity**: 
http://localhost:4000/identity/swagger-ui/index.html
http://localhost:4000/identity/v3/api-docs/public-api

---

**Product**: 
http://localhost:5000/product/swagger-ui/index.html
http://localhost:5000/product/v3/api-docs/public-api

---

**Cart**: 
http://localhost:6001/cart/swagger-ui/index.html
http://localhost:6001/cart/v3/api-docs/public-api

---

**Payment**: 
http://localhost:5154/swagger/index.html
http://localhost:5154/swagger/v1/swagger.json

---

**Order**: 
http://localhost:6001/cart/swagger-ui/index.html
http://localhost:6001/cart/v3/api-docs/public-api

---

**Discount**: 
http://localhost:7000/api/docs
http://localhost:7000/api/docs-json

---


### Identity Service

#### 📌 Giới thiệu
Identity Service chịu trách nhiệm quản lý xác thực, phân quyền người dùng, quản lý vai trò (Roles) và quyền hạn (Permissions). Hệ thống này đảm bảo rằng chỉ những người dùng được ủy quyền mới có thể thực hiện các hành động nhất định trong hệ thống.

- **Base URL**: http://localhost:4000/identity
- **Authentication**: Sử dụng JWT
- **License**: Apache 2.0

#### 🔑 Authentication API
1️⃣ **Đăng nhập**
- Endpoint: POST /auth/token
- Mô tả: Nhận token truy cập dựa trên username và password.
- Request Body:
```json
{
  "username": "user@example.com",
  "password": "securepassword"
}
```
- Response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "token": "jwt-token",
    "authenticated": true
  }
}
```

2️⃣ **Làm mới token**
- Endpoint: POST /auth/refresh
- Mô tả: Tạo token mới từ refresh token.
- Request Body:
```json
{
  "token": "existing-refresh-token"
}
```
- Response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "token": "new-jwt-token"
  }
}
```

3️⃣ **Đăng xuất**
- Endpoint: POST /auth/logout
- Mô tả: Hủy bỏ token hiện tại.
- Request Body:
```json
{
  "token": "existing-jwt-token"
}
```
- Response:
```json
{
  "code": 1000,
  "message": "Successfully logged out"
}
```

4️⃣ **Kiểm tra token**
- Endpoint: POST /auth/introspect
- Mô tả: Kiểm tra tính hợp lệ của token.
- Request Body:
```json
{
  "token": "jwt-token"
}
```
- Response:
```json
{
  "code": 1000,
  "message": "Valid token",
  "result": {
    "valid": true
  }
}
```

#### 👤 User API
1️⃣ **Lấy thông tin người dùng**
- Endpoint: GET /users/{userId}
- Mô tả: Lấy thông tin của một người dùng dựa vào ID.
- Response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "1234",
    "username": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "roles": ["USER"]
  }
}
```

2️⃣ **Cập nhật thông tin người dùng**
- Endpoint: PUT /users/{userId}
- Mô tả: Cập nhật thông tin người dùng.
- Request Body:
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "dob": "1990-01-01",
  "roles": ["USER"]
}
```

3️⃣ **Xóa người dùng**
- Endpoint: DELETE /users/{userId}
- Mô tả: Xóa người dùng dựa vào ID.

4️⃣ **Lấy danh sách người dùng**
- Endpoint: GET /users
- Mô tả: Lấy danh sách toàn bộ người dùng trong hệ thống.

5️⃣ **Tạo người dùng mới**
- Endpoint: POST /users
- Mô tả: Tạo một người dùng mới.
- Request Body:
```json
{
  "username": "newuser@example.com",
  "email": "newuser@example.com",
  "password": "strongpassword",
  "firstName": "New",
  "lastName": "User",
  "dob": "1995-02-14",
  "isVerified": false
}
```

#### 🎭 Role API
1️⃣ **Lấy danh sách Role**
- Endpoint: GET /roles
- Mô tả: Trả về danh sách các role hiện có.

2️⃣ **Tạo Role mới**
- Endpoint: POST /roles
- Mô tả: Tạo một role mới trong hệ thống.
- Request Body:
```json
{
  "name": "ADMIN",
  "description": "Administrator role",
  "permissions": ["READ", "WRITE"]
}
```

3️⃣ **Xóa Role**
- Endpoint: DELETE /roles/{role}
- Mô tả: Xóa một role dựa trên tên role.

#### 🔏 Permission API
1️⃣ **Lấy danh sách Permission**
- Endpoint: GET /permissions
- Mô tả: Trả về danh sách các permission hiện có.

2️⃣ **Tạo Permission mới**
- Endpoint: POST /permissions
- Mô tả: Tạo một permission mới trong hệ thống.
```json
{
  "name": "DELETE_USER",
  "description": "Allows deleting users"
}
```

3️⃣ **Xóa Permission**
- Endpoint: DELETE /permissions/{permission}
- Mô tả: Xóa một permission dựa trên tên permission.

---

### Product Service
#### 📌 Giới thiệu
Product Service chịu trách nhiệm quản lý sản phẩm và danh mục sản phẩm. Hệ thống hỗ trợ API để thực hiện các thao tác CRUD trên sản phẩm và danh mục, đồng thời cung cấp các API nội bộ để giao tiếp giữa các dịch vụ.

- **Base URL:** `http://localhost:5000/product`
- **Authentication:** JWT

#### 🛒 Product API
1️⃣ **Lấy thông tin sản phẩm**
- Endpoint: GET /products/{productId}
- Mô tả: Lấy thông tin của một sản phẩm dựa vào ID
- Response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "1234",
    "name": "Laptop",
    "description": "Gaming Laptop",
    "price": 1500.0,
    "stock": 10,
    "categoryId": "5678",
    "sellerId": "seller_01"
  }
}
```

2️⃣ **Cập nhật sản phẩm**
- Endpoint: PUT /products/{productId}
- Mô tả: Cập nhật thông tin của một sản phẩm
- Request Body:
```json
{
  "name": "Laptop",
  "description": "Updated description",
  "price": 1400.0,
  "stock": 15
}
```

3️⃣ **Xóa sản phẩm**
- Endpoint: DELETE /products/{productId}
- Mô tả: Xóa sản phẩm dựa vào ID

#### 📂 Category API
1️⃣ **Lấy thông tin danh mục**
- Endpoint: GET /categories/{categoryId}
- Mô tả: Lấy thông tin của một danh mục sản phẩm
- Response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": {
    "id": "5678",
    "name": "Electronics"
  }
}
```

2️⃣ **Cập nhật danh mục**
- Endpoint: PUT /categories/{categoryId}
- Mô tả: Cập nhật thông tin danh mục
- Request Body:
```json
{
  "name": "Updated Electronics"
}
```

3️⃣ **Xóa danh mục**
- Endpoint: DELETE /categories/{categoryId}
- Mô tả: Xóa danh mục dựa vào ID

#### 🔍 Danh sách & Tìm kiếm sản phẩm
1️⃣ **Lấy danh sách sản phẩm**
- Endpoint: GET /products
- Mô tả: Lấy danh sách sản phẩm với phân trang
- Query Parameters:
pageSize (mặc định: 10)
pageNumber (mặc định: 0)
sortBy (mặc định: createdAt)
sortDirection (ASC hoặc DESC)

2️⃣ **Tạo sản phẩm mới**
- Endpoint: POST /products
- Mô tả: Thêm sản phẩm vào hệ thống
- Request Body:
```json
{
  "name": "Laptop",
  "description": "High-end gaming laptop",
  "price": 1500.0,
  "stock": 10,
  "categoryId": "5678"
}
```

🔗 **API Nội bộ (Internal API)**
1️⃣ Lấy danh sách sản phẩm theo danh sách ID
- Endpoint: POST /internal/products/batch
- Mô tả: Trả về danh sách sản phẩm dựa vào danh sách ID
- Request Body:
```json
{
  "productIds": ["1234", "5678", "9012"]
}
```

2️⃣ **Lấy thông tin sản phẩm nội bộ**
- Endpoint: GET /internal/products/{productId}
- Mô tả: Trả về thông tin sản phẩm cho các service nội bộ

#### 📦 Quản lý tồn kho
1️⃣ **Cập nhật số lượng tồn kho**
- Endpoint: PATCH /products/{productId}/stock
- Mô tả: Cập nhật số lượng tồn kho của sản phẩm
- Query Parameters:
stock: số lượng tồn kho mới

#### 🏪 Tìm kiếm sản phẩm theo tiêu chí
1️⃣ **Tìm sản phẩm theo người bán**
- Endpoint: GET /products/seller/{sellerId}
- Mô tả: Lấy danh sách sản phẩm của một người bán
- Query Parameters:
pageSize, pageNumber, sortBy, sortDirection

2️⃣ **Tìm sản phẩm theo danh mục**
- Endpoint: GET /products/category/{categoryId}
- Mô tả: Lấy danh sách sản phẩm theo danh mục
- Query Parameters:
pageSize, pageNumber, sortBy, sortDirection

### Cart Service

#### 📌 Giới thiệu
Cart Service chịu trách nhiệm quản lý giỏ hàng của **Buyer**. Hệ thống cho phép người dùng thêm, cập nhật, xem và xóa sản phẩm trong giỏ hàng.

- **Base URL:** `http://localhost:6000/cart`
- **Authentication:** JWT

#### 🛍️ Cart API

1️⃣ **Lấy giỏ hàng của Buyer**
- Endpoint: GET /
- Mô tả: Trả về danh sách sản phẩm trong giỏ hàng của Buyer với phân trang và sắp xếp.
- Query Parameters:
pageSize (mặc định: 10)
pageNumber (mặc định: 0)
sortBy (mặc định: addedAt)
sortDirection (ASC hoặc DESC)
- Response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "cartId": "cart123",
      "buyerId": "buyer567",
      "items": [
        {
          "productId": "product001",
          "productName": "Laptop",
          "quantity": 2,
          "price": 1200.0,
          "addedAt": "2024-02-20T12:00:00Z"
        }
      ],
      "totalPrice": 2400.0,
      "updatedAt": "2024-02-20T12:05:00Z"
    }
  ]
}
```

2️⃣ **Thêm sản phẩm vào giỏ hàng**
- Endpoint: POST /add
- Mô tả: Buyer thêm sản phẩm vào giỏ hàng.
- Request Body:
```json
{
  "productId": "product001"
}
```

- Response:
```
{
  "code": 1000,
  "message": "Product added to cart",
  "result": {
    "cartId": "cart123",
    "buyerId": "buyer567",
    "items": [
      {
        "productId": "product001",
        "productName": "Laptop",
        "quantity": 1,
        "price": 1200.0,
        "addedAt": "2024-02-20T12:00:00Z"
      }
    ],
    "totalPrice": 1200.0,
    "updatedAt": "2024-02-20T12:00:00Z"
  }
}
```

3️⃣ **Cập nhật số lượng sản phẩm trong giỏ hàng**
- Endpoint: PUT /update
- Mô tả: Buyer cập nhật số lượng sản phẩm trong giỏ hàng.
- Request Body:
```json
{
  "productId": "product001",
  "quantity": 2
}
```
- Response:
```json
{
  "code": 1000,
  "message": "Cart updated successfully",
  "result": {
    "cartId": "cart123",
    "buyerId": "buyer567",
    "items": [
      {
        "productId": "product001",
        "productName": "Laptop",
        "quantity": 2,
        "price": 1200.0,
        "addedAt": "2024-02-20T12:00:00Z"
      }
    ],
    "totalPrice": 2400.0,
    "updatedAt": "2024-02-20T12:05:00Z"
  }
}
```

4️⃣ **Xóa sản phẩm khỏi giỏ hàng**
- Endpoint: DELETE /remove/{productId}
- Mô tả: Buyer có thể xóa một sản phẩm khỏi giỏ hàng của mình.
- Response:
```json
{
  "code": 1000,
  "message": "Product removed from cart",
  "result": {
    "cartId": "cart123",
    "buyerId": "buyer567",
    "items": [],
    "totalPrice": 0.0,
    "updatedAt": "2024-02-20T12:10:00Z"
  }
}
```

5️⃣ **Xóa toàn bộ giỏ hàng**
- Endpoint: DELETE /clear
- Mô tả: Buyer có thể xóa toàn bộ sản phẩm trong giỏ hàng.
- Response:
```json
{
  "code": 1000,
  "message": "Cart cleared",
  "result": "Cart is now empty"
}
```

### Payment Service
#### 📌 Giới thiệu
Payment Service chịu trách nhiệm xử lý thanh toán cho đơn hàng. Người dùng có thể chọn phương thức thanh toán, kiểm tra trạng thái thanh toán, hủy hoặc xác nhận thanh toán.

- **Base URL:** `http://localhost:7000/payment`
- **Authentication:** JWT

#### 💳 Payment API
1️⃣ **Chọn phương thức thanh toán**
- Endpoint: POST /payment/select-payment/{orderId}
- Mô tả: Chọn phương thức thanh toán cho đơn hàng.
- Path Parameter:
orderId (string) - ID của đơn hàng.
- Request Body:
```json
{
  "paymentMethod": "COD"
}
```
- Response:
```json
{
  "code": 1000,
  "message": "Payment method selected",
  "result": {
    "orderId": "order123",
    "paymentMethod": "COD",
    "status": "PENDING"
  }
}
```

2️⃣ **Redirect đến cổng thanh toán**
- Endpoint: GET /payment/redirect/{orderId}
- Mô tả: Điều hướng người dùng đến cổng thanh toán trực tuyến.
- Path Parameter:
orderId (string) - ID của đơn hàng.
```json
{
  "code": 1000,
  "message": "Redirect URL generated",
  "result": {
    "redirectUrl": "https://payment-gateway.com/redirect?order=order123"
  }
}
```

3️⃣ **Webhook thanh toán**
- Endpoint: POST /payment/webhook
- Mô tả: Hệ thống thanh toán gọi webhook này khi có cập nhật trạng thái.
- Request Body:
```json
{
  "orderId": "order123",
  "status": "SUCCESS"
}
```
- Response:
```json
{
  "code": 1000,
  "message": "Webhook received",
  "result": {
    "orderId": "order123",
    "status": "SUCCESS"
  }
}
```

4️⃣ **Xác nhận thanh toán COD**
- Endpoint: POST /payment/confirm/{orderId}
- Mô tả: Chỉ dành cho ADMIN để xác nhận thanh toán khi khách chọn COD.
- Path Parameter:
orderId (string) - ID của đơn hàng.
- Response:
```json
{
  "code": 1000,
  "message": "Payment confirmed",
  "result": {
    "orderId": "order123",
    "status": "CONFIRMED"
  }
}
```

5️⃣ **Hủy thanh toán**
- Endpoint: POST /payment/cancel/{orderId}
- Mô tả: Người dùng hoặc hệ thống có thể hủy giao dịch thanh toán.
- Path Parameter:
orderId (string) - ID của đơn hàng.
- Response:
```
{
  "code": 1000,
  "message": "Payment cancelled",
  "result": {
    "orderId": "order123",
    "status": "CANCELLED"
  }
}
```

6️⃣ **Truy vấn trạng thái thanh toán**
- Endpoint: GET /payment/status/{orderId}
- Mô tả: Kiểm tra trạng thái thanh toán của đơn hàng.
- Path Parameter:
orderId (string) - ID của đơn hàng.
- Response:
```json
{
  "code": 1000,
  "message": "Payment status retrieved",
  "result": {
    "orderId": "order123",
    "status": "SUCCESS"
  }
}
```

7️⃣ **Lịch sử thanh toán của người dùng**
- Endpoint: GET /payment/history/{userId}
- Mô tả: Truy xuất lịch sử thanh toán của một người dùng.
- Path Parameter:
userId (string) - ID của người dùng.
- Response:
```json
{
  "code": 1000,
  "message": "Payment history retrieved",
  "result": [
    {
      "orderId": "order123",
      "amount": 500.0,
      "paymentMethod": "Online",
      "status": "SUCCESS",
      "createdAt": "2024-02-20T12:00:00Z"
    },
    {
      "orderId": "order124",
      "amount": 200.0,
      "paymentMethod": "COD",
      "status": "PENDING",
      "createdAt": "2024-02-21T14:30:00Z"
    }
  ]
}
```

### Order Service

#### 📌 Giới thiệu
Order Service chịu trách nhiệm quản lý đơn hàng của khách hàng và người bán, bao gồm tạo đơn hàng, hủy đơn hàng, xác nhận đơn hàng và truy vấn trạng thái.

- **Base URL:** `http://localhost:8000/orders`
- **Authentication:** JWT

#### 🛒 Order API

1️⃣ **Lấy danh sách đơn hàng của người mua**
- Endpoint: GET /orders
- Mô tả: Trả về danh sách đơn hàng của người mua hiện tại.
- Yêu cầu xác thực: ✅
- Response:
```json
{
  "code": 1000,
  "message": "Success",
  "result": [
    {
      "orderId": "order123",
      "items": [
        {
          "productId": "product567",
          "quantity": 2,
          "price": 50.0
        }
      ],
      "orderValue": 100.0,
      "status": "PENDING"
    }
  ]
}
```

2️⃣ **Tạo đơn hàng mới**
- Endpoint: POST /orders
- Mô tả: API tạo đơn hàng với danh sách sản phẩm và thông tin giảm giá (nếu có).
- Yêu cầu xác thực: ✅
- Request Body:
```json
{
  "items": [
    {
      "product_id": "product567",
      "quantity": 2,
      "price": 50.0
    }
  ],
  "order_value": 100.0,
  "discount_id": "discount123",
  "discount_value": 10.0
}
```
- Response:
```json
{
  "code": 201,
  "message": "Order created successfully",
  "result": {
    "orderId": "order123",
    "status": "PENDING"
  }
}
```

3️⃣ **Hủy đơn hàng**
- Endpoint: PATCH /orders/cancel/{order_id}
- Mô tả: Chỉ có thể hủy đơn hàng nếu nó đang ở trạng thái PENDING.
- Yêu cầu xác thực: ✅
- Path Parameter:
order_id (string) - ID của đơn hàng.
- Response:
```json
{
  "code": 200,
  "message": "Order cancelled successfully",
  "result": {
    "orderId": "order123",
    "status": "CANCELLED"
  }
}
```

4️⃣ **Xem chi tiết đơn hàng**
- Endpoint: GET /orders/detail/{order_id}
- Mô tả: Lấy thông tin chi tiết về một đơn hàng cụ thể.
- Yêu cầu xác thực: ✅
- Path Parameter:
order_id (string) - ID của đơn hàng.
- Response:
```json
{
  "code": 200,
  "message": "Order details retrieved",
  "result": {
    "orderId": "order123",
    "items": [
      {
        "productId": "product567",
        "quantity": 2,
        "price": 50.0
      }
    ],
    "orderValue": 100.0,
    "status": "PENDING"
  }
}
```

#### 🛍️ Seller Order API
5️⃣ **Lấy danh sách đơn hàng của người bán**
- Endpoint: GET /seller/orders
- Mô tả: Trả về danh sách đơn hàng do người bán quản lý.
- Yêu cầu xác thực: ✅
- Response:
```json
{
  "code": 200,
  "message": "Seller orders retrieved",
  "result": [
    {
      "orderId": "order123",
      "items": [
        {
          "productId": "product567",
          "quantity": 2,
          "price": 50.0
        }
      ],
      "orderValue": 100.0,
      "status": "PENDING"
    }
  ]
}
```

6️⃣ **Xác nhận đơn hàng**
- Endpoint: PATCH /seller/orders/confirm/{order_id}
- Mô tả: Người bán xác nhận đơn hàng để tiến hành giao hàng.
- Yêu cầu xác thực: ✅
- Path Parameter:
order_id (string) - ID của đơn hàng.
- Response:
```json
{
  "code": 200,
  "message": "Order confirmed",
  "result": {
    "orderId": "order123",
    "status": "CONFIRMED"
  }
}
```

### Discount Service

#### 📌 Giới thiệu
Discount Service chịu trách nhiệm quản lý các chương trình giảm giá và giảm giá theo lịch trình (Scheduled Discounts) dành cho **Seller** và **Admin**.

- **Base URL:** `http://localhost:7000/discount`
- **Authentication:** JWT

#### 🔖 Discount Management API

1️⃣ **Tạo Discount (Seller)**
- Endpoint: `POST /discount/seller`
- Mô tả: Seller có thể tạo Discount mới.
- Yêu cầu xác thực: ✅
- Request Body:
```json
{
  "discount_name": "Black Friday Sale",
  "discount_description": "Giảm giá 50% cho tất cả đơn hàng trên 500k",
  "discount_type": "Percent",
  "discount_value": 50,
  "discount_code": "BLACKFRIDAY50",
  "discount_start_date": "2025-11-25T00:00:00.000Z",
  "discount_end_date": "2025-11-30T23:59:59.000Z",
  "discount_max_uses": 1000,
  "discount_max_value": 500000,
  "discount_use_count": 0,
  "discount_invisable": false,
  "discount_min_order_value": 500000
}
```
- Response: 201 Created
  
2️⃣ **Cập nhật Discount (Seller)**
- Endpoint: PATCH /discount/seller/{discount_id}
- Mô tả: Seller có thể cập nhật Discount của mình.
- Yêu cầu xác thực: ✅
- Path Parameter:
discount_id (string) - ID của Discount cần cập nhật.
- Request Body: (tương tự tạo mới)
- Response: 200 OK

3️⃣ **Xóa Discount (Seller)**
- Endpoint: DELETE /discount/seller/{discount_id}
- Mô tả: Seller có thể xóa Discount của mình.
- Yêu cầu xác thực: ✅
- Path Parameter:
discount_id (string) - ID của Discount cần xóa.
- Response: 200 OK

4️⃣ **Tạo Discount (Admin)**
- Endpoint: POST /discount/admin
- Mô tả: Admin có thể tạo Discount mới.
- Yêu cầu xác thực: ✅
- Request Body: (tương tự tạo mới)
- Response: 201 Created

5️⃣ **Cập nhật Discount (Admin)**
- Endpoint: PATCH /discount/admin/{discount_id}
- Mô tả: Admin có thể cập nhật Discount.
- Yêu cầu xác thực: ✅
- Path Parameter:
discount_id (string) - ID của Discount cần cập nhật.
- Request Body: (tương tự tạo mới)
- Response: 200 OK

6️⃣ **Xóa Discount (Admin)**
- Endpoint: DELETE /discount/admin/{discount_id}
- Mô tả: Admin có thể xóa Discount.
- Yêu cầu xác thực: ✅
- Path Parameter:
discount_id (string) - ID của Discount cần xóa.
- Response: 200 OK

7️⃣ **Lấy danh sách Discount của Seller**
- Endpoint: GET /discount/seller/{sellerId}
- Mô tả: Seller có thể lấy danh sách Discount của mình.
- Yêu cầu xác thực: ✅
- Query Parameters:
pageSize (number) - Số bản ghi trên mỗi trang (mặc định 10).
pageNumber (number) - Trang hiện tại (mặc định 1).
sortField (string) - Trường để sắp xếp (mặc định discount_start_date).
sortOrder (string) - asc hoặc desc (mặc định desc).

- Response: 200 OK

8️⃣ **Lấy danh sách tất cả Discount (Admin)**
- Endpoint: GET /discount/all
- Mô tả: Admin có thể lấy danh sách tất cả Discount.
- Yêu cầu xác thực: ✅
- Query Parameters: (Tương tự danh sách Seller)
- Response: 200 OK

9️⃣ **Lấy Discount bằng Code**
- Endpoint: GET /discount/get-detail-discount-bycode/{code}
- Mô tả: Tìm Discount bằng mã giảm giá.
- Yêu cầu xác thực: ✅
- Path Parameter:
code (string) - Mã giảm giá.
- Response: 200 OK

####🔄 Scheduled Discount API
🔟 **Tạo Scheduled Discount (Seller)**
- Endpoint: POST /scheduled-discount/seller
- Mô tả: Seller tạo một Discount được kích hoạt tự động.
- Yêu cầu xác thực: ✅
- Request Body:
```json
{
  "scheduled_time": "2025-11-25T00:00:00.000Z",
  "discount_name": "Black Friday Sale",
  "discount_description": "Giảm giá 50% cho tất cả đơn hàng trên 500k",
  "discount_type": "Percent",
  "discount_value": 50,
  "discount_code": "BLACKFRIDAY50",
  "discount_start_date": "2025-11-25T00:00:00.000Z",
  "discount_end_date": "2025-11-30T23:59:59.000Z",
  "discount_max_uses": 1000,
  "discount_max_value": 500000,
  "discount_use_count": 0,
  "discount_invisable": false,
  "discount_min_order_value": 500000
}
```
- Response: 201 Created
  
1️⃣1️⃣ **Cập nhật Scheduled Discount (Seller)**
- Endpoint: PATCH /scheduled-discount/seller/{settingId}
- Mô tả: Seller cập nhật Discount đã đặt lịch.
- Yêu cầu xác thực: ✅
- Path Parameter:
settingId (string) - ID của Discount đã đặt lịch.
- Request Body: (tương tự tạo mới)
- Response: 200 OK

1️⃣2️⃣ **Xóa Scheduled Discount (Seller)**
- Endpoint: DELETE /scheduled-discount/seller/{settingId}
- Mô tả: Seller có thể xóa Discount đã đặt lịch.
- Yêu cầu xác thực: ✅
- Path Parameter:
settingId (string) - ID của Discount đã đặt lịch.
- Response: 200 OK

1️⃣3️⃣ **Tạo Scheduled Discount (Admin)**
- Endpoint: POST /scheduled-discount/admin
- Mô tả: Admin tạo một Discount được kích hoạt tự động.
- Yêu cầu xác thực: ✅
- Request Body: (tương tự tạo mới)
- Response: 201 Created

1️⃣4️⃣ **Cập nhật Scheduled Discount (Admin)**
- Endpoint: PATCH /scheduled-discount/admin/{settingId}
- Mô tả: Admin cập nhật Discount đã đặt lịch.
- Yêu cầu xác thực: ✅
- Path Parameter:
settingId (string) - ID của Discount đã đặt lịch.
- Request Body: (tương tự tạo mới)
- Response: 200 OK

1️⃣5️⃣ **Xóa Scheduled Discount (Admin)**
- Endpoint: DELETE /scheduled-discount/admin/{settingId}
- Mô tả: Admin có thể xóa Discount đã đặt lịch.
- Yêu cầu xác thực: ✅
- Path Parameter:
settingId (string) - ID của Discount đã đặt lịch.
- Response: 200 OK

---

## 4️⃣ Xây dựng kiểm thử
### 1️⃣ Kiểm thử Identity, Cart, Product, API Gateway (Spring Boot, MySQL)
#### 🔹 Kiểm thử đơn vị (Unit Test)
- Framework: JUnit 5, Mockito, Spring Boot Test
- Mục tiêu: Đảm bảo các hàm xử lý nghiệp vụ như đăng nhập, tạo sản phẩm, thêm giỏ hàng hoạt động đúng.
- Ví dụ kiểm thử với JUnit & Mockito:

```java
@Test
public void testUserAuthentication() {
    when(userRepository.findByUsername("testuser"))
        .thenReturn(Optional.of(new User("testuser", "hashedpassword")));

    boolean isAuthenticated = authService.authenticate("testuser", "password");
    assertTrue(isAuthenticated);
}
```

#### 🔹 Kiểm thử tích hợp (Integration Test) (...Updating)
- Spring Boot Test với @SpringBootTest để chạy thử nghiệm trên môi trường thực.
- Testcontainers có thể được sử dụng để kiểm thử MySQL trong môi trường container.
- Ví dụ:

```java
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetProducts() throws Exception {
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.size()").value(3));
    }
}
```

### 2️⃣ Kiểm thử Order Service (Golang, Gin, gRPC, MongoDB, RabbitMQ)
#### 🔹 Kiểm thử đơn vị (Unit Test)
- Framework: testify
- Mục tiêu: Kiểm thử các logic xử lý đơn hàng, xác nhận trạng thái đơn hàng.
- Ví dụ kiểm thử với testify:

```go
func TestCreateOrder(t *testing.T) {
    mockDB := new(MockDB)
    orderService := NewOrderService(mockDB)

    order := Order{UserID: "123", Total: 100}
    mockDB.On("SaveOrder", order).Return(nil)

    err := orderService.CreateOrder(order)
    assert.Nil(t, err)
}
```

#### 🔹 Kiểm thử tích hợp (Integration Test) (...Updating)
- Dùng Docker + Testcontainers để chạy thử nghiệm với MongoDB.
- gRPC testing: Kiểm tra việc gọi gRPC service.
- Ví dụ kiểm thử gRPC API:

```go
func TestGetOrder(t *testing.T) {
    conn, err := grpc.Dial(serverAddress, grpc.WithInsecure())
    assert.Nil(t, err)
    
    client := pb.NewOrderServiceClient(conn)
    resp, err := client.GetOrder(context.Background(), &pb.GetOrderRequest{OrderId: "123"})
    assert.Nil(t, err)
    assert.Equal(t, "123", resp.Order.Id)
}
```

### 3️⃣ Kiểm thử Payment Service (.NET)
#### 🔹 Kiểm thử đơn vị (Unit Test)
- Framework: xUnit, Moq
- Mục tiêu: Đảm bảo các giao dịch thanh toán, xử lý trạng thái hoạt động đúng.
- Ví dụ kiểm thử với xUnit:

```csharp
[Fact]
public void TestProcessPayment()
{
    var mockPaymentRepo = new Mock<IPaymentRepository>();
    var paymentService = new PaymentService(mockPaymentRepo.Object);

    var payment = new Payment { OrderId = "123", Status = "Processing" };
    mockPaymentRepo.Setup(p => p.Save(payment)).Returns(Task.CompletedTask);

    var result = paymentService.ProcessPayment(payment);
    Assert.Equal("Processing", result.Status);
}
```

#### 🔹 Kiểm thử tích hợp (Integration Test) (...Updating)
- Dùng xUnit + Docker Compose để chạy thử nghiệm SQL Server hoặc RabbitMQ.
- Kiểm tra Webhook Payment với HttpClientFactory.

### 4️⃣ Kiểm thử Discount Service (NestJS)
#### 🔹 Kiểm thử đơn vị (Unit Test)
- Framework: Jest
- Mục tiêu: Kiểm tra logic tạo mã giảm giá, kiểm tra áp dụng discount.
- Ví dụ kiểm thử với Jest:

```typescript
describe('DiscountService', () => {
  let discountService: DiscountService;

  beforeEach(() => {
    discountService = new DiscountService();
  });

  it('should create a new discount', () => {
    const discount = discountService.create({ code: 'SALE50', value: 50 });
    expect(discount.code).toEqual('SALE50');
  });
});
```

#### 🔹 Kiểm thử tích hợp (Integration Test)
- Dùng NestJS Testing Module với MongoDB in-memory để kiểm thử API.
- Redis Stream testing: Kiểm tra việc bắn sự kiện vào Redis.
- Ví dụ:

```typescript
it('should publish event to Redis Stream', async () => {
  await redisService.publishEvent('discount.created', { code: 'SALE50' });
  expect(await redisService.getEvents('discount.created')).toContain({ code: 'SALE50' });
});
```
---

### Thông số coverage của các kiểm thử
![image](https://github.com/user-attachments/assets/6fab01db-b799-4156-ba71-929ba7f4b6b6)
![image](https://github.com/user-attachments/assets/92ba9796-4e4f-405f-96f3-a3f0b3c4bf02)
![image](https://github.com/user-attachments/assets/87e6031c-845d-40b7-a7ec-2bac0d444577)
.... more

---

## 5️⃣ Xây dựng hệ thống (...Updating)

---

## 6️⃣ Các bài kiểm thử với hệ thống (...Updating)
---

## 7️⃣ Tổng kết và cải tiến (...Updating)

