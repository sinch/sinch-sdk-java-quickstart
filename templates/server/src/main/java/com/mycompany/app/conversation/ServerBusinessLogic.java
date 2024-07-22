package com.mycompany.app.conversation;

import com.sinch.sdk.domains.conversation.models.v1.webhooks.events.ConversationWebhookEvent;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("ConversationServerBusinessLogic")
public class ServerBusinessLogic {

  private static final Logger LOGGER = Logger.getLogger(ServerBusinessLogic.class.getName());

  public void conversationEvent(ConversationWebhookEvent event) {

    LOGGER.info("Handle event: " + event);
  }
}
