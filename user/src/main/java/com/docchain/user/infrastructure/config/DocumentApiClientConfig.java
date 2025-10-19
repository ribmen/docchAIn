package com.docchain.user.infrastructure.config;

import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class DocumentApiClientConfig {

    @Value("http://localhost:8081")
    private String documentServiceUrl;

    @Bean
    public DocumentApiClient documentServiceClient() {
        RestClient restClient = RestClient.builder()
                .baseUrl(documentServiceUrl)
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build();

        return factory.createClient(DocumentApiClient.class);
    }
}