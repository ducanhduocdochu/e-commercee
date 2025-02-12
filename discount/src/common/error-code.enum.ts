import { HttpStatus } from '@nestjs/common';

export enum ErrorCode {
  // General Errors
  UNCATEGORIZED_EXCEPTION = 9999,
  INVALID_KEY = 1001,

  // User Errors
  USER_EXISTED = 1002,
  USERNAME_INVALID = 1003,
  INVALID_PASSWORD = 1004,
  USER_NOT_EXISTED = 1005,
  UNAUTHENTICATED = 1006,
  UNAUTHORIZED = 1007,
  INVALID_DOB = 1008,
  INVALID_EMAIL = 1010,

  // Product Errors
  PRODUCT_NOT_FOUND = 2001,
  PRODUCT_EXISTED = 2002,
  PRODUCT_INVALID_PRICE = 2003,
  PRODUCT_INVALID_STOCK = 2004,
  PRODUCT_CATEGORY_NOT_FOUND = 2005,
  PRODUCT_UNAUTHORIZED = 2006,
  PRODUCT_INVALID_ID = 2007,
  PRODUCT_INVALID_QUANTITY = 2008,
  PRODUCT_INVALID_NAME = 2009,

  // Category Errors
  CATEGORY_NOT_FOUND = 3001,
  CATEGORY_EXISTED = 3002,
  CATEGORY_DELETE_FAILED = 3003,
  CATEGORY_INVALID_NAME = 3004,
  CATEGORY_INVALID_ID = 3005,

  // Cart Errors
  CART_NOT_FOUND = 6001,
  CART_EMPTY = 6002,
  CART_ITEM_NOT_FOUND = 6003,
  CART_ITEM_ALREADY_EXISTS = 6004,
  CART_INVALID_QUANTITY = 6005,
  CART_PRODUCT_OUT_OF_STOCK = 6006,
  CART_UNAUTHORIZED_ACCESS = 6007,

  // Order Errors
  ORDER_NOT_FOUND = 4001,
  ORDER_INVALID_STATUS = 4002,
  ORDER_PAYMENT_FAILED = 4003,

  // Discount Errors
  DISCOUNT_NOT_FOUND = 5001,
  DISCOUNT_EXPIRED = 5002,
  DISCOUNT_INVALID = 5003,
  DISCOUNT_NOT_APPLICABLE = 5004,
  DISCOUNT_LIMIT_REACHED = 5005,
  DISCOUNT_MIN_ORDER_VALUE = 5006,
  DISCOUNT_ALREADY_USED = 5007,
}

export class ErrorCodeDetails {
  static readonly [ErrorCode.UNCATEGORIZED_EXCEPTION]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.UNCATEGORIZED_EXCEPTION,
    message: 'Uncategorized error',
    statusCode: HttpStatus.INTERNAL_SERVER_ERROR,
  };

  static readonly [ErrorCode.INVALID_KEY]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.INVALID_KEY,
    message: 'Invalid key',
    statusCode: HttpStatus.BAD_REQUEST,
  };

  static readonly [ErrorCode.DISCOUNT_NOT_FOUND]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.DISCOUNT_NOT_FOUND,
    message: 'Discount code not found',
    statusCode: HttpStatus.NOT_FOUND,
  };

  static readonly [ErrorCode.DISCOUNT_EXPIRED]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.DISCOUNT_EXPIRED,
    message: 'Discount code has expired',
    statusCode: HttpStatus.BAD_REQUEST,
  };

  static readonly [ErrorCode.DISCOUNT_INVALID]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.DISCOUNT_INVALID,
    message: 'Invalid discount code',
    statusCode: HttpStatus.BAD_REQUEST,
  };

  static readonly [ErrorCode.DISCOUNT_NOT_APPLICABLE]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.DISCOUNT_NOT_APPLICABLE,
    message: 'Discount code not applicable for this order',
    statusCode: HttpStatus.BAD_REQUEST,
  };

  static readonly [ErrorCode.DISCOUNT_LIMIT_REACHED]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.DISCOUNT_LIMIT_REACHED,
    message: 'Discount limit reached',
    statusCode: HttpStatus.BAD_REQUEST,
  };

  static readonly [ErrorCode.DISCOUNT_MIN_ORDER_VALUE]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.DISCOUNT_MIN_ORDER_VALUE,
    message:
      'Order value does not meet the minimum requirement for the discount',
    statusCode: HttpStatus.BAD_REQUEST,
  };

  static readonly [ErrorCode.DISCOUNT_ALREADY_USED]: {
    code: number;
    message: string;
    statusCode: HttpStatus;
  } = {
    code: ErrorCode.DISCOUNT_ALREADY_USED,
    message: 'Discount code has already been used',
    statusCode: HttpStatus.BAD_REQUEST,
  };
}
