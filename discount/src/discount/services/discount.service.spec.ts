import { Test, TestingModule } from '@nestjs/testing';
import { DiscountService } from '../services/discount.service';
import { getModelToken } from '@nestjs/mongoose';
import { Model } from 'mongoose';
import { Discount } from '../interfaces/discount.interface';

const mockDiscount = {
  _id: '123456',
  discount_name: 'Black Friday Sale',
  discount_description: 'Giảm giá 50% cho tất cả đơn hàng trên 500k',
  discount_type: 'Percent',
  discount_value: 50,
  discount_code: 'BLACKFRIDAY50',
  discount_start_date: new Date(),
  discount_end_date: new Date(),
  discount_max_uses: 1000,
  discount_max_value: '500000',
  discount_use_count: 0,
  discount_invisable: false,
  discount_min_order_value: 500000,
};

// ✅ Fix lỗi mock Model bằng cách sử dụng class thay vì object
class MockDiscountModel {
  constructor(private data: Partial<Discount>) {}

  static create = jest.fn().mockResolvedValue(mockDiscount);
  static findById = jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue(mockDiscount),
  });
  static findOneAndUpdate = jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue(mockDiscount),
  });
  static findOneAndDelete = jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue(mockDiscount),
  });
  static find = jest.fn().mockReturnValue({
    exec: jest.fn().mockResolvedValue([mockDiscount]),
  });
}

describe('DiscountService', () => {
  let service: DiscountService;
  let model: Model<Discount>;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        DiscountService,
        {
          provide: getModelToken('Discount'),
          useValue: MockDiscountModel,
        },
      ],
    }).compile();

    service = module.get<DiscountService>(DiscountService);
    model = module.get<Model<Discount>>(getModelToken('Discount'));
  });

  afterEach(() => {
    jest.clearAllMocks(); // ✅ Reset lại mock giữa các test để tránh bị ảnh hưởng.
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  it('should create a discount', async () => {
    const result = await service.createDiscount({
      createDiscountDto: mockDiscount,
      userId: 'seller123',
      discountAppliesType: 'Seller',
    });

    expect(result).toEqual(mockDiscount);
    expect(MockDiscountModel.create).toHaveBeenCalledWith({
      ...mockDiscount,
      shop_id: 'seller123',
      discount_applies_type: 'Seller',
    });
  });

  it('should get a discount by ID', async () => {
    const result = await service.getDiscountById('123456');

    expect(result).toEqual(mockDiscount);
    expect(MockDiscountModel.findById).toHaveBeenCalledWith('123456');
    expect(MockDiscountModel.findById().exec).toHaveBeenCalled();
  });

  it('should update a discount', async () => {
    const updatedDiscount = { ...mockDiscount, discount_value: 30 };
    MockDiscountModel.findOneAndUpdate.mockReturnValue({
      exec: jest.fn().mockResolvedValue(updatedDiscount),
    });

    const result = await service.updateDiscount({
      discountId: '123456',
      createDiscountDto: updatedDiscount,
      userId: 'seller123',
    });

    expect(result).toEqual(updatedDiscount);
    expect(MockDiscountModel.findOneAndUpdate).toHaveBeenCalledWith(
      { shop_id: 'seller123', _id: '123456' },
      updatedDiscount,
      { new: true },
    );
    expect(MockDiscountModel.findOneAndUpdate().exec).toHaveBeenCalled();
  });

  it('should delete a discount', async () => {
    const result = await service.deleteDiscount({
      discountId: '123456',
      userId: 'seller123',
    });

    expect(result).toEqual(mockDiscount);
    expect(MockDiscountModel.findOneAndDelete).toHaveBeenCalledWith({
      _id: '123456',
      shop_id: 'seller123',
    });
    expect(MockDiscountModel.findOneAndDelete().exec).toHaveBeenCalled();
  });

  it('should get all discounts for a shop', async () => {
    const result = await service.getAllDiscountForShop('shop123');

    expect(result).toEqual([mockDiscount]);
    expect(MockDiscountModel.find).toHaveBeenCalledWith({ shop_id: 'shop123' });
    expect(MockDiscountModel.find().exec).toHaveBeenCalled();
  });
});
