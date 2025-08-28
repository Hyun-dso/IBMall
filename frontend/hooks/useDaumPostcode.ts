// /hooks/useDaumPostcode.ts
'use client';

import { useCallback, useRef, useState } from 'react';
import { ensurePostcodeScript } from '@/lib/validators/rules';

export function useDaumPostcode() {
    const [open, setOpen] = useState(false);
    const detailRef = useRef<HTMLInputElement | null>(null);

    const search = useCallback(async (onSelect: (address: string) => void) => {
        await ensurePostcodeScript();
        if (open) return;
        setOpen(true);

        new (window as any).daum.Postcode({
            oncomplete: (data: any) => {
                const addr: string = data?.roadAddress?.trim?.() || data?.address?.trim?.() || '';
                onSelect(addr);
                setOpen(false);
                setTimeout(() => detailRef.current?.focus(), 0);
            },
            onclose: () => setOpen(false),
        }).open();
    }, [open]);

    return { search, detailRef, open };
}
