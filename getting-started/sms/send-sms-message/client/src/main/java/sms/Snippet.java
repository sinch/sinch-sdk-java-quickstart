package sms;

import com.sinch.sdk.domains.sms.api.v1.BatchesService;
import com.sinch.sdk.domains.sms.api.v1.SMSService;
import com.sinch.sdk.domains.sms.models.v1.batches.request.TextRequest;
import com.sinch.sdk.domains.sms.models.v1.batches.response.BatchResponse;
import java.util.Collections;
import java.util.logging.Logger;

public class Snippet {

  private static final Logger LOGGER = Logger.getLogger(Snippet.class.getName());

  static void execute(SMSService smsService) {

    BatchesService batchesService = smsService.batches();

    String from = "YOUR_sinch_phone_number";
    String recipient = "YOUR_recipient_phone_number";
    String body = "This is a test SMS message using the Sinch Java SDK.";

    LOGGER.info("Sending SMS Text");
    BatchResponse value =
        batchesService.send(
            TextRequest.builder()
                .setTo(Collections.singletonList(recipient))
                .setBody(body)
                .setFrom(from)
                .build());

    LOGGER.info("Response: " + value);
  }
}
