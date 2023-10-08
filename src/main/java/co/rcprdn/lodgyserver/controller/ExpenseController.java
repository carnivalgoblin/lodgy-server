package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;

  @GetMapping
  public List<Expense> getAllExpenses() {
    return expenseService.getAllExpenses();
  }

  @GetMapping("/{id}")
  public Expense getExpenseById(@PathVariable Long id) {
    return expenseService.getExpenseById(id);
  }

  @PostMapping
  public Expense createExpense(@RequestBody Expense expense) {
    return expenseService.createExpense(expense);
  }

  @DeleteMapping("/{id}")
  public void deleteExpense(@PathVariable Long id) {
    expenseService.deleteExpense(id);
  }

}
