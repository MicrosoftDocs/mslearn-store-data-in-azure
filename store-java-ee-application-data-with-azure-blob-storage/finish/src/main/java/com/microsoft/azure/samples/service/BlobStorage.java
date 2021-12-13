package com.microsoft.azure.samples.service;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;

@Singleton
public class BlobStorage implements Storage {

    private BlobContainerClient blobContainerClient;

    @PostConstruct
    private void init() {
        String connectionString = System.getenv("STORAGE_CONNECTION_STRING");
        String containerName = System.getenv("STORAGE_CONTAINER_NAME");
        BlobServiceClient blobServiceClient = new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
        blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
    }

    public List<String> listNames() {
        return blobContainerClient.listBlobs()
          .stream()
          .map(BlobItem::getName)
          .collect(Collectors.toList());
    }

    public void save(String name, InputStream inputStream, long contentLength) {
        BlobClient blobClient = blobContainerClient.getBlobClient(name);
        blobClient.upload(inputStream, contentLength);
    }

    public InputStream read(String name) {
        BlobClient blobClient = blobContainerClient.getBlobClient(name);
        return blobClient.openInputStream();
    }
    
}
