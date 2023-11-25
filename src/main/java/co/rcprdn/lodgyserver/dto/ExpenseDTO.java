package co.rcprdn.lodgyserver.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {

  private Long id;

  @NotNull(message = "User ID cannot be null")
  private Long userId;

  @NotNull(message = "Trip ID cannot be null")
  private Long tripId;

  @NotNull(message = "Amount cannot be null")
  @PositiveOrZero(message = "Amount must be a positive or zero value")
  private Double amount;

  private String description;

}
