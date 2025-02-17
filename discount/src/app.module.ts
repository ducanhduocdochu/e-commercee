import { Module } from '@nestjs/common';
import { MongooseModule } from '@nestjs/mongoose';
import { DiscountModule } from './discount/discount.module';
import { ScheduledDiscountModule } from './discount/scheduled-discount.module';
import { DiscountInternalModule } from './discount/discount.internal.module';

@Module({
  imports: [
    MongooseModule.forRoot('mongodb://localhost:27017/ecommerce'), // Kết nối MongoDB
    DiscountModule, // Import module chứa DiscountService
    ScheduledDiscountModule,
    DiscountInternalModule,
  ],
})
export class AppModule {}
