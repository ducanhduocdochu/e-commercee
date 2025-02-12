import { Injectable, NotFoundException } from '@nestjs/common';
import { InjectModel } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { ScheduledDiscount } from '../interfaces/scheduled-discount.interface';
import { ScheduledDiscountDto } from '../dtos/scheduled-discount.dto';

@Injectable()
export class ScheduledDiscountService {
  constructor(
    @InjectModel('ScheduledDiscount')
    private readonly scheduledDiscountModel: Model<ScheduledDiscount>,
  ) {}

  // ✅ 1 & 4. Tạo setting với seller hoặc admin
  async createSetting({
    userId,
    createSettingDto,
    discountAppliesType,
  }: {
    userId: string;
    createSettingDto: ScheduledDiscountDto;
    discountAppliesType: string;
  }): Promise<ScheduledDiscount> {
    return await this.scheduledDiscountModel.create({
      ...createSettingDto,
      shop_id: userId,
      discount_applies_type: discountAppliesType,
    });
  }

  // ✅ 2 & 5. Update setting (seller hoặc admin)
  async updateSetting({
    userId,
    settingId,
    createSettingDto,
  }: {
    userId: string;
    settingId: string;
    createSettingDto: ScheduledDiscountDto;
  }): Promise<ScheduledDiscount> {
    const updatedSetting = await this.scheduledDiscountModel
      .findOneAndUpdate({ _id: settingId, shop_id: userId }, createSettingDto, {
        new: true,
      })
      .exec(); // ✅ Thêm exec() để đảm bảo nó trả về một Promise

    if (!updatedSetting) {
      throw new NotFoundException('Scheduled setting not found');
    }
    return updatedSetting;
  }

  // ✅ 3 & 6. Xóa setting (seller hoặc admin)
  async deleteSetting({
    userId,
    settingId,
  }: {
    userId: string;
    settingId: string;
  }): Promise<ScheduledDiscount> {
    const deletedSetting = await this.scheduledDiscountModel
      .findOneAndDelete({
        _id: settingId,
        shop_id: userId,
      })
      .exec(); // ✅ Thêm exec() để đảm bảo nó trả về một Promise

    if (!deletedSetting) {
      throw new NotFoundException('Scheduled setting not found');
    }
    return deletedSetting;
  }

  // ✅ 7. Lấy danh sách setting của chính mình
  async getMySettings(userId: string): Promise<ScheduledDiscount[]> {
    return await this.scheduledDiscountModel.find({ shop_id: userId }).exec();
  }

  // ✅ 8. Lấy chi tiết setting của mình
  async getMySettingDetail({
    userId,
    settingId,
  }: {
    userId: string;
    settingId: string;
  }): Promise<ScheduledDiscount> {
    const setting = await this.scheduledDiscountModel
      .findOne({ _id: settingId, shop_id: userId })
      .exec(); // ✅ Thêm exec() để đảm bảo trả về đúng kiểu dữ liệu
    if (!setting) {
      throw new NotFoundException('Scheduled setting not found');
    }
    return setting;
  }

  // ✅ 9. Lấy danh sách setting của seller (chỉ admin)
  async getSellerSettings(sellerId: string): Promise<ScheduledDiscount[]> {
    return await this.scheduledDiscountModel.find({ shop_id: sellerId }).exec();
  }

  // ✅ 10. Lấy chi tiết setting của seller (chỉ admin)
  async getSellerSettingDetail({
    sellerId,
    settingId,
  }: {
    sellerId: string;
    settingId: string;
  }): Promise<ScheduledDiscount> {
    const setting = await this.scheduledDiscountModel
      .findOne({ _id: settingId, shop_id: sellerId })
      .exec(); // ✅ Thêm exec() để đảm bảo trả về đúng kiểu dữ liệu

    if (!setting) {
      throw new NotFoundException('Scheduled setting not found');
    }
    return setting;
  }
}
