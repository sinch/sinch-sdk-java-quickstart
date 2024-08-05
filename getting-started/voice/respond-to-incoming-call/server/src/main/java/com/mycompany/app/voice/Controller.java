package com.mycompany.app.voice;

import com.sinch.sdk.SinchClient;
import com.sinch.sdk.domains.voice.WebHooksService;
import com.sinch.sdk.domains.voice.models.webhooks.AnsweredCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.DisconnectCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.IncomingCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.NotifyEvent;
import com.sinch.sdk.domains.voice.models.webhooks.PromptInputEvent;
import java.util.Map;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController("Voice")
public class Controller {

  private final SinchClient sinchClient;
  private final ServerBusinessLogic webhooksBusinessLogic;
  private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());

  @Autowired
  public Controller(SinchClient sinchClient, ServerBusinessLogic webhooksBusinessLogic) {
    this.sinchClient = sinchClient;
    this.webhooksBusinessLogic = webhooksBusinessLogic;
  }

  @PostMapping(
      value = "/VoiceEvent",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> VoiceEvent(
      @RequestHeader Map<String, String> headers, @RequestBody String body) {

    WebHooksService webhooks = sinchClient.voice().webhooks();

    // ensure valid authentication to handle request
    var validAuth =
        webhooks.validateAuthenticatedRequest(
            // The HTTP verb this controller is managing
            "POST",
            // The URI this controller is managing
            "/VoiceEvent",
            // request headers
            headers,
            // request payload body
            body);

    // token validation failed
    if (!validAuth) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
    }

    // decode the payload request
    var event = webhooks.unserializeWebhooksEvent(body);

    // let business layer process the request
    var response =
        switch (event) {
          case IncomingCallEvent e -> webhooksBusinessLogic.incoming(e);
          case AnsweredCallEvent e -> webhooksBusinessLogic.answered(e);
          case DisconnectCallEvent e -> {
            webhooksBusinessLogic.disconnect(e);
            yield null;
          }
          case PromptInputEvent e -> {
            webhooksBusinessLogic.prompt(e);
            yield null;
          }
          case NotifyEvent e -> {
            webhooksBusinessLogic.notify(e);
            yield null;
          }
          default -> throw new IllegalStateException("Unexpected value: " + event);
        };

    String serializedResponse = "";
    if (null != response) {
      serializedResponse = webhooks.serializeWebhooksResponse(response);
    }

    LOGGER.finest("JSON response: " + serializedResponse);

    return ResponseEntity.ok().body(serializedResponse);
  }
}
