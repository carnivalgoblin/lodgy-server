package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.dto.SimpleUserDTO;
import co.rcprdn.lodgyserver.dto.UserDTO;
import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.entity.UserTrip;
import co.rcprdn.lodgyserver.exceptions.ResourceNotFoundException;
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

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  public final UserService userService;

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

  @PostMapping("/update")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public User updateUser(@RequestBody User user) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    if (hasUserRole("ADMIN", authentication)) {
      return userService.updateUser(user);
    } else if (hasUserRole("MODERATOR", authentication)) {
      user.setRoles(userService.getUserById(user.getId()).getRoles());
      return userService.updateUser(user);
    } else if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(user.getId())) {
        user.setRoles(userService.getUserById(user.getId()).getRoles());
        return userService.updateUser(user);
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new ResourceNotFoundException("Resource not found.");
    }
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

    return userDTO;
  }

}
