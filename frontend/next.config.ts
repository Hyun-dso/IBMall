// next.config.js
/** / @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
  if (process.env.NODE_ENV && process.env.NODE_ENV === 'development') {
    return [{ source: '/api/:path*', destination: 'http://localhost:8080/api/:path*' }];
  }
  return [];
  },
};
module.exports = nextConfig;
