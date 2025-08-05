/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    "./app/**/*.{js,ts,jsx,tsx}",
    "./components/**/*.{js,ts,jsx,tsx}",
    "./styles/**/*.{css}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#BFA2DB",
        accent: "#b668ff",
        background: "#F3F1F5",
        surface: "#ffffff",
        "text-primary": "#000000",
        "text-secondary": "#6B7280",
        border: "#bbbbbbff",
        error: "#DC2626",
        warning: "#FACC15",
        success: "#16A34A",
        "dark-background": "#171717",
        "dark-surface": "#1A1A1A",
        "dark-text-primary": "#d1d5db",
        "dark-text-secondary": "#94A3B8",
        "dark-border": "#444444",
      },
    },
  },
  plugins: [],
};