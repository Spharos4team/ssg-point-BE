package com.spharos.ssgpoint.config.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AWSConfig {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder().region(Region.AP_NORTHEAST_2) // 예: 서울 리전
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("YOUR_AWS_ACCESS_KEY", "YOUR_AWS_SECRET_KEY"))).build();
    }
}
