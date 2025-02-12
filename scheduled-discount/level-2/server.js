require("dotenv").config();
const express = require("express");
const mongoose = require("mongoose");
const helmet = require("helmet");
const Redis = require("ioredis");
const {
  ScheduledDiscountModel,
} = require("./src/model/scheduled-discount.model");
const { DiscountModel } = require("./src/model/discount.model");

const app = express();
const PORT = process.env.PORT || 3000;
const MONGO_URI = process.env.MONGO_URI;
const REDIS_URL = process.env.REDIS_URL || "redis://127.0.0.1:6379";
const redis = new Redis(REDIS_URL);
const streamName = "scheduled_discount_stream";
const consumerGroup = "discount_consumer_group";
const consumerName = `consumer_${Math.random().toString(36).substr(2, 6)}`;

app.use(helmet());

// 🔌 Kết nối MongoDB
if (!MONGO_URI) {
  console.error("❌ Lỗi: MONGO_URI chưa được cấu hình trong biến môi trường!");
  process.exit(1);
}

mongoose
  .connect(MONGO_URI)
  .then(() => console.log("✅ Đã kết nối MongoDB"))
  .catch((err) => {
    console.error("❌ Lỗi kết nối MongoDB:", err);
    process.exit(1);
  });

// Đảm bảo nhóm Consumer Group tồn tại
async function createConsumerGroup() {
  try {
    await redis.xgroup("CREATE", streamName, consumerGroup, "$", "MKSTREAM");
    console.log(`✅ Consumer group '${consumerGroup}' created`);
  } catch (error) {
    if (!error.message.includes("BUSYGROUP")) {
      console.error("❌ Error creating consumer group:", error.message);
    }
  }
}

// 🛠 Hàm lắng nghe dữ liệu từ Redis Stream và lưu vào MongoDB (Bỏ Transaction)
async function consume() {
  while (true) {
    try {
      console.log("🔄 Đang chờ dữ liệu từ Redis Stream...");
      const response = await redis.xreadgroup(
        "GROUP",
        consumerGroup,
        consumerName,
        "COUNT",
        1,
        "BLOCK",
        5000,
        "STREAMS",
        streamName,
        ">"
      );

      if (!response) {
        console.log("⏳ Không có dữ liệu mới...");
        continue;
      }

      const [[, messages]] = response;
      for (const [id, fields] of messages) {
        if (!fields[1]) {
          console.warn("⚠️ Nhận được dữ liệu trống từ Redis, bỏ qua...");
          await redis.xack(streamName, consumerGroup, id);
          continue;
        }

        let data;
        try {
          data = JSON.parse(fields[1]);
          console.log(`📥 Nhận dữ liệu từ Redis Stream:`, data);
        } catch (parseError) {
          console.error("❌ Lỗi phân tích JSON:", parseError);
          await redis.xack(streamName, consumerGroup, id);
          continue;
        }

        if (!data || !data.scheduledDiscount) {
          console.warn("⚠️ Dữ liệu không hợp lệ, bỏ qua...", data);
          await redis.xack(streamName, consumerGroup, id);
          continue;
        }

        try {
          // 🛠 Thêm vào DiscountModel KHÔNG dùng transaction
          await DiscountModel.create({
            ...data.scheduledDiscount,
          });
          console.log(
            `✅ Đã lưu vào MongoDB: ${data.scheduledDiscount.discount_name}`
          );

          // 🛠 Xóa dữ liệu từ ScheduledDiscountModel nếu tồn tại
          const deleted = await ScheduledDiscountModel.findByIdAndDelete(
            data.scheduledDiscount._id
          );
          if (deleted) {
            console.log(
              `❌ Đã xóa ScheduledDiscount: ${data.scheduledDiscount._id}`
            );
          } else {
            console.warn(
              `⚠️ Không tìm thấy ScheduledDiscount để xóa: ${data.scheduledDiscount._id}`
            );
          }

          // ✅ Xác nhận message trong Redis Stream
          await redis.xack(streamName, consumerGroup, id);
        } catch (err) {
          console.error("❌ Lỗi khi thực hiện insert vào MongoDB:", err);
        }
      }
    } catch (error) {
      console.error("❌ Lỗi trong consumer:", error.message);
    }
  }
}

// Khởi tạo consumer
(async () => {
  await createConsumerGroup();
  consume();
})();

app.listen(PORT, () => {
  console.log(`🚀 Scheduled Discount Level 2 chạy trên cổng ${PORT}`);
});
