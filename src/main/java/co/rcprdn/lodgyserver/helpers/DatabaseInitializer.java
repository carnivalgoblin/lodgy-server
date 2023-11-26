package co.rcprdn.lodgyserver.helpers;

import co.rcprdn.lodgyserver.entity.Role;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.repository.RoleRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static co.rcprdn.lodgyserver.enums.ERole.*;

@Component
@AllArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;

  private final UserRepository userRepository;

  private final PasswordEncoder encoder;

  @Override
  public void run(String... args) {
    roleRepository.save(new Role(ROLE_USER));
    roleRepository.save(new Role(ROLE_MODERATOR));
    roleRepository.save(new Role(ROLE_ADMIN));

    User adminUser = new User();

    adminUser.setUsername("rico");
    adminUser.setEmail("rico.prodan@mail.com");
    adminUser.setPassword(encoder.encode("12345678")); // Set new password here!
    Set<Role> roles = new HashSet<>();
    roles.add(roleRepository.findByName(ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
    roles.add(roleRepository.findByName(ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
    roles.add(roleRepository.findByName(ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
    adminUser.setRoles(roles);

    userRepository.save(adminUser);

    User modUser = new User();

    modUser.setUsername("mod");
    modUser.setEmail("mod@mail.com");
    modUser.setPassword(encoder.encode("12345678")); // Set new password here!
    Set<Role> modRoles = new HashSet<>();
    modRoles.add(roleRepository.findByName(ROLE_MODERATOR).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
    modRoles.add(roleRepository.findByName(ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
    modUser.setRoles(modRoles);

    userRepository.save(modUser);

    User testUser = new User();

    testUser.setUsername("test");
    testUser.setEmail("test@mail.com");
    testUser.setPassword(encoder.encode("12345678")); // Set new password here!
    Set<Role> testRoles = new HashSet<>();
    testRoles.add(roleRepository.findByName(ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
    testUser.setRoles(testRoles);

    userRepository.save(testUser);

  }
}
