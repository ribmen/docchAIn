package com.docchain.user.infrastructure.config;

import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class DocumentApiClientConfig {

    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Value("${document.service.url:lb://document-service}")
    private String documentServiceUrl;

    @Bean
    public DocumentApiClient documentServiceClient(WebClient.Builder builder) {
        WebClient client = builder
                .baseUrl(documentServiceUrl)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(WebClientAdapter.create(client))
                .build();

        return factory.createClient(DocumentApiClient.class);
    }
}