package io.game.sq.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@Configuration
@ComponentScan(basePackages = "io.game")
public class AppConfig {

}
