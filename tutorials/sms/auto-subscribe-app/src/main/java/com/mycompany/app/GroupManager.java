package com.mycompany.app;

import com.sinch.sdk.domains.sms.api.v1.GroupsService;
import com.sinch.sdk.domains.sms.api.v1.SMSService;
import com.sinch.sdk.domains.sms.models.v1.groups.Group;
import com.sinch.sdk.domains.sms.models.v1.groups.request.GroupRequest;
import jakarta.annotation.PreDestroy;
import java.util.Optional;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GroupManager {

  private static final Logger LOGGER = Logger.getLogger(GroupManager.class.getName());

  private final String GROUP_NAME = "Sinch Pirates";

  private final SMSService smsService;

  private Optional<Group> group = Optional.empty();

  @PreDestroy
  public void onDestroy() {
    group.ifPresent(this::deleteGroup);
  }

  @Autowired
  public GroupManager(SMSService smsService) {
    this.smsService = smsService;
  }

  /**
   * Create or return existing group to be used for this tutorial
   *
   * @return Group to be used
   */
  Group getGroup() {

    GroupsService groupsService = smsService.groups();

    // ensure that we do not create a new group if one with the same name already exists
    group = retrieveGroup(groupsService);
    return group.orElseGet(() -> createGroup(groupsService));
  }

  /*
   * Retrieve group if existing
   */
  private Optional<Group> retrieveGroup(GroupsService groupsService) {

    Optional<Group> found =
        groupsService.list().stream()
            .filter(group -> null != group.getName() && group.getName().equals(GROUP_NAME))
            .findFirst();

    found.ifPresent(
        group ->
            LOGGER.info("Group '%s' find with id '%s'".formatted(group.getName(), group.getId())));
    return found;
  }

  /*
   * Create a new group
   */
  private Group createGroup(GroupsService groupsService) {

    var request = GroupRequest.builder().setName(GROUP_NAME).build();

    var group = groupsService.create(request);

    LOGGER.info("Group '%s' created with id '%s'".formatted(group.getName(), group.getId()));
    return group;
  }

  /*
   */

  /**
   * Delete a group
   *
   * @param group Group to be deleted
   */
  void deleteGroup(Group group) {

    LOGGER.info("Deleting '%s' with id '%s' deleted".formatted(group.getName(), group.getId()));

    GroupsService groupsService = smsService.groups();
    groupsService.delete(group.getId());
  }
}
