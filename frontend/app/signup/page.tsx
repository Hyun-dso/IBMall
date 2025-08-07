'use client';

import { useRef, useState } from 'react';
import Logo from '@/components/ui/Logo';
import Button from '@/components/ui/Button';
import { Toaster, toast } from 'react-hot-toast';
import { useRouter } from 'next/navigation';

export default function SignupPage() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [passwordMatch, setPasswordMatch] = useState(true);
    const [name, setName] = useState('');
    const [nickname, setNickname] = useState('');
    const [phone, setPhone] = useState('');
    const [address, setAddress] = useState('');
    const [address1, setAddress1] = useState('');
    const [address2, setAddress2] = useState('');
    const router = useRouter();

    const validateEmail = (email: string) => {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    };

    const validatePassword = (password: string) => {
        return password.length >= 8 && /[0-9]/.test(password) && /[a-zA-Z]/.test(password);
    };

    const validateNickname = (nickname: string) => {
        const regex = /^[a-zA-Z0-9가-힣]{2,20}$/;
        return regex.test(nickname);
    };

    const validatePhone = (phone: string) => {
        const digits = phone.replace(/\D/g, '');
        return /^010\d{7,8}$/.test(digits);
    };

    const handlePasswordChange = (value: string) => {
        setPassword(value);
        setPasswordMatch(value === confirmPassword);
    };

    const handleConfirmPasswordChange = (value: string) => {
        setConfirmPassword(value);
        setPasswordMatch(password === value);
    };

    const handleConfirmPasswordBlur = () => {
        if (confirmPassword && password !== confirmPassword) {
            setPasswordMatch(false);
            toast.dismiss();
            toast.error('비밀번호가 일치하지 않습니다.');
        }
    };

    const formatPhoneNumber = (value: string) => {
        const digits = value.replace(/\D/g, '').slice(0, 11);
        if (digits.length < 4) return digits;
        if (digits.length < 8) return `${digits.slice(0, 3)}-${digits.slice(3)}`;
        return `${digits.slice(0, 3)}-${digits.slice(3, 7)}-${digits.slice(7)}`;
    };

    const detailRef = useRef<HTMLInputElement>(null); // 상세주소 input을 위한 ref
    const [popupOpened, setPopupOpened] = useState(false);

    const handleSearchAddress = async () => {
        if (!(window as any).daum?.Postcode) {
            await new Promise<void>((resolve) => {
                const script = document.createElement('script');
                script.src = 'https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js';
                script.onload = () => resolve();
                document.body.appendChild(script);
            });
        }

        if (popupOpened) return;
        setPopupOpened(true);

        new (window as any).daum.Postcode({
            oncomplete: function (data: any) {
                const fullAddress = data.address;
                setAddress1(fullAddress);
                setPopupOpened(false);

                setTimeout(() => {
                    detailRef.current?.focus();
                }, 0);
            },
            onclose: () => {
                setPopupOpened(false);
            },
        }).open();
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        toast.dismiss(); // 로딩 제거

        if (!validateEmail(email)) {
            toast.error('올바른 이메일 형식이 아닙니다.');
            return;
        }

        if (!validatePassword(password)) {
            toast.error('비밀번호는 8자 이상, 숫자와 문자를 포함해야 합니다.');
            return;
        }

        if (password !== confirmPassword) {
            toast.error('비밀번호가 일치하지 않습니다.');
            return;
        }

        if (name.trim() === '') {
            toast.error('이름을 입력해주세요.');
            return;
        }

        if (!validateNickname(nickname)) {
            toast.error('닉네임은 2~20자 이내, 특수문자 없이 입력해야 합니다.');
            return;
        }

        if (!validatePhone(phone)) {
            toast.error('올바른 연락처 형식이 아닙니다. 예: 010-1234-5678');
            return;
        }

        if (address1.trim() === '') {
            toast.error('주소를 입력해주세요.');
            return;
        }

        if (address2.trim() === '') {
            toast.error('상세주소를 입력해주세요.');
            return;
        }
        setAddress([address1, address2].filter(Boolean).join(' '));
        setConfirmPassword(password);

        // API 요청
        try {
            toast.dismiss();
            toast.loading('회원가입 중...');

            const res = await fetch(`${process.env.API_BASE_URL}:8080/api/members/signup`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    email,
                    password,
                    confirmPassword,
                    name,
                    nickname,
                    phone,
                    address,
                }),
            });

            toast.dismiss();

            if (!res.ok) {
                const { message } = await res.json();
                toast.error(message || '회원가입에 실패했습니다.');
                return;
            }

            toast.success('회원가입 성공!');
            router.push('/signin');
        } catch (err) {
            toast.dismiss();
            toast.error('네트워크 오류가 발생했습니다.');
        }
    };

    return (
        <main className="min-h-screen flex items-center justify-center bg-background dark:bg-dark-background text-text-primary dark:text-dark-text-primary">
            <form
                noValidate
                onSubmit={handleSubmit}
                className="w-full max-w-sm p-8 bg-surface dark:bg-dark-surface rounded-lg shadow-md"
            >
                <div className="w-100 flex items-center justify-center">
                    <Logo />
                </div>
                <h1 className="text-2xl font-bold mb-6 text-center">회원가입</h1>

                {/* 이메일 + 비밀번호 */}
                <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                    <input
                        id="email"
                        type="email"
                        placeholder="이메일"
                        required
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                    <input
                        id="password"
                        type="password"
                        placeholder="비밀번호"
                        required
                        value={password}
                        onChange={e => handlePasswordChange(e.target.value)}
                        className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                    <input
                        id="confirmPassword"
                        type="password"
                        placeholder="비밀번호 확인"
                        required
                        value={confirmPassword}
                        onChange={e => handleConfirmPasswordChange(e.target.value)}
                        onBlur={handleConfirmPasswordBlur}
                        className="w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                </div>

                {/* 이름 + 닉네임 */}
                <div className="mb-4 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                    <input
                        id="name"
                        type="text"
                        placeholder="이름"
                        required
                        value={name}
                        onChange={e => setName(e.target.value)}
                        className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                    <input
                        id="nickname"
                        type="text"
                        placeholder="닉네임"
                        required
                        value={nickname}
                        onChange={e => setNickname(e.target.value)}
                        className="w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                </div>

                {/* 연락처 + 주소 */}
                <div className="mb-6 border border-border dark:border-dark-border rounded-md overflow-hidden bg-surface dark:bg-dark-surface">
                    <input
                        id="phone"
                        type="text"
                        placeholder="연락처"
                        required
                        value={phone}
                        onChange={e => setPhone(formatPhoneNumber(e.target.value))}
                        className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                    <input
                        id="address1"
                        type="text"
                        placeholder="주소"
                        required
                        value={address1}
                        readOnly
                        onClick={handleSearchAddress}
                        onFocus={handleSearchAddress}
                        className="w-full px-4 py-3 border-b border-border dark:border-dark-border bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                    <input
                        id="address2"
                        type="text"
                        placeholder="상세주소"
                        required
                        value={address2}
                        ref={detailRef}
                        onChange={e => setAddress2(e.target.value)}
                        className="w-full px-4 py-3 bg-transparent text-text-primary dark:text-dark-text-primary placeholder-text-secondary dark:placeholder-dark-text-secondary focus:outline-none"
                    />
                </div>

                {/* 버튼 */}
                <Button type="submit" fullWidth className="font-bold">
                    회원가입
                </Button>
            </form>
        </main>
    );
}
