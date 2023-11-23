package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.dto.TripDTO;
import co.rcprdn.lodgyserver.entity.Trip;
//import co.rcprdn.lodgyserver.entity.UserTripExpense;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.TripService;
import co.rcprdn.lodgyserver.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static co.rcprdn.lodgyserver.service.TripService.getTripDTO;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {

  private final TripService tripService;
  private final UserService userService;


  @GetMapping("/all")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<TripDTO>> getAllTrips() {
    List<Trip> trips = tripService.getAllTrips();

    if (!trips.isEmpty()) {
      List<TripDTO> tripDTOs = trips.stream()
              .map(this::convertToDTO)
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
      TripDTO tripDTO = convertToDTO(trip);
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
  public ResponseEntity<TripDTO> addUserToTrip(@PathVariable("tripId") long tripId,
                                               @PathVariable("userId") long userId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

    if (userDetails != null) {
      TripDTO tripDTO = tripService.addUserToTrip(tripId, userId);
      return new ResponseEntity<>(tripDTO, HttpStatus.OK);
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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


  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

  private TripDTO convertToDTO(Trip trip) {
    return getTripDTO(trip);
  }
}
