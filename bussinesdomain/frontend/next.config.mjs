/** @type {import('next').NextConfig} */
const gateway = process.env.API_GATEWAY_URL || "http://localhost:8080";

const nextConfig = {
  output: "standalone",
  async rewrites() {
    return [
      {
        source: "/api/gw/:path*",
        destination: `${gateway}/:path*`,
      },
    ];
  },
};

export default nextConfig;
