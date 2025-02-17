import { Controller, Get, Param } from '@nestjs/common';
import { DiscountService } from '../services/discount.service';
import { Discount } from '../interfaces/discount.interface';
import { ApiTags, ApiOperation, ApiParam } from '@nestjs/swagger';

@ApiTags('Discount Internal Management') // ✅ Nhóm API "Discount Management" trong Swagger
@Controller('discount/internal')
export class DiscountInternalController {
  constructor(private readonly discountService: DiscountService) {}

  @ApiOperation({
    summary: 'Lấy chi tiết Discount bằng ID giữa các service',
    description: 'Lấy Discount theo discount_id giữa các service',
  })
  @ApiParam({ name: 'discount_id', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Get('/:discount_id')
  async getDiscount(
    @Param('discount_id') discountId: string,
  ): Promise<Discount> {
    return this.discountService.getDiscountById(discountId);
  }
}
