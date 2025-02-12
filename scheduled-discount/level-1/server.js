require("dotenv").config();
const express = require("express");
const mongoose = require("mongoose");
const cron = require("node-cron");
const helmet = require("helmet");
const Redis = require("ioredis");
const {
  ScheduledDiscountModel,
} = require("./src/model/scheduled-discount.model");

const app = express();
const PORT = process.env.PORT || 3000;
const MONGO_URI = process.env.MONGO_URI;
const REDIS_URL = process.env.REDIS_URL || "redis://127.0.0.1:6379";
const redis = new Redis(REDIS_URL);
const streamName = "scheduled_discount_stream";

app.use(helmet());

// 🔌 Kết nối MongoDB
mongoose
  .connect(MONGO_URI, {
    useNewUrlParser: true,
    useUnifiedTopology: true,
  })
  .then(() => console.log("✅ Đã kết nối MongoDB"))
  .catch((err) => console.error("❌ Lỗi kết nối MongoDB:", err));

// 🕒 Cronjob chạy mỗi 5 giây
cron.schedule("*/5 * * * * *", async () => {
  try {
    const now = new Date(); // Lấy thời gian hiện tại
    const scheduledDiscounts = await ScheduledDiscountModel.find({
      scheduled_time: { $lte: now }, // Lọc các bản ghi có scheduled_time nhỏ hơn hoặc bằng thời gian hiện tại
    });

    console.log(`🕒 Thời gian hiện tại: ${now.toISOString()}`);

    if (scheduledDiscounts.length > 0) {
      for (const scheduledDiscount of scheduledDiscounts) {
        console.log(scheduledDiscount.discount_name);
        const message = {
          scheduledDiscount,
        };

        const response = await redis.xadd(
          streamName,
          "*",
          "data",
          JSON.stringify(message)
        );
        console.log(`📤 Sent message to Redis Stream: ${response}`);
      }
    }
  } catch (error) {
    console.error("❌ Lỗi trong cronjob:", error);
  }
});

// 🚀 Khởi động Express server mà không có API
app.listen(PORT, () => {
  console.log(`🚀 Scheduled Discount Level 1 chạy trên cổng ${PORT}`);
  console.log(`⏳ Cronjob sẽ chạy mỗi 5 giây...`);
});
