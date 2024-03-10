package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.dto.ExpenseDTO;
import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/expenses")
public class ExpenseController {

  private final ExpenseService expenseService;
  private final UserRepository userRepository;
  private final TripRepository tripRepository;

  @GetMapping("/all")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
    List<Expense> expenses = expenseService.getAllExpenses();
    List<ExpenseDTO> expenseDTOs = convertToDTOs(expenses);
    return new ResponseEntity<>(expenseDTOs, HttpStatus.OK);
  }

  @GetMapping("/user/{userId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<ExpenseDTO>> getExpensesByUserId(@PathVariable Long userId) {
    List<Expense> expenses = expenseService.getExpensesByUserId(userId);
    List<ExpenseDTO> expenseDTOs = convertToDTOs(expenses);
    return new ResponseEntity<>(expenseDTOs, HttpStatus.OK);
  }


  @GetMapping("/{expenseId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long expenseId) {

    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    Expense expense = expenseService.getExpenseById(expenseId);

    if (expense != null) {
      ExpenseDTO expenseDTO = convertToDTO(expense);
      return new ResponseEntity<>(expenseDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

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

  @GetMapping("/trip/{tripId}/total")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<ExpenseDTO> getTotalExpensesByTripId(@PathVariable Long tripId) {
    List<Expense> expenses = expenseService.getExpensesByTripId(tripId);

    if (!expenses.isEmpty()) {
      double totalAmount = expenses.stream().mapToDouble(Expense::getAmount).sum();
      ExpenseDTO tripExpenseDTO = new ExpenseDTO(null, null, tripId, totalAmount, null);
      return new ResponseEntity<>(tripExpenseDTO, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @GetMapping("/trip/{tripId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<ExpenseDTO>> getExpensesByTripIdByUserId(@PathVariable Long tripId, @RequestParam Long userId) {
    List<Expense> expenses = expenseService.getExpensesByTripIdAndUserId(tripId, userId);
    List<ExpenseDTO> expenseDTOs = convertToDTOs(expenses);
    return new ResponseEntity<>(expenseDTOs, HttpStatus.OK);
  }

  @PostMapping("/create")
  public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO expenseDTO, @RequestParam Long tripId) {

    Expense expense = convertToEntity(expenseDTO);
    Expense savedExpense = expenseService.createExpense(expense, tripId);
    ExpenseDTO responseDTO = convertToDTO(savedExpense);

    return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
  }

//  @PostMapping("/create")
//  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
//  public ResponseEntity<Expense> createExpense(@RequestBody Expense expense) {
//
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
//
//    return ResponseEntity.ok(expenseService.createExpense(expense));

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
//  }

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

  private List<ExpenseDTO> convertToDTOs(List<Expense> expenses) {
    List<ExpenseDTO> expenseDTOs = new ArrayList<>();
    for (Expense expense : expenses) {
      ExpenseDTO expenseDTO = new ExpenseDTO();
      expenseDTO.setId(expense.getId());
      expenseDTO.setTripId(expense.getTrip().getId());
      expenseDTO.setUserId(expense.getUser().getId());
      expenseDTO.setAmount(expense.getAmount());
      expenseDTO.setDescription(expense.getDescription());

      expenseDTOs.add(expenseDTO);
    }
    return expenseDTOs;
  }

  private ExpenseDTO convertToDTO(Expense expense) {
    ExpenseDTO expenseDTO = new ExpenseDTO();

    expenseDTO.setId(expense.getId());
    expenseDTO.setTripId(expense.getTrip().getId());
    expenseDTO.setUserId(expense.getUser().getId());
    expenseDTO.setAmount(expense.getAmount());
    expenseDTO.setDescription(expense.getDescription());

    return expenseDTO;
  }

  private Expense convertToEntity(ExpenseDTO expenseDTO) {
    Expense expense = new Expense();

    expense.setUserId(expenseDTO.getUserId(), userRepository);
    expense.setTripId(expenseDTO.getTripId(), tripRepository);
    expense.setAmount(expenseDTO.getAmount());
    expense.setDescription(expenseDTO.getDescription());

    return expense;
  }

}
