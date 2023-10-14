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



}
