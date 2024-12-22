package com.example.jip.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.AccessLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class S3Service {

    @Value("${application.bucket.name}")
    String bucketName;

    @Autowired
    AmazonS3 s3Client;

    public String uploadFile(MultipartFile file, String folderName, String fileName){
        // Create the full path for the file in the folder
        String fullPath = folderName + "/" + fileName;

        // Convert the MultipartFile to a File object
        File convFile = convertMultipartFile(file);

        // Create PutObjectRequest with the PublicRead ACL
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fullPath, convFile)
                .withCannedAcl(CannedAccessControlList.PublicRead); // Make the file public

        // Upload the file to S3
        s3Client.putObject(putObjectRequest);

        // Return the file URL
        // Get the public URL of the uploaded file (it should be accessible via HTTP)
        String fileUrl = s3Client.getUrl(bucketName, fullPath).toString();
        return "File uploaded and publicly accessible at: " + fileUrl;
    }

    public void deleteFile(String fileUrl, String folderName) {
        try {
            // Extract the file name from the URL
            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            // Construct the full S3 object key (path) based on the folder and file name
            String s3ObjectKey = folderName + "/" + fileName;

            // Delete the file from S3
            s3Client.deleteObject(new DeleteObjectRequest(bucketName, s3ObjectKey));

            log.info("File deleted successfully: " + s3ObjectKey);
        } catch (Exception e) {
            log.error("Error deleting file from S3: " + e.getMessage(), e);
            throw new RuntimeException("Error deleting file from S3", e);
        }
    }


    public void deleteFolder(String folderPath) {
        // Ensure folderPath ends with a trailing slash
        if (!folderPath.endsWith("/")) {
            folderPath += "/";
        }

        try {
            // List all objects under the folder prefix
            ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(folderPath);

            ListObjectsV2Result result;
            do {
                result = s3Client.listObjectsV2(listObjectsRequest);

                // Delete objects in batch
                if (!result.getObjectSummaries().isEmpty()) {
                    DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                            .withKeys(result.getObjectSummaries().stream()
                                    .map(S3ObjectSummary::getKey)
                                    .toArray(String[]::new));
                    s3Client.deleteObjects(deleteObjectsRequest);
                }

                // Set the continuation token for pagination
                listObjectsRequest.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());
        } catch (AmazonServiceException e) {
            throw new RuntimeException("Error deleting folder: " + folderPath, e);
        }
    }

    public List<String> listFilesInFolder(String folderPath) {
        // Ensure the folder path ends with a trailing slash

        if (!folderPath.endsWith("/")) {
            folderPath += "/";
        }

        List<String> fileUrls = new ArrayList<>();

        try {
            ListObjectsV2Request request = new ListObjectsV2Request()
                    .withBucketName(bucketName)
                    .withPrefix(folderPath);

            ListObjectsV2Result result;

            do {
                result = s3Client.listObjectsV2(request);

                for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                    // Skip the folder itself
                    if (objectSummary.getKey().equals(folderPath)) {
                        continue;
                    }

                    // Get the file URL and decode it
                    String fileUrl = s3Client.getUrl(bucketName, objectSummary.getKey()).toString();
                    String decodedFileUrl = URLDecoder.decode(fileUrl, StandardCharsets.UTF_8);

                    fileUrls.add(decodedFileUrl);
                }

                // Set continuation token for paginated results
                request.setContinuationToken(result.getNextContinuationToken());
            } while (result.isTruncated());

        } catch (Exception e) {
            throw new RuntimeException("Error listing file URLs in folder: " + folderPath, e);
        }

        return fileUrls;
    }


    private File convertMultipartFile(MultipartFile file){
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)){
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error");
        }
        return convFile;
    }

}
