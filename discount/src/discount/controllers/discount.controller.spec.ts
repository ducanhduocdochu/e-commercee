import { Test, TestingModule } from '@nestjs/testing';
import { DiscountController } from '../controllers/discount.controller';
import { DiscountService } from '../services/discount.service';
import { CreateDiscountDto } from '../dtos/create-discount.dto';

const mockDiscount: CreateDiscountDto = {
  discount_name: 'Black Friday Sale',
  discount_description: 'Giảm giá 50% cho tất cả đơn hàng trên 500k',
  discount_type: 'Percent',
  discount_value: 50,
  discount_code: 'BLACKFRIDAY50',
  discount_start_date: new Date(),
  discount_end_date: new Date(),
  discount_max_uses: 1000,
  discount_max_value: 500000,
  discount_use_count: 0,
  discount_invisable: false,
  discount_min_order_value: 500000,
};

const mockDiscountService = {
  createDiscount: jest.fn().mockResolvedValue(mockDiscount),
  getDiscountById: jest.fn().mockResolvedValue(mockDiscount), // ✅ Đúng
  updateDiscount: jest.fn().mockResolvedValue(mockDiscount),
  deleteDiscount: jest.fn().mockResolvedValue(mockDiscount),
};

describe('DiscountController', () => {
  let controller: DiscountController;

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [DiscountController],
      providers: [{ provide: DiscountService, useValue: mockDiscountService }],
    }).compile();

    controller = module.get<DiscountController>(DiscountController);
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  it('should create a discount', async () => {
    const result = await controller.createDiscountBySeller(
      { user: { id: 'seller123' } },
      mockDiscount,
    );
    expect(result).toEqual(mockDiscount);
    expect(mockDiscountService.createDiscount).toHaveBeenCalledWith({
      createDiscountDto: mockDiscount,
      userId: 'seller123',
      discountAppliesType: 'Seller',
    });
  });

  it('should get a discount by ID', async () => {
    const result = await controller.getDiscount('discount123');
    expect(result).toEqual(mockDiscount);
    expect(mockDiscountService.getDiscountById).toHaveBeenCalledWith(
      'discount123',
    ); // ✅ Đúng
  });

  it('should update a discount', async () => {
    const result = await controller.updateDiscountBySeller(
      { user: { id: 'seller123' } },
      'discount123',
      mockDiscount,
    );
    expect(result).toEqual(mockDiscount);
  });

  it('should delete a discount', async () => {
    const result = await controller.deleteDiscountBySeller(
      { user: { id: 'seller123' } },
      'discount123',
    );
    expect(result).toEqual(mockDiscount);
  });
});
