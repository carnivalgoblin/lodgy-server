package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TripService {

  private final TripRepository tripRepository;

  private final UserRepository userRepository;

  private static final Logger log = LoggerFactory.getLogger(TripService.class);

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

  public Trip addUserToTrip(Long tripId, Long userId) {
    Trip trip = tripRepository.findById(tripId)
            .orElse(null);

    User user = userRepository.findById(userId)
            .orElse(null);

    if (trip != null && user != null) {
      trip.addUser(user);
      user.addTrip(trip);

      tripRepository.save(trip);
      userRepository.save(user);

      return trip;
    }

    return null;
  }

}
