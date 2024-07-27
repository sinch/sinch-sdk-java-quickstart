package com.mycompany.app.voice;

import com.sinch.sdk.domains.voice.VoiceService;
import com.sinch.sdk.domains.voice.WebHooksService;
import com.sinch.sdk.domains.voice.models.svaml.SVAMLControl;
import com.sinch.sdk.domains.voice.models.webhooks.AnsweredCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.PromptInputEvent;
import java.util.Map;
import java.util.Optional;
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

    validateRequest(headers, body);

    // decode the request payload
    var event = webhooks.unserializeWebhooksEvent(body);

    Optional<SVAMLControl> response = Optional.empty();

    // let business layer process the request
    if (event instanceof AnsweredCallEvent e) {
      response = Optional.of(webhooksBusinessLogic.answeredCallEvent(e));
    }
    if (event instanceof PromptInputEvent e) {
      response = Optional.of(webhooksBusinessLogic.promptInputEvent(e));
    }

    if (response.isEmpty()) {
      return ResponseEntity.ok().body("");
    }

    String serializedResponse = webhooks.serializeWebhooksResponse(response.get());

    return ResponseEntity.ok().body(serializedResponse);
  }

  void validateRequest(Map<String, String> headers, String body) {

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
  }
}
