package com.app.insurance_management.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentStorageService {

    String storeFile(MultipartFile file);
}