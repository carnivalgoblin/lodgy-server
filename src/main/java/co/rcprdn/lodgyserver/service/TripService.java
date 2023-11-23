package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

  public Trip addUserToTrip(Long tripId, Long userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    Optional<Trip> tripOptional = tripRepository.findById(tripId);

    if (userOptional.isPresent() && tripOptional.isPresent()) {
      User user = userOptional.get();
      Trip trip = tripOptional.get();

      // Füge den Benutzer zum Trip hinzu
      trip.getUsers().add(user);
      user.getTrips().add(trip);

      // Speichere die Aktualisierungen in der Datenbank
      tripRepository.save(trip);
      userRepository.save(user);

      if (!trip.getUsers().contains(user)) {
        trip.getUsers().add(user);
        return tripRepository.save(trip);
      } else {
        // Beziehung besteht bereits
        return trip;
      }
    } else {
      // Benutzer oder Trip nicht gefunden
      // Hier könntest du eine entsprechende Fehlerbehandlung durchführen
      return null;
    }
  }
}
