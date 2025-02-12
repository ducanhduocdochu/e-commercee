import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { DiscountController } from './controllers/discount.controller';
import { DiscountService } from './services/discount.service';
import { DiscountSchema } from './models/discount.model';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'Discount', schema: DiscountSchema }]), // Đảm bảo đã import schema
  ],
  controllers: [DiscountController],
  providers: [DiscountService],
  exports: [DiscountService],
})
export class DiscountModule {}
