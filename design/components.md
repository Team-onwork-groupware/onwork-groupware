# Component Catalog

design-agent와 frontend-agent가 따르는 컴포넌트 명세. 임의 컴포넌트 신규 생성 금지, 여기 정의된 것을 재사용한다.

## Button

- Variants: primary, secondary, ghost, danger
- Sizes: sm, md, lg
- States: default, hover, active, disabled, loading
- Token bindings: `color.primary`, `color.primary-contrast`, `radius.md`, `spacing.md`

## Card

- Variants: plain, elevated, outline
- Padding: `spacing.lg`
- Token bindings: `color.surface`, `color.border`, `radius.lg`, `shadow.sm`

## Input (Text)

- Variants: default, error
- Sizes: sm, md
- Token bindings: `color.border`, `color.text`, `radius.md`, `spacing.sm`

## Modal

- Sizes: sm, md, lg
- Overlay: rgba(0,0,0,0.4)
- Token bindings: `color.background`, `radius.lg`, `shadow.md`

## Toast

- Variants: info, success, warning, error
- Position: top-right
- Token bindings: `color.surface`, `color.accent`, `radius.md`, `shadow.md`

## AI 기본 룩 회피 체크리스트

- [ ] 균등 간격 카드 격자(3x3) 사용 안 함 — 화면당 1~2개 강조 영역만 카드 사용
- [ ] 보라색 그라데이션 헤더 사용 안 함 — `color.primary` 단색 또는 무채색
- [ ] 모든 아이콘에 emoji 사용 금지 — SVG 아이콘 또는 텍스트 라벨
- [ ] 화면당 최소 한 영역은 `design/references/` 무드보드의 스타일 매칭 시도
