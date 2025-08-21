// tailwind.config.js
export default {
    content: [
        './app/**/*.{js,ts,jsx,tsx}',
        './components/**/*.{js,ts,jsx,tsx}',
    ],
    theme: {
        extend: {
            colors: {
                primary: '#BFA2DB',
                accent: '#b668ffff',
                background: '#F3F1F5',
                surface: '#E5E5E5',
                'text-primary': '#000000',
                'text-secondary': '#6B7280',
                border: '#bbbbbbff',
                error: '#DC2626',
                warning: '#FACC15',
                success: '#16A34A',
                'dark-background': '#171717',
                'dark-surface': '#1A1A1A',
                'dark-text-primary': '#d1d5db',
                'dark-text-secondary': '#94A3B8',
                'dark-border': '#444444',
            },
        },
    },
    plugins: [],
};
