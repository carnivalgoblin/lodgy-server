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
public class Trip {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String destination;

  private String startDate;

  private String endDate;

  @ManyToMany
  @JoinTable(
    name = "trip_participant",
    joinColumns = @JoinColumn(name = "trip_id"),
    inverseJoinColumns = @JoinColumn(name = "participant_id")
  )
  private List<Participant> participants;

}
