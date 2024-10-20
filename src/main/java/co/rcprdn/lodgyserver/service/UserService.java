package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.Role;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.enums.ERole;
import co.rcprdn.lodgyserver.exceptions.ResourceNotFoundException;
import co.rcprdn.lodgyserver.repository.RoleRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  public User getUserById(Long id) {
    return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
  }

  public User updateUser(User user) {
    return userRepository.save(user);
  }

  public void deleteUser(Long id) {
    userRepository.deleteById(id);
  }

  public User getUserByUsername(String username) {
    return userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
  }

  public Boolean existsByUsername(String username) {
    return userRepository.existsByUsername(username);
  }

  public Boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public String getUsernameById(Long userId) {
    return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found")).getUsername();
  }

  public ERole getRoleByName(String roleName) {
    return roleRepository.findByName(ERole.valueOf(roleName))
            .orElseThrow(() -> new ResourceNotFoundException("Role not found")).getName();
  }

  public Role getRoleByERole(ERole eRole) {
    return roleRepository.findByName(ERole.valueOf(eRole.name()))
            .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
  }

}
