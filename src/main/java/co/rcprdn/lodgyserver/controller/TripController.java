package co.rcprdn.lodgyserver.controller;

import co.rcprdn.lodgyserver.entity.Trip;
import co.rcprdn.lodgyserver.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/trips")
public class TripController {

  private final TripService tripService;

  @GetMapping
  public List<Trip> getAllTrips() {
    return tripService.getAllTrips();
  }

  @GetMapping("/{id}")
  public Trip getTripById(@PathVariable Long id) {
    return tripService.getTripById(id);
  }

  @PostMapping
  public Trip createTrip(@RequestBody Trip trip) {
    return tripService.createTrip(trip);
  }

  @DeleteMapping("/{id}")
  public void deleteTrip(@PathVariable Long id) {
    tripService.deleteTrip(id);
  }

}
