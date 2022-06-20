package com.yandex.market.config;

import com.yandex.market.config.custom.CustomRestTemplateCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;
import org.zalando.logbook.spring.LogbookClientHttpRequestInterceptor;

import java.util.concurrent.Executor;

@ConfigurationPropertiesScan
@Configuration
@RequiredArgsConstructor
@EnableJpaRepositories
@EntityScan({"com.yandex"})
@EnableAsync
public class ApplicationConfig {

    public static final int API_DIGITAL_LEAGUE_USERS_THREAD_POOL = 100;

    @Bean
    public CustomRestTemplateCustomizer customRestTemplateCustomizer() {
        return new CustomRestTemplateCustomizer();
    }

    @Bean
    @DependsOn(value = {"customRestTemplateCustomizer"})
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder(customRestTemplateCustomizer());
    }

    @Bean
    public RestTemplate restTemplate(
            RestTemplateBuilder builder,
            LogbookClientHttpRequestInterceptor interceptor) {
        return builder
                .requestFactory(() -> new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .additionalInterceptors(interceptor)
                .build();
    }

    @Bean(name = "apiYandexMarketThreadPool")
    public Executor apiYandexMarketThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(API_DIGITAL_LEAGUE_USERS_THREAD_POOL);
        executor.setMaxPoolSize(API_DIGITAL_LEAGUE_USERS_THREAD_POOL);
        executor.setQueueCapacity(API_DIGITAL_LEAGUE_USERS_THREAD_POOL * 2);
        return executor;
    }
}
