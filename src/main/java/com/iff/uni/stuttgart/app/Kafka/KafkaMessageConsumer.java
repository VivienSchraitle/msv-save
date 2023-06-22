package com.iff.uni.stuttgart.app.Kafka;

import java.time.Instant;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.write.Point;
import com.influxdb.client.domain.WritePrecision; // Add this import

@Component
public class KafkaMessageConsumer {

    private final InfluxDBClient influxDBClient;

    @Autowired
    public KafkaMessageConsumer(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    @KafkaListener(topics = "msv-topic")
    public void consumeMessage(ConsumerRecord<String, String> record) {
       System.out.print("got here");
        String message = record.value();

        // Create InfluxDB point
        Point point = Point.measurement("my_measurement")
                .addTag("key", "value")
                .addField("value", message)
                .time(Instant.now(), WritePrecision.NS);

        // Write the point to InfluxDB
        System.out.print("got here");
        try {
            influxDBClient.getWriteApiBlocking().writePoint("${influxDbBucket}", "${influxDbOrg}", point);
        } catch (Exception e) {
            // Handle write error
            e.printStackTrace();
        }
    }
}



