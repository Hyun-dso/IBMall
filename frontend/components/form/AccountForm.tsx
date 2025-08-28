// /components/account/AccountFormAll.tsx
'use client';

import { useMemo, useReducer, useState } from 'react';
import Logo from '@/components/ui/Logo';
import { showToast } from '@/lib/toast';
import {
  CENTER_CONTENT,
  FORM_CLASS,
  INPUT_CLASS,
  INPUT_DIVIDER_CLASS,
  INPUT_GROUP_CLASS,
} from '@/app/constants/styles';
import type { MemberDto } from '@/types/account';
import { isPhone, normalizePhone, isPasswordConfirmed, formatPhoneInput, formatPhoneOnBlur, isEmail } from '@/lib/validators/rules';
import { useDaumPostcode } from '@/hooks/useDaumPostcode';
import { useEmailCode } from '@/hooks/useEmailCode';
import { Phone } from '@/types/common';

type Props = {
  initial?: Partial<MemberDto>;
  noEmail?: boolean;
  noPhone?: boolean;
  noAddress?: boolean;  // true면 address1/2 둘 다 비활성
  noPassword?: boolean;
  noName?: boolean;
  noNickname?: boolean;
  submitLabel?: string;
  title: string;
  onSubmit: (payload: Partial<MemberDto> & { password?: string }) => Promise<void> | void;
};

type FormState = {
  email: string;
  password: string;
  confirmPassword: string;
  name: string;
  nickname: string;
  phone: Phone;
  address1: string;
  address2: string;
};

export default function AccountForm({
  initial,
  noEmail = false,
  noPhone = false,
  noAddress = false,
  noPassword = false,
  noName = false,
  noNickname = false,
  submitLabel = '확인',
  title,
  onSubmit,
}: Props) {
  const [submitting, setSubmitting] = useState(false);
  const [pendingEmail, setPendingEmail] = useState(false);
  const group = 'account.form';

  const [form, update] = useReducer(
    (s: FormState, p: Partial<FormState>) => ({ ...s, ...p }),
    {
      email: initial?.email ?? '',
      password: '',
      confirmPassword: '',
      name: initial?.name ?? '',
      nickname: initial?.nickname ?? '',
      phone: initial?.phone ?? '',
      address1: initial?.address1 ?? '',
      address2: initial?.address2 ?? '',
    }
  );
  const { status, cooldown, send } = useEmailCode();
  const [authCode, setAuthCode] = useState<number | undefined>();

  const { search: openPostcode, detailRef } = useDaumPostcode();

  const flags = useMemo(
    () => ({ noEmail, noPhone, noAddress, noPassword, noName, noNickname }),
    [noEmail, noPhone, noAddress, noPassword, noName, noNickname]
  );

  function validate(): string | null {
    if (!flags.noEmail) {
      if (!form.email.trim()) return '이메일을 입력해야 해요';
      if (!isEmail(form.email)) return '이메일 형식이 올바르지 않아요';
    }
    if (!flags.noPassword) {
      if (!form.password.trim()) return '비밀번호를 입력해야 해요';
      if (!isPasswordConfirmed(form.password, form.confirmPassword)) return '비밀번호가 일치하지 않아요';
    }
    if (!flags.noName && !form.name.trim()) return '이름을 입력해야 해요';
    if (!flags.noNickname && !form.nickname.trim()) return '닉네임을 입력해야 해요';
    if (!flags.noPhone && form.phone && !isPhone(form.phone)) return '연락처 형식이 올바르지 않아요';
    // 주소 필수 규칙이 있으면 활성화:
    // if (!flags.noAddress && !form.address1.trim()) return '주소를 입력하세요.';
    return null;
  }

  function verifyEmail(authCode: number | undefined) {
    if (authCode === undefined) return;
    showToast.loading(authCode.toString());

    setPendingEmail(false);
  }

  function buildPayload(): Partial<MemberDto> & { password?: string } {
    const p: Partial<MemberDto> & { password?: string } = {};
    if (!flags.noEmail) p.email = form.email.trim();
    if (!flags.noPassword) p.password = form.password;
    if (!flags.noName) p.name = form.name.trim();
    if (!flags.noNickname) p.nickname = form.nickname.trim();
    if (!flags.noPhone) p.phone = form.phone ? normalizePhone(form.phone) : '';
    if (!flags.noAddress) {
      p.address1 = form.address1.trim();
      p.address2 = form.address2.trim();
    }
    return p;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (submitting) return;

    const msg = validate();
    if (msg) {
      showToast.error(msg, { group });
      return;
    }

    try {
      setSubmitting(true);
      await onSubmit(buildPayload());
    } catch {
      showToast.error('요청 처리 중 오류가 발생했어요', { group });
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <form
      noValidate
      className={`${FORM_CLASS} ${CENTER_CONTENT} flex flex-col justify-center items-center`}
      onSubmit={handleSubmit}
    >
      <Logo size="lg" />
      <h2 className="text-lg font-semibold">{title}</h2>

      {/* 이메일/비밀번호 */}
      {(!flags.noEmail || !flags.noPassword) && (
        <div className={`mt-4 ${INPUT_GROUP_CLASS}`}>
          {!flags.noEmail && (
            <div className='flex'>
              <input
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                type="email"
                placeholder="이메일"
                value={form.email}
                onChange={(e) => update({ email: e.currentTarget.value })}
                // onBlur={(e) => test(form.email)}
                autoComplete="email"
                autoCapitalize="none"
                autoCorrect="off"
                aria-invalid={!form.email.trim() ? true : undefined}
              />
              <button
                type='button'
                className={`text-center w-32 rounded-md border border-white ${status === 'sending' || status === 'cooldown' || !isEmail(form.email) ? 'hover:cursor-not-allowed' : 'hover:cursor-pointer'}`}
                onClick={() => send(form.email)}
                disabled={status === 'sending' || status === 'cooldown' || !isEmail(form.email)}>
                {cooldown > 0 ? cooldown : '메일 인증'}
              </button>
            </div>
          )}
          {pendingEmail && (
            <input className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
              type="number"
              placeholder="인증번호"
              value={authCode}
              inputMode="numeric"
              onChange={() => verifyEmail(authCode)}
            />
          )}
          {!flags.noPassword && (
            <>
              <input
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                type="password"
                placeholder="비밀번호"
                value={form.password}
                onChange={(e) => update({ password: e.currentTarget.value })}
              />
              <input
                className={INPUT_CLASS}
                type="password"
                placeholder="비밀번호 확인"
                value={form.confirmPassword}
                onChange={(e) => update({ confirmPassword: e.currentTarget.value })}
              />
            </>
          )}
        </div>
      )
      }

      {/* 이름/닉네임 */}
      {
        (!flags.noName || !flags.noNickname) && (
          <div className={`mt-4 ${INPUT_GROUP_CLASS}`}>
            {!flags.noName && (
              <input
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                type="text"
                placeholder="이름"
                value={form.name}
                onChange={(e) => update({ name: e.currentTarget.value })}
                autoComplete="name"
                autoCapitalize="words"
                autoCorrect="off"
              />
            )}
            {!flags.noNickname && (
              <input
                className={INPUT_CLASS}
                type="text"
                placeholder="닉네임"
                value={form.nickname}
                onChange={(e) => update({ nickname: e.currentTarget.value })}
              />
            )}
          </div>
        )
      }

      {/* 연락처/주소 */}
      {
        (!flags.noPhone || !flags.noAddress) && (
          <div className={`mt-4 ${INPUT_GROUP_CLASS}`}>
            {!flags.noPhone && (
              <input
                className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                type="tel"
                placeholder="연락처"
                value={form.phone}
                onChange={(e) => update({ phone: formatPhoneInput(e.currentTarget.value) })}
                onBlur={(e) => update({ phone: formatPhoneOnBlur(e.currentTarget.value) })}
                autoComplete="tel"
                inputMode="numeric"
              />
            )}
            {!flags.noAddress && (
              <>
                <input
                  className={`${INPUT_CLASS} ${INPUT_DIVIDER_CLASS}`}
                  type="text"
                  placeholder="주소"
                  readOnly
                  value={form.address1}
                  onClick={() => openPostcode((addr) => update({ address1: addr }))}
                  onFocus={() => openPostcode((addr) => update({ address1: addr }))}
                  autoComplete="address-line1"
                />
                <input
                  ref={detailRef}
                  className={INPUT_CLASS}
                  type="text"
                  placeholder="상세주소"
                  value={form.address2}
                  onChange={(e) => update({ address2: e.currentTarget.value })}
                  autoComplete="address-line2"
                />
              </>
            )}
          </div>
        )
      }

      <button
        type="submit"
        className="w-full mt-4 p-2 rounded-md border border-[var(--primary)] hover:cursor-pointer disabled:cursor-not-allowed"
        disabled={submitting}
      >
        {submitting ? '처리 중...' : submitLabel}
      </button>
    </form >
  );
}
