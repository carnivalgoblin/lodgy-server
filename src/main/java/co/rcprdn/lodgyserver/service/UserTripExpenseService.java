package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.UserTripExpense;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import co.rcprdn.lodgyserver.repository.UserTripExpenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserTripExpenseService {

  private final UserTripExpenseRepository userTripExpenseRepository;
  private final UserRepository userRepository;
  private final TripRepository tripRepository;

  public UserTripExpense addUserToTrip(Long userId, Long tripId) {
    User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found"));

    UserTripExpense userTripExpense = new UserTripExpense();
    userTripExpense.setUser(user);
    userTripExpense.setTrip(trip);

    user.getUserTripExpenses().add(userTripExpense);
    trip.getUserTripExpenses().add(userTripExpense);

    return userTripExpenseRepository.save(userTripExpense);
  }

}
