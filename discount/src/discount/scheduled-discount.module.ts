import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { ScheduledDiscountController } from './controllers/scheduled-discount.controller';
import { ScheduledDiscountService } from './services/scheduled-discount.service';
import { ScheduledDiscountSchema } from './models/scheduled-discount.schema';

@Module({
  imports: [
    MongooseModule.forFeature([
      { name: 'ScheduledDiscount', schema: ScheduledDiscountSchema },
    ]), // Đảm bảo đã import schema
  ],
  controllers: [ScheduledDiscountController],
  providers: [ScheduledDiscountService],
  exports: [ScheduledDiscountService],
})
export class ScheduledDiscountModule {}
