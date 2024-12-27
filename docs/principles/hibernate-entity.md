# JPA/Hibernate 엔티티 클래스 구현원칙
- 안전한 엔티티 동작 보장
- 일관된 코드 스타일 유지
- 잠재적인 문제 방지
- 유지보수성 향상

## 참조 클래스
- [AbstractEntity.java](../../src/main/java/kr/co/kcs/cims/domain/common/AbstractEntity.java)
- [Customer.java](../../src/main/java/kr/co/kcs/cims/domain/customer/entity/Customer.java)
- [CreditTransaction.java](../../src/main/java/kr/co/kcs/cims/domain/customer/entity/CreditTransaction.java)

## 주요 고려사항

1. **ID 필드 접근**:
    - getId() 추상 메서드로 정의
    - 각 엔티티에서 구현

2. **equals()와 hashCode()**:
    - Hibernate 프록시를 고려한 구현
    - ID 기반 비교
    - final로 선언하여 오버라이드 방지

3. **toString()**:
    - 순환 참조를 피하기 위해 연관관계 필드 제외
    - 디버깅에 유용한 정보 포함

4. **clone() 방지**:
    - JPA 엔티티는 clone을 지원하지 않는 것이 좋음
    - CloneNotSupportedException을 throw하도록 구현

### 추가 고려사항

1. **접근 제어**:
    - 필드는 private으로 선언
    - getter/setter 사용 (필요한 경우에만)

2. **불변성 보장**:
    - 컬렉션 필드는 불변으로 초기화
    - setter 대신 비즈니스 메서드 사용

3. **생성자**:
    - protected 또는 public 기본 생성자 필수
    - @NoArgsConstructor 사용 가능
