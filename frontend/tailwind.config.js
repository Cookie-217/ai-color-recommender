/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#eef6ff',
          100: '#d9eaff',
          200: '#bcd9ff',
          300: '#8ec2ff',
          400: '#599fff',
          500: '#3478f6',
          600: '#2058e6',
          700: '#1946c4',
          800: '#1b3b9e',
          900: '#1c367d'
        },
        accent: {
          400: '#ff7eb6',
          500: '#ff4d97',
          600: '#ed2f80'
        }
      },
      fontFamily: {
        sans: ['"Inter"', '"PingFang SC"', '"Microsoft YaHei"', 'system-ui', 'sans-serif']
      },
      boxShadow: {
        soft: '0 10px 40px -12px rgba(52,120,246,0.25)'
      },
      keyframes: {
        'fade-up': {
          '0%': { opacity: '0', transform: 'translateY(12px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' }
        },
        'pop': {
          '0%': { transform: 'scale(0.96)' },
          '100%': { transform: 'scale(1)' }
        }
      },
      animation: {
        'fade-up': 'fade-up 0.5s ease-out both',
        'pop': 'pop 0.25s ease-out'
      }
    }
  },
  plugins: []
}
