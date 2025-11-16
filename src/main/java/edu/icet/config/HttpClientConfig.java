package edu.icet.config;

import edu.icet.client.UserClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class HttpClientConfig {

    @Bean
    public UserClient userClient(){
        WebClient client = WebClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
                .exchangeAdapter(WebClientAdapter.create(client))
                .build();

        return factory.createClient(UserClient.class);

    }

}
