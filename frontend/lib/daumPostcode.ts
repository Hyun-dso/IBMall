// /lib/daumPostcode.ts
'use client';

let loaded = false;
export async function ensurePostcodeScript(): Promise<void> {
    if (loaded || (window as any).daum?.Postcode) {
        loaded = true;
        return;
    }
    await new Promise<void>((resolve) => {
        const script = document.createElement('script');
        script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
        script.onload = () => {
            loaded = true;
            resolve();
        };
        document.body.appendChild(script);
    });
}
