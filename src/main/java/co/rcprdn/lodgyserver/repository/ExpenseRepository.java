package co.rcprdn.lodgyserver.repository;

import co.rcprdn.lodgyserver.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
  List<Expense> findAllByTripId(Long tripId);

  List<Expense> findAllByTripIdAndUserId(Long tripId, Long userId);

  List<Expense> findAllByUserId(Long userId);
}