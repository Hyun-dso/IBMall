// /next.config.js
/** @type {import('next').NextConfig} */
const isProd = process.env.NODE_ENV === 'production';

const nextConfig = {
  async rewrites() {
    if (isProd) return []; // 배포: CloudFront가 /api/* → ALB 프록시
    return [
      // 개발: 프론트(3000) → 백엔드(8080) 프록시
      { source: '/api/:path*', destination: 'http://localhost:8080/api/:path*' },
    ];
  },
};

export default nextConfig;