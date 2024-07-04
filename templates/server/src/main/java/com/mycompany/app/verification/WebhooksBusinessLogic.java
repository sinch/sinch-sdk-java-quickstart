package com.mycompany.app.verification;

import com.sinch.sdk.core.exceptions.ApiException;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationEventResponseAction;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationRequestEvent;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationRequestEvent.MethodEnum;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationRequestEventResponse;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationRequestEventResponseFlashCall;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationRequestEventResponsePhoneCall;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationRequestEventResponseSms;
import com.sinch.sdk.domains.verification.models.v1.webhooks.VerificationResultEvent;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("VerificationWebhooksBusinessLogic")
public class WebhooksBusinessLogic {

  private static final Logger LOGGER = Logger.getLogger(WebhooksBusinessLogic.class.getName());

  public WebhooksBusinessLogic() {}

  public VerificationRequestEventResponse verificationEvent(VerificationRequestEvent event) {

    LOGGER.info("Handle event :" + event);

    // add your logic here according to SMS, FlashCall, PhoneCall, ... verification
    return null;
  }

  public void verificationEvent(VerificationResultEvent event) {

    LOGGER.info("Handle event: " + event);
  }
}
