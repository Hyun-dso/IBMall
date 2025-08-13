// /constants/styles.ts
export const CLASS = {
    card:
        'border border-border dark:border-dark-border rounded-lg bg-surface dark:bg-dark-surface shadow-sm',
    input:
        'w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none',
    section: 'max-w-7xl mx-auto px-4 sm:px-6 lg:px-8',
} as const;

export const INPUT_CLASS =
    'w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary ' +
    'placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none';

export const INPUT_GROUP_CLASS =
    'border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface';

export const INPUT_DIVIDER_CLASS =
    'border-b border-border dark:border-dark-border';

export const SR_ONLY = 'sr-only';
