# kcs-homework-test

## 프로젝트 설명
- 고객 신용 정보 관리 시스템 (Customer Information Management System)
- 고객의 개인정보와 금융거래 정보를 관리하고 신용등급을 산정하는 시스템
- DDD(Domain Driven Design) 패턴 적용

### 1. 프로젝트 구조

- Spring Boot 기반의 웹 애플리케이션
- REST API 아키텍처 적용
- JPA/Hibernate를 사용한 데이터 접근 계층
- 테스트 환경으로 TestContainers (MariaDB) 사용

### 2. 주요 도메인 모델

1. **Customer (고객)**
   - 개인정보(PersonalInfo) 포함
   - 신용등급(CreditGrade) 관리
   - 신용거래 내역(CreditTransaction) 관리

2. **CreditTransaction (신용거래)**
   - 거래 유형, 금액, 상환 상태

3. **PersonalInfo (개인정보)**
   - 이름, 생년월일, 이메일, 전화번호

### 3. 기술적 특징

1. **데이터 관리**
   - Soft Delete 구현 (데이터 실제 삭제 대신 플래그 처리)
   - 낙관적 락킹(Optimistic Locking) 사용
   - 감사 정보(생성/수정/삭제 시간) 자동 관리

2. **코드 품질**
   - DTO 패턴 사용
   - Builder 패턴 적용
   - 유효성 검증(Validation) 구현
   - Spotless 코드포맷터(palantir) git hooks(pre-commit) 적용

3. **테스트**
   - 통합 테스트 환경 구성
   - Github Action으로 테스트 자동수행
   - TestContainers를 통한 실제 DB 환경 테스트

## 설치 및 실행 방법
1. 요구사항
   - Java 21
   - Spring Boot 3.4.x
   - MariaDB 11.x

2. 데이터베이스 유저 및 디비 생성
```sql
-- User
CREATE USER 'kcs'@'localhost' IDENTIFIED BY 'password!@#$';
GRANT ALL PRIVILEGES ON `kcs`.* TO 'kcs'@'localhost';
FLUSH PRIVILEGES;

-- Database
CREATE DATABASE kcs DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

```

3. 빌드 및 실행
   ```bash
   ./gradlew clean build
   java -jar build/libs/cims-0.0.1-SNAPSHOT.jar
   ```

4. (Optional) Flyway migration
   ```bash
   ./gradlew clean build
   java -jar build/libs/cims-0.0.1-SNAPSHOT.jar --flyway=true 
   ```

## API 명세
* http://localhost:8080/kcs/swagger.html - Swagger UI 페이지
* http://localhost:8080/v3/api-docs - OpenAPI 스펙 JSON

## DB 구성
* TODO DB 구성 업데이트 하기

## 개선포인트
- [보안] 개인정보 암호화
- [보안] 비밀번호 설정파일 하드코딩 