/*package co.rcprdn.lodgyserver.repository;

//import co.rcprdn.lodgyserver.entity.UserTripExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTripExpenseRepository extends JpaRepository<UserTripExpense, Long> {

  List<UserTripExpense> findByTripId(Long tripId);

  List<UserTripExpense> findByUserIdAndTripId(Long userId, Long tripId);
}*/
