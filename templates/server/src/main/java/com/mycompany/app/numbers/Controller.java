package com.mycompany.app.numbers;

import com.sinch.sdk.SinchClient;
import com.sinch.sdk.domains.numbers.api.v1.WebHooksService;
import com.sinch.sdk.domains.numbers.models.v1.webhooks.NumberEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("Numbers")
public class Controller {

  private final SinchClient sinchClient;
  private final ServerBusinessLogic webhooksBusinessLogic;

  @Autowired
  public Controller(SinchClient sinchClient, ServerBusinessLogic webhooksBusinessLogic) {
    this.sinchClient = sinchClient;
    this.webhooksBusinessLogic = webhooksBusinessLogic;
  }

  @PostMapping(
      value = "/NumbersEvent",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> NumbersEvent(@RequestBody String body) {

    WebHooksService webhooks = sinchClient.numbers().v1().webhooks();

    // decode the request payload
    NumberEvent event = webhooks.parseEvent(body);

    // let business layer process the request
    webhooksBusinessLogic.numbersEvent(event);

    return ResponseEntity.ok().build();
  }
}
