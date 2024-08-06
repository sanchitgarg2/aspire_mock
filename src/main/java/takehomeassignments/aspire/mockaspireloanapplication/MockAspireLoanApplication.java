package takehomeassignments.aspire.mockaspireloanapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MockAspireLoanApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockAspireLoanApplication.class, args);
    }

}
