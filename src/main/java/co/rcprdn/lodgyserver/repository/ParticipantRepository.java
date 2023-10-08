package co.rcprdn.lodgyserver.repository;

import co.rcprdn.lodgyserver.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

  List<Participant> getParticipantsByTripId(Long tripId);

  Participant findByUsername(String username);
}
