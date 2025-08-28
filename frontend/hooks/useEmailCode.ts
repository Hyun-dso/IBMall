// /hooks/useEmailCode.ts
'use client';
import { useEffect, useRef, useState } from 'react';
import { sendSignupEmailCode } from '@/lib/api/account.client';

type Status = 'idle' | 'sending' | 'sent' | 'taken' | 'invalid' | 'cooldown' | 'error';

export function useEmailCode() {
    const [status, setStatus] = useState<Status>('idle');
    const [cooldown, setCooldown] = useState(0);
    const acRef = useRef<AbortController | null>(null);

    useEffect(() => {
        if (cooldown <= 0) return;
        const id = setInterval(() => setCooldown((s) => s - 1), 1000);
        return () => clearInterval(id);
    }, [cooldown]);

    async function send(email: string) {
        acRef.current?.abort();
        const ctrl = new AbortController();
        acRef.current = ctrl;

        setStatus('sending');
        const r = await sendSignupEmailCode(email, ctrl.signal);
        if (r.ok) { setStatus('sent'); return; }

        if (r.reason === 'cooldown') { setCooldown(r.retryAfter); setStatus('cooldown'); return; }
        if (r.reason === 'invalid') { setStatus('invalid'); return; }
        if (r.reason === 'taken') { setStatus('taken'); return; }
        setStatus('error');
    }

    return { status, cooldown, send };
}
