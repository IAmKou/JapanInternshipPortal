package com.example.jip.configuration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component  // Đảm bảo Spring quản lý class này
@ConfigurationProperties(prefix = "application.file-paths")
public class ConfigProperties {

    // Getters và Setters
    private String htmlPath;
    private String csvPath;

}
