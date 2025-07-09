# 📦 IB Mall 프로젝트 - 회원/로그인/결제 관련 DB 구조

본 문서는 쇼핑몰 프로젝트 `ib_mall`의 로그인, 회원가입, 소셜 연동, 이메일 인증, 결제, 쪽지 관련 DB 테이블 구조를 문서화한 내용입니다.

---

## ✅ 사용 데이터베이스 정보

- DB 이름: `ib_mall`
- 사용자 계정: `ibmall_user`
- 비밀번호: `1234`

---

## 📁 테이블 목록

| 테이블명 | 설명 |
|----------|------|
| `member` | 회원 정보 (일반 + 소셜 로그인 통합) |
| `email_verification` | 이메일 인증 요청 정보 (회원가입용) |
| `password_reset_token` | 비밀번호 재설정 요청 정보 |
| `payment_log` | 결제 기록 (1원 테스트 결제용 포함) |
| `email_send_log` | 이메일 전송 로그 (모든 메일 발송 기록) |
| `member_message` | 사용자 쪽지함 (시스템 알림 등 저장) |

---

## 🧑‍💻 member – 회원 정보 테이블

```sql
CREATE TABLE member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255),
    nickname VARCHAR(50),
    provider VARCHAR(20), -- 'local', 'google', 'naver'
    provider_id VARCHAR(255),
    is_verified BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

일반 회원가입 시: provider = 'local', password 사용

소셜 로그인 시: provider = 'google' | 'naver', provider_id 저장

이메일 인증 여부 관리용 is_verified 포함

📧 email_verification – 이메일 인증 요청 정보
sql
복사
편집
CREATE TABLE email_verification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    expired_at DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
회원가입 이메일 인증용

토큰 유효기간 및 사용 여부 관리

🔒 password_reset_token – 비밀번호 재설정 토큰
sql
복사
편집
CREATE TABLE password_reset_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,
    expires_at DATETIME NOT NULL,
    is_used BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
비밀번호 재설정 시 인증 링크 발송용

이메일 기반 토큰 관리

💳 payment_log – 결제 기록
sql
복사
편집
CREATE TABLE payment_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    amount INT NOT NULL DEFAULT 1,
    status VARCHAR(50),
    payment_method VARCHAR(50),
    transaction_id VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);
결제 시도 및 결과 기록 (1원 테스트 포함)

상태: SUCCESS, FAILED, PENDING

📬 email_send_log – 이메일 전송 로그
sql
복사
편집
CREATE TABLE email_send_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    content TEXT,
    type VARCHAR(50), -- SIGNUP, RESET_PASSWORD, ORDER, COUPON 등
    success BOOLEAN DEFAULT TRUE,
    sent_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
발송된 모든 이메일의 로그 기록용 테이블

성공/실패 여부 저장

📩 member_message – 사용자 쪽지함
sql
복사
편집
CREATE TABLE member_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    member_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(50), -- ORDER, POINT, COUPON, SYSTEM 등
    is_read BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (member_id) REFERENCES member(id)
);

사용자 마이페이지에서 보여지는 쪽지함 기능용

읽음 여부, 생성일자 포함

포인트 적립/쿠폰 지급/주문 완료 등 이벤트 발생 시 자동 전송 가능

📌 확장 고려
쿠폰/포인트/주문/배송 등은 별도 팀에서 구현 예정

본 구조는 로그인 및 사용자 기반 처리 중심으로 설계됨

OAuth2 로그인은 Google/Naver 2종 지원 예정