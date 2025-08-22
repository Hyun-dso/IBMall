// /constants/styles.ts
export const CLASS = {
    card:
        'border border-border dark:border-dark-border rounded-lg bg-surface dark:bg-dark-surface shadow-sm',
    input:
        'w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none',
    section: 'max-w-7xl mx-auto px-4 sm:px-6 lg:px-8',
} as const;

export const FORM_CLASS = 'p-8 bg-surface dark:bg-dark-surface rounded-lg shadow-md'

export const INPUT_CLASS =
    'w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary ' +
    'placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none';

export const INPUT_GROUP_CLASS =
    'border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface';

export const INPUT_DIVIDER_CLASS =
    'border-b border-border dark:border-dark-border';

export const SR_ONLY = 'sr-only';

export const CENTER_CONTENT = 'w-full max-w-sm mx-auto my-auto';

export const CARD_CLASS = 'border border-border dark:border-dark-border bg-surface dark:bg-dark-surface rounded-md shadow';