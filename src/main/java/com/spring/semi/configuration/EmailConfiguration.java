package com.spring.semi.configuration;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;


/**
 * EmailConfiguration - 스프링 설정.
 */
@Configuration
public class EmailConfiguration {

	@Bean
    public JavaMailSenderImpl sender(
        @Value("${spring.mail.host}") String host,
        @Value("${spring.mail.port}") int port,
        @Value("${spring.mail.username}") String username,
        @Value("${spring.mail.password}") String password
    ) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(host);
        sender.setPort(port);
        sender.setUsername(username);
        sender.setPassword(password);

        Properties props = new Properties();
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        props.setProperty("mail.smtp.ssl.trust", "smtp.gmail.com");
        sender.setJavaMailProperties(props);

        return sender;
    }
}
