package io.game;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
 
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Configuration
@ComponentScan
public @interface PrincipleSpringBootApplication {
    @AliasFor(
            annotation = ComponentScan.class,
            attribute = "basePackages"
    )
    String[] scanBasePackages() default {};
}