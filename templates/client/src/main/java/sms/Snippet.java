package sms;

import com.sinch.sdk.domains.sms.*;
import java.util.logging.Logger;

public class Snippet {

  private static final Logger LOGGER = Logger.getLogger(Snippet.class.getName());

  static void execute(SMSService smsService) {

    LOGGER.info("Snippet execution");
  }
}
