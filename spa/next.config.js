/** @type {import('next').NextConfig} */
const nextConfig = {
  output: "export",
  trailingSlash: true,
  reactStrictMode: true,
  experimental: {
    forceSwcTransforms: true,
    ppr: "incremental",
  },
};

module.exports = nextConfig;
