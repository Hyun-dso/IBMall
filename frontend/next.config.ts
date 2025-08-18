import type { NextConfig } from 'next'

const nextConfig: NextConfig = {
  async rewrites() {
    if (process.env.NODE_ENV === 'development') {
      return [
        {
          source: '/api/:path*',
          destination: 'http://localhost:8080/api/:path*',
        },
      ]
    }
    // 운영 환경에서는 Nginx/ALB가 proxy_pass 처리
    return []
  },
}

export default nextConfig
