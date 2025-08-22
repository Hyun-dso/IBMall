// eslint.config.mjs
import next from 'eslint-config-next';

export default [
  ...next(),
  {
    files: ['**/*.{ts,tsx}'],
    plugins: { 'unused-imports': require('eslint-plugin-unused-imports') },
    rules: {
      // 사용 안 하는 import는 자동 삭제
      'unused-imports/no-unused-imports': 'warn',
      // 변수/파라미터는 _로 시작하면 무시 (자동 fix는 아니지만 노이즈 감소)
      '@typescript-eslint/no-unused-vars': ['warn', {
        argsIgnorePattern: '^_',
        varsIgnorePattern: '^_',
        caughtErrorsIgnorePattern: '^_',
      }],
      // any 경고만 (필요시 아래 override에서 끕니다)
      '@typescript-eslint/no-explicit-any': ['warn', { fixToUnknown: false, ignoreRestArgs: true }],
    },
    settings: {},
  },

  // 경로별 강약 조절
  // 1) 페이지·컴포넌트는 DX 위해 any 경고만
  {
    files: ['app/**/*.{ts,tsx}', 'components/**/*.{ts,tsx}'],
    rules: {
      '@typescript-eslint/no-explicit-any': 'warn',
    },
  },
  // 2) 라이브러리/유틸은 엄격 (원하면 unknown 제안)
  {
    files: ['lib/**/*.{ts,tsx}'],
    rules: {
      '@typescript-eslint/no-explicit-any': ['error', { fixToUnknown: true }],
    },
  },
  // 3) 자동 생성/서드파티 타입 등은 통째로 무시
  {
    files: ['.next/**', 'node_modules/**', 'next-env.d.ts', 'global.d.ts'],
    ignores: ['**/*'],
  },
];
