package co.rcprdn.lodgyserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private Long id;

  private String username;

  private List<Long> tripIds;

  private List<Long> expenseIds;

  private List<UserTripDTO> userTrips;

}
