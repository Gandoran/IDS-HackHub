package unicam.it.idshackhub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Fa partire lo scheduler!
public class IdsHackHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdsHackHubApplication.class, args);
    }
}
