package com.mrkresnofatihdev.gulugulu.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration
public class AwsConfig {
    @Value("${cloud.aws.credentials.access}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret}")
    private String secretKey;

    @Value("${cloud.aws.credentials.region}")
    private String region;

    @Bean
    public DynamoDbClient dynamoDbClient() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                accessKey,
                secretKey
        );
        return DynamoDbClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsBasicCredentials))
                .region(Region.of(region))
                .build();
    }
}
