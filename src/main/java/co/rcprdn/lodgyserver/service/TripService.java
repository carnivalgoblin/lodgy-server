package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.dto.ExpenseDTO;
import co.rcprdn.lodgyserver.dto.TripDTO;
import co.rcprdn.lodgyserver.dto.UserTripDTO;
import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.entity.User;
import co.rcprdn.lodgyserver.entity.UserTrip;
import co.rcprdn.lodgyserver.exceptions.ResourceNotFoundException;
import co.rcprdn.lodgyserver.exceptions.UserAlreadyInTripException;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import co.rcprdn.lodgyserver.repository.UserTripRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class TripService {

  private final TripRepository tripRepository;
  private final UserRepository userRepository;
  private final UserTripRepository userTripRepository;
  private final UserTripService userTripService;

  @PersistenceContext
  private EntityManager entityManager;

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

  @Transactional
  public TripDTO addUserToTrip(long tripId, long userId, int days) {
    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + tripId));

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

    // Check if the user is already associated with the trip
    if (trip.getUsers().contains(user)) {
      // Handle the case where the user is already associated with the trip
      // You might throw an exception, return a specific response, or handle it as needed
      throw new UserAlreadyInTripException("User is already in the trip.");
    }

    UserTrip userTrip = new UserTrip();
    userTrip.setUser(user);
    userTrip.setTrip(trip);
    userTrip.setDays(days);

    UserTrip attachedUserTrip = entityManager.merge(userTrip);

    // Now, update the associations in the Trip and User entities
    trip.getUsers().add(user);
    trip.getUserTrips().add(attachedUserTrip);

    user.getTrips().add(trip);
    user.getUserTrips().add(attachedUserTrip);

    tripRepository.save(trip);
    userRepository.save(user);

    // Assuming you have a method to convert Trip to TripDTO
    return convertToDTO(trip);
  }

  public List<TripDTO> getTripsForUser(Long userId) {
    List<UserTripDTO> userTripDTOs = userTripService.getUserTripDTOsByUserId(userId);
    return userTripDTOs.stream()
            .map(userTripDTO -> this.getTripDTOById(userTripDTO.getTripId()))
            .collect(Collectors.toList());
  }

  public List<UserTripDTO> getOwedAmountsForTrip(Long tripId, List<UserTripDTO> userTripDTOs) {
    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found"));

    List<UserTripDTO> userOwedAmounts = new ArrayList<>();

    for (UserTripDTO userTripDTO : userTripDTOs) {
      User user = getUserById(userTripDTO.getUserId());  // Implement a method to get user by ID
      double owedAmount = calculateOwedAmount(userTripDTO);
      userOwedAmounts.add(new UserTripDTO(user.getId(), trip.getId(), userTripDTO.getDays(), owedAmount, user.getUsername()));
    }

    return userOwedAmounts;
  }

  private double calculateOwedAmount(UserTripDTO userTripDTO) {
    double owedAmount = 0.0;
    int days = userTripDTO.getDays();
    Trip trip = tripRepository.findById(userTripDTO.getTripId())
            .orElseThrow(() -> new RuntimeException("Trip not found"));

    for (Expense expense : trip.getExpenses()) {
      owedAmount += expense.getAmount() / trip.getUsers().size() * days;
    }

    return owedAmount;
  }

  private User getUserById(Long userId) {
    return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
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
    tripDTO.setUserTrips(trip.getUserTrips().stream().map(UserTrip::getId).collect(Collectors.toList()));

    return tripDTO;
  }

  public TripDTO getTripDTOById(Long tripId) {
    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

    return convertToDTO(trip);
  }

  public List<ExpenseDTO> getExpensesForTrip(Long tripId) {
    Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));

    return trip.getExpenses().stream()
            .map(expense -> {
              ExpenseDTO expenseDTO = new ExpenseDTO();
              expenseDTO.setId(expense.getId());
              expenseDTO.setAmount(expense.getAmount());
              expenseDTO.setTripId(expense.getTrip().getId());
              return expenseDTO;
            })
            .collect(Collectors.toList());
  }

  public boolean isUserInTrip(Long tripId, Long userId) {
    return userTripRepository.existsByTripIdAndUserId(tripId, userId);
  }
}
