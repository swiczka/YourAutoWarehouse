package io.swiczka.github.apiforklift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ApiForkliftApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiForkliftApplication.class, args);
    }

}
