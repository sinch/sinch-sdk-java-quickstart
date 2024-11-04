package com.sinch.appointment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import com.sinch.sdk.domains.sms.SMSService;
import com.sinch.sdk.domains.sms.models.requests.SendSmsBatchTextRequest;
import java.util.Collections;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.Instant;

@Controller
public class AppointmentController {
  private final SMSService smsService;
  private final Config config;


 
  
  
  public AppointmentController(Config config, SMSService smsService) {

    this.config = config;
    this.smsService = smsService;

  }

  @GetMapping("/")
  public String index() {
    return "redirect:/appointment";
  }

  @GetMapping("/appointment")
  public String appointment() {
    return "patient_details";
  }

  private void remindPatient(String to, String message, Instant sendAt) {

    smsService.batches().send(SendSmsBatchTextRequest.builder()
        .setTo(Collections.singletonList(to))
        .setBody(message)
        .setFrom(config.fromNumber).setSendAt(sendAt)
        .build());

  }

  @GetMapping("/success")
  public String getSuccess(HttpServletRequest request) {
    return "success";
  }

  @PostMapping("/appointment")
  public RedirectView submitPost(@RequestParam("doctor") String doctor,
      @RequestParam("patient") String patient,
      @RequestParam("phone") String phone,
      @RequestParam("appointment_date") LocalDate date,
      @RequestParam("appointment_time") String time,
      HttpServletRequest request,
      RedirectAttributes redirectAttributes) {
    LocalDateTime appointmentDateTime = LocalDateTime.of(date, LocalTime.parse(time));

    LocalDateTime now = LocalDateTime.now();
    ZonedDateTime zonedAppointmentDateTime = appointmentDateTime.atZone(ZoneId.systemDefault());
    ZonedDateTime reminderDateTime = zonedAppointmentDateTime.withZoneSameInstant(ZoneId.of("UTC")).minusHours(2);
    Instant sendAt = reminderDateTime.toInstant();

    if (appointmentDateTime.isBefore(now.plusHours(2))) {
      redirectAttributes.addFlashAttribute("message", "Appointment must be at least 2 hours from now");
      return new RedirectView("/appointment", true);

    }

    

   
     String country_code = "+1";
     if(config.region=="eu"){
      country_code=config.country_code;
     }
    
 
    String to = phone.replaceFirst("0", country_code);

    String message = "Hi " + patient + ", you have a Sinch Hospital appointment with Dr "
        + doctor + " scheduled for " + appointmentDateTime;

    remindPatient(to, message, sendAt);
    redirectAttributes.addFlashAttribute("patient", patient);
    redirectAttributes.addFlashAttribute("doctor", doctor);
    redirectAttributes.addFlashAttribute("phone", phone);
    redirectAttributes.addFlashAttribute("appointmentDateTime", appointmentDateTime);
    return new RedirectView("/success", true);

  }

}