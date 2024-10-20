package co.rcprdn.lodgyserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {

  private Long id;

  private Long userId;

  private Long tripId;

  private Double amount;

  private String description;

  private LocalDateTime createdAt;

}
