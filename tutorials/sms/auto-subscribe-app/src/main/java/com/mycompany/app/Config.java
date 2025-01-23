package com.mycompany.app;

import com.sinch.sdk.SinchClient;
import com.sinch.sdk.domains.sms.api.v1.SMSService;
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

  @Bean
  public SMSService smsService() {

    var configuration =
        Configuration.builder()
            .setProjectId(projectId)
            .setKeyId(keyId)
            .setKeySecret(keySecret)
            .setSmsRegion(SMSRegion.US)
            .build();

    return new SinchClient(configuration).sms().v1();
  }
}
