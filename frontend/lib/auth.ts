// /lib/auth.ts

import { parse } from 'cookie';
import type { User } from '@/types/auth';

export async function getUserFromServer(cookie: string): Promise<User | null> {
  const parsed = parse(cookie);
  const accessToken = parsed.accessToken;

  if (!accessToken) return null;

  const res = await fetch(`http://localhost:8080/api/members/me`, {
    headers: {
      Cookie: `accessToken=${accessToken}`,
    },
    cache: 'no-store',
  });

  if (!res.ok) return null;

  const json = await res.json();
  return json.data;
}
