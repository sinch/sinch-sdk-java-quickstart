package com.mycompany.app.numbers;

import com.sinch.sdk.domains.numbers.models.v1.webhooks.NumberEvent;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("NumbersWebhooksBusinessLogic")
public class WebhooksBusinessLogic {

  private static final Logger LOGGER = Logger.getLogger(WebhooksBusinessLogic.class.getName());

  public void numbersEvent(NumberEvent event) {

    LOGGER.info("Handle event :" + event);
  }
}
