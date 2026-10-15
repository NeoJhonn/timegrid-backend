package br.com.jhonnyazevedo.timegrid_backend.config;

import org.hibernate.generator.internal.CurrentTimestampGeneration;
import org.springframework.boot.hibernate.autoconfigure.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class TimeZoneConfig {

    @Bean
    public HibernatePropertiesCustomizer timestampClockCustomizer() {
        return properties -> properties.put(
                CurrentTimestampGeneration.CLOCK_SETTING_NAME,
                Clock.system(ZoneId.of("America/Sao_Paulo")));
    }
}
