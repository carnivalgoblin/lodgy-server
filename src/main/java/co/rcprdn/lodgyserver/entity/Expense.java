package co.rcprdn.lodgyserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  private Double amount;

  @ManyToOne
  @JoinColumn(name = "participant_id")
  private User user;

  @ManyToOne
  @JoinColumn(name = "trip_id")
  private Trip trip;

}
