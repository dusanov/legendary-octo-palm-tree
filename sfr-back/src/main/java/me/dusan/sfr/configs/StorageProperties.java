package me.dusan.sfr.configs;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "file")
public class StorageProperties {

    @Getter @Setter private String airport;
    @Getter @Setter private String routes;

}
