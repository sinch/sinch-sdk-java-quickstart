package com.mycompany.app.voice;

import com.sinch.sdk.core.utils.StringUtil;
import com.sinch.sdk.domains.voice.CalloutsService;
import com.sinch.sdk.domains.voice.VoiceService;
import com.sinch.sdk.domains.voice.models.requests.CalloutRequestParametersCustom;
import com.sinch.sdk.domains.voice.models.svaml.ActionConnectPstn;
import com.sinch.sdk.domains.voice.models.svaml.AnsweringMachineDetection;
import com.sinch.sdk.domains.voice.models.svaml.SVAMLControl;
import com.sinch.sdk.models.E164PhoneNumber;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CLIHelper implements CommandLineRunner {

  @Value("${sinch-number}")
  String sinchNumber;

  private final CalloutsService calloutsService;

  @Autowired
  public CLIHelper(VoiceService voiceService) {
    this.calloutsService = voiceService.callouts();
  }

  @Override
  public void run(String... args) {

    while (true) {
      E164PhoneNumber phoneNumber = promptPhoneNumber();

      proceedCallout(phoneNumber);
    }
  }

  void proceedCallout(E164PhoneNumber phoneNumber) {
    var response =
        calloutsService.custom(
            CalloutRequestParametersCustom.builder()
                .setIce(
                    SVAMLControl.builder()
                        .setAction(
                            ActionConnectPstn.builder()
                                .setNumber(phoneNumber)
                                .setCli(sinchNumber)
                                .setAnsweringMachineDetection(
                                    AnsweringMachineDetection.builder().setEnabled(true).build())
                                .build())
                        .build())
                .build());

    echo("Callout response: '%s'", response);
  }

  private E164PhoneNumber promptPhoneNumber() {
    String input;
    boolean valid;
    do {
      input = prompt("\nEnter the phone number you want to call");
      valid = E164PhoneNumber.validate(input);
      if (!valid) {
        echo("Invalid number '%s'", input);
      }
    } while (!valid);

    return E164PhoneNumber.valueOf(input);
  }

  private String prompt(String prompt) {

    String input = null;
    Scanner scanner = new Scanner(System.in);

    while (StringUtil.isEmpty(input)) {
      System.out.println(prompt + " ([Q] to quit): ");
      input = scanner.nextLine();
    }

    if ("Q".equalsIgnoreCase(input)) {
      System.out.println("Quit application");
      System.exit(0);
    }

    return input.replaceAll(" ", "");
  }

  private void echo(String text, Object... args) {
    System.out.println("  " + String.format(text, args));
  }
}
