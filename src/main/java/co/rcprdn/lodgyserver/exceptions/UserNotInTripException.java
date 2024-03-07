package co.rcprdn.lodgyserver.exceptions;

public class UserNotInTripException extends RuntimeException {

  public UserNotInTripException(String message) {
    super(message);
  }
}
