package com.mycompany.app.voice;

import com.sinch.sdk.domains.voice.models.DestinationSip;
import com.sinch.sdk.domains.voice.models.TransportType;
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
import com.sinch.sdk.domains.voice.models.webhooks.PromptInputEvent;
import com.sinch.sdk.models.DualToneMultiFrequency;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("VoiceServerBusinessLogic")
public class ServerBusinessLogic {

  private final String SIP_MENU = "sip";

  private final String NON_SIP_MENU = "non-sip";

  @Value("${sinch-number}")
  String sinchNumber;

  @Value("${sip-address}")
  String sipAddress;

  public SVAMLControl answeredCallEvent(AnsweredCallEvent event) {

    var amdResult = event.getAmd();

    if (amdResult.getStatus() == AmdAnswerStatusType.MACHINE) {
      return machineResponse();
    }
    if (amdResult.getStatus() == AmdAnswerStatusType.HUMAN) {
      return humanResponse();
    }
    throw new IllegalStateException("Unexpected value: " + event);
  }

  public SVAMLControl promptInputEvent(PromptInputEvent event) {
    var menuResult = event.getMenuResult();

    if (SIP_MENU.equals(menuResult.getValue())) {
      return sipResponse();
    }
    if (NON_SIP_MENU.equals(menuResult.getValue())) {
      return nonSipResponse();
    }
    return defaultResponse();
  }

  private SVAMLControl sipResponse() {

    String instruction =
        "Thanks for agreeing to speak to one of our sales reps! We'll now connect your call.";

    return SVAMLControl.builder()
        .setAction(
            ActionConnectSip.builder()
                .setDestination(DestinationSip.valueOf(sipAddress))
                .setCli(sinchNumber)
                .setTransport(TransportType.TLS)
                .build())
        .setInstructions(
            Collections.singletonList(InstructionSay.builder().setText(instruction).build()))
        .build();
  }

  private SVAMLControl nonSipResponse() {

    String instruction =
        "Thank you for choosing to speak to one of our sales reps! If this were in production, at"
            + " this point you would be connected to a sales rep on your sip network. Since you do"
            + " not, you have now completed this tutorial. We hope you had fun and learned"
            + " something new. Be sure to keep visiting https://developers.sinch.com for more great"
            + " tutorials.";

    return SVAMLControl.builder()
        .setAction(ActionHangUp.builder().build())
        .setInstructions(
            Collections.singletonList(InstructionSay.builder().setText(instruction).build()))
        .build();
  }

  private SVAMLControl defaultResponse() {

    String instruction = "Thank you for trying our tutorial! This call will now end.";

    return SVAMLControl.builder()
        .setAction(ActionHangUp.builder().build())
        .setInstructions(
            Collections.singletonList(InstructionSay.builder().setText(instruction).build()))
        .build();
  }

  private SVAMLControl humanResponse() {

    String SIP_MENU_OPTION = "1";
    String NON_SIP_MENU_OPTION = "2";

    String mainPrompt =
        String.format(
            "#tts[Hi, you awesome person! Press '%s' if you have performed this tutorial using a"
                + " sip infrastructure. Press '%s' if you have not used a sip infrastructure. Press"
                + " any other digit to end this call.]",
            SIP_MENU_OPTION, NON_SIP_MENU_OPTION);

    String repeatPrompt =
        String.format(
            "#tts[Again, simply press '%s' if you have used sip, press '%s' if you have not, or"
                + " press any other digit to end this call.]",
            SIP_MENU_OPTION, NON_SIP_MENU_OPTION);

    MenuOption option1 =
        MenuOption.builder()
            .setDtfm(DualToneMultiFrequency.valueOf(SIP_MENU_OPTION))
            .setAction(MenuOptionAction.from(MenuOptionActionType.RETURN, SIP_MENU))
            .build();

    MenuOption option2 =
        MenuOption.builder()
            .setDtfm(DualToneMultiFrequency.valueOf(NON_SIP_MENU_OPTION))
            .setAction(MenuOptionAction.from(MenuOptionActionType.RETURN, NON_SIP_MENU))
            .build();

    Collection<MenuOption> options = Arrays.asList(option1, option2);

    return SVAMLControl.builder()
        .setAction(
            ActionRunMenu.builder()
                .setBarge(false)
                .setMenus(
                    Collections.singletonList(
                        Menu.builder()
                            .setId("main")
                            .setMainPrompt(mainPrompt)
                            .setRepeatPrompt(repeatPrompt)
                            .setRepeats(2)
                            .setOptions(options)
                            .build()))
                .build())
        .build();
  }

  private SVAMLControl machineResponse() {

    String instruction =
        "Hi there! We tried to reach you to speak with you about our awesome products. We will try"
            + " again later. Bye!";

    return SVAMLControl.builder()
        .setAction(ActionHangUp.builder().build())
        .setInstructions(
            Collections.singletonList(InstructionSay.builder().setText(instruction).build()))
        .build();
  }
}
