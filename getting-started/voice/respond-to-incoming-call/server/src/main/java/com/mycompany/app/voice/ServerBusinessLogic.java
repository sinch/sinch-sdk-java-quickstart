package com.mycompany.app.voice;

import com.sinch.sdk.domains.voice.models.svaml.ActionHangUp;
import com.sinch.sdk.domains.voice.models.svaml.InstructionSay;
import com.sinch.sdk.domains.voice.models.svaml.SVAMLControl;
import com.sinch.sdk.domains.voice.models.webhooks.DisconnectCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.IncomingCallEvent;
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

  public void disconnect(DisconnectCallEvent event) {

    LOGGER.info("Handle event: " + event);
  }
}
