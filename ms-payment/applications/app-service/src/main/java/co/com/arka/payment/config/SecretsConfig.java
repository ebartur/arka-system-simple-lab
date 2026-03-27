package co.com.arka.payment.config;

import co.com.arka.payment.r2dbc.config.PostgresqlConnectionProperties;
import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import co.com.bancolombia.secretsmanager.config.AWSSecretsManagerConfig;
import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnectorAsync;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.regions.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class SecretsConfig {

  /*
    Use GenericManagerAsync bean in your reactive pipe.
    connector.getSecret("mySecretName", SecretModel.class).map(...);
  */

  @Bean
  public GenericManagerAsync getSecretManager(@Value("${aws.region}") String region, @Value("${aws.endpoint}") String endpoint) {
    return new AWSSecretManagerConnectorAsync(getConfig(region, endpoint));
  }

  private <T> Mono<T> getSecret(String secretName, Class<T> cls, GenericManagerAsync connector) throws SecretException {
    return connector.getSecret(secretName, cls)
            .doOnSuccess(e -> log.info("Secret was obtained successfully: {}", secretName))
            .doOnError(e -> log.error("Error getting secret: {}", e.getMessage()))
            .onErrorMap(e -> new RuntimeException("Error getting secret", e));
  }

  @Bean
  public PostgresqlConnectionProperties postgresqlSecret(GenericManagerAsync connector,
                                                         @Value("${aws.secrets.db-name}") String dbSecretName)
          throws SecretException {
    return getSecret(dbSecretName, PostgresqlConnectionProperties.class, connector).block();
  }

  private AWSSecretsManagerConfig getConfig(String region, String endpoint) {
    return AWSSecretsManagerConfig.builder()
      .region(Region.of(region))
      .endpoint(endpoint)
      .cacheSize(5) // TODO Set cache size
      .cacheSeconds(3600) // TODO Set cache seconds
      .build();
  }
}
