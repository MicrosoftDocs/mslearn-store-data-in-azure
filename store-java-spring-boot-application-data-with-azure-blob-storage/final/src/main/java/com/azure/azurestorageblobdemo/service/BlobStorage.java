package com.azure.azurestorageblobdemo.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import com.azure.spring.autoconfigure.storage.resource.AzureStorageResourcePatternResolver;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service("BlobStorage")
public class BlobStorage implements Storage {

    private static final String CONTAINER_NAME = "file-uploader";
    private static final String RESOURCE_SEARCH_PATTERN_PREFIX = "azure-blob://file-uploader/";

    @Autowired
    private BlobServiceClientBuilder blobServiceClientBuilder;

    private BlobServiceClient blobServiceClient;

    @PostConstruct
    private void init() {
        blobServiceClient = blobServiceClientBuilder.buildClient();
    }

    @Override
    public List<String> listFiles() {
        AzureStorageResourcePatternResolver storageResourcePatternResolver = new AzureStorageResourcePatternResolver(blobServiceClient);
        try {
            Resource[] resources = storageResourcePatternResolver.getResources(RESOURCE_SEARCH_PATTERN_PREFIX + "*");
            return Stream.of(resources)
                .map(Resource::getFilename)
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public void save(String fileName, InputStream fileInputStream) {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream read(String fileName) {
        throw new UnsupportedOperationException();
    }
    
}
