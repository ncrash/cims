# 단위테스트 작성 가이드

- 테스트는 `methodName_when{상황}_should{기대결과}` 형식으로 명명하고, Given-When-Then 구조로 작성하여 명확한 의도를 전달한다.
- [Mockito](https://site.mockito.org/) 를 활용해 의존성을 관리(`@Mock`, `@Spy`)하고, [AssertJ](https://assertj.github.io/doc/) 를 사용해(`assertThat`, `assertThatThrownBy`) 검증한다.
- 각 테스트는 독립적으로 실행될 수 있도록 `@BeforeEach`에서 초기화하고, 복잡한 테스트 데이터 설정은 별도 헬퍼 메소드로 분리하여 재사용성을 높인다.

## 1. 기본 구조

```java
@ExtendWith(MockitoExtension.class)
class ClassNameTest {
    // 테스트할 대상
    @Spy or @InjectMocks
    private TargetClass targetObject;

    // 의존성
    @Mock
    private DependencyClass dependency;

    @BeforeEach
    void setUp() {
        // 테스트 전 공통 설정
    }
}
```

## 2. 테스트 메소드 네이밍 컨벤션

```java
void methodName_when{Condition}_should{ExpectedResult}()
```

예시:

- `verifyNotYetPaid_whenPaymentNotCompleted_shouldThrowBusinessException()`
- `changeStatusAfterPayment_Success_Success_shouldSetCorrectFieldsAndDates()`

## 3. 테스트 구조 (GWT 패턴)

```java
@Test
void testMethod() {
    // Given (준비)
    // 테스트에 필요한 객체와 값 설정

    // When (실행)
    // 테스트할 메소드 실행

    // Then (검증)
    // 결과 검증
}
```

## 4. 주요 검증 방법

### 4.1 예외 검증

```java
assertThatThrownBy(() -> object.method())
    .isInstanceOf(ExpectedException.class)
    .hasFieldOrPropertyWithValue("fieldName", expectedValue);
```

### 4.2 정상 실행 검증

```java
assertThatCode(() -> object.method())
    .doesNotThrowAnyException();
```

### 4.3 값 검증

```java
assertThat(actualValue)
    .isEqualTo(expectedValue);
```

## 5. 파라미터화된 테스트

```java
@ParameterizedTest
@MethodSource("provideTestCases")
void parameterizedTest(String testCase, Type1 param1, Type2 param2...) {
    // 테스트 로직
}

private static Stream<Arguments> provideTestCases() {
    return Stream.of(
        arguments("테스트 케이스 설명", value1, value2...),
        arguments("다른 테스트 케이스", value1, value2...)
    );
}
```

## 6. 로깅 테스트

```java
@Mock
private static Logger mockLogger;

@BeforeEach
void setUp() {
    TargetClass.log = mockLogger;
}

verify(mockLogger).error(matches("에러 메시지 패턴.*"), any(), any());
```

## 7. Mockito 사용

### 7.1 동작 정의

```java
when(mockObject.method()).thenReturn(value);
```

### 7.2 호출 검증

```java
verify(mockObject).method();
verify(mockObject, times(n)).method();
```

## 8. 테스트 데이터 설정

- 테스트 메소드 내에서 직접 설정하거나
- 별도의 private 헬퍼 메소드를 만들어서 재사용

```java
private void setupOrderForPaymentCompletion(Clock clock) {
    when(payment.getStatus()).thenReturn(PaymentStatus.COMPLETED);
    when(product.getWarranty()).thenReturn(1);
    when(product.getExpiration()).thenReturn(6);
    // ...
}
```

## 9. 테스트 격리

- 각 테스트는 독립적으로 실행될 수 있어야 함
- `@BeforeEach`에서 테스트 환경 초기화
- 테스트 간 상태 공유하지 않기

## 10. 테스트 가독성

- 명확한 테스트 케이스 설명
- 관련 테스트 케이스들은 하나의 클래스에 그룹화
- 복잡한 테스트 데이터는 별도 메소드로 분리
