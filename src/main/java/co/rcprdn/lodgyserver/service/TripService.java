package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.dto.TripDTO;
import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TripService {

  private final TripRepository tripRepository;

  private final UserRepository userRepository;

  public List<Trip> getAllTrips() {
    return tripRepository.findAll();
  }

  public Trip getTripById(Long id) {
    return tripRepository.findById(id).orElseThrow(() -> new RuntimeException("Trip not found"));
  }

  public Trip createTrip(Trip trip) {
    return tripRepository.save(trip);
  }

  public Trip updateTrip(Trip trip) {
    return tripRepository.save(trip);
  }

  public void deleteTrip(Long id) {
    tripRepository.deleteById(id);
  }

  public TripDTO addUserToTrip(long tripId, long userId) {
    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    trip.getUsers().add(user);
    tripRepository.save(trip);

    // Assuming you have a method to convert Trip to TripDTO
    return convertToDTO(trip);
  }

  private TripDTO convertToDTO(Trip trip) {
    return getTripDTO(trip);
  }

  public static TripDTO getTripDTO(Trip trip) {
    TripDTO tripDTO = new TripDTO();

    tripDTO.setId(trip.getId());
    tripDTO.setDestination(trip.getDestination());
    tripDTO.setStartDate(String.valueOf(trip.getStartDate()));
    tripDTO.setEndDate(String.valueOf(trip.getEndDate()));
    tripDTO.setDescription(trip.getDescription());
    tripDTO.setUserIds(trip.getUsers().stream().map(User::getId).collect(Collectors.toList()));
    tripDTO.setExpenseIds(trip.getExpenses().stream().map(Expense::getId).collect(Collectors.toList()));

    return tripDTO;
  }

  public boolean existsById(Long tripId) {
    return tripRepository.existsById(tripId);
  }
}
