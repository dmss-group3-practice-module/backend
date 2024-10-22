package nus.iss.team3.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main starting Class for SpringBoot
 *
 * @author Desmond Tan Zhi Heng, Ren Jiarui
 */
@SpringBootApplication
@EnableScheduling
public class BackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }
}
