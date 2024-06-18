package com.mycompany.app;

import com.sinch.sdk.domains.sms.SMSService;
import com.sinch.sdk.domains.sms.models.Group;
import com.sinch.sdk.domains.sms.models.InboundText;
import com.sinch.sdk.domains.sms.models.requests.GroupUpdateRequestParameters;
import com.sinch.sdk.domains.sms.models.requests.SendSmsBatchTextRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutoSubscribeService {

  private static final Logger LOGGER = Logger.getLogger(AutoSubscribeService.class.getName());

  static final String SUBSCRIBE_ACTION = "SUBSCRIBE";
  static final String STOP_ACTION = "STOP";

  private final SMSService smsService;
  private final Group group;

  @Autowired
  public AutoSubscribeService(SMSService smsService, GroupManager groupManager) {
    this.smsService = smsService;
    this.group = groupManager.getGroup();
  }

  public void processInboundEvent(InboundText event) {

    LOGGER.info("Received event:" + event);

    var from = event.getFrom();
    var to = event.getTo();
    var action = event.getBody().trim();

    var membersList = getMembersList(group);
    var isMemberInGroup = isMemberInGroup(membersList, from);

    String response = processAction(from, to, action, membersList, isMemberInGroup);

    sendResponse(to, from, response);
  }

  private Collection<String> getMembersList(Group group) {
    return smsService.groups().listMembers(group.getId());
  }

  private boolean isMemberInGroup(Collection<String> membersList, String member) {
    return membersList.contains(member);
  }

  private String processAction(
      String from,
      String to,
      String action,
      Collection<String> membersList,
      boolean isMemberInGroup) {

    if (SUBSCRIBE_ACTION.equals(action)) {
      return subscribe(group, isMemberInGroup, to, from);
    } else if (STOP_ACTION.equals(action)) {
      return unsubscribe(group, isMemberInGroup, to, from);
    }

    return unknwownAction(isMemberInGroup, to);
  }

  private String subscribe(
      Group group, boolean isMemberInGroup, String groupPhoneNumber, String member) {

    if (isMemberInGroup) {
      return "You have already subscribed to '%s'. Text \"%s\" to +%s to leave this group."
          .formatted(group.getName(), STOP_ACTION, groupPhoneNumber);
    }

    var request =
        GroupUpdateRequestParameters.builder().setAdd(Collections.singletonList(member)).build();

    smsService.groups().update(group.getId(), request);
    return "Congratulations! You are now subscribed to '%s'. Text \"%s\" to +%s to leave this group."
        .formatted(group.getName(), STOP_ACTION, groupPhoneNumber);
  }

  private String unsubscribe(
      Group group, boolean isMemberInGroup, String groupPhoneNumber, String member) {

    if (!isMemberInGroup) {
      return "You haven't subscribed to '%s' yet. Text \"%s\" to +%s to join this group."
          .formatted(group.getName(), SUBSCRIBE_ACTION, groupPhoneNumber);
    }

    var request =
        GroupUpdateRequestParameters.builder().setRemove(Collections.singletonList(member)).build();

    smsService.groups().update(group.getId(), request);
    return "We're sorry to see you go. You can always rejoin '%s' by texting \"%s\" to +%s."
        .formatted(group.getName(), SUBSCRIBE_ACTION, groupPhoneNumber);
  }

  private String unknwownAction(boolean isMemberInGroup, String to) {

    String message =
        isMemberInGroup
            ? "Thanks for your interest. If you want to unsubscribe from this group, text \"%s\" to"
                + " +%s"
            : "Thanks for your interest. If you want to subscribe to this group, text \"%s\" to"
                + " +%s";
    String actionKeyword = isMemberInGroup ? STOP_ACTION : SUBSCRIBE_ACTION;

    return message.formatted(actionKeyword, to);
  }

  private void sendResponse(String from, String to, String response) {

    var request =
        SendSmsBatchTextRequest.builder()
            .setTo(Collections.singletonList(to))
            .setBody(response)
            .setFrom(from)
            .build();

    smsService.batches().send(request);

    LOGGER.info("Replied: '%s'".formatted(response));
  }
}
