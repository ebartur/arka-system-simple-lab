package co.com.arka.orders.config;

import co.com.arka.orders.events.config.KafkaBrokerSecretConsumer;
import co.com.arka.orders.events.config.KafkaBrokerSecretProducer;
import co.com.arka.orders.r2dbc.config.PostgresqlConnectionProperties;
import co.com.bancolombia.secretsmanager.api.GenericManagerAsync;
import co.com.bancolombia.secretsmanager.api.exceptions.SecretException;
import co.com.bancolombia.secretsmanager.config.AWSSecretsManagerConfig;
import co.com.bancolombia.secretsmanager.connector.AWSSecretManagerConnectorAsync;
import lombok.extern.slf4j.Slf4j;
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
  public GenericManagerAsync getSecretManager(@Value("${aws.region}") String region,
                                              @Value("${aws.endpoint}") String endpoint) {
    return new AWSSecretManagerConnectorAsync(getConfig(region, endpoint));
  }

  @Bean
  public PostgresqlConnectionProperties postgresqlSecret(
          @Value("${aws.secrets.db-name}") String secretName,
          GenericManagerAsync secretManager) throws SecretException {
    return secretManager.getSecret(secretName, PostgresqlConnectionProperties.class)
            .doOnSuccess(e -> log.info("Secret was obtained successfully: {}", secretName))
            .doOnError(e -> log.error("Error getting secret: {}", e.getMessage()))
            .onErrorMap(e -> new RuntimeException("Error getting secret", e))
            .block();
  }

  @Bean
  public KafkaBrokerSecretProducer brokerSecretProducer(
          @Value("${aws.secrets.kafka-name}") String secretName,
          GenericManagerAsync secretManager) throws SecretException {
    return secretManager.getSecret(secretName, KafkaBrokerSecretProducer.class)
            .doOnSuccess(e -> log.info("Secret was obtained successfully: {}", secretName))
            .doOnError(e -> log.error("Error getting secret: {}", e.getMessage()))
            .onErrorMap(e -> new RuntimeException("Error getting secret", e))
            .block();
  }

  @Bean
  public KafkaBrokerSecretConsumer brokerSecretConsumer(
          @Value("${aws.secrets.kafka-name}") String secretName,
          GenericManagerAsync secretManager) throws SecretException {
    return secretManager.getSecret(secretName, KafkaBrokerSecretConsumer.class)
            .doOnSuccess(e -> log.info("Secret was obtained successfully: {}", secretName))
            .doOnError(e -> log.error("Error getting secret: {}", e.getMessage()))
            .onErrorMap(e -> new RuntimeException("Error getting secret", e))
            .block();
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
