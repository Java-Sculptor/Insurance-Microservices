
package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.service.DocumentStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class DocumentStorageServiceImpl
        implements DocumentStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of("pdf", "jpg", "jpeg", "png");

    private final Path storageDirectory;

    public DocumentStorageServiceImpl(
            @Value("${app.document.storage-dir:uploads/claims}")
            String storageDirectory) {

        this.storageDirectory = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Could not initialize document storage", ex);
        }
    }

    @Override
    public String storeFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a document to upload");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null
                        ? ""
                        : file.getOriginalFilename());

        String extension =
                StringUtils.getFilenameExtension(originalName);

        if (extension == null
                || !ALLOWED_EXTENSIONS.contains(
                extension.toLowerCase(Locale.ROOT))) {

            throw new IllegalArgumentException(
                    "Only PDF, JPG, JPEG and PNG files are allowed");
        }

        String storedFileName =
                UUID.randomUUID() + "."
                        + extension.toLowerCase(Locale.ROOT);

        Path targetPath = storageDirectory
                .resolve(storedFileName)
                .normalize();

        if (!targetPath.startsWith(storageDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        try {
            // Copy uploaded file to the storage directory
            Files.copy(file.getInputStream(), targetPath);

            return targetPath.toString();

        } catch (IOException ex) {
            throw new IllegalStateException(
                    "Failed to store uploaded document", ex);
        }
    }
}
