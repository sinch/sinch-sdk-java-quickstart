package com.mycompany.app.voice;

import com.sinch.sdk.domains.voice.models.svaml.ActionHangUp;
import com.sinch.sdk.domains.voice.models.svaml.InstructionSay;
import com.sinch.sdk.domains.voice.models.svaml.SVAMLControl;
import com.sinch.sdk.domains.voice.models.webhooks.AnsweredCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.DisconnectCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.IncomingCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.NotifyEvent;
import com.sinch.sdk.domains.voice.models.webhooks.PromptInputEvent;
import java.util.Collections;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("VoiceServerBusinessLogic")
public class ServerBusinessLogic {

  private static final Logger LOGGER = Logger.getLogger(ServerBusinessLogic.class.getName());

  public SVAMLControl incoming(IncomingCallEvent event) {

    LOGGER.info("Handle event: " + event);

    String instruction =
        "Thank you for calling your Sinch number. You've just handled an incoming call.";

    return SVAMLControl.builder()
        .setAction(ActionHangUp.builder().build())
        .setInstructions(
            Collections.singletonList(InstructionSay.builder().setText(instruction).build()))
        .build();
  }

  public SVAMLControl answered(AnsweredCallEvent event) {

    LOGGER.info("Handle event: " + event);

    return SVAMLControl.builder().build();
  }

  public void disconnect(DisconnectCallEvent event) {

    LOGGER.info("Handle event: " + event);
  }

  public void prompt(PromptInputEvent event) {

    LOGGER.info("Handle event: " + event);
  }

  public void notify(NotifyEvent event) {

    LOGGER.info("Handle event: " + event);
  }
}
