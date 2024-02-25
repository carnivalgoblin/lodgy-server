package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.dto.UserTripDTO;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.entity.UserTrip;
import co.rcprdn.lodgyserver.repository.UserTripRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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

    UserTripDTO userTripDTO = convertToDTO(userTrip);
    User user = userTrip.getUser();
    userTripDTO.setUsername(user.getUsername());

    return userTripDTO;
  }

  public static UserTripDTO getUserTripDTO(UserTrip userTrip) {
    UserTripDTO userTripDTO = new UserTripDTO();

    userTripDTO.setId(userTrip.getId());
    userTripDTO.setDays(userTrip.getDays());
    userTripDTO.setTripId(userTrip.getTrip().getId());
    userTripDTO.setUserId(userTrip.getUser().getId());
    userTripDTO.setUsername(userTrip.getUser().getUsername());

    return userTripDTO;
  }

  public List<UserTrip> getAllUserTrips() {
    return userTripRepository.findAll();
  }

  public List<UserTrip> getAllUserTripsByTripId(Long tripId) {
    return userTripRepository.findAllByTripId(tripId);
  }

  public List<UserTripDTO> getAllUserTripsByUserId(Long userId) {
    List<UserTrip> userTrips = userTripRepository.findAllByUserId(userId);

    return userTrips.stream()
            .map(UserTripService::getUserTripDTO)
            .collect(java.util.stream.Collectors.toList());
  }

  public UserTrip findById(Long id) {
    return userTripRepository.findById(id).orElseThrow(() -> new RuntimeException("UserTrip not found"));
  }

  public List<UserTripDTO> getUserTripsForTrip(Long tripId) {
    List<UserTrip> userTrips = userTripRepository.findAllByTripId(tripId);

    return userTrips.stream()
            .map(UserTripService::getUserTripDTO)
            .collect(java.util.stream.Collectors.toList());
  }

  public List<UserTripDTO> getUserTripDTOsByUserId(Long userId) {
    List<UserTrip> userTrips = userTripRepository.findAllByUserId(userId);
    return userTrips.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
  }
}
