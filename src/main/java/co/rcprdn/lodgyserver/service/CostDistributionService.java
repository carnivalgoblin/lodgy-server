package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.dto.ExpenseDTO;
import co.rcprdn.lodgyserver.dto.TripDTO;
import co.rcprdn.lodgyserver.dto.UserTripDTO;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.entity.UserTrip;
import co.rcprdn.lodgyserver.exceptions.ResourceNotFoundException;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import co.rcprdn.lodgyserver.repository.UserTripRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@AllArgsConstructor
@Service
public class CostDistributionService {

  public final UserTripRepository userTripRepository;
  public final UserRepository userRepository;
  public final TripRepository tripRepository;

  public void distributeCosts(TripDTO tripDTO, List<UserTripDTO> userTripDTOs, List<ExpenseDTO> expenseDTOs, boolean basedOnDays) {
    double totalExpenses = expenseDTOs.stream().mapToDouble(ExpenseDTO::getAmount).sum();
    int numberOfParticipants = userTripDTOs.size();

    double sharePerParticipant;
    int tripDuration;

    if (tripDTO.getStartDate() != null && tripDTO.getEndDate() != null) {
      tripDuration = calculateTripDurationInDays(tripDTO.getStartDate(), tripDTO.getEndDate());
    } else {
      tripDuration = userTripDTOs.stream().mapToInt(UserTripDTO::getDays).sum();
    }

    if (basedOnDays) {
      sharePerParticipant = totalExpenses / userTripDTOs.stream()
              .mapToDouble(UserTripDTO::getDays)
              .sum();
    } else {
      sharePerParticipant = totalExpenses / numberOfParticipants;
    }

    for (UserTripDTO userTripDTO : userTripDTOs) {
      UserTrip userTrip = userTripRepository.findByUserIdAndTripId(userTripDTO.getUserId(), userTripDTO.getTripId())
              .orElse(new UserTrip());

      double participantShare = basedOnDays ? sharePerParticipant * userTripDTO.getDays() : sharePerParticipant;
      userTripDTO.setOwedAmount(participantShare);

      userTrip.setDays(userTripDTO.getDays());
      userTrip.setOwedAmount(participantShare);

      userTrip.setUser(userRepository.getById(userTripDTO.getUserId()));
      userTrip.setTrip(tripRepository.getById(userTripDTO.getTripId()));

      userTripRepository.save(userTrip);
    }
  }

  private int calculateTripDurationInDays(String startDateStr, String endDateStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    LocalDate startDate = LocalDate.parse(startDateStr, formatter);
    LocalDate endDate = LocalDate.parse(endDateStr, formatter);
    return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
  }

  private UserTrip convertUserTripDTOToEntity(UserTripDTO userTripDTO) {
    UserTrip userTrip = new UserTrip();

    // Set the existing User and Trip entities based on the provided IDs
    User user = userRepository.findById(userTripDTO.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userTripDTO.getUserId()));

    Trip trip = tripRepository.findById(userTripDTO.getTripId())
            .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + userTripDTO.getTripId()));

    userTrip.setUser(user);
    userTrip.setTrip(trip);
    userTrip.setDays(userTripDTO.getDays());
    userTrip.setOwedAmount(userTripDTO.getOwedAmount());

    return userTrip;
  }

}
