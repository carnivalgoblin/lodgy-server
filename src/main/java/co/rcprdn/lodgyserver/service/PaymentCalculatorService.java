package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.dto.ExpenseDTO;
import co.rcprdn.lodgyserver.dto.UserTripDTO;
import co.rcprdn.lodgyserver.entity.Payment;
import co.rcprdn.lodgyserver.entity.UserPayment;
import co.rcprdn.lodgyserver.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@AllArgsConstructor
@Service
public class PaymentCalculatorService {

  public final UserRepository userRepository;

  public List<Payment> calculatePayments(List<UserTripDTO> userTripDTOs, List<ExpenseDTO> expenseDTOs) {
    Map<Long, Double> userExpenses = new HashMap<>();
    Map<Long, Double> userOwes = new HashMap<>();

    // Step 1: Calculate total expenses paid by each user
    for (ExpenseDTO expenseDTO : expenseDTOs) {
      userExpenses.merge(expenseDTO.getUserId(), expenseDTO.getAmount(), Double::sum);
    }

    // Step 2: Calculate total amount owed by each user
    for (UserTripDTO userTripDTO : userTripDTOs) {
      userOwes.merge(userTripDTO.getUserId(), userTripDTO.getOwedAmount(), Double::sum);
    }

    // Step 3: Find the difference between expenses paid and amount owed for each user
    List<Payment> payments = new ArrayList<>();
    List<UserPayment> userPayments = new ArrayList<>();

    for (Map.Entry<Long, Double> entry : userExpenses.entrySet()) {
      long userId = entry.getKey();
      double expense = entry.getValue();
      double owes = userOwes.getOrDefault(userId, 0.0);
      double difference = expense - owes;

      if (difference > 0) {
        // User owes money
        userPayments.add(new UserPayment(userRepository.findById(userId).orElse(null), difference));
      } else if (difference < 0) {
        // User is owed money
        userPayments.add(new UserPayment(userRepository.findById(userId).orElse(null), -difference));
      }
      // If difference is 0, no payment needed for this user
    }

    // Step 4: Pair users to even out amounts
    minimizePayments(userPayments, payments);

    return payments;
  }

  private void minimizePayments(List<UserPayment> userPayments, List<Payment> payments) {
    userPayments.sort(Comparator.comparingDouble(UserPayment::getAmount));

    int start = 0;
    int end = userPayments.size() - 1;

    while (start < end) {
      UserPayment payer = userPayments.get(start);
      UserPayment receiver = userPayments.get(end);

      double amountToPay = payer.getAmount();
      double amountToReceive = receiver.getAmount();

      if (amountToPay + amountToReceive == 0) {
        // Create a payment to even out the amounts
        payments.add(new Payment(payer.getUser(), receiver.getUser(), null, amountToPay));
        start++;
        end--;
      } else if (amountToPay + amountToReceive > 0) {
        // Payer needs to pay more, update the amount for the payer
        payer.setAmount(amountToPay + amountToReceive);
        end--;
      } else {
        // Receiver needs to receive more, update the amount for the receiver
        receiver.setAmount(amountToPay + amountToReceive);
        start++;
      }
    }
  }
}