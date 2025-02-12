import {
  Injectable,
  CanActivate,
  ExecutionContext,
  UnauthorizedException,
} from '@nestjs/common';
import * as jwt from 'jsonwebtoken';

@Injectable()
export class AuthGuard implements CanActivate {
  async canActivate(context: ExecutionContext): Promise<boolean> {
    const request = context.switchToHttp().getRequest();
    const authHeader = request.headers['authorization'];

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new UnauthorizedException('Token is missing or invalid');
    }

    const token = authHeader.split(' ')[1];

    try {
      // ✅ Xác thực và giải mã token bằng SECRET_KEY
      const decoded: any = jwt.verify(token, process.env.JWT_SECRET_KEY);

      // ✅ Lấy `user_id` từ `sub` (subject)
      const userId = decoded.sub; // Trong Java, `subject(user.getId())`

      // ✅ Chuyển đổi `scope` thành danh sách quyền
      const permissions = decoded.scope ? decoded.scope.split(' ') : [];

      // ✅ Lưu thông tin vào request để các Guard khác có thể dùng
      request.user = {
        id: userId, // ID người dùng từ Java JWT
        permissions, // Danh sách quyền từ scope
      };

      return true; // Cho phép request tiếp tục
    } catch (error) {
      throw new UnauthorizedException('Invalid token');
    }
  }
}
