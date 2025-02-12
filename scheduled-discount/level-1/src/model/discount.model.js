const { Schema, model } = require("mongoose");

const DiscountSchema = new Schema(
  {
    discount_name: { type: String, required: true },
    discount_description: { type: String, required: true },
    discount_type: { type: String, enum: ["Percent", "Value"], required: true },
    discount_value: { type: Number, required: true },
    discount_code: { type: String, required: true },
    discount_start_date: { type: Date, required: true },
    discount_end_date: { type: Date, required: true },
    discount_max_uses: { type: Number, required: true },
    discount_max_value: { type: Number, required: true },
    discount_use_count: { type: Number, required: true },
    discount_invisable: { type: Boolean, required: true },
    discount_applies_type: {
      type: String,
      enum: ["All", "Seller"],
      required: true,
    },
    discount_min_order_value: { type: Number, required: true },
    shop_id: { type: String, required: false },
  },
  {
    timestamps: true,
    collection: "Discounts",
  }
);

module.exports = {
  DiscountModel: model("Discount", DiscountSchema),
};
