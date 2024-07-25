package com.mycompany.app.sms;

import com.sinch.sdk.SinchClient;
import com.sinch.sdk.domains.sms.BatchesService;
import com.sinch.sdk.domains.sms.models.InboundText;
import com.sinch.sdk.domains.sms.models.requests.SendSmsBatchTextRequest;
import com.sinch.sdk.domains.sms.models.webhooks.WebhooksEvent;

import java.util.Collections;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("SMSServerBusinessLogic")
public class ServerBusinessLogic {
  
  private final BatchesService batches; 
  
  public ServerBusinessLogic(SinchClient sinchClient){
    this.batches = sinchClient.sms().batches();
  }

  private static final Logger LOGGER = Logger.getLogger(ServerBusinessLogic.class.getName());

  public void processInboundEvent(InboundText event) {
    trace(event);

    batches.send(SendSmsBatchTextRequest.builder()
        .setTo(Collections.singletonList(event.getFrom()))
        .setBody("You sent: " + event.getBody())
        .setFrom(event.getTo())
        .build());
      }

  private void trace(WebhooksEvent event) {
    LOGGER.info("Handle event: " + event);
  }
}
