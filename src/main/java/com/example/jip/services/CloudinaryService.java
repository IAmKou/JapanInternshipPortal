package com.example.jip.services;
import com.cloudinary.Search;
import com.cloudinary.api.exceptions.BadRequest;
import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.exception.CloudinaryFolderAccessException;
import com.example.jip.exception.FuncErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
            final Map<String, Object> uploadParams = Map.of(
                    "folder", folderName,   // Specify the folder where the file will be uploaded
                    "public_id", UUID.randomUUID().toString() // Generate a unique ID for the file
            );
            // Upload the file to Cloudinary
            final Map result = this.cloudinary.uploader().upload(file.getBytes(), uploadParams);

            // Extract secure URL and public ID
            final String url = (String) result.get("secure_url");
            final String publicId = (String) result.get("public_id");

            return CloudinaryResponse.builder()
                    .url(url)
                    .publicId(publicId)
                    .folder(folderName)
                    .build();

        } catch (final IOException e) {
            throw new RuntimeException("Failed to upload file to Cloudinary", e);
        }
    }

    public String getFolderUrl(String folderName) {
        return cloudinary.url().generate(folderName); // Get the folder URL
    }

    public List<Map<String, Object>> getFilesFromFolder(String folderName) {
        try {
            folderName = folderName.replaceAll("[^a-zA-Z0-9_/\\-]", "").trim(); // Sanitize input
            if (folderName.isEmpty()) {
                throw new IllegalArgumentException("Folder name cannot be empty.");
            }

            // Perform Cloudinary search
            Search search = cloudinary.search().expression("folder:" + folderName);
            Map<String, Object> results = search.execute();

            // Return the resources (files)
            if (results.containsKey("resources")) {
                return (List<Map<String, Object>>) results.get("resources");
            } else {
                return Collections.emptyList();
            }
        } catch (Exception e) {
            throw new CloudinaryFolderAccessException("Failed to retrieve files from folder: " + folderName, e);
        }
    }



    public void deleteFolder(String folderName) {
        try {
            log.info("Deleting all resources in folder: {}", folderName);

            // Delete all resources in the folder
            Map result = cloudinary.api().deleteResourcesByPrefix(folderName, ObjectUtils.emptyMap());
            log.info("Deleted resources: {}", result);

            // Delete the folder itself
            cloudinary.api().deleteFolder(folderName, ObjectUtils.emptyMap());
            log.info("Deleted folder: {}", folderName);
        } catch (Exception e) {
            log.error("Error deleting Cloudinary folder: {}", folderName, e);
            throw new RuntimeException("Failed to delete folder in Cloudinary: " + folderName, e);
        }
    }

    @Transactional
    public List<String> listFilesInFolder(String folderName) {
        try {
            Map result = cloudinary.api().resources(ObjectUtils.asMap(
                    "type", "upload",
                    "prefix", folderName  // Use the folder name here, not its URL
            ));

            // Extract public IDs or URLs of the files
            return ((List<Map<String, Object>>) result.get("resources")).stream()
                    .map(resource -> (String) resource.get("secure_url"))
                    .collect(Collectors.toList());

        } catch (BadRequest e) {
            throw new RuntimeException("Failed to list files in folder: " + folderName, e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error while listing files in folder", e);
        }
    }
}

