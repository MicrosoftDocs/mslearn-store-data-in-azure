package com.azure.azurestorageblobdemo.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.azure.spring.core.resource.AzureStorageBlobProtocolResolver;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

@Service("BlobStorage")
public class BlobStorage implements Storage {

    private static final String CONTAINER_NAME = "file-uploader";
    private static final String RESOURCE_SEARCH_PATTERN_PREFIX = "azure-blob://file-uploader/";

    @Autowired
    private BlobServiceClientBuilder blobServiceClientBuilder;

    @Autowired
    private AzureStorageBlobProtocolResolver storageResourcePatternResolver;

    @PostConstruct
    private void init() {
        BlobServiceClient blobServiceClient = blobServiceClientBuilder.buildClient();
        BlobContainerClient blobContainerClient = blobServiceClient.getBlobContainerClient(CONTAINER_NAME);
        if (!blobContainerClient.exists()) {
            blobContainerClient.create();
        }
    }

    @Override
    public List<String> listFiles() throws IOException {
        Resource[] resources = storageResourcePatternResolver.getResources(RESOURCE_SEARCH_PATTERN_PREFIX + "*");
        return Stream.of(resources)
            .map(Resource::getFilename)
            .collect(Collectors.toList());
    }

    @Override
    public void save(String fileName, InputStream fileInputStream) throws IOException {
        WritableResource resource = (WritableResource) storageResourcePatternResolver.getResource(RESOURCE_SEARCH_PATTERN_PREFIX + fileName);
        try (OutputStream outputStream = resource.getOutputStream()) {
            StreamUtils.copy(fileInputStream, outputStream);
        }
    }

    @Override
    public InputStream read(String fileName) throws IOException {
        Resource resource = storageResourcePatternResolver.getResource(RESOURCE_SEARCH_PATTERN_PREFIX + fileName);
        return resource.getInputStream();
    }
}
