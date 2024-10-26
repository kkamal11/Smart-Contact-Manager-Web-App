/** @type {import('tailwindcss').Config} */
export default {
  mode: 'jit',
  content: [
    "./src/main/resources/**/*.{html,js}",
    "./src/main/resources/templates/user/*.{html,js}"
  ],
  theme: {
    extend: {},
  },
  plugins: [],
  darkMode: 'selector'
}

