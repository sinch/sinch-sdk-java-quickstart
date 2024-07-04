package com.mycompany.app.sms;

import com.sinch.sdk.domains.sms.models.DeliveryReportBatch;
import com.sinch.sdk.domains.sms.models.DeliveryReportRecipient;
import com.sinch.sdk.domains.sms.models.InboundBinary;
import com.sinch.sdk.domains.sms.models.InboundText;
import com.sinch.sdk.domains.sms.models.webhooks.WebhooksEvent;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

@Component("SMSWebhooksBusinessLogic")
public class WebhooksBusinessLogic {

  private static final Logger LOGGER = Logger.getLogger(WebhooksBusinessLogic.class.getName());

  public void processInboundEvent(InboundText event) {
    trace(event);
  }

  public void processInboundEvent(InboundBinary event) {
    trace(event);
  }

  public void processDeliveryReportEvent(DeliveryReportRecipient event) {
    trace(event);
  }

  public void processDeliveryReportEvent(DeliveryReportBatch event) {
    trace(event);
  }

  private void trace(WebhooksEvent event) {
    LOGGER.info("Handle event :" + event);
  }
}
