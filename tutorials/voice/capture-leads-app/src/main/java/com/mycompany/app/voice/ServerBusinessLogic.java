package com.mycompany.app.voice;

import com.sinch.sdk.domains.voice.models.DestinationSip;
import com.sinch.sdk.domains.voice.models.TransportType;
import com.sinch.sdk.domains.voice.models.svaml.ActionConnectPstn;
import com.sinch.sdk.domains.voice.models.svaml.ActionConnectSip;
import com.sinch.sdk.domains.voice.models.svaml.ActionHangUp;
import com.sinch.sdk.domains.voice.models.svaml.ActionRunMenu;
import com.sinch.sdk.domains.voice.models.svaml.InstructionSay;
import com.sinch.sdk.domains.voice.models.svaml.Menu;
import com.sinch.sdk.domains.voice.models.svaml.MenuOption;
import com.sinch.sdk.domains.voice.models.svaml.MenuOptionAction;
import com.sinch.sdk.domains.voice.models.svaml.MenuOptionActionType;
import com.sinch.sdk.domains.voice.models.svaml.SVAMLControl;
import com.sinch.sdk.domains.voice.models.webhooks.AmdAnswerStatusType;
import com.sinch.sdk.domains.voice.models.webhooks.AnsweredCallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.CallEvent;
import com.sinch.sdk.domains.voice.models.webhooks.PromptInputEvent;
import com.sinch.sdk.models.DualToneMultiFrequency;
import com.sinch.sdk.models.E164PhoneNumber;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("VoiceServerBusinessLogic")
public class ServerBusinessLogic {

  private static final Logger LOGGER = Logger.getLogger(ServerBusinessLogic.class.getName());

  public SVAMLControl processCallEvent(CallEvent event) {

    LOGGER.info("Received event:" + event);

    if (event instanceof AnsweredCallEvent) {
      return parseAnsweredCallEvent(((AnsweredCallEvent) event));
    }
    if (event instanceof PromptInputEvent) {
      return parsePromptInputEvent(((PromptInputEvent) event));
    } else {
      throw new IllegalStateException("Unexpected value: " + event);
    }
  }

  public SVAMLControl parseAnsweredCallEvent(AnsweredCallEvent event) {

    var amdResult = event.getAmd();

    if (amdResult.getStatus() == AmdAnswerStatusType.MACHINE) {
      return machineResponse();
    }
    if (amdResult.getStatus() == AmdAnswerStatusType.HUMAN) {
      return humanResponse();
    } else {
      throw new IllegalStateException("Unexpected value: " + event);
    }
  }

  public SVAMLControl parsePromptInputEvent(PromptInputEvent event) {
    var menuResult = event.getMenuResult();

    if (menuResult.getValue() == "sip") {
      return sipResponse();
    }
    if (menuResult.getValue() == "non-sip") {
      return nonSipResponse();
    } else {
      return defaultResponse();
    }
  }

  SVAMLControl sipResponse() {

    return SVAMLControl.builder()
        .setAction(
            ActionConnectSip.builder()
                .setDestination(DestinationSip.valueOf("YOUR_sip_address"))
                .setCli("YOUR_sinch_number")
                .setTransport(TransportType.TLS)
                .build())
        .setInstructions(
            Collections.singletonList(
                InstructionSay.builder()
                    .setText(
                        "Thanks for agreeing to speak to one of our sales reps! We'll now connect"
                            + " your call.")
                    .build()))
        .build();
  }

  SVAMLControl nonSipResponse() {

    return SVAMLControl.builder()
        .setAction(
            ActionConnectPstn.builder()
                .setNumber(E164PhoneNumber.valueOf("YOUR_phone_number"))
                .setCli("YOUR_sinch_number")
                .build())
        .setInstructions(
            Collections.singletonList(
                InstructionSay.builder()
                    .setText(
                        "Thanks for agreeing to speak to one of our sales reps! We'll now connect"
                            + " your call.")
                    .build()))
        .build();
  }

  SVAMLControl defaultResponse() {

    return SVAMLControl.builder()
        .setAction(ActionHangUp.builder().build())
        .setInstructions(
            Collections.singletonList(
                InstructionSay.builder()
                    .setText("Thank you for trying our tutorial! This call will now end.")
                    .build()))
        .build();
  }

  SVAMLControl humanResponse() {

    var option1 =
        MenuOption.builder()
            .setDtfm(DualToneMultiFrequency.valueOf("1"))
            .setAction(MenuOptionAction.from(MenuOptionActionType.RETURN, "sip"))
            .build();

    var option2 =
        MenuOption.builder()
            .setDtfm(DualToneMultiFrequency.valueOf("2"))
            .setAction(MenuOptionAction.from(MenuOptionActionType.RETURN, "non-sip"))
            .build();

    Collection<MenuOption> options = Collections.emptyList();

    options.add(option1);
    options.add(option2);

    return SVAMLControl.builder()
        .setAction(
            ActionRunMenu.builder()
                .setBarge(false)
                .setMenus(
                    Collections.singletonList(
                        Menu.builder()
                            .setId("main")
                            .setMainPrompt(
                                "Hi, you awesome person! Press 1 if you have performed this"
                                    + " tutorial using a sip infrastructure. Press 2 if you have"
                                    + " not used a sip infrastructure. Press any other digit to end"
                                    + " this call.")
                            .setRepeatPrompt(
                                "Again, simply press 1 if you have used sip, press 2 if you have"
                                    + " not, or press any other digit to end this call.")
                            .setRepeats(2)
                            .setOptions(options)
                            .build()))
                .build())
        .build();
  }

  SVAMLControl machineResponse() {

    return SVAMLControl.builder()
        .setAction(ActionHangUp.builder().build())
        .setInstructions(
            Collections.singletonList(
                InstructionSay.builder()
                    .setText(
                        "Hi there! We tried to reach you to speak with you about our awesome"
                            + " products. We will try again later. Bye!")
                    .build()))
        .build();
  }
}
