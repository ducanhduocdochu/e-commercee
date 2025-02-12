require("dotenv").config();
const express = require("express");
const mongoose = require("mongoose");
const cron = require("node-cron");
const helmet = require("helmet");

const app = express();
const PORT = process.env.PORT || 3000;
const MONGO_URI = process.env.MONGO_URI;

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
  console.log(`[${new Date().toISOString()}] 🔄 Cronjob chạy mỗi 5 giây`);
});

// 🚀 Khởi động Express server mà không có API
app.listen(PORT, () => {
  console.log(`🚀 Express server chạy trên cổng ${PORT}`);
  console.log(`⏳ Cronjob sẽ chạy mỗi 5 giây...`);
});
