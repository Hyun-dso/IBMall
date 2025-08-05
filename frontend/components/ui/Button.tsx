// /components/ui/Button.tsx
import { cva } from 'class-variance-authority';
import { cn } from '@/lib/utils'; // clsx 래퍼일 수도 있음
import { ButtonHTMLAttributes } from 'react';

const buttonVariants = cva(
  'inline-flex items-center justify-center rounded font-medium transition-colors focus:outline-none disabled:opacity-50 disabled:pointer-events-none',
  {
    variants: {
      variant: {
        default: 'bg-primary text-white hover:bg-accent',
        outline: 'border border-border text-text-primary',
        ghost: 'text-text-secondary hover:text-text-primary',
      },
      size: {
        sm: 'h-8 px-3 text-sm',
        md: 'h-10 px-4 text-base',
        lg: 'h-12 px-6 text-lg',
      },
    },
    defaultVariants: {
      variant: 'default',
      size: 'md',
    },
  }
);

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'default' | 'outline' | 'ghost';
  size?: 'sm' | 'md' | 'lg';
}

export default function Button({ className, variant, size, ...props }: ButtonProps) {
  return (
    <button
      className={cn(buttonVariants({ variant, size }), className)}
      {...props}
    />
  );
}
