# kcs-homework-test

## 프로젝트 설명
- 고객 신용 정보 관리 시스템 (Customer Information Management System)
- 고객의 개인정보와 금융거래 정보를 관리하고 신용등급을 산정하는 시스템
- DDD(Domain Driven Design) 패턴 적용

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
* http://localhost:8080/v3/api-docs - OpenAPI 스펙 JSON
* http://localhost:8080/swagger-ui.html - Swagger UI 페이지

