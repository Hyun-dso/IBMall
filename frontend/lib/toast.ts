// /lib/toast.ts
'use client';
import { toast } from 'react-hot-toast';

type ToastKind = 'success' | 'error' | 'loading';
type Options = {
  persist?: boolean;   // 강한 전환 후 다음 화면에서 재표시
  showNow?: boolean;   // persist 시 현재 화면에서도 즉시 표시
  group?: string;      // 없으면 pathname 또는 data-toast-group
};

const KEY = '__next_toast__' as const;

// 그룹 → 현재 표시 중인 토스트 인스턴스 id
const currentByGroup = new Map<string, string>();
let seq = 0;

function resolveGroup(explicit?: string): string {
  if (explicit) return explicit;
  if (typeof document !== 'undefined') {
    const scoped = document.body.getAttribute('data-toast-group');
    if (scoped) return scoped;
  }
  if (typeof window !== 'undefined') return window.location.pathname || 'global';
  return 'global';
}

function nextInstanceId(group: string) {
  seq += 1;
  return `g:${group}#${seq}`;
}

function show(kind: ToastKind, message: string, id: string) {
  const base = { id };
  switch (kind) {
    case 'success': {
      toast.success(message, base);
      return;
    }
    case 'error': {
      toast.error(message, base);
      return;
    }
    case 'loading': {
      toast.loading(message, base);
      return;
    }
    default:
      toast(message, base);
      return;
  }
}

// 동일 그룹은 항상 교체: 기존 인스턴스 dismiss → 새 id로 즉시 show
function spawn(kind: ToastKind, message: string, group: string) {
  const prevId = currentByGroup.get(group);
  if (prevId) toast.dismiss(prevId);

  const newId = nextInstanceId(group);
  currentByGroup.set(group, newId);
  show(kind, message, newId);
}

// 전환 후 재표시용 큐(그룹 문자열만 보관)
type QueuedToast = { kind: ToastKind; message: string; group: string };
function enqueue(kind: ToastKind, message: string, group: string) {
  try {
    const payload: QueuedToast = { kind, message, group };
    sessionStorage.setItem(KEY, JSON.stringify(payload));
  } catch { }
}

function handle(kind: ToastKind, msg: string, opts?: Options) {
  const group = resolveGroup(opts?.group);

  if (opts?.persist) {
    if (opts.showNow) spawn(kind, msg, group);
    enqueue(kind, msg, group);
    return;
  }
  spawn(kind, msg, group);
}

export const showToast = {
  success(msg: string, opts?: Options) { handle('success', msg, opts); },
  error(msg: string, opts?: Options) { handle('error', msg, opts); },
  loading(msg: string, opts?: Options) { handle('loading', msg, opts); },

  async promise<T>(
    p: Promise<T>,
    msgs: { loading: string; success: string; error: string },
    opts?: { persistOn?: 'success' | 'error' | 'always'; showNow?: boolean; group?: string }
  ): Promise<T> {
    const group = resolveGroup(opts?.group);
    const loadingId = `g:${group}:loading`;

    toast.loading(msgs.loading, { id: loadingId, duration: Infinity });
    try {
      const res = await p;
      toast.dismiss(loadingId);

      const persist = opts?.persistOn === 'success' || opts?.persistOn === 'always';
      if (persist) {
        if (opts?.showNow) spawn('success', msgs.success, group);
        enqueue('success', msgs.success, group);
      } else {
        spawn('success', msgs.success, group);
      }
      return res;
    } catch (e) {
      toast.dismiss(loadingId);

      const persist = opts?.persistOn === 'error' || opts?.persistOn === 'always';
      if (persist) {
        if (opts?.showNow) spawn('error', msgs.error, group);
        enqueue('error', msgs.error, group);
      } else {
        spawn('error', msgs.error, group);
      }
      throw e;
    }
  },

  flushQueued() {
    try {
      const raw = sessionStorage.getItem(KEY);
      if (!raw) return;
      sessionStorage.removeItem(KEY);
      const q = JSON.parse(raw) as QueuedToast;
      spawn(q.kind, q.message, q.group);
    } catch { }
  },

  dismissGroup(group?: string) {
    const g = resolveGroup(group);
    const id = currentByGroup.get(g);
    if (id) toast.dismiss(id);
  },

  dismissAll() {
    toast.dismiss();
  },
};
