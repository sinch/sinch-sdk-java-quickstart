package com.mycompany.app;

import com.sinch.sdk.domains.sms.api.v1.SMSService;
import com.sinch.sdk.domains.sms.models.v1.inbounds.TextMessage;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutoSubscribeController {

  private final SMSService smsService;
  private final AutoSubscribeService service;

  @Autowired
  public AutoSubscribeController(SMSService smsService, AutoSubscribeService service) {
    this.smsService = smsService;
    this.service = service;
  }

  @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void smsDeliveryEvent(@RequestBody String body) {

    // decode the request payload
    var event = smsService.webhooks().parseEvent(body);

    // let business layer process the request
    if (Objects.requireNonNull(event) instanceof TextMessage e) {
      service.processInboundEvent(e);
    } else {
      throw new IllegalStateException("Unexpected value: " + event);
    }
  }
}
