package com.ecom.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Override
    public String uploadImage(String imagePath, MultipartFile image) throws IOException {

        String originalFilename = image.getOriginalFilename();

        String fileName = UUID.randomUUID().toString() // Get a unique id
                .concat(originalFilename.substring(originalFilename.lastIndexOf("."))); // append image extension

        String filePath = imagePath + File.separator + fileName;

        File folder = new File(imagePath);

        if (!folder.exists()) {
            folder.mkdir();
        }

        Files.copy(image.getInputStream(), Paths.get(filePath));

        return fileName;

    }

}
