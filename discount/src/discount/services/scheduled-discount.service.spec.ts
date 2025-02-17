import { Test, TestingModule } from '@nestjs/testing';
import { ScheduledDiscountService } from './scheduled-discount.service';
import { getModelToken } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { ScheduledDiscount } from '../interfaces/scheduled-discount.interface';
import { NotFoundException } from '@nestjs/common';

const mockScheduledDiscount = {
  _id: '123456',
  scheduled_time: new Date(),
  discount_name: 'Scheduled Black Friday',
  discount_description: 'Scheduled discount for Black Friday',
  discount_type: 'Percent',
  discount_value: 30,
  discount_code: 'SCHEDULEDFRIDAY30',
  discount_start_date: new Date(),
  discount_end_date: new Date(),
  discount_max_uses: 1000,
  discount_max_value: 500000,
  discount_use_count: 0,
  discount_invisable: false,
  discount_min_order_value: 500000,
  shop_id: 'seller123',
  discount_applies_type: 'Seller',
};

const mockScheduledDiscountModel = {
  create: jest.fn().mockResolvedValue(mockScheduledDiscount), // ✅ Mock create()
  findById: jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue(mockScheduledDiscount),
  }), // ✅ Mock findById()
  findOneAndUpdate: jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue(mockScheduledDiscount),
  }), // ✅ Mock findOneAndUpdate()
  findOneAndDelete: jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue(mockScheduledDiscount),
  }), // ✅ Mock findOneAndDelete()
  find: jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue([mockScheduledDiscount]),
  }), // ✅ Mock find()
  findOne: jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue(mockScheduledDiscount),
  }), // ✅ Fix lỗi thiếu findOne()
};

describe('ScheduledDiscountService', () => {
  let service: ScheduledDiscountService;
  let model: Model<ScheduledDiscount>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ScheduledDiscountService,
        {
          provide: getModelToken('ScheduledDiscount'),
          useValue: mockScheduledDiscountModel,
        },
      ],
    }).compile();

    service = module.get<ScheduledDiscountService>(ScheduledDiscountService);
    model = module.get<Model<ScheduledDiscount>>(
      getModelToken('ScheduledDiscount'),
    );
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('should create a scheduled discount', async () => {
    const result = await service.createSetting({
      userId: 'seller123',
      createSettingDto: mockScheduledDiscount,
      discountAppliesType: 'Seller',
    });

    expect(result).toEqual(mockScheduledDiscount);
    expect(mockScheduledDiscountModel.create).toHaveBeenCalledWith({
      ...mockScheduledDiscount,
      shop_id: 'seller123',
      discount_applies_type: 'Seller',
    });
  });

  it('should update a scheduled discount', async () => {
    const result = await service.updateSetting({
      userId: 'seller123',
      settingId: '123456',
      createSettingDto: mockScheduledDiscount,
    });

    expect(result).toEqual(mockScheduledDiscount);
    expect(mockScheduledDiscountModel.findOneAndUpdate).toHaveBeenCalled();
  });

  it('should delete a scheduled discount', async () => {
    const result = await service.deleteSetting({
      userId: 'seller123',
      settingId: '123456',
    });

    expect(result).toEqual(mockScheduledDiscount);
    expect(mockScheduledDiscountModel.findOneAndDelete).toHaveBeenCalled();
  });

  it('should get scheduled discounts for a seller', async () => {
    const result = await service.getSellerSettings('seller123');

    expect(result).toEqual([mockScheduledDiscount]);
    expect(mockScheduledDiscountModel.find).toHaveBeenCalled();
  });

  it('should get a scheduled discount by ID', async () => {
    mockScheduledDiscountModel.findOne.mockReturnValue({
      exec: jest.fn().mockResolvedValue(mockScheduledDiscount),
    });

    const result = await service.getMySettingDetail({
      userId: 'seller123',
      settingId: '123456',
    });

    expect(result).toEqual(mockScheduledDiscount);
    expect(mockScheduledDiscountModel.findOne).toHaveBeenCalledWith({
      _id: '123456',
      shop_id: 'seller123',
    });
    expect(mockScheduledDiscountModel.findOne().exec).toHaveBeenCalled();
  });

  it('should throw an error when a scheduled discount is not found', async () => {
    mockScheduledDiscountModel.findOne.mockReturnValue({
      exec: jest.fn().mockResolvedValue(null), // ✅ Trả về null khi không tìm thấy
    });

    await expect(
      service.getMySettingDetail({
        userId: 'seller123',
        settingId: 'notfound',
      }),
    ).rejects.toThrow('Scheduled setting not found'); // ✅ Kiểm tra lỗi
  });
});
