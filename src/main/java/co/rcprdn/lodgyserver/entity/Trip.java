package co.rcprdn.lodgyserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String destination;

  private Date startDate;

  private Date endDate;

  private String description;

  @ManyToMany(mappedBy = "trips", cascade = CascadeType.MERGE)
  private Set<User> users = new HashSet<>();

  @OneToMany(mappedBy = "trip")
  private List<Expense> expenses;

  @OneToMany(mappedBy = "trip")
  private List<TripNights> nights = new ArrayList<>();

}
