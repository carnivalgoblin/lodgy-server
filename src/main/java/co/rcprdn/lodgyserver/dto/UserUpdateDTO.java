package co.rcprdn.lodgyserver.dto;

import co.rcprdn.lodgyserver.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {

  private Long id;
  private Set<ERole> roles;
  private boolean isActivated;

}
