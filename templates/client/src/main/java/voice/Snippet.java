package voice;

import com.sinch.sdk.domains.voice.VoiceService;
import java.util.logging.Logger;

public class Snippet {

  private static final Logger LOGGER = Logger.getLogger(Snippet.class.getName());

  static void execute(VoiceService voiceService) {

    LOGGER.info("Snippet execution");
  }
}
