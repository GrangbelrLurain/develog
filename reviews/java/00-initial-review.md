# Java 01~08 초안 검증 보고서

- 검증 환경: OpenJDK 21.0.12, Spring Boot 3.4.1
- 판정: **확인**(실행으로 확인) / **틀림** / **과장** / **미확인**(실행하지 못했고 문서 지식에 근거)
- 근거 코드: `examples/java/Ch*.java`, `examples/spring/src/test/java/com/example/ticket/TicketApiTest.java`

8개 챕터 모두 `status: draft`로 두었다. "틀림" 항목이 남아 있는 챕터는 `verified`로 올리지 않는다.

## 전체에 걸친 문제

| 판정 | 내용 |
|---|---|
| 과장 | 제목이 "모던 자바(Java 21)"이지만 다루는 내용 대부분은 Java 8 이전부터 있던 것이다(`Optional`, Stream, 생성자 주입, 예외 계층, `==`/`equals`, `ConcurrentHashMap`). Java 9 이후 기능은 `record`(16), `sealed`(17), `Stream.toList()`(16), `ifPresentOrElse`(9) 정도다. 가상 스레드는 README에 나오지만 다루는 챕터가 없다. |
| 틀림 | "Java 21 `record`"라는 표현이 01, 06, 08장에 반복된다. `record`는 Java 16에 정식 도입됐다. |

## 01. record

| 판정 | 내용 |
|---|---|
| 확인 | `toString()` 형식, 값 기반 `equals`/`hashCode`, 방어적 복사가 없을 때 외부 변경이 새는 것, 리플렉션으로도 필드를 바꿀 수 없는 것. (`Ch01Record`) |
| 틀림 | "`List.copyOf()`는 완전한 불변 리스트 복사". 리스트만 수정 불가이고 요소 객체는 그대로 변경된다. 바로 위에서 경고한 얕은 불변성과 어긋난다. null 요소가 있으면 NPE를 던진다는 점도 빠져 있다. (`Ch01Record`) |
| 틀림 | 비교표의 "TypeScript 객체 리터럴에는 메서드 불가". 객체 리터럴은 메서드를 가질 수 있다. |
| 과장 | 비교표의 "컴파일 및 런타임 수준의 완전한 불변성". 얕은 불변성이다. |
| 미확인 | JPA 엔티티 요구사항(기본 생성자, non-final). JPA 명세와 일치하는 것으로 알고 있으나 실행하지 않았다. |

## 02. Optional

| 판정 | 내용 |
|---|---|
| 확인 | `orElse`는 값이 있어도 인자를 실행하고 `orElseGet`은 비어 있을 때만 실행한다. 빈 `Optional`의 `get()`은 `NoSuchElementException`. (`Ch02Optional`) |
| 과장 | "NPE를 원천 차단", "호출자에게 처리하도록 강제". 컴파일러는 아무것도 강제하지 않는다. `get()`을 바로 부를 수 있고 `Optional` 변수 자체가 null일 수도 있다(`Ch02Optional`). TypeScript의 `strictNullChecks`는 컴파일러가 강제한다는 점이 TypeScript 개발자에게 가장 중요한 차이인데 비교표에 없다. |
| 과장 | "`orElse`/`orElseGet` 차이가 수많은 장애를 일으킨다". 근거 없음. |
| 틀림(사소) | "`orElseGet`은 값이 null일 때만". "비어 있을 때만"이 맞다. |

## 03. Stream

| 판정 | 내용 |
|---|---|
| 확인 | 지연 평가와 `limit` 단락, 소비된 스트림 재사용 시 `IllegalStateException`, `toList()` 결과 수정 불가. (`Ch03Stream`) |
| 틀림 | "좋은 코드"가 "나쁜 코드"와 동작이 다르다. 원래 코드의 `customer != null` 검사가 빠져서 고객이 null이면 NPE가 난다(`Ch03Stream`). `distinct()`도 새로 추가돼 결과가 달라진다. |
| 틀림 | 비교표의 "JavaScript는 `find` 외에는 전체 순회". `some`, `every`, `findIndex`, `includes`도 중간에 멈춘다. 최신 JavaScript에는 지연 평가하는 Iterator helpers도 있다. |
| 틀림 | "외부 가변 리스트에 추가하므로 멀티스레드 환경에서 안전하지 않다". 예제의 리스트는 메서드 안의 지역 변수라 다른 스레드가 접근할 수 없다. |
| 과장 | "`parallelStream()`에서 블로킹이 일어나면 다른 모든 웹 요청의 스트림 연산이 멈춘다". 영향받는 것은 공용 풀을 쓰는 작업(다른 병렬 스트림 등)이다. 일반 `stream()`은 호출한 스레드에서 실행된다. |
| 과장 | 한 줄 요약의 "메모리 낭비 없이". `sorted`, `distinct`는 내부에 요소를 쌓는다. |
| 틀림(사소) | 컴파일 에러 문구가 IntelliJ의 것이다. javac는 "local variables referenced from a lambda expression must be final or effectively final". 예제에서 `int total`을 두 번 선언한다. |
| 미확인 | "`Collectors.toList()`는 가변 ArrayList 반환". 현재 구현은 그렇지만(`Ch03Stream`) API 문서는 타입과 가변성을 보장하지 않는다. |

## 04. DI

| 판정 | 내용 |
|---|---|
| 틀림 | 비교표의 "NestJS는 프레임워크 없는 테스트에 `Test.createTestingModule` 필요". NestJS도 `new TicketService(mock)`으로 바로 테스트할 수 있다. |
| 틀림 | "필드 주입은 순환 참조를 구동 시점에 잡지 못한다". Spring Boot 2.6부터 순환 참조는 주입 방식과 무관하게 기본 차단된다. 같은 챕터 지뢰 2의 "최신 스프링 부트는 기본 차단"과도 모순된다. |
| 틀림(추정) | 예제가 `result.isSuccess()`와 `result.message()`를 섞어 쓴다. `PaymentResult`가 record라면 접근자는 `success()`다. `PaymentResult` 정의가 본문에 없어 컴파일을 확인하지 못했다. |
| 과장 | "0.001초 만에 테스트", "테스트 속도가 수십 배 느려진다". 측정 없음. |
| 미확인 | 빈 충돌 메시지, `@Primary`/`@Qualifier`, `BeanCurrentlyInCreationException`. 알고 있는 동작과 일치하나 실행하지 않았다. |

## 05. 예외

| 판정 | 내용 |
|---|---|
| 확인 | `BusinessException`이 `ErrorCode`의 상태 코드로 변환된다. (`TicketApiTest`) |
| 틀림(중요) | 본문의 `@ExceptionHandler(Exception.class)`는 스프링이 원래 400/404/405로 처리하던 예외까지 전부 500으로 만든다. `@Valid` 검증 실패, 깨진 JSON, 경로 변수 타입 불일치, 없는 URL, 지원하지 않는 메서드가 모두 500이 된다(`TicketApiTest`의 다섯 테스트). 08장은 검증 실패가 400이라고 설명하므로 두 챕터를 합치면 설명과 다르게 동작한다. `ResponseEntityExceptionHandler`를 상속하거나 해당 예외의 핸들러를 따로 둬야 한다. |
| 틀림 | 지뢰 2의 예제가 컴파일되지 않는다. `ErrorCode.PAYMENT_GATEWAY_ERROR`가 enum에 없고, 원인 예외를 받는 3인자 생성자가 `BusinessException`에 없으며, `throw` 두 개가 연달아 있어 두 번째는 도달할 수 없는 코드다. |
| 과장 | "예외 생성은 수십~수백 배 무겁다". 이 환경에서 얕은 스택 기준 약 30배, 스택 깊이 60에서 약 150배로 측정됐다. 스택 깊이와 JVM에 따라 달라지므로 범위로 단정하기 어렵다. |
| 모순 | 지뢰 3은 "입력값 누락" 같은 분기에 예외를 쓰지 말라고 하지만, 본문은 `INVALID_PAYMENT_AMOUNT` 같은 입력 오류를 예외로 던지는 것을 권장한다. |
| 미확인 | `@Transactional`은 기본적으로 `RuntimeException`과 `Error`에만 롤백한다. 스프링 문서와 일치하는 것으로 알고 있으나 실행하지 않았다. |
| 사소 | RFC 7807은 RFC 9457로 대체됐다. |

## 06. 동등성

| 판정 | 내용 |
|---|---|
| 확인 | 동적 문자열의 `==`는 false, `Long 1000L == 1000L`은 false, `Integer 100`은 true이고 `200`은 false, 삼항 연산자의 null 언박싱 NPE와 `Integer.valueOf(0)`로 고치는 방법. (`Ch06Equality`) |
| 틀림 | 비교표의 "`Number(10)`, `10` 구분 적음". `Number(10)`은 원시값을 반환한다. 래퍼 객체는 `new Number(10)`이다. |
| 과장 | "`int`는 스택 메모리 값", "기본값 0". 객체의 `int` 필드는 힙에 있고, 지역 변수에는 기본값이 없다. |
| 사소 | `Integer` 캐시 상한은 JVM 옵션으로 바꿀 수 있어 "200은 false"가 언제나 보장되는 것은 아니다. |

## 07. 동시성

| 판정 | 내용 |
|---|---|
| 확인 | `putIfAbsent`의 반환값, `computeIfPresent`가 null을 반환하면 엔트리 제거, 동시에 시도해도 `putIfAbsent`의 승자는 항상 1명. `containsKey` 후 `put`은 실제로 이중 예약이 발생한다(2만 회 중 52회 관찰). (`Ch07Concurrency`) |
| 틀림 | "`putIfAbsent`는 단 1회의 CPU 명령(CAS) 수준으로 처리". CAS는 빈 버킷에 넣을 때만 쓰이고, 그 외에는 버킷의 첫 노드에 `synchronized`를 건다. 원자적이라는 결론은 맞다. |
| 틀림 | "`HashMap` 리사이징 중 순환 참조로 CPU 100% 무한 루프". Java 7까지의 문제다. Java 8에서 리사이징 방식이 바뀌어 이 형태의 무한 루프는 없어졌다. 데이터 유실은 여전히 맞다. |
| 과장 | 비교표의 "Node.js는 메모리 경합 없음, `count++` 안전". 한 번의 동기 구간 안에서만 맞다. `if (!map.has(k)) { await ...; map.set(k) }`는 Node.js에서도 같은 check-then-act 버그다. TypeScript 개발자가 가장 오해하기 쉬운 부분이다. |
| 미확인 | 톰캣 기본 스레드 200개. 스프링 부트 문서의 `server.tomcat.threads.max` 기본값과 일치하는 것으로 알고 있다. |
| 설계 | `LockInfo`에 만료가 없어 클라이언트가 사라지면 락이 영원히 남는다. 주석은 "만료시각 등"이라고 하지만 구현이 없다. 첫 예제의 `if (current > 0)`은 키가 없으면 06장에서 경고한 언박싱 NPE다. |

## 08. Spring Web

| 판정 | 내용 |
|---|---|
| 확인 | record DTO가 기본 생성자 없이 역직렬화된다. 생성 201, 삭제 204. (`TicketApiTest`) |
| 틀림 | 지뢰 1의 "`TicketResponse`라는 이름의 템플릿을 찾는다". 뷰 이름은 반환 타입이 아니라 요청 경로에서 나온다. `/plain/ticket` 요청은 `plain/ticket` 뷰로 넘어간다(`TicketApiTest`). |
| 틀림 | 지뢰 2의 "최신 Gradle/Maven은 `-parameters`를 기본으로 켠다", "과거 환경의 위험". 켜 주는 것은 Spring Boot의 빌드 플러그인이다. 그리고 Spring Framework 6.1부터는 `-parameters` 없이 매개변수 이름을 알아내는 대체 수단이 제거되어, 과거보다 지금 더 중요한 문제다. (실행하지 않음. 스프링 6.1 릴리스 노트 기준) |
| 미확인 | "NestJS가 스프링의 구조를 모방해 설계되었다". NestJS 공식 문서는 Angular에서 영감을 받았다고 밝힌다. 출처 없이 단정할 수 없다. |
| 사소 | NestJS의 `@Query('page') page: number`는 변환 파이프 없이는 런타임에 문자열이다. 나쁜 예제의 "뷰 템플릿 탐색 에러" 주석은 `HttpServletResponse`를 직접 받는 `void` 메서드에는 해당하지 않는 것으로 알고 있다(미확인). |

## 권장 순서

1. 05장의 전역 핸들러와 08장의 검증 설명을 함께 고친다. 예제 그대로 쓰면 검증 실패가 500이 된다.
2. 컴파일되지 않는 예제(05장 지뢰 2, 04장 `PaymentResult`)와 동작이 달라진 예제(03장)를 고친다.
3. TypeScript/Node.js 쪽 비교표의 틀린 칸(01, 03, 04, 06, 07장)을 고친다.
4. Java 7 이전 내용(07장 `HashMap` 무한 루프)과 버전 표기를 정리한다.
5. 그 뒤 퀴즈 세션 기록을 재료로 "내가 틀린 것" 절을 추가한다.
