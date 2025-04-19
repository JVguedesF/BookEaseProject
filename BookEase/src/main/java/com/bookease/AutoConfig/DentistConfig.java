package com.bookease.AutoConfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "initial.dentist")
public class DentistConfig {

    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String crm;
}
