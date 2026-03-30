package co.com.arka.inventory.config;

import co.com.arka.inventory.events.config.KafkaBrokerSecretConsumer;
import co.com.arka.inventory.events.config.KafkaBrokerSecretProducer;
import co.com.arka.inventory.r2dbc.config.PostgresqlConnectionProperties;
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
  public PostgresqlConnectionProperties postgresqlConnectionProperties(
          GenericManagerAsync secretManager,
          @Value("${aws.secrets.db-name}") String dbSecretName) throws SecretException {
    return secretManager.getSecret(dbSecretName, PostgresqlConnectionProperties.class)
            .doOnSuccess(e -> log.info("Secret was obtained successfully: {}", dbSecretName))
            .doOnError(e -> log.error("Error getting secret: {}", e.getMessage()))
            .onErrorMap(e -> new RuntimeException("Error getting secret", e))
            .block();
  }

  @Bean
  public KafkaBrokerSecretConsumer  kafkaBrokerSecretConsumer(
          GenericManagerAsync secretManager,
          @Value("${aws.secrets.kafka-name}") String kafkaSecretName
  ) throws SecretException {
    return secretManager.getSecret(kafkaSecretName, KafkaBrokerSecretConsumer.class)
            .doOnSuccess(e -> log.info("Secret was obtained successfully: {}", kafkaSecretName))
            .doOnError(e -> log.error("Error getting secret: {}", e.getMessage()))
            .onErrorMap(e -> new RuntimeException("Error getting secret", e))
            .block();
  }

  @Bean
  public KafkaBrokerSecretProducer kafkaBrokerSecretProducer(
          GenericManagerAsync secretManager,
          @Value("${aws.secrets.kafka-name}") String kafkaSecretName
  ) throws SecretException {
    return secretManager.getSecret(kafkaSecretName, KafkaBrokerSecretProducer.class)
            .doOnSuccess(e -> log.info("Secret was obtained successfully: {}", kafkaSecretName))
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
