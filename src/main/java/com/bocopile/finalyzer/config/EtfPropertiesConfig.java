package com.bocopile.finalyzer.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(EtfClientProperties.class)
public class EtfPropertiesConfig {

}
