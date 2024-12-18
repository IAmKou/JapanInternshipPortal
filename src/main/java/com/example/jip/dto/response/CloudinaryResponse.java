package com.example.jip.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CloudinaryResponse {
    private String url;// Public URL of the file
    private String publicId;  // Unique identifier for the file
    private String folder;    // Folder path
}