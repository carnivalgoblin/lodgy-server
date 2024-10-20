package co.rcprdn.lodgyserver.dto;

import co.rcprdn.lodgyserver.entity.Role;
import co.rcprdn.lodgyserver.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private Long id;

  private String username;

  private List<Long> tripIds;

  private List<Long> expenseIds;

  private List<Long> userTrips;

  private List<ERole> userRoles;

  private Boolean enabled;

}
