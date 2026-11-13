# develog

TypeScript 개발자의 눈으로 다른 언어를 배우며 정리하는 사이트입니다.
사이트: https://grangbelrlurain.github.io/develog/

## 글이 만들어지는 과정

1. **학습**: 에이전트와 설명 → 퀴즈를 진행하고 세션 기록을 남긴다 (`/study`).
2. **초안**: 에이전트가 세션 기록으로 챕터와 예제를 쓴다 (`/draft`).
3. **검증**: 다른 에이전트가 예제를 실행하고 문장을 공식 문서와 대조한다 (`/verify`).
4. **검토**: 직접 읽고 머리말을 `status: reviewed`로 바꾼다.
5. **발행**: `main`에 푸시하면 예제 검증 후 `reviewed` 챕터만 배포된다.

규칙은 [STYLE.md](./STYLE.md)와 [AGENTS.md](./AGENTS.md)에 있습니다.

## 로컬 실행

```bash
npm install
```

```bash
npm run dev
```

`npm run dev`는 초안까지 보여주고, `npm run build`는 발행 대상만 빌드합니다.

## 예제 검증

```bash
npm run verify:java
```

```bash
cd examples/spring && mvn -q test
```
