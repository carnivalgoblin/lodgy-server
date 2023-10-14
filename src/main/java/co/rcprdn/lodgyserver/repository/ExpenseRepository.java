package co.rcprdn.lodgyserver.repository;

import co.rcprdn.lodgyserver.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

  List<Expense> findByTripId(Long tripId);

  List<Expense> findByTripIdAndUserId(Long tripId, Long userId);

  List<Expense> findByUserId(Long userId);
}
