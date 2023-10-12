package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

  private final UserRepository userRepository;

  public List<User> getAllParticipants() {
    return userRepository.findAll();
  }

  public User getParticipantById(Long id) {
    return userRepository.findById(id).orElse(null);
  }

  public User createParticipant(User user) {
    return userRepository.save(user);
  }

  public void deleteParticipant(Long id) {
    userRepository.deleteById(id);
  }

  public List<User> getParticipantsByTripId(Long tripId) {
    return userRepository.getUsersByTripId(tripId);
  }


}
