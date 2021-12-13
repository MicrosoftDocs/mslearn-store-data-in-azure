package com.microsoft.azure.samples.service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.options.BlobParallelUploadOptions;

@Singleton
public class BlobStorage implements Storage {
    private static final String CONTAINER_NAME = "fileuploader";

    private BlobContainerClient blobContainerClient;

    @PostConstruct
    private void init() {
        String connectionString = System.getenv("STORAGE_CONNECTION_STRING");
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
    }

    public List<String> listFiles() {
        return blobContainerClient.listBlobs()
          .stream()
          .map(BlobItem::getName)
          .collect(Collectors.toList());
    }

    public void save(String fileName, InputStream fileInputStream) {
        blobContainerClient.getBlobClient(fileName)
            .uploadWithResponse(new BlobParallelUploadOptions(fileInputStream), null, null);
    }

    public InputStream read(String fileName) {
        return blobContainerClient.getBlobClient(fileName)
            .openInputStream();
    }
    
}
