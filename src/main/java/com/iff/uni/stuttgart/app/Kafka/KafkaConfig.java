package com.iff.uni.stuttgart.app.Kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Value("${kafka.consumer.group-id}")
    private String groupId;

    @Value("${influxdb.url}")
    private String influxDbUrl;

    @Value("${influxdb.token}")
    private String influxDbToken;

    @Value("${influxdb.organization}")
    private String influxDbOrganization;

    @Value("${influxdb.database}")
    private String influxDbDatabase;

    @Value("${influxdb.bucket}")
    private String influxDbBucket;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    @Bean
    public KafkaMessageConsumer kafkaMessageConsumer(InfluxDBClient influxDBClient) {
        return new KafkaMessageConsumer(influxDBClient);
    }

    @Bean
    public ConcurrentMessageListenerContainer<String, String> container() {
        return kafkaListenerContainerFactory().createContainer("mytopic");
    }

    @Bean
    public InfluxDBClient influxDBClient() {
        InfluxDBClient client = InfluxDBClientFactory.create(influxDbUrl, influxDbToken.toCharArray(),influxDbOrganization,influxDbBucket);
        return client;
    }

    @Bean
    public WriteApiBlocking writeApiBlocking() {
        return influxDBClient().getWriteApiBlocking();
    }

    @Bean
    public void shutdown() {
        influxDBClient().close();
    }
}