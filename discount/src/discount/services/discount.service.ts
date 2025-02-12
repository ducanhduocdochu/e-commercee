import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model, SortOrder } from 'mongoose';
import { Discount } from '../interfaces/discount.interface';
import { CreateDiscountDto } from '../dtos/create-discount.dto';
import { ErrorCode, ErrorCodeDetails } from '../../common/error-code.enum';

@Injectable()
export class DiscountService {
  constructor(
    @InjectModel('Discount') private readonly discountModel: Model<Discount>,
  ) {}

  async createDiscount({
    createDiscountDto,
    userId,
    discountAppliesType,
  }: {
    createDiscountDto: CreateDiscountDto;
    userId: string;
    discountAppliesType: string;
  }): Promise<Discount> {
    return await this.discountModel.create({
      ...createDiscountDto,
      shop_id: userId,
      discount_applies_type: discountAppliesType,
    });
  }

  async updateDiscount({
    discountId,
    createDiscountDto,
    userId,
  }: {
    discountId: string;
    createDiscountDto: CreateDiscountDto;
    userId: string;
  }): Promise<Discount> {
    const updatedDiscount = await this.discountModel
      .findOneAndUpdate(
        { shop_id: userId, _id: discountId },
        createDiscountDto,
        { new: true },
      )
      .exec(); // ✅ Thêm `.exec()` để tránh lỗi TypeError

    if (!updatedDiscount) {
      throw new NotFoundException({
        message: ErrorCodeDetails[ErrorCode.DISCOUNT_NOT_FOUND].message,
        code: ErrorCode.DISCOUNT_NOT_FOUND,
      });
    }
    return updatedDiscount;
  }

  // ✅ Xóa discount
  async deleteDiscount({
    discountId,
    userId,
  }: {
    discountId: string;
    userId: string;
  }): Promise<Discount> {
    const deletedDiscount = await this.discountModel
      .findOneAndDelete({ _id: discountId, shop_id: userId })
      .exec(); // ✅ Thêm `.exec()` để tránh lỗi TypeError

    if (!deletedDiscount) {
      throw new NotFoundException({
        message: ErrorCodeDetails[ErrorCode.DISCOUNT_NOT_FOUND].message,
        code: ErrorCode.DISCOUNT_NOT_FOUND,
      });
    }
    return deletedDiscount;
  }

  async getDiscountByUser(
    userId: string,
    pageSize: number = 10,
    pageNumber: number = 1,
    sortField: string = 'discount_start_date',
    sortOrder: 'asc' | 'desc' = 'desc',
    status?: string,
  ): Promise<Discount[]> {
    const skip = (pageNumber - 1) * pageSize; // Tính offset để phân trang

    // ✅ Cách sửa lỗi: ép kiểu về Record<string, SortOrder>
    const sortOption: Record<string, SortOrder> = {
      [sortField]: sortOrder === 'asc' ? 1 : -1,
    };

    const query: any = { shop_id: userId };
    if (status) query.status = status;

    return await this.discountModel
      .find(query)
      .sort(sortOption) // ✅ Không còn lỗi TypeScript
      .skip(skip)
      .limit(pageSize)
      .exec();
  }

  // ✅ Lấy discount theo ID
  async getDiscountById(discountId: string): Promise<Discount> {
    const discount = await this.discountModel.findById(discountId).exec();
    if (!discount) {
      throw new NotFoundException({
        message: ErrorCodeDetails[ErrorCode.DISCOUNT_NOT_FOUND].message,
        code: ErrorCode.DISCOUNT_NOT_FOUND,
      });
    }
    return discount;
  }

  // ✅ Lấy discount theo mã code
  async getDiscountByCode(discountCode: string): Promise<Discount> {
    const discount = await this.discountModel
      .findOne({ discount_code: discountCode })
      .exec();
    if (!discount) {
      throw new NotFoundException({
        message: ErrorCodeDetails[ErrorCode.DISCOUNT_NOT_FOUND].message,
        code: ErrorCode.DISCOUNT_NOT_FOUND,
      });
    }
    return discount;
  }

  // ✅ Lấy tất cả discount của một shop
  async getAllDiscountForShop(shopId: string): Promise<Discount[]> {
    return await this.discountModel.find({ shop_id: shopId }).exec();
  }

  // ✅ Lấy tất cả discount của hệ thống (dành cho admin)
  async getAllDiscounts(
    pageSize: number = 10,
    pageNumber: number = 1,
    sortField: string = 'discount_start_date',
    sortOrder: 'asc' | 'desc' = 'desc',
    discountType?: string,
  ): Promise<Discount[]> {
    const skip = (pageNumber - 1) * pageSize; // Tính offset để phân trang

    // ✅ Sửa lỗi kiểu TypeScript khi sử dụng sort()
    const sortOption: Record<string, SortOrder> = {
      [sortField]: sortOrder === 'asc' ? 1 : -1,
    };

    // ✅ Thêm điều kiện lọc
    const query: any = {};
    if (discountType) query.discount_type = discountType;

    return await this.discountModel
      .find(query)
      .sort(sortOption) // ✅ Sắp xếp theo trường mong muốn
      .skip(skip) // ✅ Phân trang (bỏ qua bản ghi đầu tiên)
      .limit(pageSize) // ✅ Giới hạn số lượng discount mỗi trang
      .exec();
  }
}
