// /components/ui/Button.tsx
'use client';

import { cva, type VariantProps } from 'class-variance-authority';
import { Slot } from '@radix-ui/react-slot';
import { type ButtonHTMLAttributes } from 'react';
import { cn } from '@/lib/utils'; // 없으면 /lib/utils.ts에 cn 유틸 추가

const buttonVariants = cva(
    'inline-flex items-center justify-center rounded-md font-medium transition-colors focus:outline-none disabled:opacity-60 disabled:cursor-not-allowed hover:cursor-pointer',
    {
        variants: {
            variant: {
                solid:
                    // 배경은 primary, hover는 opacity로 처리(팔레트 외 색상 금지)
                    'bg-primary text-text-primary hover:opacity-90',
                outline:
                    'border border-border bg-transparent text-text-primary hover:bg-surface dark:border-dark-border dark:text-dark-text-primary dark:hover:bg-dark-surface',
                ghost:
                    'bg-transparent text-text-primary hover:bg-surface dark:text-dark-text-primary dark:hover:bg-dark-surface',
            },
            size: {
                sm: 'h-9 px-3 text-sm',
                md: 'h-11 px-4 text-base',
                lg: 'h-12 px-6 text-base',
                icon: 'h-11 w-11 p-0',
            },
            full: {
                true: 'w-full',
                false: '',
            },
        },
        defaultVariants: {
            variant: 'solid',
            size: 'md',
            full: false,
        },
    }
);

export interface ButtonProps
    extends ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
    asChild?: boolean;
}

export default function Button({
    className,
    variant,
    size,
    full,
    asChild = false,
    type = 'button',
    ...props
}: ButtonProps) {
    const Comp = asChild ? Slot : 'button';
    return (
        <Comp
            type={asChild ? undefined : type}
            className={cn(buttonVariants({ variant, size, full }), className)}
            {...props}
        />
    );
}
