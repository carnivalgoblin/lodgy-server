package co.rcprdn.lodgyserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class LodgyServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(LodgyServerApplication.class, args);
  }

}
