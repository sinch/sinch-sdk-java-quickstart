package com.mycompany.app.sms;

import com.sinch.sdk.SinchClient;
import com.sinch.sdk.domains.sms.WebHooksService;
import com.sinch.sdk.domains.sms.models.DeliveryReportBatch;
import com.sinch.sdk.domains.sms.models.DeliveryReportRecipient;
import com.sinch.sdk.domains.sms.models.InboundBinary;
import com.sinch.sdk.domains.sms.models.InboundText;
import com.sinch.sdk.domains.sms.models.webhooks.WebhooksEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("SMS")
public class Controller {

  private final SinchClient sinchClient;
  private final WebhooksBusinessLogic webhooksBusinessLogic;

  @Autowired
  public Controller(SinchClient sinchClient, WebhooksBusinessLogic webhooksBusinessLogic) {
    this.sinchClient = sinchClient;
    this.webhooksBusinessLogic = webhooksBusinessLogic;
  }

  @PostMapping(
      value = "/SmsEvent",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public void smsDeliveryEvent(@RequestBody String body) {

    WebHooksService webhooks = sinchClient.sms().webHooks();

    // decode the request payload
    WebhooksEvent event = webhooks.parse(body);

    // let business layer process the request
    switch (event) {
      case InboundBinary e -> webhooksBusinessLogic.processInboundEvent(e);
      case InboundText e -> webhooksBusinessLogic.processInboundEvent(e);
      case DeliveryReportRecipient e -> webhooksBusinessLogic.processDeliveryReportEvent(e);
      case DeliveryReportBatch e -> webhooksBusinessLogic.processDeliveryReportEvent(e);
      default -> throw new IllegalStateException("Unexpected value: " + event);
    }
  }
}
