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
public class TripDTO {

  private Long id;

  private String destination;

  private String startDate;

  private String endDate;

  private String description;

  private List<Long> userIds;

  private List<Long> expenseIds;
}
