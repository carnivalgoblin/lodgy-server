package co.rcprdn.lodgyserver.entity;

import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Expense {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String description;

  @NotNull
  private Double amount;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id")
  private Trip trip;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  public Expense(Long id, String destination, Double amount, User user, Trip trip) {
    this.id = id;
    this.description = destination;
    this.amount = amount;
    this.user = user;
    this.trip = trip;
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

  public void setUserId(Long userId, UserRepository userRepository) {
    // Check if the user with the given ID exists
    Optional<User> optionalUser = userRepository.findById(userId);

    if (optionalUser.isPresent()) {
      this.user = optionalUser.get();
    } else {
      throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
      // You can use a different exception type or handle the situation as appropriate for your application.
    }
  }

  public void setTripId(Long tripId, TripRepository tripRepository) {
    // Check if the trip with the given ID exists
    Optional<Trip> optionalTrip = tripRepository.findById(tripId);

    if (optionalTrip.isPresent()) {
      this.trip = optionalTrip.get();
    } else {
      throw new IllegalArgumentException("Trip with ID " + tripId + " does not exist.");
      // You can use a different exception type or handle the situation as appropriate for your application.
    }
  }
}
