package co.rcprdn.lodgyserver.repository;

import co.rcprdn.lodgyserver.entity.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

  List<Trip> findByUserId(Long userId);
}
