package com.microsoft.azure.samples.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.options.BlobParallelUploadOptions;

@Named("BlobStorage")
@Singleton
public class BlobStorage implements Storage {
    private static final String CONTAINER_NAME = "fileuploader";

    @Inject
    private BlobServiceClient blobServiceClient;

    @PostConstruct
    private void init() {
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
    }

    @Override
    public List<String> listFiles() throws IOException {
        return blobServiceClient.getBlobContainerClient(CONTAINER_NAME)
          .listBlobs()
          .stream()
          .map(BlobItem::getName)
          .collect(Collectors.toList());
    }

    @Override
    public void save(String fileName, InputStream fileInputStream) throws IOException {
        blobServiceClient.getBlobContainerClient(CONTAINER_NAME)
            .getBlobClient(fileName)
            .uploadWithResponse(new BlobParallelUploadOptions(fileInputStream), null, null);
    }

    @Override
    public InputStream read(String fileName) throws IOException {
        return blobServiceClient.getBlobContainerClient(CONTAINER_NAME)
            .getBlobClient(fileName)
            .openInputStream();
    }
    
}
