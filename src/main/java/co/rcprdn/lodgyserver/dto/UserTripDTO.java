package co.rcprdn.lodgyserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTripDTO {

  private Long id;

  private Long userId;

  private String username;

  private Long tripId;

  private int days;

  private double owedAmount;

  public UserTripDTO(Long userId, Long tripId, int days, double owedAmount) {
    this.userId = userId;
    this.tripId = tripId;
    this.days = days;
    this.owedAmount = owedAmount;
  }

}
