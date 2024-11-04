package com.sinch.appointment;

import com.sinch.sdk.SinchClient;
import com.sinch.sdk.domains.sms.SMSService;
import com.sinch.sdk.models.Configuration;
import com.sinch.sdk.models.SMSRegion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Config {

  @Value("${sinch_details.project-id}")
  String projectId;

  @Value("${sinch_details.key-id}")
  String keyId;

  @Value("${sinch_details.key-secret}")
  String keySecret;

  @Value("${sinch_details.from_number}")
  String fromNumber;

  @Value("${sinch_details.country_code}")
  String country_code;

  @Value("${sinch_details.sms_region}")
  String region;

  

  @Bean
  public SMSService smsService() {

    var configuration =
        Configuration.builder()
            .setProjectId(projectId)
            .setKeyId(keyId)
            .setKeySecret(keySecret)
            .build();
            if(region=="eu"){
              configuration.builder().setSmsRegion(SMSRegion.EU);
            }

    return new SinchClient(configuration).sms();
  }
}
