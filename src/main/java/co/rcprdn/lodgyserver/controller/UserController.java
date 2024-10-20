package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.dto.SimpleUserDTO;
import co.rcprdn.lodgyserver.dto.UserDTO;
import co.rcprdn.lodgyserver.entity.*;
import co.rcprdn.lodgyserver.enums.ERole;
import co.rcprdn.lodgyserver.exceptions.ResourceNotFoundException;
import co.rcprdn.lodgyserver.repository.RoleRepository;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  public final UserService userService;
  public final RoleRepository roleRepository;

  @GetMapping("/all")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<User> users = userService.getAllUsers();

    if (!users.isEmpty()) {
      List<UserDTO> userDTOs = users.stream()
              .map(this::convertUserToDTO)
              .collect(Collectors.toList());
      return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }

  @GetMapping("/all/usernames")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<SimpleUserDTO>> getAllUsernames() {
    List<User> users = userService.getAllUsers();

    if (!users.isEmpty()) {
      List<SimpleUserDTO> userDTOs = users.stream()
              .map(user -> new SimpleUserDTO(user.getId(), user.getUsername()))
              .collect(Collectors.toList());
      return new ResponseEntity<>(userDTOs, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") long userId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    User user = userService.getUserById(userId);

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication) ||
            (hasUserRole("USER", authentication ) && userDetails.getId().equals(userId))) {
      UserDTO userDTO = convertUserToDTO(user);
      return new ResponseEntity<>(userDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @PutMapping("/update")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<UserDTO> updateUser(@RequestBody User user) {

    // Fetch the existing user from the database
    User existingUser = userService.getUserById(user.getId());

    // Update only the fields you want to allow
    existingUser.setUsername(user.getUsername()); // Allow username update
    existingUser.setEnabled(user.isEnabled()); // Allow enabled update

    // Save the updated user
    User updatedUser = userService.updateUser(existingUser);

    // Log the updated user for debugging
    System.out.println("Updated user: " + updatedUser);

    UserDTO userDTO = convertUserToDTO(updatedUser);
    return new ResponseEntity<>(userDTO, HttpStatus.OK);
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public void deleteUser(@PathVariable("userId") long userId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    if (hasUserRole("ADMIN", authentication)) {
      userService.deleteUser(userId);
    } else if (hasUserRole("MODERATOR", authentication) || hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userId)) {
        userService.deleteUser(userId);
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new ResourceNotFoundException("Resource not found.");
    }
  }

  @GetMapping("/current")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<UserDTO> getCurrentUser() {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    User user = userService.getUserById(userDetails
            .getId());

    UserDTO userDTO = convertUserToDTO(user);
    return new ResponseEntity<>(userDTO, HttpStatus.OK);
  }

//  @GetMapping("/{userId}/trips")
//  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
//  public List<Trip> getTripsByUserId(@PathVariable("userId") long userId) {
//
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//      return (List<Trip>) userService.getUserById(userId).getTrips();
//
//    } else if (hasUserRole("USER", authentication)) {
//      if (userDetails.getId().equals(userId)) {
//        return (List<Trip>) userService.getUserById(userId).getTrips();
//      } else {
//        throw new AccessDeniedException("You are not authorized to access this resource.");
//      }
//    } else {
//      throw new ResourceNotFoundException("Resource not found.");
//    }
//  }

//  @GetMapping("/{userId}/expenses")
//  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
//  public List<Expense> getExpensesByUserId(@PathVariable("userId") long userId) {
//
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//      return userService.getUserById(userId).getExpenses();
//
//    } else if (hasUserRole("USER", authentication)) {
//      if (userDetails.getId().equals(userId)) {
//        return userService.getUserById(userId).getExpenses();
//      } else {
//        throw new AccessDeniedException("You are not authorized to access this resource.");
//      }
//    } else {
//      throw new ResourceNotFoundException("Resource not found.");
//    }
//  }

  @PutMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<UserDTO> updateUser(@PathVariable("userId") Long userId, @RequestBody UserDTO userDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    // Check if the user has permission to update the user
    if (hasUserRole("ADMIN", authentication) || hasUserRole("MODERATOR", authentication) || userDetails.getId().equals(userId)) {
      // Get the existing user from the database
      User user = userService.getUserById(userId);

      // Update user fields based on incoming UserDTO
      user.setUsername(userDTO.getUsername());
      user.setEnabled(userDTO.getEnabled());

      // If Admin, also allow updating roles
      if (hasUserRole("ADMIN", authentication)) {
        Set<Role> roles = userDTO.getUserRoles().stream()
                .map(userService::getRoleByERole)
                .collect(Collectors.toSet());
        user.setRoles(roles);
      }

      // Save the updated user
      User updatedUser = userService.updateUser(user);

      // Convert the updated user back to a DTO and return
      UserDTO updatedUserDTO = convertUserToDTO(updatedUser);
      return new ResponseEntity<>(updatedUserDTO, HttpStatus.OK);
    } else {
      throw new AccessDeniedException("You are not authorized to update this user.");
    }
  }



  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

  private UserDTO convertUserToDTO(User user) {
    UserDTO userDTO = new UserDTO();

    userDTO.setId(user.getId());
    userDTO.setUsername(user.getUsername());
    userDTO.setExpenseIds(user.getExpenses().stream().map(Expense::getId).collect(Collectors.toList()));
    userDTO.setTripIds(user.getTrips().stream().map(Trip::getId).collect(Collectors.toList()));
    userDTO.setUserTrips(user.getUserTrips().stream().map(UserTrip::getId).collect(Collectors.toList()));
    userDTO.setUserRoles(user.getRoles().stream()
            .map(Role::getName)
            .collect(Collectors.toList()));
    userDTO.setEnabled(user.isEnabled());

    return userDTO;
  }

}
