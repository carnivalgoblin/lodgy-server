package co.rcprdn.lodgyserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
          name = "trip_user",
          joinColumns = @JoinColumn(name = "trip_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id"))
  private List<User> users = new ArrayList<>();

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Expense> expenses = new ArrayList<>();

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<UserTrip> userTrips = new ArrayList<>();

}
