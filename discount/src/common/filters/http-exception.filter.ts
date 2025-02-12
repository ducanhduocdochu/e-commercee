import { ExceptionFilter, Catch, ArgumentsHost } from '@nestjs/common';
import { Response } from 'express';
import { ApiResponse } from '../dtos/api-response.dto';

@Catch()
export class HttpExceptionFilter implements ExceptionFilter {
  catch(exception: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const status = exception instanceof Error ? 500 : 400;
    const errorMessage =
      exception instanceof Error ? exception.message : 'Internal Server Error';

    console.log(errorMessage);

    const apiResponse = new ApiResponse(status, errorMessage);

    response.status(status).json(apiResponse);
  }
}
