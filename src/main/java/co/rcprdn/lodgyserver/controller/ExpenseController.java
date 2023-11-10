package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;

  @GetMapping("/all")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Iterable<Expense>> getAllExpenses() {
    return ResponseEntity.ok(expenseService.getAllExpenses());
  }

  @GetMapping("/{expenseId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Expense> getExpenseById(@PathVariable("expenseId") long expenseId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    Expense expense = expenseService.getExpenseById(expenseId);

    return ResponseEntity.ok(expense);

//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//      return ResponseEntity.ok(expense);
//
//    } else if (hasUserRole("USER", authentication)) {
//      if (userDetails.getId().equals(expense.getUser().getId())) {
//        return ResponseEntity.ok(expense);
//      } else {
//        throw new AccessDeniedException("You are not authorized to access this resource.");
//      }
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
//    }
  }

  @PostMapping("/create")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Expense> createExpense(Expense expense) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    return ResponseEntity.ok(expenseService.createExpense(expense));

//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//      return ResponseEntity.ok(expenseService.createExpense(expense));
//
//    } else if (hasUserRole("USER", authentication)) {
//      if (userDetails.getId().equals(expense.getUser().getId())) {
//        return ResponseEntity.ok(expenseService.createExpense(expense));
//      } else {
//        throw new AccessDeniedException("You are not authorized to create this resource.");
//      }
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
//    }
  }

  @DeleteMapping("/delete/{id}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Void> deleteExpense(@PathVariable("id") Long id) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    return null;

//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//      expenseService.deleteExpense(id);
//      return ResponseEntity.ok().build();
//    } else if (hasUserRole("USER", authentication)) {
//      if (userDetails.getId().equals(expenseService.getExpenseById(id).getUser().getId())) {
//        expenseService.deleteExpense(id);
//        return ResponseEntity.ok().build();
//      } else {
//        throw new AccessDeniedException("You are not authorized to access this resource.");
//      }
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
//    }
  }

  @PutMapping("/update")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Expense> updateExpense(Expense expense) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    return ResponseEntity.ok(expenseService.updateExpense(expense));

//    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
//      return ResponseEntity.ok(expenseService.updateExpense(expense));
//
//    } else if (hasUserRole("USER", authentication)) {
//      if (userDetails.getId().equals(expense.getUser().getId())) {
//        return ResponseEntity.ok(expenseService.updateExpense(expense));
//      } else {
//        throw new AccessDeniedException("You are not authorized to access this resource.");
//      }
//    } else {
//      throw new AccessDeniedException("You are not authorized to access this resource.");
//    }
  }

  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

}
