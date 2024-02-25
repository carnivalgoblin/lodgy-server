package co.rcprdn.lodgyserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "payer_user_id", nullable = false)
  private User payerUser;

  @ManyToOne
  @JoinColumn(name = "receiver_user_id", nullable = false)
  private User receiverUser;

  @ManyToOne
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  private double amount;

  public Payment(User payerUser, User receiverUser, Trip trip, double amount) {
    this.payerUser = payerUser;
    this.receiverUser = receiverUser;
    this.trip = trip;
    this.amount = amount;
  }
}
