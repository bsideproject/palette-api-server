package com.palette.infra.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class S3Client {
    private static AmazonS3 s3;

    @Value("${ncp.region}")
    private String region;

    @Value("${ncp.object-storage.endpoint}")
    private  String endpoint;

    @Value("${ncp.access-key}")
    private String access_key;

    @Value("${ncp.secret-key}")
    private String secret_key;

    public AmazonS3 get() {
        if (s3 == null) {
            s3 = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(
                            new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(access_key, secret_key)))
                    .build();
        }
        return s3;
    }
}
