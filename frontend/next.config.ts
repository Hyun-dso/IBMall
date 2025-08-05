// /next.config.js
/** @type {import('next').NextConfig} */
const nextConfig = {
  async rewrites() {
    return [
      {
        source: '/api/:path*',
        destination: 'http://192.168.52.215:8080/api/:path*',
      },
    ];
  },
};

export default nextConfig;
