# Flyway

## How to apply
- `--flyway=true` 옵션 지정
  - 주의 : 애플리케이션 수행 시 위 옵션을 추가해서 실행하면 마이그레이션 스크립트 수행됨

## How to migration script maintenance
- Schema migration script 작성 시 Hibernate Schema generation 기능 활용
- `application-test.yml` 파일 수정. 아래 옵션들로 옵션 변경
    - `hibernate.ddl-auto: create-drop`
    - `flyway.enabled: false`
    - _임시로 변경하는 내용이며 반드시 커밋하지 않아야함_
- 통합테스트(ex. `CimsApplicationTests`) 실행
- 테스트 수행 로그에서 테이블 생성 스크립트 확보
- `src/main/resources/db/migration` 경로에 마이그레이션 파일 적용
    - 아래 쿼리 예시와 같이 쿼리가 출력되는데 이 쿼리를 적용
```text
Hibernate: 
    create table customers (
        birth_date date,
        credit_grade tinyint check (credit_grade between 0 and 9),
        last_updated date,
        id bigint not null auto_increment,
        email varchar(255),
        name varchar(255),
        phone_number varchar(255),
        primary key (id)
    ) engine=InnoDB
```
