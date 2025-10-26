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

/**
 * Configuração do cliente HTTP para Document Service.
 * 
 * MUDANÇA PRINCIPAL: WebClient (reativo) → RestClient (síncrono/imperativo)
 * 
 * MOTIVO DA MUDANÇA:
 * - O projeto usa uma abordagem IMPERATIVA (bloqueante), não reativa
 * - WebClient é ideal para programação reativa (Mono/Flux)
 * - RestClient é ideal para padrões imperativos síncronos
 * - RestClient é a recomendação oficial do Spring 6+ para chamadas REST síncronas
 * - Evita complexidade desnecessária de trabalhar com WebClient em contexto não-reativo
 * 
 * TIMEOUTS CONFIGURADOS:
 * - connectTimeout: 2s - Tempo máximo para estabelecer conexão TCP
 * - readTimeout: 5s - Tempo máximo para ler a resposta completa
 * 
 * Por que timeouts baixos? Padrão FAIL-FAST:
 * - Se o serviço não responde rápido, é melhor falhar rápido
 * - Permite que o Retry tente novamente mais rápido
 * - Evita threads bloqueadas por muito tempo
 * - O CircuitBreaker pode detectar problemas mais rapidamente
 */
@Configuration
public class DocumentApiClientConfig {

    @Value("${document.service.url:lb://document-service}")
    private String documentServiceUrl;

    /**
     * Bean LoadBalanced do RestClient.Builder.
     * 
     * @LoadBalanced permite usar nome do serviço (lb://document-service) 
     * em vez de IP:porta hardcoded, integrando com Eureka para descoberta de serviços.
     */
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Factory para configurar timeouts do cliente HTTP.
     * 
     * IMPORTANTE: Timeouts devem ser MENORES que o timeout do TimeLimiter
     * se estiver usando um. Aqui usamos 5s de read timeout, e o TimeLimiter
     * está configurado para 5s no YAML (mas não estamos mais usando TimeLimiter
     * na abordagem declarativa, os timeouts aqui são suficientes).
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(2000));  // 2 segundos para conectar
        factory.setReadTimeout(Duration.ofMillis(5000));     // 5 segundos para ler resposta
        return factory;
    }

    /**
     * Cria o bean do DocumentApiClient usando HttpServiceProxyFactory.
     * 
     * FUNCIONAMENTO:
     * 1. Cria RestClient com baseUrl e timeouts
     * 2. Adapta para RestClientAdapter
     * 3. HttpServiceProxyFactory cria um proxy dinâmico da interface
     * 4. As anotações @Retry e @CircuitBreaker são interceptadas pelo Spring AOP
     * 
     * @param builder RestClient.Builder com @LoadBalanced injetado
     * @return Instância proxy de DocumentApiClient
     */
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