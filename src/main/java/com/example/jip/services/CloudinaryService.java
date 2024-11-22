package com.example.jip.services;
import com.cloudinary.Search;
import com.cloudinary.api.exceptions.BadRequest;
import com.example.jip.dto.response.CloudinaryResponse;

import lombok.AccessLevel;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CloudinaryService {

    Cloudinary cloudinary;

    @Transactional
    public CloudinaryResponse uploadFileToFolder(MultipartFile file, String folderName) {
        try {
            folderName = sanitizeFolderName(folderName);
            Map<String, Object> uploadParams = Map.of(
                    "folder", folderName,
                    "public_id", UUID.randomUUID().toString()
            );

            Map result = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            return CloudinaryResponse.builder()
                    .url((String) result.get("secure_url"))
                    .publicId((String) result.get("public_id"))
                    .folder(folderName)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    public List<Map<String, Object>> getFilesFromFolder(String folderName) {
        try {
            folderName = sanitizeFolderName(folderName);
            Search search = cloudinary.search()
                    .expression("folder:" + folderName) // Specify folder search
                    .maxResults(50); // Limit results for testing

            Map<String, Object> results = search.execute();
            if (results.containsKey("resources")) {
                List<Map<String, Object>> resources = (List<Map<String, Object>>) results.get("resources");
                log.info("Files retrieved from Cloudinary: {}", resources);
                return resources;
            }
            log.warn("No resources found in folder: {}", folderName);
            return Collections.emptyList();

        } catch (Exception e) {
            log.error("Failed to retrieve files from folder: {}", folderName, e);
            throw new RuntimeException("Failed to retrieve files from folder: " + folderName, e);
        }
    }

    public void deleteFolder(String folderName) {
        try {
            folderName = sanitizeFolderName(folderName);
            cloudinary.api().deleteResourcesByPrefix(folderName, ObjectUtils.emptyMap());
            cloudinary.api().deleteFolder(folderName, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete folder in Cloudinary: " + folderName, e);
        }
    }

    private String sanitizeFolderName(String folderName) {
        return folderName.replaceAll("[^a-zA-Z0-9_/\\- ]", "").trim();
    }

}

