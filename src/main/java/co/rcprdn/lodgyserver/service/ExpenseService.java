package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.repository.ExpenseRepository;
import co.rcprdn.lodgyserver.repository.TripRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ExpenseService {

  private final ExpenseRepository expenseRepository;
  private final TripRepository tripRepository;

  public List<Expense> getAllExpenses() {
    return expenseRepository.findAll();
  }

  public Expense getExpenseById(Long id) {
    return expenseRepository.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
  }

  public Expense createExpense(Expense expense, Long tripId) {
    Trip trip = tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found with id: " + tripId));
    expense.setTrip(trip);
    Expense savedExpense = expenseRepository.save(expense);
    Long savedExpenseId = savedExpense.getId();
    Expense returnedExpense = expenseRepository.findById(savedExpenseId)
            .orElseThrow(() -> new RuntimeException("Saved Expense not found after creation"));

    return returnedExpense;
  }

  public Expense updateExpense(Expense expense) {
    return expenseRepository.save(expense);
  }

  public void deleteExpense(Long id) {
    expenseRepository.deleteById(id);
  }

  public List<Expense> getExpensesByTripId(Long tripId) {
    return expenseRepository.findAllByTripId(tripId);
  }

  public List<Expense> getExpensesByTripIdAndUserId(Long tripId, Long userId) {
    return expenseRepository.findAllByTripIdAndUserId(tripId, userId);
  }

  public List<Expense> getExpensesByUserId(Long userId) {
    return expenseRepository.findAllByUserId(userId);
  }
}
