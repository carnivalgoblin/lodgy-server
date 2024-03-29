package co.rcprdn.lodgyserver.helpers;

import co.rcprdn.lodgyserver.entity.*;
import co.rcprdn.lodgyserver.enums.ERole;
import co.rcprdn.lodgyserver.repository.*;
import co.rcprdn.lodgyserver.service.TripService;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static co.rcprdn.lodgyserver.enums.ERole.*;

@Component
@AllArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final TripRepository tripRepository;
  private final TripService tripService;
  private final ExpenseRepository expenseRepository;
  private final PasswordEncoder encoder;

  private void createUserWithRoles(String username, String email, String password, ERole... roles) {
    User user = new User();
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(encoder.encode(password));

    Set<Role> userRoles = new HashSet<>();
    for (ERole role : roles) {
      userRoles.add(roleRepository.findByName(role).orElseThrow(() -> new RuntimeException("Error: Role is not found.")));
    }
    user.setRoles(userRoles);

    userRepository.save(user);
  }

  @Override
  public void run(String... args) {

    for (ERole role : ERole.values()) {
      roleRepository.save(new Role(role));
    }

    createUserWithRoles("rico", "rico.prodan@mail.com", "12345678", ROLE_ADMIN, ROLE_MODERATOR, ROLE_USER);
    createUserWithRoles("mod", "mod@mail.com", "12345678", ROLE_MODERATOR, ROLE_USER);
    createUserWithRoles("test", "test@mail.com", "12345678", ROLE_USER);

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    try {
      tripRepository.save(new Trip(null, "Amsterdam", formatter.parse("2021-05-01"), formatter.parse("2021-05-10"), "A beautiful city known for its canals and historic architecture.", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
      tripRepository.save(new Trip(null, "Berlin", formatter.parse("2021-06-01"), formatter.parse("2021-06-10"), "The capital and largest city of Germany, famous for its rich history.", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
      tripRepository.save(new Trip(null, "Paris", formatter.parse("2021-07-01"), formatter.parse("2021-07-10"), "The City of Love, known for its art, fashion, and culture.", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
      tripRepository.save(new Trip(null, "London", formatter.parse("2023-12-01"), formatter.parse("2023-12-31"), "The capital of the United Kingdom, a global city with a diverse history.", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
      tripRepository.save(new Trip(null, "Rome", formatter.parse("2024-01-01"), formatter.parse("2024-12-10"), "The Eternal City, known for its ancient history and architecture.", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
      tripRepository.save(new Trip(null, "Madrid", formatter.parse("2025-02-01"), formatter.parse("2025-02-10"), "The capital and largest city of Spain, famous for its lively atmosphere.", new ArrayList<>(), new ArrayList<>(), new ArrayList<>()));
    } catch (ParseException e) {
      e.printStackTrace();
    }

    Expense expense1 = new Expense(null, "Flight", 100.0, userRepository.findById(1L).orElse(null), tripRepository.findById(1L).orElse(null));
    Expense expense2 = new Expense(null, "Hotel", 200.0, userRepository.findById(2L).orElse(null), tripRepository.findById(1L).orElse(null));
    Expense expense3 = new Expense(null, "Food", 300.0, userRepository.findById(1L).orElse(null), tripRepository.findById(2L).orElse(null));
    Expense expense4 = new Expense(null, "Souvenirs", 400.0, userRepository.findById(2L).orElse(null), tripRepository.findById(2L).orElse(null));
    Expense expense5 = new Expense(null, "Flight", 500.0, userRepository.findById(1L).orElse(null), tripRepository.findById(3L).orElse(null));
    Expense expense6 = new Expense(null, "Hotel", 600.0, userRepository.findById(2L).orElse(null), tripRepository.findById(3L).orElse(null));

    expenseRepository.save(expense1);
    expenseRepository.save(expense2);
    expenseRepository.save(expense3);
    expenseRepository.save(expense4);
    expenseRepository.save(expense5);
    expenseRepository.save(expense6);

    List<User> users = userRepository.findAll();
    List<Trip> trips = tripRepository.findAll();
    Random random = new Random();

    for (User user : users) {
      Set<Trip> addedTrips = new HashSet<>();
      while (addedTrips.size() < 3) {
        Trip trip = trips.get(random.nextInt(trips.size()));
        if (!tripService.isUserInTrip(trip.getId(), user.getId())) {
          tripService.addUserToTrip(trip.getId(), user.getId(), random.nextInt(10) + 1);
          addedTrips.add(trip);
        }
      }
    }

  }
}
