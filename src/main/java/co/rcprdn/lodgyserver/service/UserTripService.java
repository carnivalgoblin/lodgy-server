package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.dto.UserTripDTO;
import co.rcprdn.lodgyserver.entity.UserTrip;
import co.rcprdn.lodgyserver.repository.UserTripRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserTripService {

  private final ModelMapper modelMapper;

  private final UserTripRepository userTripRepository;

  public UserTripDTO convertToDTO(UserTrip userTrip) {
    return modelMapper.map(userTrip, UserTripDTO.class);
  }

  public UserTripDTO getUserTripById(Long id) {
    UserTrip userTrip = userTripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("UserTrip not found with id: " + id));

    return convertToDTO(userTrip);
  }

  public static UserTripDTO getUserTripDTO(UserTrip userTrip) {
    UserTripDTO userTripDTO = new UserTripDTO();

    userTripDTO.setId(userTrip.getId());
    userTripDTO.setDays(userTrip.getDays());
    userTripDTO.setTripId(userTrip.getTrip().getId());
    userTripDTO.setUserId(userTrip.getUser().getId());

    return userTripDTO;
  }

  public List<UserTrip> getAllUserTrips() {
    return userTripRepository.findAll();
  }

  public List<UserTrip> getAllUserTripsByTripId(Long tripId) {
    return userTripRepository.findAllByTripId(tripId);
  }

  public List<UserTrip> getAllUserTripsByUserId(Long userId) {
    return userTripRepository.findAllByUserId(userId);
  }

  public UserTrip findById(Long id) {
    return userTripRepository.findById(id).orElseThrow(() -> new RuntimeException("UserTrip not found"));
  }
}
