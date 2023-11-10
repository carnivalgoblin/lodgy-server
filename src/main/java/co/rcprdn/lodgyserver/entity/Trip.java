package co.rcprdn.lodgyserver.entity;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "trips")
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String destination;

  private Date startDate;

  private Date endDate;

  private String description;

  @ManyToMany
  @JoinTable(
          name = "user_trip",
          joinColumns = @JoinColumn(name = "trip_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id"))
  @JsonIgnoreProperties("trips")
  private Set<User> users = new HashSet<>();

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Expense> expenses = new HashSet<>();


  // METHODS

  public void addUser(User user) {
    this.users.add(user);
    user.getTrips().add(this);
  }

}
