package com.mycompany.app;

import com.sinch.sdk.SinchClient;
import com.sinch.sdk.domains.voice.VoiceService;
import com.sinch.sdk.models.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@org.springframework.context.annotation.Configuration
public class Config {

  @Value("${credentials.application-api-key}")
  String applicationKey;

  @Value("${credentials.application-api-secret}")
  String applicationSecret;

  @Bean
  public VoiceService voiceService() {

    var configuration =
        Configuration.builder()
            .setApplicationKey(applicationKey)
            .setApplicationSecret(applicationSecret)
            .build();

    return new SinchClient(configuration).voice();
  }
}
