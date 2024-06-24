package verification;

import com.sinch.sdk.domains.verification.api.v1.VerificationService;
import java.util.logging.Logger;

public class Snippet {

  private static final Logger LOGGER = Logger.getLogger(Snippet.class.getName());

  static void execute(VerificationService verificationService) {

    LOGGER.info("Snippet execution");
  }
}
