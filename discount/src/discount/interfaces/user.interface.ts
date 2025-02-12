// src/interfaces/user.interface.ts
export interface User {
  id: string;
  username: string;
  role: 'admin' | 'seller' | 'buyer'; // Điều chỉnh theo hệ thống vai trò của bạn
}
