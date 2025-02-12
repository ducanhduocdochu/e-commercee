module.exports = {
  collectCoverage: true, // ✅ Bật coverage
  collectCoverageFrom: [
    'src/discount/controllers/**/*.ts', // ✅ Chỉ tính coverage của controller
    'src/discount/services/**/*.ts', // ✅ Chỉ tính coverage của service
  ],
  coverageDirectory: 'coverage', // ✅ Thư mục chứa báo cáo coverage
  coverageReporters: ['json', 'lcov', 'text', 'clover'], // ✅ Định dạng báo cáo
  testEnvironment: 'node', // ✅ Đảm bảo môi trường test phù hợp với NestJS
};
