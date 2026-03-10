package core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "core",
    "infrastructure"
})
public class MicroservicoNotifApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroservicoNotifApplication.class, args);
    }
}
