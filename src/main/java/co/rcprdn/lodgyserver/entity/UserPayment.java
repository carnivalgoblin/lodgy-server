package co.rcprdn.lodgyserver.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserPayment {
  private final User user;
  private double amount;
}
