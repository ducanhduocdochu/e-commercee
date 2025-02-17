import {
  ExceptionFilter,
  Catch,
  ArgumentsHost,
  HttpException,
  HttpStatus,
} from '@nestjs/common';
import { Response } from 'express';
import { ApiResponse } from '../dtos/api-response.dto'; // Import DTO chuẩn

@Catch()
export class HttpExceptionFilter implements ExceptionFilter {
  catch(exception: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();

    let status: number;
    let errorMessage: string;
    let errorCode: number | null = null;
    let errorDetails: any = null;

    // ✅ Nếu exception là HttpException của NestJS
    if (exception instanceof HttpException) {
      status = exception.getStatus();
      const responseBody = exception.getResponse();

      // ✅ Nếu responseBody là object (Validation Error hoặc Custom Error)
      if (typeof responseBody === 'object' && responseBody !== null) {
        errorMessage = (responseBody as any).message || 'An error occurred';
        errorCode = (responseBody as any).code || status; // ✅ Lấy code lỗi nếu có
        errorDetails = responseBody;
      } else {
        errorMessage = String(responseBody);
        errorCode = status;
      }
    } else if (exception instanceof Error) {
      // ✅ Nếu là lỗi hệ thống (Runtime error)
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      errorMessage = exception.message;
      errorCode = status;
      errorDetails = {
        name: exception.name,
        stack: exception.stack, // ✅ Log stack trace để debug
      };
    } else {
      // ✅ Nếu là lỗi không xác định (Unknown Exception)
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      errorMessage = 'Internal Server Error';
      errorCode = status;
    }

    // ✅ Log lỗi chi tiết để debug
    console.error('❌ Exception:', {
      status,
      code: errorCode,
      message: errorMessage,
      details: errorDetails,
    });

    // ✅ Chuẩn hóa response với ApiResponse DTO
    const apiResponse = new ApiResponse(errorCode || status, errorMessage);
    response.status(status).json(apiResponse);
  }
}
