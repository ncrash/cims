package kr.co.kcs.cims;

import org.springframework.boot.SpringApplication;

public class TestCimsApplication {

  public static void main(String[] args) {
    SpringApplication.from(CimsApplication::main).with(TestcontainersConfiguration.class).run(args);
  }
}
