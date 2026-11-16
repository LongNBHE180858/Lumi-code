/** @type {import('tailwindcss').Config} */
export default {
  content: ['./src/main/webapp/**/*.{html,js,ts,jsx,tsx}'],
  corePlugins: {
    preflight: false, // Giữ nguyên reset của Bootstrap 5, không gây xung đột style
  },
  blocklist: ['collapse'],
  theme: {
    extend: {},
  },
  plugins: [],
};
