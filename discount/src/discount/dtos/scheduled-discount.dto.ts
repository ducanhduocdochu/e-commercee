import { Type } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';
import {
  IsNotEmpty,
  IsDate,
  IsNumber,
  IsString,
  IsBoolean,
  IsEnum,
} from 'class-validator';

export class ScheduledDiscountDto {
  @ApiProperty({
    example: '2025-11-25T00:00:00.000Z',
    description: 'Thời gian tự động kích hoạt giảm giá',
  })
  @IsDate()
  @Type(() => Date)
  @IsNotEmpty()
  scheduled_time: Date;

  @ApiProperty({
    example: 'Black Friday Sale',
    description: 'Tên chương trình giảm giá',
  })
  @IsString()
  @IsNotEmpty()
  discount_name: string;

  @ApiProperty({
    example: 'Giảm giá 50% cho tất cả đơn hàng trên 500k',
    description: 'Mô tả chương trình giảm giá',
  })
  @IsString()
  @IsNotEmpty()
  discount_description: string;

  @ApiProperty({
    example: 'Percent',
    enum: ['Percent', 'Value'],
    description:
      'Loại giảm giá (Percent: theo phần trăm, Value: theo giá trị cố định)',
  })
  @IsEnum(['Percent', 'Value'])
  @IsNotEmpty()
  discount_type: string;

  @ApiProperty({
    example: 50,
    description:
      'Giá trị giảm giá (số phần trăm hoặc số tiền tùy theo discount_type)',
  })
  @IsNumber()
  @IsNotEmpty()
  discount_value: number;

  @ApiProperty({
    example: 'BLACKFRIDAY50',
    description: 'Mã giảm giá để khách hàng sử dụng',
  })
  @IsString()
  @IsNotEmpty()
  discount_code: string;

  @ApiProperty({
    example: '2025-11-25T00:00:00.000Z',
    description: 'Ngày bắt đầu hiệu lực của mã giảm giá',
  })
  @IsDate()
  @Type(() => Date)
  @IsNotEmpty()
  discount_start_date: Date;

  @ApiProperty({
    example: '2025-11-30T23:59:59.000Z',
    description: 'Ngày hết hạn của mã giảm giá',
  })
  @IsDate()
  @Type(() => Date)
  @IsNotEmpty()
  discount_end_date: Date;

  @ApiProperty({
    example: 1000,
    description: 'Tổng số lần mã giảm giá có thể được sử dụng',
  })
  @IsNumber()
  @IsNotEmpty()
  discount_max_uses: number;

  @ApiProperty({
    example: 500000,
    description: 'Giá trị giảm giá tối đa có thể áp dụng',
  })
  @IsNumber()
  @IsNotEmpty()
  discount_max_value: number;

  @ApiProperty({
    example: 0,
    description: 'Số lần mã giảm giá đã được sử dụng',
  })
  @IsNumber()
  @IsNotEmpty()
  discount_use_count: number;

  @ApiProperty({
    example: false,
    description: 'Xác định xem mã giảm giá có bị ẩn không',
  })
  @IsBoolean()
  @IsNotEmpty()
  discount_invisable: boolean;

  @ApiProperty({
    example: 500000,
    description: 'Giá trị đơn hàng tối thiểu để áp dụng mã giảm giá',
  })
  @IsNumber()
  @IsNotEmpty()
  discount_min_order_value: number;
}
