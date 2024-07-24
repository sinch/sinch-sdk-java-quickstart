package com.mycompany.app.voice;

import com.sinch.sdk.domains.voice.models.DestinationSip;
import com.sinch.sdk.domains.voice.models.TransportType;
import com.sinch.sdk.domains.voice.models.svaml.ActionConnectPstn;
import com.sinch.sdk.domains.voice.models.svaml.ActionConnectSip;
import com.sinch.sdk.domains.voice.models.svaml.ActionHangUp;
import com.sinch.sdk.domains.voice.models.svaml.ActionRunMenu;
import com.sinch.sdk.domains.voice.models.svaml.Instruction;
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
import com.sinch.sdk.models.E164PhoneNumber;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("VoiceServerBusinessLogic")
public class ServerBusinessLogic {

  private static final Logger LOGGER = Logger.getLogger(ServerBusinessLogic.class.getName());

  private final String SIP_MENU = "sip";

  private final String NON_SIP_MENU = "non-sip";

  private final String SIP_ADDRESS = "YOUR_sip_address";
  private final String SINCH_NUMBER = "YOUR_sinch_number";
  private final String PHONE_NUMBER = "YOUR_phone_number";

  private final List<Instruction> responseInstructions =
      Collections.singletonList(
          InstructionSay.builder()
              .setText(
                  "Thanks for agreeing to speak to one of our sales reps! We'll now connect"
                      + " your call.")
              .build());

  public SVAMLControl answeredCallEvent(AnsweredCallEvent event) {

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

  public SVAMLControl promptInputEvent(PromptInputEvent event) {
    var menuResult = event.getMenuResult();

    if (SIP_MENU.equals(menuResult.getValue())) {
      return sipResponse();
    }
    if (NON_SIP_MENU.equals(menuResult.getValue())) {
      return nonSipResponse();
    } else {
      return defaultResponse();
    }
  }

  private SVAMLControl sipResponse() {

    return SVAMLControl.builder()
        .setAction(
            ActionConnectSip.builder()
                .setDestination(DestinationSip.valueOf(SIP_ADDRESS))
                .setCli(SINCH_NUMBER)
                .setTransport(TransportType.TLS)
                .build())
        .setInstructions(responseInstructions)
        .build();
  }

  private SVAMLControl nonSipResponse() {

    return SVAMLControl.builder()
        .setAction(
            ActionConnectPstn.builder()
                .setNumber(E164PhoneNumber.valueOf(PHONE_NUMBER))
                .setCli(SINCH_NUMBER)
                .build())
        .setInstructions(responseInstructions)
        .build();
  }

  private SVAMLControl defaultResponse() {

    List<Instruction> defaultResponseInstructions =
        Collections.singletonList(
            InstructionSay.builder()
                .setText("Thank you for trying our tutorial! This call will now end.")
                .build());

    return SVAMLControl.builder()
        .setAction(ActionHangUp.builder().build())
        .setInstructions(defaultResponseInstructions)
        .build();
  }

  private SVAMLControl humanResponse() {

    String SIP_MENU_OPTION = "1";
    String NON_SIP_MENU_OPTION = "2";

    String mainPrompt =
        String.format(
            "Hi, you awesome person! Press '%s' if you have performed this tutorial using a sip"
                + " infrastructure. Press '%s' if you have not used a sip infrastructure. Press any"
                + " other digit to end this call.",
            SIP_MENU_OPTION, NON_SIP_MENU_OPTION);

    String repeatPrompt =
        String.format(
            "Again, simply press '%s' if you have used sip, press '%s' if you have not, or press"
                + " any other digit to end this call.",
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

    List<Instruction> instructions =
        Collections.singletonList(
            InstructionSay.builder()
                .setText(
                    "Hi there! We tried to reach you to speak with you about our awesome products."
                        + " We will try again later. Bye!")
                .build());

    return SVAMLControl.builder()
        .setAction(ActionHangUp.builder().build())
        .setInstructions(instructions)
        .build();
  }
}
