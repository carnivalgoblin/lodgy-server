package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.repository.TripRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class TripService {

  public final TripRepository tripRepository;

  public List<Trip> getAllTrips() {
    return tripRepository.findAll();
  }

  public Trip getTripById(Long id) {
    return tripRepository.findById(id).orElse(null);
  }

  public Trip createTrip(Trip trip) {
    return tripRepository.save(trip);
  }

  public void deleteTrip(Long id) {
    tripRepository.deleteById(id);
  }

  public List<Trip> getTripsByParticipantId(Long participantId) {
    return tripRepository.getTripsByParticipantId(participantId);
  }

}
