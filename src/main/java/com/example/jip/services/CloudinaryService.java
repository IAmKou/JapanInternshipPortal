package com.example.jip.services;
import com.example.jip.dto.response.CloudinaryResponse;
import com.example.jip.exception.FuncErrorException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CloudinaryService {

    Cloudinary cloudinary;

    @Transactional
    public CloudinaryResponse uploadFile(MultipartFile file) {
        try {
             Map result = cloudinary.uploader()
                    .upload(file.getBytes(),
                            Map.of("public_id",
                                    UUID.randomUUID().toString()));
            final String url      = (String) result.get("secure_url");
            return CloudinaryResponse.builder()
                    .url(url)
                    .build();

        } catch (final IOException e) {
            throw new FuncErrorException("Failed to upload file");
        }
    }
}

