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

  private Long tripId;

  private int days;
}
