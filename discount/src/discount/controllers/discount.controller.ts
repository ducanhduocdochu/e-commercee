import {
  Controller,
  Post,
  Get,
  Patch,
  Delete,
  Param,
  Body,
  NotFoundException,
  UseGuards,
  Request,
  Query,
} from '@nestjs/common';
import { DiscountService } from '../services/discount.service';
import { CreateDiscountDto } from '../dtos/create-discount.dto';
import { Discount } from '../interfaces/discount.interface';
import { ErrorCode, ErrorCodeDetails } from '../../common/error-code.enum';
import { AuthGuard } from '../guards/auth.guard';
import { RolesGuard, Roles } from '../guards/role.guard';
import {
  ApiTags,
  ApiOperation,
  ApiBearerAuth,
  ApiParam,
  ApiQuery,
} from '@nestjs/swagger';

@ApiTags('Discount Management') // ✅ Nhóm API "Discount Management" trong Swagger
@ApiBearerAuth() // ✅ Bật JWT Authorization
@Controller('discount')
export class DiscountController {
  constructor(private readonly discountService: DiscountService) {}

  @ApiOperation({
    summary: 'Tạo Discount (Seller)',
    description: 'Seller có thể tạo discount mới',
  })
  @Post('seller')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_SELLER')
  async createDiscountBySeller(
    @Request() req,
    @Body() createDiscountDto: CreateDiscountDto,
  ): Promise<Discount> {
    return this.discountService.createDiscount({
      createDiscountDto,
      userId: req.user.id,
      discountAppliesType: 'Seller',
    });
  }

  @ApiOperation({
    summary: 'Cập nhật Discount (Seller)',
    description: 'Seller cập nhật discount',
  })
  @ApiParam({
    name: 'discount_id',
    example: '64b9f8a7e31a7a0012a4f9b2',
    description: 'ID của Discount',
  })
  @Patch('seller/:discount_id')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_SELLER')
  async updateDiscountBySeller(
    @Request() req,
    @Param('discount_id') discountId: string,
    @Body() createDiscountDto: CreateDiscountDto,
  ): Promise<Discount> {
    return this.discountService.updateDiscount({
      discountId,
      userId: req.user.id,
      createDiscountDto,
    });
  }

  @ApiOperation({
    summary: 'Xóa Discount (Seller)',
    description: 'Seller có thể xóa discount',
  })
  @ApiParam({ name: 'discount_id', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Delete('seller/:discount_id')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_SELLER')
  async deleteDiscountBySeller(
    @Request() req,
    @Param('discount_id') discountId: string,
  ): Promise<Discount> {
    return this.discountService.deleteDiscount({
      userId: req.user.id,
      discountId,
    });
  }

  @ApiOperation({
    summary: 'Tạo Discount (Admin)',
    description: 'Admin có thể tạo discount mới',
  })
  @Post('admin')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_ADMIN')
  async createDiscountByAdmin(
    @Request() req,
    @Body() createDiscountDto: CreateDiscountDto,
  ): Promise<Discount> {
    return this.discountService.createDiscount({
      createDiscountDto,
      userId: req.user.id,
      discountAppliesType: 'All',
    });
  }

  @ApiOperation({
    summary: 'Cập nhật Discount (Admin)',
    description: 'Admin cập nhật discount',
  })
  @ApiParam({ name: 'discount_id', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Patch('admin/:discount_id')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_ADMIN')
  async updateDiscountByAdmin(
    @Request() req,
    @Param('discount_id') discountId: string,
    @Body() createDiscountDto: CreateDiscountDto,
  ): Promise<Discount> {
    return this.discountService.updateDiscount({
      discountId,
      userId: req.user.id,
      createDiscountDto,
    });
  }

  @ApiOperation({
    summary: 'Xóa Discount (Admin)',
    description: 'Admin có thể xóa discount',
  })
  @ApiParam({ name: 'discount_id', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Delete('admin/:discount_id')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_ADMIN')
  async deleteDiscountByAdmin(
    @Request() req,
    @Param('discount_id') discountId: string,
  ): Promise<Discount> {
    return this.discountService.deleteDiscount({
      userId: req.user.id,
      discountId,
    });
  }

  @ApiOperation({
    summary: 'Lấy danh sách Discount của Seller',
    description: 'Seller có thể lấy danh sách discount',
  })
  @ApiParam({
    name: 'sellerId',
    example: '123456',
    description: 'ID của Seller',
  })
  @ApiQuery({
    name: 'pageSize',
    example: 10,
    description: 'Số bản ghi trên mỗi trang',
    required: false,
  })
  @ApiQuery({
    name: 'pageNumber',
    example: 1,
    description: 'Trang hiện tại',
    required: false,
  })
  @ApiQuery({
    name: 'sortField',
    example: 'discount_start_date',
    description: 'Trường để sắp xếp',
    required: false,
  })
  @ApiQuery({
    name: 'sortOrder',
    example: 'desc',
    description: 'Sắp xếp theo asc/desc',
    required: false,
  })
  @Get('seller/:sellerId')
  async getDiscountByUser(
    @Param('sellerId') sellerId: string,
    @Query('pageSize') pageSize: number = 10,
    @Query('pageNumber') pageNumber: number = 1,
    @Query('sortField') sortField: string = 'discount_start_date',
    @Query('sortOrder') sortOrder: 'asc' | 'desc' = 'desc',
  ): Promise<Discount[]> {
    return this.discountService.getDiscountByUser(
      sellerId,
      Number(pageSize),
      Number(pageNumber),
      sortField,
      sortOrder,
    );
  }

  @ApiOperation({
    summary: 'Lấy tất cả Discount',
    description: 'Admin có thể lấy danh sách tất cả discount',
  })
  @ApiQuery({
    name: 'pageSize',
    example: 10,
    description: 'Số bản ghi trên mỗi trang',
    required: false,
  })
  @ApiQuery({
    name: 'pageNumber',
    example: 1,
    description: 'Trang hiện tại',
    required: false,
  })
  @ApiQuery({
    name: 'sortField',
    example: 'discount_start_date',
    description: 'Trường để sắp xếp',
    required: false,
  })
  @ApiQuery({
    name: 'sortOrder',
    example: 'desc',
    description: 'Sắp xếp theo asc/desc',
    required: false,
  })
  @ApiQuery({
    name: 'discountType',
    example: 'Percent',
    description: 'Lọc theo loại giảm giá',
    required: false,
  })
  @Get('all')
  async getAllDiscounts(
    @Query('pageSize') pageSize: number = 10,
    @Query('pageNumber') pageNumber: number = 1,
    @Query('sortField') sortField: string = 'discount_start_date',
    @Query('sortOrder') sortOrder: 'asc' | 'desc' = 'desc',
    @Query('discountType') discountType?: string,
  ): Promise<Discount[]> {
    return this.discountService.getAllDiscounts(
      Number(pageSize),
      Number(pageNumber),
      sortField,
      sortOrder,
      discountType,
    );
  }

  @ApiOperation({
    summary: 'Lấy Discount bằng Code',
    description: 'Tìm Discount bằng mã giảm giá',
  })
  @ApiParam({ name: 'code', example: 'BLACKFRIDAY50' })
  @Get('/get-detail-discount-bycode/:code')
  async getDiscountByCode(
    @Param('code') discountCode: string,
  ): Promise<Discount> {
    return this.discountService.getDiscountByCode(discountCode);
  }

  @ApiOperation({
    summary: 'Lấy chi tiết Discount bằng ID',
    description: 'Lấy Discount theo discount_id',
  })
  @ApiParam({ name: 'discount_id', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Get('/get-detail-discount/:discount_id')
  async getDiscount(
    @Param('discount_id') discountId: string,
  ): Promise<Discount> {
    return this.discountService.getDiscountById(discountId);
  }
}
