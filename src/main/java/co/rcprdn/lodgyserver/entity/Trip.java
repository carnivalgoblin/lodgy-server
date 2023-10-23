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
@Table(name = "trips")
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String destination;

  private Date startDate;

  private Date endDate;

  private String description;

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL)
  private List<UserTripExpense> userTripExpenses = new ArrayList<>();

}
