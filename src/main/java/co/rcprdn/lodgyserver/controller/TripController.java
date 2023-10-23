package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.UserTripExpense;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.TripService;
import co.rcprdn.lodgyserver.service.UserService;
import co.rcprdn.lodgyserver.service.UserTripExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {

  private final TripService tripService;

  private final UserService userService;

  private final UserTripExpenseService userTripExpenseService;

  @GetMapping("/all")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Iterable<Trip>> getAllTrips() {
    return ResponseEntity.ok(tripService.getAllTrips());
  }

  @GetMapping("/{tripId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Trip> getTripById(@PathVariable("tripId") long tripId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    Trip trip = tripService.getTripById(tripId);

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      return ResponseEntity.ok(trip);

//    } else if (hasUserRole("USER", authentication)) {
//      if (trip.getUsers().contains(userDetails.getUser())) {
//        return ResponseEntity.ok(trip);
//      } else {
//        throw new AccessDeniedException("You are not authorized to access this resource.");
//      }
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
    return null;
  }

  @PostMapping("/create")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

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

  @PostMapping("/{tripId}/addUser/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<UserTripExpense> addUserToTrip(@PathVariable("tripId") long tripId, @PathVariable("userId") long userId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    UserTripExpense userTripExpense = userTripExpenseService.addUserToTrip(userId, tripId);

    return ResponseEntity.ok(userTripExpense);

//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//
//      if (trip == null) {
//        throw new ResourceNotFoundException("Trip not found with ID: " + tripId);
//      }
//
//      if (user == null) {
//        throw new ResourceNotFoundException("User not found with ID: " + userId);
//      }
//
//      trip.getUsers().add(user);
//      tripService.updateTrip(trip);
//
//      return ResponseEntity.ok(trip);
//
//    } else if (hasUserRole("USER", authentication)) {
//
//      if (userDetails.getId().equals(userId)) {
//
//        if (trip == null) {
//          throw new ResourceNotFoundException("Trip not found with ID: " + tripId);
//        }
//
//        trip.getUsers().add(user);
//        tripService.updateTrip(trip);
//
//        return ResponseEntity.ok(trip);
//
//      } else {
//        throw new AccessDeniedException("You are not authorized to access this resource.");
//      }
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
//    }
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

//  @PutMapping("/update/{tripId}")
//  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
//  public ResponseEntity<Trip> updateTrip(@PathVariable("tripId") long tripId, @RequestBody Trip trip) {
//
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//    Trip existingTrip = tripService.getTripById(tripId);
//
//    UserTripExpense userTripExpense = userTripExpenseService.getUserTripExpenseByTripId(tripId);
//
//    return ResponseEntity.ok(userTripExpense.getTrip());

//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//      return getTripResponseEntity(trip, numberOfNights, existingTrip);
//
//    } else if (hasUserRole("USER", authentication)) {
//      if (trip.getUsers().contains(userDetails.getUser())) {
//        return getTripResponseEntity(trip, numberOfNights, existingTrip);
//      } else {
//        throw new AccessDeniedException("You are not authorized to update this resource.");
//      }
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
//    }
//  }

  private ResponseEntity<Trip> getTripResponseEntity(@RequestBody Trip trip, @RequestParam int numberOfNights, Trip existingTrip) {
//    TripNights tripNights = existingTrip.getNights().get(0);
//    tripNights.setNumberOfNights(numberOfNights);
    existingTrip.setDestination(trip.getDestination());
    existingTrip.setStartDate(trip.getStartDate());
    existingTrip.setEndDate(trip.getEndDate());
    existingTrip.setDescription(trip.getDescription());

    Trip updatedTrip = tripService.updateTrip(existingTrip);

    return ResponseEntity.ok(updatedTrip);
  }

  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

}
