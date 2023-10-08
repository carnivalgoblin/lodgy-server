package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.dto.LoginRequest;
import co.rcprdn.lodgyserver.entity.Participant;
import co.rcprdn.lodgyserver.security.JwtTokenUtil;
import co.rcprdn.lodgyserver.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/participants")
public class ParticipantController {

  private final ParticipantService participantService;
  private final JwtTokenUtil jwtTokenUtil;

  @GetMapping
  public List<Participant> getAllParticipants() {
    return participantService.getAllParticipants();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Participant> getParticipantById(@PathVariable Long id) {
    Participant participant = participantService.getParticipantById(id);
    return ResponseEntity.ok(participant);
  }

  @PostMapping
  public Participant createParticipant(@RequestBody Participant participant) {
    return participantService.createParticipant(participant);
  }

  @DeleteMapping("/{id}")
  public void deleteParticipant(@PathVariable Long id) {
    participantService.deleteParticipant(id);
  }

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
    String username = loginRequest.getUsername();
    String password = loginRequest.getPassword();

    Participant participant = new Participant();
    participant.setUsername(username);
    participant.setPassword(password);

    if (authenticate(loginRequest.getUsername(), loginRequest.getPassword())) {
      // Authentifizierung erfolgreich, generieren Sie einen JWT-Token
      String token = jwtTokenUtil.generateToken(loginRequest.getUsername());
      return ResponseEntity.ok(token);
    } else {
      // Authentifizierung fehlgeschlagen, geben Sie einen Fehler zurück
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
  }

  @PostMapping("/register")
  public ResponseEntity<Participant> register(@RequestBody Participant participant) {
    Participant participantFound = participantService.register(participant);
    return ResponseEntity.ok(participantFound);
  }

  // Hilfsmethode, um die Authentifizierung zu implementieren
  private boolean authenticate(String username, String password) {

    boolean participantFound = participantService.authenticate(username, password);

    return participantFound;
  }

}
