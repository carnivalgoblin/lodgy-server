package co.rcprdn.lodgyserver.controller;


import co.rcprdn.lodgyserver.dto.ExpenseDTO;
import co.rcprdn.lodgyserver.dto.TripDTO;
import co.rcprdn.lodgyserver.dto.UserDTO;
import co.rcprdn.lodgyserver.dto.UserUpdateDTO;
import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.entity.Role;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
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
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

  public final UserService userService;

  @GetMapping("/all")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
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

  @GetMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") long userId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    User user = userService.getUserById(userId);

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication) ||
            (hasUserRole("USER", authentication) && userDetails.getId().equals(userId))) {
      UserDTO userDTO = convertUserToDTO(user);
      return new ResponseEntity<>(userDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
  }

  @PostMapping("/update")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<UserDTO> updateUser(@RequestBody UserUpdateDTO userUpdateDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    User existingUser = userService.getUserById(userUpdateDTO.getId());

    if (hasUserRole("ADMIN", authentication)) {
      Set<Role> roles = userUpdateDTO.getRoles().stream()
              .map(role -> new Role(role))
              .collect(Collectors.toSet());

      existingUser.setRoles(roles);
      existingUser.setActivated(userUpdateDTO.isActivated());
      User updatedUser = userService.updateUser(existingUser);
      UserDTO userDTO = convertUserToDTO(updatedUser);
      return ResponseEntity.ok(userDTO);
    } else if (hasUserRole("MODERATOR", authentication)) {
      Set<Role> roles = userUpdateDTO.getRoles().stream()
              .map(role -> new Role(role))
              .collect(Collectors.toSet());

      existingUser.setRoles(roles);
      User updatedUser = userService.updateUser(existingUser);
      UserDTO userDTO = convertUserToDTO(updatedUser);
      return ResponseEntity.ok(userDTO);
    } else if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userUpdateDTO.getId())) {
        Set<Role> roles = userUpdateDTO.getRoles().stream()
                .map(role -> new Role(role))
                .collect(Collectors.toSet());

        existingUser.setRoles(roles);
        User updatedUser = userService.updateUser(existingUser);
        UserDTO userDTO = convertUserToDTO(updatedUser);
        return ResponseEntity.ok(userDTO);
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
    }
  }

  @DeleteMapping("/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Void> deleteUser(@PathVariable("userId") long userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    if (hasUserRole("ADMIN", authentication)) {
      userService.deleteUser(userId);
      return ResponseEntity.noContent().build();
    } else if (hasUserRole("MODERATOR", authentication) || hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userId)) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
    }
  }

  @GetMapping("/{userId}/trips")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<TripDTO>> getTripsByUserId(@PathVariable("userId") long userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    User user = userService.getUserById(userId);

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      List<TripDTO> tripDTOs = user.getTrips().stream()
              .map(this::convertTripToDTO)
              .collect(Collectors.toList());
      return new ResponseEntity<>(tripDTOs, HttpStatus.OK);
    } else if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userId)) {
        List<TripDTO> tripDTOs = user.getTrips().stream()
                .map(this::convertTripToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(tripDTOs, HttpStatus.OK);
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
    }
  }

  @GetMapping("/{userId}/expenses")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<ExpenseDTO>> getExpensesByUserId(@PathVariable("userId") long userId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    User user = userService.getUserById(userId);

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      List<ExpenseDTO> expenseDTOs = user.getExpenses().stream()
              .map(this::convertExpenseToDTO)
              .collect(Collectors.toList());
      return new ResponseEntity<>(expenseDTOs, HttpStatus.OK);
    } else if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(userId)) {
        List<ExpenseDTO> expenseDTOs = user.getExpenses().stream()
                .map(this::convertExpenseToDTO)
                .collect(Collectors.toList());
        return new ResponseEntity<>(expenseDTOs, HttpStatus.OK);
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found.");
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

    return userDTO;
  }

  private TripDTO convertTripToDTO(Trip trip) {
    TripDTO tripDTO = new TripDTO();

    tripDTO.setId(trip.getId());
    tripDTO.setDestination(trip.getDestination());
    tripDTO.setStartDate(String.valueOf(trip.getStartDate()));
    tripDTO.setEndDate(String.valueOf(trip.getEndDate()));
    tripDTO.setDescription(trip.getDescription());
    tripDTO.setUserIds(trip.getUserIdList());
    tripDTO.setExpenseIds(trip.getExpenseIdList());

    return tripDTO;
  }

  private ExpenseDTO convertExpenseToDTO(Expense expense) {
    ExpenseDTO expenseDTO = new ExpenseDTO();

    expenseDTO.setId(expense.getId());
    expenseDTO.setUserId(expense.getUser().getId());
    expenseDTO.setTripId(expense.getTrip().getId());
    expenseDTO.setAmount(expense.getAmount());
    expenseDTO.setDescription(expense.getDescription());

    return expenseDTO;
  }

}
