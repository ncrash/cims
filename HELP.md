# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.1/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.1/gradle-plugin/packaging-oci-image.html)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.4.1/reference/testing/testcontainers.html#testing.testcontainers)
* [Testcontainers MariaDB Module Reference Guide](https://java.testcontainers.org/modules/databases/mariadb/)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.1/reference/using/devtools.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.1/reference/web/servlet.html)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.1/reference/web/spring-security.html)
* [Validation](https://docs.spring.io/spring-boot/3.4.1/reference/io/validation.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Testcontainers](https://java.testcontainers.org/)
* [Flyway Migration](https://docs.spring.io/spring-boot/3.4.1/how-to/data-initialization.html#howto.data-initialization.migration-tool.flyway)
* [Spring Data JDBC](https://docs.spring.io/spring-boot/3.4.1/reference/data/sql.html#data.sql.jdbc)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Validation](https://spring.io/guides/gs/validating-form-input/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Testcontainers support

This project uses [Testcontainers at development time](https://docs.spring.io/spring-boot/3.4.1/reference/features/dev-services.html#features.dev-services.testcontainers).

Testcontainers has been configured to use the following Docker images:

* [`mariadb:latest`](https://hub.docker.com/_/mariadb)

Please review the tags of the used images and set them to the same as you're running in production.

