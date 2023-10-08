package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.repository.ExpenseRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;

  public List<Expense> getAllExpenses() {
    return expenseRepository.findAll();
  }

  public Expense getExpenseById(Long id) {
    return expenseRepository.findById(id).orElse(null);
  }

  public Expense createExpense(Expense expense) {
    return expenseRepository.save(expense);
  }

  public void deleteExpense(Long id) {
    expenseRepository.deleteById(id);
  }

  public List<Expense> getExpensesByParticipantIdAndTripId(Long participantId, Long tripId) {
    return expenseRepository.getExpensesByParticipantIdAndTripId(participantId, tripId);
  }

  public List<Expense> getExpensesByTripId(Long tripId) {
    return expenseRepository.getExpensesByTripId(tripId);
  }

}
