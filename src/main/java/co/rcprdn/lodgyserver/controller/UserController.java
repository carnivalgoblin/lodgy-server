package co.rcprdn.lodgyserver.controller;


import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
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

  private final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

  private final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
  List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());

  @GetMapping("/all")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public User getUserById(@PathVariable("userId") long userId) {

    // TODO: change to ResponseEntity

    if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userId)) {
        return userService.getUserById(userId);
      } else {
        // TODO: return error message
        System.out.println("You are not authorized to access this resource or it does not exist.");
      }

    } else if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      return userService.getUserById(userId);
    }

    return null;
  }

  @GetMapping("/username/{username}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public User getUserByUsername(@PathVariable("username") String username) {

    Long userId = userService.getUserByUsername(username).getId();

    if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userId)) {
        return userService.getUserByUsername(username);
      } else {
        // TODO: return error message
        System.out.println("You are not authorized to access this resource or it does not exist.");
      }
    } else if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      return userService.getUserByUsername(username);
    }

    return null;
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public HttpStatus deleteUserById(@PathVariable("userId") long userId) {

    if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userId)) {
        userService.deleteUser(userId);
        return HttpStatus.OK;
      }  else {
        // TODO: return error message
        System.out.println("You are not authorized to access this resource or it does not exist.");
      }
    } else if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      userService.deleteUser(userId);
      return HttpStatus.OK;
    }

    return HttpStatus.NOT_FOUND;
  }

  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

}
