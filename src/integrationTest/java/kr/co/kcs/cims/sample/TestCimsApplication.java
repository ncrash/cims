package kr.co.kcs.cims.sample;

import org.springframework.boot.SpringApplication;

import kr.co.kcs.cims.CimsApplication;

public class TestCimsApplication {

    public static void main(String[] args) {
        SpringApplication.from(CimsApplication::main)
                .with(TestcontainersConfiguration.class)
                .run(args);
    }
}
