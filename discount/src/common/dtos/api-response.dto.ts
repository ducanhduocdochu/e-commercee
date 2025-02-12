import { ApiProperty } from '@nestjs/swagger';

export class ApiResponse<T> {
  @ApiProperty()
  code: number;

  @ApiProperty()
  message: string;

  @ApiProperty()
  result?: T;

  constructor(code: number, message: string, result?: T) {
    this.code = code;
    this.message = message;
    this.result = result;
  }
}
