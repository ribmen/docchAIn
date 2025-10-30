package com.docchain.user.infrastructure.config;

import com.docchain.user.infrastructure.http.client.DocumentApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class DocumentApiClientConfig {

    @Value("${document.service.url:lb://document-service}")
    private String documentServiceUrl;

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));   // 5 segundos para conectar
        factory.setReadTimeout(Duration.ofSeconds(75));     // 75 segundos para aguardar resposta (inclui tempo da IA)
        return factory;
    }

    @Bean
    public DocumentApiClient documentServiceClient(RestClient.Builder builder) {
        RestClient restClient = builder
                .baseUrl(documentServiceUrl)
                .requestFactory(clientHttpRequestFactory())
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(DocumentApiClient.class);
    }
}