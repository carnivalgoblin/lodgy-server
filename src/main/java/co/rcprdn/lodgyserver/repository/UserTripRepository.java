package co.rcprdn.lodgyserver.repository;

import co.rcprdn.lodgyserver.entity.UserTrip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTripRepository extends JpaRepository<UserTrip, Long> {

  List<UserTrip> findAllByTripId(Long tripId);

  List<UserTrip> findAllByUserId(Long userId);

  Optional<UserTrip> findByUserIdAndTripId(Long userId, Long tripId);

  boolean existsByTripIdAndUserId(Long tripId, Long userId);
}
