package co.rcprdn.lodgyserver.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserAlreadyInTripException extends RuntimeException {

  public UserAlreadyInTripException(String message) {
    super(message);
  }
}
