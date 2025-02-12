// src/discount/interfaces/discount.interface.ts

import { Document } from 'mongoose';

export interface Discount extends Document {
  discount_name: string;
  discount_description: string;
  discount_type: string;
  discount_value: number;
  discount_code: string;
  discount_start_date: Date;
  discount_end_date: Date;
  discount_max_uses: number;
  discount_max_value: string;
  discount_use_count: number;
  discount_invisable: boolean;
  discount_applies_type: string;
  discount_min_order_value: number;
  shop_id: string;
}
