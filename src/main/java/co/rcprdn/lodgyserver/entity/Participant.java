package co.rcprdn.lodgyserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter

@Entity
public class Participant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String username;

  private String password;

  @OneToMany(mappedBy = "participant")
  private List<Expense> expenses;

  @ManyToMany(mappedBy = "participants")
  private List<Trip> trips;

}
