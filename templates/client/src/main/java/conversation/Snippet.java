package conversation;

import com.sinch.sdk.domains.conversation.api.v1.ConversationService;
import java.util.logging.Logger;

public class Snippet {

  private static final Logger LOGGER = Logger.getLogger(Snippet.class.getName());

  static void execute(ConversationService conversationService) {

    LOGGER.info("Snippet execution");
  }
}
