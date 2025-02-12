import {
  Controller,
  Post,
  Patch,
  Delete,
  Get,
  Param,
  Body,
  UseGuards,
  Request,
} from '@nestjs/common';
import { ScheduledDiscountService } from '../services/scheduled-discount.service';
import { ScheduledDiscountDto } from '../dtos/scheduled-discount.dto';
import { AuthGuard } from '../guards/auth.guard';
import { RolesGuard, Roles } from '../guards/role.guard';
import {
  ApiTags,
  ApiOperation,
  ApiBearerAuth,
  ApiParam,
} from '@nestjs/swagger';

@ApiTags('Scheduled Discount Management') // ✅ Nhóm API "Scheduled Discount Management"
@ApiBearerAuth() // ✅ Bật JWT Authorization
@Controller('scheduled-discount')
export class ScheduledDiscountController {
  constructor(
    private readonly scheduledDiscountService: ScheduledDiscountService,
  ) {}

  @ApiOperation({
    summary: 'Tạo Scheduled Discount (Seller)',
    description: 'Seller tạo một discount được kích hoạt tự động',
  })
  @Post('seller')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_SELLER')
  async createSettingBySeller(
    @Request() req,
    @Body() createSettingDto: ScheduledDiscountDto,
  ) {
    return this.scheduledDiscountService.createSetting({
      userId: req.user.id,
      createSettingDto,
      discountAppliesType: 'Seller',
    });
  }

  @ApiOperation({
    summary: 'Cập nhật Scheduled Discount (Seller)',
    description: 'Seller cập nhật discount đã đặt lịch',
  })
  @ApiParam({ name: 'settingId', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Patch('seller/:settingId')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_SELLER')
  async updateSettingBySeller(
    @Request() req,
    @Param('settingId') settingId: string,
    @Body() createSettingDto: ScheduledDiscountDto,
  ) {
    return this.scheduledDiscountService.updateSetting({
      userId: req.user.id,
      settingId,
      createSettingDto,
    });
  }

  @ApiOperation({
    summary: 'Xóa Scheduled Discount (Seller)',
    description: 'Seller xóa một discount đã đặt lịch',
  })
  @ApiParam({ name: 'settingId', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Delete('seller/:settingId')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_SELLER')
  async deleteSettingBySeller(
    @Request() req,
    @Param('settingId') settingId: string,
  ) {
    return this.scheduledDiscountService.deleteSetting({
      userId: req.user.id,
      settingId,
    });
  }

  @ApiOperation({
    summary: 'Tạo Scheduled Discount (Admin)',
    description: 'Admin tạo một discount được kích hoạt tự động',
  })
  @Post('admin')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_ADMIN')
  async createSettingByAdmin(
    @Request() req,
    @Body() createSettingDto: ScheduledDiscountDto,
  ) {
    return this.scheduledDiscountService.createSetting({
      userId: req.user.id,
      createSettingDto,
      discountAppliesType: 'All',
    });
  }

  @ApiOperation({
    summary: 'Cập nhật Scheduled Discount (Admin)',
    description: 'Admin cập nhật discount đã đặt lịch',
  })
  @ApiParam({ name: 'settingId', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Patch('admin/:settingId')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_ADMIN')
  async updateSettingByAdmin(
    @Request() req,
    @Param('settingId') settingId: string,
    @Body() createSettingDto: ScheduledDiscountDto,
  ) {
    return this.scheduledDiscountService.updateSetting({
      userId: req.user.id,
      settingId,
      createSettingDto,
    });
  }

  @ApiOperation({
    summary: 'Xóa Scheduled Discount (Admin)',
    description: 'Admin xóa một discount đã đặt lịch',
  })
  @ApiParam({ name: 'settingId', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Delete('admin/:settingId')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_ADMIN')
  async deleteSettingByAdmin(
    @Request() req,
    @Param('settingId') settingId: string,
  ) {
    return this.scheduledDiscountService.deleteSetting({
      userId: req.user.id,
      settingId,
    });
  }

  @ApiOperation({
    summary: 'Lấy danh sách Scheduled Discount của chính mình',
    description: 'Seller lấy danh sách các discount đã đặt lịch',
  })
  @Get('all')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_SELLER')
  async getMySettings(@Request() req) {
    return this.scheduledDiscountService.getMySettings(req.user.id);
  }

  @ApiOperation({
    summary: 'Lấy chi tiết một Scheduled Discount của chính mình',
    description: 'Seller xem chi tiết discount đã đặt lịch',
  })
  @ApiParam({ name: 'settingId', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Get(':settingId')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_SELLER')
  async getMySettingDetail(
    @Request() req,
    @Param('settingId') settingId: string,
  ) {
    return this.scheduledDiscountService.getMySettingDetail({
      userId: req.user.id,
      settingId,
    });
  }

  @ApiOperation({
    summary: 'Lấy danh sách Scheduled Discount của một Seller (Admin)',
    description: 'Admin lấy danh sách discount của một seller cụ thể',
  })
  @ApiParam({ name: 'sellerId', example: '123456' })
  @Get('admin/:sellerId')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_ADMIN')
  async getSellerSettings(@Param('sellerId') sellerId: string) {
    return this.scheduledDiscountService.getSellerSettings(sellerId);
  }

  @ApiOperation({
    summary: 'Lấy chi tiết Scheduled Discount của một Seller (Admin)',
    description: 'Admin xem chi tiết discount đã đặt lịch của một seller',
  })
  @ApiParam({ name: 'sellerId', example: '123456' })
  @ApiParam({ name: 'settingId', example: '64b9f8a7e31a7a0012a4f9b2' })
  @Get('admin/:sellerId/:settingId')
  @UseGuards(AuthGuard, RolesGuard)
  @Roles('ROLE_ADMIN')
  async getSellerSettingDetail(
    @Param('sellerId') sellerId: string,
    @Param('settingId') settingId: string,
  ) {
    return this.scheduledDiscountService.getSellerSettingDetail({
      sellerId,
      settingId,
    });
  }
}
