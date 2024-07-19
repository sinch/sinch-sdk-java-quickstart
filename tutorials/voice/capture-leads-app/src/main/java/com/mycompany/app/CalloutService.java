package com.mycompany.app;

import java.util.Scanner;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sinch.sdk.core.utils.StringUtil;
import com.sinch.sdk.domains.voice.VoiceService;
import com.sinch.sdk.domains.voice.models.requests.CalloutRequestParametersCustom;
import com.sinch.sdk.domains.voice.models.svaml.ActionConnectPstn;
import com.sinch.sdk.domains.voice.models.svaml.AnsweringMachineDetection;
import com.sinch.sdk.domains.voice.models.svaml.SVAMLControl;
import com.sinch.sdk.models.E164PhoneNumber;

@Service
public class CalloutService {

    private static final Logger LOGGER = Logger.getLogger(CalloutService.class.getName());
    private final VoiceService voiceService;

    @Autowired
    public CalloutService(VoiceService voiceService) {
        this.voiceService = voiceService;
    }

    public void makeCallout() {
        var response = voiceService.callouts().custom(CalloutRequestParametersCustom.builder()
                .setIce(SVAMLControl.builder()
                    .setAction(ActionConnectPstn.builder()
                        .setNumber(E164PhoneNumber.valueOf(prompt("Enter the phone number you want to call: ")))
                        .setCli("YOUR_sinch_number")
                        .setAnsweringMachineDetection(AnsweringMachineDetection.builder()
                            .setEnabled(true)
                            .build())
                        .build())
                    .build())
                .build());

        echo("Callout response: '%s'", response);
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
        return input.trim();
    }

    private void echo(String text, Object... args) {
        System.out.println("  " + String.format(text, args));
    }
}
