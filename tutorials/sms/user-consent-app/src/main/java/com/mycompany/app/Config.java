package com.mycompany.app;

import com.sinch.sdk.SinchClient;
import com.sinch.sdk.domains.sms.SMSService;
import com.sinch.sdk.models.Configuration;
import com.sinch.sdk.models.SMSRegion;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Config {

  @Value("${credentials.project-id}")
  String projectId;

  @Value("${credentials.key-id}")
  String keyId;

  @Value("${credentials.key-secret}")
  String keySecret;

  @Value("${sms.region}")
  String smsRegion;

  @Bean
  public SMSService smsService() {

    var builder =
        Configuration.builder().setProjectId(projectId).setKeyId(keyId).setKeySecret(keySecret);

    if (!smsRegion.isEmpty()) {
      builder.setSmsRegion(SMSRegion.from(smsRegion));
    }
    return new SinchClient(builder.build()).sms();
  }
}
