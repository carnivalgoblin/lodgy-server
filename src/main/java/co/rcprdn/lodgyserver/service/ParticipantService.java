package co.rcprdn.lodgyserver.service;

import co.rcprdn.lodgyserver.entity.Participant;
import co.rcprdn.lodgyserver.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ParticipantService {

  private final ParticipantRepository participantRepository;

  public List<Participant> getAllParticipants() {
    return participantRepository.findAll();
  }

  public Participant getParticipantById(Long id) {
    return participantRepository.findById(id).orElse(null);
  }

  public Participant createParticipant(Participant participant) {
    return participantRepository.save(participant);
  }

  public void deleteParticipant(Long id) {
    participantRepository.deleteById(id);
  }

  public Participant updateParticipant(Participant participant) {
    Participant existingParticipant = participantRepository.findById(participant.getId()).orElse(null);
    existingParticipant.setName(participant.getName());
    existingParticipant.setUsername(participant.getUsername());
    existingParticipant.setPassword(participant.getPassword());
    return participantRepository.save(existingParticipant);
  }

  public List<Participant> getParticipantsByTripId(Long tripId) {
    return participantRepository.getParticipantsByTripId(tripId);
  }

  public Participant login(Participant participant) {
    Participant participantFound = participantRepository.findByUsername(participant.getUsername());
    if (participantFound != null && participantFound.getPassword().equals(participant.getPassword())) {
      return participantFound;
    }
    return null;
  }

  public Participant register(Participant participant) {
    Participant participantFound = participantRepository.findByUsername(participant.getUsername());
    if (participantFound == null) {
      return participantRepository.save(participant);
    }
    return null;
  }

  public boolean authenticate(String username, String password) {
    Participant participant = new Participant();
    participant.setUsername(username);
    participant.setPassword(password);
    Participant participantFound = participantRepository.findByUsername(participant.getUsername());
    return participantFound != null && participantFound.getPassword().equals(participant.getPassword());
  }
}
