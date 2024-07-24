package com.mycompany.app.voice;

import com.sinch.sdk.domains.voice.VoiceService;
import com.sinch.sdk.domains.voice.WebHooksService;
import com.sinch.sdk.domains.voice.models.webhooks.CallEvent;
import java.util.Map;
import java.util.Objects;
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

  private final WebHooksService webhooks;
  private final ServerBusinessLogic webhooksBusinessLogic;

  @Autowired
  public Controller(VoiceService voiceService, ServerBusinessLogic webhooksBusinessLogic) {
    this.webhooks = voiceService.webhooks();
    this.webhooksBusinessLogic = webhooksBusinessLogic;
  }

  @PostMapping(
      value = "/VoiceEvent",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> VoiceEvent(
      @RequestHeader Map<String, String> headers, @RequestBody String body) {

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

    // decode the request payload
    var event = webhooks.unserializeWebhooksEvent(body);

    // let business layer process the request
    if (Objects.requireNonNull(event) instanceof CallEvent e) {
      return ResponseEntity.ok()
          .body(webhooks.serializeWebhooksResponse(webhooksBusinessLogic.processCallEvent(e)));
    } else {
      throw new IllegalStateException("Unexpected value: " + event);
    }
  }
}
