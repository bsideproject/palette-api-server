package com.palette.file;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.palette.infra.s3.S3Client;
import java.io.InputStream;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileService {

    private final S3Client s3Client;

    @Value("${ncp.object-storage.bucket-name}")
    private String bucketName;

    public FileService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String upload(InputStream inputStream, String originalFileName, Long fileSize) {
        AmazonS3 s3 = s3Client.get();

        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i + 1);
        }
        String s3FileKey = UUID.randomUUID() + "." + extension;
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);

        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3FileKey, inputStream,
            objectMetadata)
            .withCannedAcl(CannedAccessControlList.PublicRead);
        s3.putObject(putObjectRequest);

        return s3.getUrl(bucketName, s3FileKey).toString();
    }

    public void delete(String url) {
        AmazonS3 s3 = s3Client.get();
        URI uri = URI.create(url);
        String key = uri.getPath().substring(1);
        s3.deleteObject(bucketName, key);
    }
}
