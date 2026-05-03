import type { NextConfig } from "next";

/** Spring Boot base URL for API proxy (see frontend/env.local.template). */
const BACKEND_ORIGIN = process.env.BACKEND_ORIGIN ?? "http://localhost:8081";

const nextConfig: NextConfig = {
  async rewrites() {
    return [
      {
        source: "/api/v1/:path*",
        destination: `${BACKEND_ORIGIN.replace(/\/$/, "")}/api/v1/:path*`,
      },
    ];
  },
};

export default nextConfig;
