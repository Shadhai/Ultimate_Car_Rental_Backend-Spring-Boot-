package com.carrental.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class FileUploadUtil {
    
    private final String UPLOAD_DIR = "./uploads/";
    
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = UUID.randomUUID() + fileExtension;
        
        // Create directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR, folder);
        Files.createDirectories(uploadPath);
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return folder + "/" + uniqueFilename;
    }
    
    public void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(UPLOAD_DIR, filePath);
        Files.deleteIfExists(path);
    }
}