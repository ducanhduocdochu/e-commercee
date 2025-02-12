import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { HttpExceptionFilter } from './common/filters/http-exception.filter';
import * as dotenv from 'dotenv';
import { ValidationPipe } from '@nestjs/common';
import { DocumentBuilder, SwaggerModule } from '@nestjs/swagger';

dotenv.config();

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  // ✅ Cấu hình Swagger
  const config = new DocumentBuilder()
    .setTitle('Discount API')
    .setDescription('API quản lý Discounts && Scheduled Discounts')
    .setVersion('1.0')
    .addBearerAuth() // ✅ Thêm hỗ trợ Authorization bằng JWT
    .build();

  const document = SwaggerModule.createDocument(app, config);

  // ✅ Tạo Swagger UI
  SwaggerModule.setup('api/docs', app, document);

  // ✅ Xuất API Docs dưới dạng JSON
  app.getHttpAdapter().get('/api/docs-json', (req, res) => {
    res.json(document);
  });

  // ✅ Kích hoạt ValidationPipe toàn bộ hệ thống
  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
      transform: true,
      exceptionFactory: (errors) => {
        return new Error(
          JSON.stringify({
            message: 'Validation failed',
            errors: errors.map((err) => ({
              field: err.property,
              errors: err.constraints ? Object.values(err.constraints) : [], // ✅ Fix lỗi tại đây
            })),
          }),
        );
      },
    }),
  );

  // Đăng ký filter xử lý lỗi toàn cục
  app.useGlobalFilters(new HttpExceptionFilter());

  await app.listen(7000);
}
bootstrap();
