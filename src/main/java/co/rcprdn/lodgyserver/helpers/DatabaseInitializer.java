package co.rcprdn.lodgyserver.helpers;

import co.rcprdn.lodgyserver.entity.Role;
import co.rcprdn.lodgyserver.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static co.rcprdn.lodgyserver.enums.ERole.*;

@Component
@AllArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;

  @Override
  public void run(String... args) {
    roleRepository.save(new Role(ROLE_USER));
    roleRepository.save(new Role(ROLE_MODERATOR));
    roleRepository.save(new Role(ROLE_ADMIN));
  }
}
