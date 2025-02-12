import { Test, TestingModule } from '@nestjs/testing';
import { ScheduledDiscountController } from './scheduled-discount.controller';
import { ScheduledDiscountService } from '../services/scheduled-discount.service';
import { ScheduledDiscountDto } from '../dtos/scheduled-discount.dto';
import { NotFoundException } from '@nestjs/common';

const mockScheduledDiscount = {
  _id: '123456',
  scheduled_time: new Date(), // ✅ Thêm trường này
  discount_name: 'Scheduled Black Friday',
  discount_description: 'Scheduled discount for Black Friday',
  discount_type: 'Percent',
  discount_value: 30,
  discount_code: 'SCHEDULEDFRIDAY30',
  discount_start_date: new Date(),
  discount_end_date: new Date(),
  discount_max_uses: 1000,
  discount_max_value: '500000',
  discount_use_count: 0,
  discount_invisable: false,
  discount_min_order_value: 500000,
  shop_id: 'seller123',
  discount_applies_type: 'Seller',
};

describe('ScheduledDiscountController', () => {
  let controller: ScheduledDiscountController;
  let service: ScheduledDiscountService;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [ScheduledDiscountController],
      providers: [
        {
          provide: ScheduledDiscountService,
          useValue: {
            createSetting: jest.fn().mockResolvedValue(mockScheduledDiscount),
            updateSetting: jest.fn().mockResolvedValue(mockScheduledDiscount),
            deleteSetting: jest.fn().mockResolvedValue(mockScheduledDiscount),
            getMySettings: jest.fn().mockResolvedValue([mockScheduledDiscount]),
            getMySettingDetail: jest
              .fn()
              .mockResolvedValue(mockScheduledDiscount),
            getSellerSettings: jest
              .fn()
              .mockResolvedValue([mockScheduledDiscount]),
            getSellerSettingDetail: jest
              .fn()
              .mockResolvedValue(mockScheduledDiscount),
          },
        },
      ],
    }).compile();

    controller = module.get<ScheduledDiscountController>(
      ScheduledDiscountController,
    );
    service = module.get<ScheduledDiscountService>(ScheduledDiscountService);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  it('should create a scheduled discount', async () => {
    const result = await controller.createSettingBySeller(
      { user: { id: 'seller123' } } as any,
      mockScheduledDiscount as ScheduledDiscountDto,
    );

    expect(result).toEqual(mockScheduledDiscount);
    expect(service.createSetting).toHaveBeenCalled();
  });

  it('should update a scheduled discount', async () => {
    const result = await controller.updateSettingBySeller(
      { user: { id: 'seller123' } } as any,
      '123456',
      mockScheduledDiscount as ScheduledDiscountDto,
    );

    expect(result).toEqual(mockScheduledDiscount);
    expect(service.updateSetting).toHaveBeenCalled();
  });

  it('should delete a scheduled discount', async () => {
    const result = await controller.deleteSettingBySeller(
      { user: { id: 'seller123' } } as any,
      '123456',
    );

    expect(result).toEqual(mockScheduledDiscount);
    expect(service.deleteSetting).toHaveBeenCalled();
  });

  it('should get all scheduled discounts for a seller', async () => {
    const result = await controller.getMySettings({
      user: { id: 'seller123' },
    } as any);

    expect(result).toEqual([mockScheduledDiscount]);
    expect(service.getMySettings).toHaveBeenCalled();
  });

  it('should throw NotFoundException if scheduled discount not found', async () => {
    jest
      .spyOn(service, 'getMySettingDetail')
      .mockRejectedValueOnce(new NotFoundException());

    await expect(
      controller.getMySettingDetail(
        { user: { id: 'seller123' } } as any,
        'notfound',
      ),
    ).rejects.toThrow(NotFoundException);
  });
});
