package co.rcprdn.lodgyserver.controller;


import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  public final UserService userService;

  @GetMapping("/all")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public List<User> getAllUsers() {
    return userService.getAllUsers();
  }

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public User getUserById(@PathVariable("userId") long userId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      return userService.getUserById(userId);

    } else if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userId)) {
        return userService.getUserById(userId);
      } else {
        throw AuthenticationException.class.cast(HttpStatus.UNAUTHORIZED);
      }
    } else {
      throw AuthenticationException.class.cast(HttpStatus.NOT_FOUND);
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
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
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
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
    }
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
//      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
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
//      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
//    }
//  }


  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

}
