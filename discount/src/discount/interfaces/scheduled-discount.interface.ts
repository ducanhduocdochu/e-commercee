export interface ScheduledDiscount {
  scheduled_time: Date; // Thời gian tạo discount
  discount_name: string;
  discount_description: string;
  discount_type: 'Percent' | 'Value'; // Enum chỉ nhận giá trị này
  discount_value: number;
  discount_code: string;
  discount_start_date: Date;
  discount_end_date: Date;
  discount_max_uses: number;
  discount_max_value: number;
  discount_use_count: number;
  discount_invisable: boolean;
  discount_applies_type: string;
  discount_min_order_value: number;
  shop_id: string;
}
