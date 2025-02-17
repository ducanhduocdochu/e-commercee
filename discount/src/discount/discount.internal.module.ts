import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { DiscountService } from './services/discount.service';
import { DiscountSchema } from './models/discount.model';
import { DiscountInternalController } from './controllers/discount.internal.controller';

@Module({
  imports: [
    MongooseModule.forFeature([{ name: 'Discount', schema: DiscountSchema }]), // Đảm bảo đã import schema
  ],
  controllers: [DiscountInternalController],
  providers: [DiscountService],
  exports: [DiscountService],
})
export class DiscountInternalModule {}
