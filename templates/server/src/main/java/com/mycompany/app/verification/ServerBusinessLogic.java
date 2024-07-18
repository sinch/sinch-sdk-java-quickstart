package com.mycompany.app.verification;

import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationRequestEvent;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationRequestEventResponse;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationResultEvent;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("VerificationServerBusinessLogic")
public class ServerBusinessLogic {

  private static final Logger LOGGER = Logger.getLogger(ServerBusinessLogic.class.getName());

  public VerificationRequestEventResponse verificationEvent(VerificationRequestEvent event) {

    LOGGER.info("Handle event :" + event);

    // add your logic here according to SMS, FlashCall, PhoneCall, ... verification
    return null;
  }

  public void verificationEvent(VerificationResultEvent event) {

    LOGGER.info("Handle event: " + event);
  }
}
