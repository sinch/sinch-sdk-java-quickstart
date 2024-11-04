package com.sinch.campaign;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.sinch.sdk.domains.sms.SMSService;
import com.sinch.sdk.domains.sms.models.InboundText;
import com.sinch.sdk.domains.sms.models.requests.SendSmsBatchTextRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.logging.Logger;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Instant;

@Controller
public class CampaignController {
  private final SMSService smsService;
  private final AutoSubscribeService service;
  @Value("${sinch_details.from_number}")
  String from_number;

  private static final Logger LOGGER = Logger.getLogger(CampaignController.class.getName());

  public CampaignController(SMSService smsService, AutoSubscribeService service) {

    this.smsService = smsService;
    this.service = service;

  }

  @PostMapping(value = "/subscribe", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> smsDeliveryEvent(@RequestBody String body) {

    // decode the request payload
    var event = smsService.webHooks().parse(body);

    // let business layer process the request
    if (event instanceof InboundText e) {
      service.processInboundEvent(e);
    } else {
      throw new IllegalStateException("Unexpected value: " + event);
    }
    return new ResponseEntity<String>("OK", HttpStatus.NO_CONTENT);
  }

  @GetMapping("/")
  public String index() {
    return "redirect:/campaign";
  }

  @GetMapping("/campaign")
  public String campaign() {
    return "message_composer";
  }

  private void send_campaign(String message, Instant sendAt) {

    smsService.batches().send(SendSmsBatchTextRequest.builder()
        .setTo(smsService.groups().listMembers(service.group.getId()))
        .setBody(message)
        .setFrom(from_number).setSendAt(sendAt)
        .build());

  }

  @GetMapping("/success")
  public String getSuccess(HttpServletRequest request) {
    return "success";
  }

  @PostMapping("/campaign")
  public RedirectView submitPost(
      @RequestParam("message") String message,
      @RequestParam("campaign_date") LocalDate date,
      @RequestParam("campaign_time") String time,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    LocalDateTime campaignDateTime = LocalDateTime.of(date, LocalTime.parse(time));

    ZonedDateTime zonedCampaignDateTime = campaignDateTime.atZone(ZoneId.systemDefault());
    ZonedDateTime utcDateTime = zonedCampaignDateTime.withZoneSameInstant(ZoneId.of("UTC"));

    Instant sendAt = utcDateTime.toInstant();

    send_campaign(message, sendAt);

    redirectAttributes.addFlashAttribute("campaign_datetime", campaignDateTime);
    return new RedirectView("/success", true);

  }

}