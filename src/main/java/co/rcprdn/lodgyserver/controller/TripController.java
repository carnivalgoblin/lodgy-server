package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.dto.ExpenseDTO;
import co.rcprdn.lodgyserver.dto.TripDTO;
import co.rcprdn.lodgyserver.dto.UserTripDTO;
import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.entity.Payment;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.UserTrip;
import co.rcprdn.lodgyserver.exceptions.ResourceNotFoundException;
import co.rcprdn.lodgyserver.exceptions.UserNotInTripException;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static co.rcprdn.lodgyserver.service.TripService.getTripDTO;
import static co.rcprdn.lodgyserver.service.UserTripService.getUserTripDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {

  private final TripService tripService;
  private final UserTripService userTripService;
  private final UserService userService;
  private final CostDistributionService costDistributionService;
  private final PaymentCalculatorService paymentCalculatorService;
  private final ExpenseService expenseService;
  private final TripRepository tripRepository;

  @GetMapping("/all")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<TripDTO>> getAllTrips() {
    List<Trip> trips = tripService.getAllTrips();

    if (!trips.isEmpty()) {
      List<TripDTO> tripDTOs = trips.stream()
              .map(this::convertTripToDTO)
              .collect(Collectors.toList());
      return new ResponseEntity<>(tripDTOs, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }

  @GetMapping("/{tripId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<TripDTO> getTripById(@PathVariable Long tripId) {
    Trip trip = tripService.getTripById(tripId);

    if (trip != null) {
      TripDTO tripDTO = convertTripToDTO(trip);
      return new ResponseEntity<>(tripDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);


//    } else if (hasUserRole("USER", authentication)) {
//      if (trip.getUsers().contains(userDetails.getUser())) {
//        return ResponseEntity.ok(trip);
//      } else {
//        throw new AccessDeniedException("You are not authorized to access this resource.");
//      }
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
  }

  @PostMapping("/create")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    trip.setStartDate(LocalDate.from(trip.getStartDate().atStartOfDay()));
    trip.setEndDate(LocalDate.from(trip.getEndDate().atStartOfDay()));

    Trip createdTrip = tripService.createTrip(trip);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTrip);


//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//
//      Trip createdTrip = tripService.createTrip(trip);
//
//      return ResponseEntity.ok(createdTrip);
//
//    } else if (hasUserRole("USER", authentication)) {
//
//      Trip createdTrip = tripService.createTrip(trip);
//
//      return ResponseEntity.ok(createdTrip);
//
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
//    }
  }

  @PostMapping("/{tripId}/users/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<TripDTO> addUserToTrip(@PathVariable("tripId") long tripId,
                                               @PathVariable("userId") long userId,
                                               @RequestParam("days") int days,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

    if (userDetails != null) {
      TripDTO tripDTO = tripService.addUserToTrip(tripId, userId, days);
      return new ResponseEntity<>(tripDTO, HttpStatus.OK);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }


  @DeleteMapping("/{tripId}/users/{userId}")
  public ResponseEntity<?> removeUserFromTrip(@PathVariable("tripId") long tripId, @PathVariable("userId") long userId) {
    try {
      TripDTO updatedTrip = tripService.removeUserFromTrip(tripId, userId);
      return ResponseEntity.ok(updatedTrip);
    } catch (ResourceNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    } catch (UserNotInTripException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while removing the user from the trip.");
    }
  }


  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Void> deleteTrip(@PathVariable("id") Long id) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      tripService.deleteTrip(id);
      return ResponseEntity.ok().build();
    } else if (hasUserRole("USER", authentication)) {
      throw new AccessDeniedException("You are not authorized to delete trips.");
    } else {
      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
  }

  @PutMapping("/update/{id}")
  public Trip updateTrip(@PathVariable("id") Long id, @RequestBody Trip updatedTrip) {
    // Retrieve the existing trip from the database
    Optional<Trip> optionalExistingTrip = tripRepository.findById(id);
    if (optionalExistingTrip.isPresent()) {
      Trip existingTrip = optionalExistingTrip.get();

      // Update fields if they are present in the updatedTrip object
      if (updatedTrip.getDestination() != null) {
        existingTrip.setDestination(updatedTrip.getDestination());
      }

      if (updatedTrip.getStartDate() != null) {
        existingTrip.setStartDate(updatedTrip.getStartDate());
      }

      if (updatedTrip.getEndDate() != null) {
        existingTrip.setEndDate(updatedTrip.getEndDate());
      }

      if (updatedTrip.getDescription() != null) {
        existingTrip.setDescription(updatedTrip.getDescription());
      }

      // Save the updated trip back to the database
      return tripRepository.save(existingTrip);
    } else {
      throw new ResourceNotFoundException("Trip not found with ID: " + id);
    }
  }

  @GetMapping("/userTrip/{id}")
  public ResponseEntity<UserTripDTO> getUserTrip(@PathVariable Long id) {
    try {
      UserTripDTO userTripDTO = userTripService.getUserTripById(id);
      return new ResponseEntity<>(userTripDTO, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/{tripId}/userTrips")
  public ResponseEntity<List<UserTripDTO>> getUserTripsForTrip(@PathVariable Long tripId) {
    try {
      List<UserTripDTO> userTrips = userTripService.getUserTripsForTrip(tripId);
      return new ResponseEntity<>(userTrips, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  // Get all user trips for the current user by userID
  @GetMapping("/userTrips")
  public ResponseEntity<List<UserTripDTO>> getUserTripsForUser(@AuthenticationPrincipal UserDetailsImpl userDetails) {
    try {
      List<UserTripDTO> userTrips = userTripService.getAllUserTripsByUserId(userDetails.getId());
      return new ResponseEntity<>(userTrips, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/userTrips/{userId}")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<UserTripDTO>> getUserTripsForUserById(@PathVariable Long userId) {
    try {
      List<UserTripDTO> userTrips = userTripService.getAllUserTripsByUserId(userId);
      return new ResponseEntity<>(userTrips, HttpStatus.OK);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<TripDTO>> getTripsForUser(@PathVariable Long userId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
    if (!userDetails.getId().equals(userId) && !hasUserRole("MODERATOR", userDetails) && !hasUserRole("ADMIN", userDetails)) {
      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
    List<TripDTO> trips = tripService.getTripsForUser(userId);
    return ResponseEntity.ok(trips);
  }

  private boolean hasUserRole(String roleName, UserDetailsImpl userDetails) {
    return userDetails.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

  // Expenses

  @PostMapping("/{tripId}/distribute-costs")
  public ResponseEntity<List<UserTripDTO>> distributeCosts(
          @PathVariable Long tripId,
          @RequestBody List<UserTripDTO> userTripDTOs,
          @RequestParam(defaultValue = "true") boolean basedOnDays) {

    TripDTO tripDTO = tripService.getTripDTOById(tripId);

    List<ExpenseDTO> expenseDTOs = tripService.getExpensesForTrip(tripId);

    costDistributionService.distributeCosts(tripDTO, userTripDTOs, expenseDTOs, basedOnDays);

    // Enhance UserTripDTOs with usernames
    List<UserTripDTO> enhancedUserTripDTOs = userTripDTOs.stream()
            .peek(userTripDTO -> {
              // Assuming you have a method to get the username based on userId
              String username = userService.getUsernameById(userTripDTO.getUserId());
              userTripDTO.setUsername(username);
            })
            .collect(Collectors.toList());

    return ResponseEntity.ok(enhancedUserTripDTOs);
  }

  @PostMapping("/{tripId}/calculate-payments")
  public ResponseEntity<List<Payment>> calculatePayments(
          @PathVariable String tripId,
          @RequestBody List<UserTripDTO> userTripDTOs) {
    // Use tripId to fetch expenses for the trip
    List<Expense> expenses = expenseService.getExpensesByTripId(Long.parseLong(tripId));

    // Convert Expense entities to ExpenseDTO
    List<ExpenseDTO> expenseDTOs = expenses.stream()
            .map(this::convertExpenseToDTO)
            .collect(Collectors.toList());

    // Perform payment calculations using userTripDTOs and expenseDTOs
    List<Payment> payments = paymentCalculatorService.calculatePayments(userTripDTOs, expenseDTOs);

    return ResponseEntity.ok(payments);
  }

  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

  private TripDTO convertTripToDTO(Trip trip) {
    return getTripDTO(trip);
  }

  private UserTripDTO convertUserTripToDTO(UserTrip userTrip) {
    return getUserTripDTO(userTrip);
  }

  private ExpenseDTO convertExpenseToDTO(Expense expense) {
    ExpenseDTO expenseDTO = new ExpenseDTO();

    expenseDTO.setId(expense.getId());
    expenseDTO.setTripId(expense.getTrip().getId());
    expenseDTO.setAmount(expense.getAmount());
    expenseDTO.setUserId(expense.getUser().getId());
    expenseDTO.setDescription(expense.getDescription());

    return expenseDTO;
  }

}
