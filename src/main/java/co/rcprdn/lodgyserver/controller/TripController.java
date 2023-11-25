package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.dto.TripDTO;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.TripService;
import co.rcprdn.lodgyserver.service.UserService;
import jakarta.validation.Valid;
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

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      TripDTO tripDTO = convertTripToDTO(trip);
      return ResponseEntity.ok(tripDTO);
    } else if (hasUserRole("USER", authentication)) {
      if (trip.getUsers().stream().anyMatch(user -> user.getId().equals(userDetails.getId()))) {
        TripDTO tripDTO = convertTripToDTO(trip);
        return ResponseEntity.ok(tripDTO);
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
  }

  @PostMapping("/create")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Trip> createTrip(@RequestBody Trip trip) {

    Trip createdTrip = tripService.createTrip(trip);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTrip);

  }

  @PostMapping("/{tripId}/addUser/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<TripDTO> addUserToTrip(@PathVariable("tripId") long tripId,
                                               @PathVariable("userId") long userId,
                                               @AuthenticationPrincipal UserDetailsImpl userDetails) {

    if (userDetails != null) {
      // Check if the authenticated user has the role 'ADMIN' or 'MODERATOR'
      boolean isAdminOrModerator = userDetails.getAuthorities().stream()
              .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_MODERATOR"));

      if (isAdminOrModerator || (userDetails.getId() == userId)) {
        // Admins and moderators can add any user, users can only add themselves
        TripDTO tripDTO = tripService.addUserToTrip(tripId, userId);
        return new ResponseEntity<>(tripDTO, HttpStatus.OK);
      } else {
        throw new AccessDeniedException("You are not authorized to add other users to this trip.");
      }
    } else {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }


  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Void> deleteTrip(@PathVariable("id") Long id,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails) {

    if (userDetails != null) {
      // Check if the authenticated user has the role 'ADMIN' or 'MODERATOR'
      boolean isAdminOrModerator = userDetails.getAuthorities().stream()
              .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN") || role.getAuthority().equals("ROLE_MODERATOR"));

      if (isAdminOrModerator) {
        // Admins and moderators can delete any trip
        tripService.deleteTrip(id);
        return ResponseEntity.ok().build();
      } else {
        throw new AccessDeniedException("You are not authorized to delete trips.");
      }
    } else {
      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
  }

  @PutMapping("/update/{tripId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<TripDTO> updateTrip(@PathVariable("tripId") long tripId, @RequestBody @Valid TripDTO tripDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    Trip existingTrip = tripService.getTripById(tripId);

    if (existingTrip == null) {
      // Handle the case where the trip with the given ID doesn't exist
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      // Admin or moderator logic for updating trip
      Trip updatedTrip = updateTripDetails(existingTrip, tripDTO, userDetails);
      return ResponseEntity.ok(convertTripToDTO(updatedTrip));
    } else if (hasUserRole("USER", authentication)) {
      // User logic for updating trip
      if (tripDTO.getUserIds().contains(userDetails.getId())) {
        Trip updatedTrip = updateTripDetails(existingTrip, tripDTO, userDetails);
        return ResponseEntity.ok(convertTripToDTO(updatedTrip));
      } else {
        throw new AccessDeniedException("You are not authorized to update this resource.");
      }
    } else {
      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
  }


  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

  private Trip updateTripDetails(Trip existingTrip, TripDTO tripDTO, UserDetailsImpl userDetails) {
    // Update the trip details based on the logic specific to your application
    // For example, you might update destination, start date, end date, etc.
    // You'll need to implement this based on your requirements.

    // Here's a simple example:
    existingTrip.setDestination(tripDTO.getDestination());
    existingTrip.setStartDate(tripDTO.getStartDate());
    existingTrip.setEndDate(tripDTO.getEndDate());
    existingTrip.setDescription(tripDTO.getDescription());

    // Check if the user has ROLE_USER
    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
      // Only allow users with ROLE_USER to update specific fields
      // For example, you might check and update only destination and description
      existingTrip.setDestination(tripDTO.getDestination());
      existingTrip.setDescription(tripDTO.getDescription());
    }


    // Assuming getUserIdList() is a method in your Trip entity
    // Assuming tripDTO.getUserIds() returns a List<Long> of user IDs
    List<User> users = userService.getUsersByIds(tripDTO.getUserIds());
    existingTrip.setUsers(users);

    // You might need additional logic based on your application's requirements

    // Save the updated trip
    return tripService.updateTrip(existingTrip);
  }

  // Convert Trip to TripDTO
  private TripDTO convertTripToDTO(Trip trip) {
    TripDTO tripDTO = new TripDTO();

    tripDTO.setId(trip.getId());
    tripDTO.setDestination(trip.getDestination());
    tripDTO.setStartDate(trip.getStartDate());
    tripDTO.setEndDate(trip.getEndDate());
    tripDTO.setDescription(trip.getDescription());

    // Assuming getUserIdList() is a method in your Trip entity
    tripDTO.setUserIds(trip.getUserIdList());

    // You might need additional conversion logic based on your application's requirements

    return tripDTO;
  }
}
