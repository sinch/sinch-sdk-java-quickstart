package verification;

import com.sinch.sdk.domains.verification.api.v1.VerificationService;

public class VerificationQuickStart {

  private final VerificationService verificationService;

  public VerificationQuickStart(VerificationService verificationService) {
    this.verificationService = verificationService;

    // replace by your code and business logic
    Snippet.execute(this.verificationService);
  }
}
