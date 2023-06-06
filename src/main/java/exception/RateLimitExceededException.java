package exception;

import java.io.IOException;

public class RateLimitExceededException extends IOException {
  public RateLimitExceededException(String message) {
    super(message);
  }
}
