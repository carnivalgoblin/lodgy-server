package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.dto.ExpenseDTO;
import co.rcprdn.lodgyserver.entity.Expense;
import co.rcprdn.lodgyserver.repository.TripRepository;
import co.rcprdn.lodgyserver.repository.UserRepository;
import co.rcprdn.lodgyserver.security.services.UserDetailsImpl;
import co.rcprdn.lodgyserver.service.ExpenseService;
import co.rcprdn.lodgyserver.service.TripService;
import co.rcprdn.lodgyserver.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
  private final UserService userService;
  private final TripService tripService;
  private final UserRepository userRepository;
  private final TripRepository tripRepository;

  @GetMapping("/all")
  @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<List<ExpenseDTO>> getAllExpenses() {
    List<Expense> expenses = expenseService.getAllExpenses();
    List<ExpenseDTO> expenseDTOs = convertExpenseListToDTOs(expenses);

    return new ResponseEntity<>(expenseDTOs, HttpStatus.OK);
  }


  @GetMapping("/{expenseId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<ExpenseDTO> getExpenseById(@PathVariable Long expenseId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    Expense expense = expenseService.getExpenseById(expenseId);

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      ExpenseDTO expenseDTO = convertExpenseToDTO(expense);
      return ResponseEntity.ok(expenseDTO);
    } else if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(expense.getUser().getId())) {
        ExpenseDTO expenseDTO = convertExpenseToDTO(expense);
        return ResponseEntity.ok(expenseDTO);
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
  }

  @PostMapping("/create")
  public ResponseEntity<String> addExpense(@Valid @RequestBody ExpenseDTO expenseDTO, @RequestParam Long tripId) {

    // Check if user ID and trip ID exist
    if (!userService.existsById(expenseDTO.getUserId())) {
      return new ResponseEntity<>("User not found with ID: " + expenseDTO.getUserId(), HttpStatus.NOT_FOUND);
    }

    if (!tripService.existsById(tripId)) {
      return new ResponseEntity<>("Trip not found with ID: " + tripId, HttpStatus.NOT_FOUND);
    }

    if (expenseDTO.getAmount() == null || expenseDTO.getDescription() == null) {
      return new ResponseEntity<>("Amount and description are required fields.", HttpStatus.BAD_REQUEST);
    }

    // Convert DTO to entity
    Expense expense = convertExpenseDTOToEntity(expenseDTO);

    expenseService.createExpense(expense, tripId);

    return new ResponseEntity<>("Expense added successfully", HttpStatus.CREATED);
  }

  @DeleteMapping("/delete/{expenseId}")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Void> deleteExpense(@PathVariable("expenseId") Long expenseId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    Expense expense = expenseService.getExpenseById(expenseId);

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      expenseService.deleteExpense(expense.getId());
      return ResponseEntity.ok().build();
    } else if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(expenseService.getExpenseById(expense.getId()).getUser().getId())) {
        expenseService.deleteExpense(expense.getId());
        return ResponseEntity.ok().build();
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
  }

  @PutMapping("/update")
  @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
  public ResponseEntity<Expense> updateExpense(@RequestBody ExpenseDTO expenseDTO) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    // Convert DTO to entity
    Expense expense = convertExpenseDTOToEntity(expenseDTO);

    if (hasUserRole("MODERATOR", authentication) || hasUserRole("ADMIN", authentication)) {
      return ResponseEntity.ok(expenseService.updateExpense(expense));
    } else if (hasUserRole("USER", authentication)) {
      if (userDetails.getId().equals(expense.getUser().getId())) {
        return ResponseEntity.ok(expenseService.updateExpense(expense));
      } else {
        throw new AccessDeniedException("You are not authorized to access this resource.");
      }
    } else {
      throw new AccessDeniedException("You are not authorized to access this resource.");
    }
  }

  private boolean hasUserRole(String roleName, Authentication authentication) {
    return authentication.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals("ROLE_" + roleName));
  }

  private List<ExpenseDTO> convertExpenseListToDTOs(List<Expense> expenses) {
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

  private ExpenseDTO convertExpenseToDTO(Expense expense) {
    ExpenseDTO expenseDTO = new ExpenseDTO();

    expenseDTO.setId(expense.getId());
    expenseDTO.setTripId(expense.getTrip().getId());
    expenseDTO.setUserId(expense.getUser().getId());
    expenseDTO.setAmount(expense.getAmount());
    expenseDTO.setDescription(expense.getDescription());

    return expenseDTO;
  }

  private Expense convertExpenseDTOToEntity(ExpenseDTO expenseDTO) {
    Expense expense = new Expense();

    expense.setUserId(expenseDTO.getUserId(), userRepository);
    expense.setTripId(expenseDTO.getTripId(), tripRepository);
    expense.setAmount(expenseDTO.getAmount());
    expense.setDescription(expenseDTO.getDescription());

    return expense;
  }

}
