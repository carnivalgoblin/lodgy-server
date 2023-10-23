package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.repository.TripRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class TripService {

  private final TripRepository tripRepository;

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

}
