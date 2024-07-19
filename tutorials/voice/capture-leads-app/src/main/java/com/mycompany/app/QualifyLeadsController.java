package com.mycompany.app;

import com.sinch.sdk.domains.voice.VoiceService;
import com.sinch.sdk.domains.voice.models.webhooks.CallEvent;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QualifyLeadsController {

  private final VoiceService voiceService;
  private final QualifyLeadsService service;
  
  @Autowired
  public QualifyLeadsController(VoiceService voiceService, QualifyLeadsService service) {
    this.voiceService = voiceService;
    this.service = service;
  }

  @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody
  public String callEvent(@RequestBody String body) {

    // decode the request payload
    var event = voiceService.webhooks().unserializeWebhooksEvent(body);

    // let business layer process the request
    if (Objects.requireNonNull(event) instanceof CallEvent e) {
      return voiceService.webhooks().serializeWebhooksResponse(service.processCallEvent(e));
    } else {
      throw new IllegalStateException("Unexpected value: " + event);
    }
  }

}
