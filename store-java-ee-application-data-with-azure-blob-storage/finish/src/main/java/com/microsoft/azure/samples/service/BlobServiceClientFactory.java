package com.microsoft.azure.samples.service;

import javax.enterprise.inject.Produces;
import javax.inject.Singleton;

import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;

public class BlobServiceClientFactory {
    
    @Produces
    @Singleton
    public BlobServiceClient createBlobServiceClient() {
        String connectionString = System.getenv("STORAGE_CONNECTION_STRING");
        if (connectionString == null) {
            throw new IllegalArgumentException("STORAGE_CONNECTION_STRING environment variable is not defined");
        }
        return new BlobServiceClientBuilder()
            .connectionString(connectionString)
            .buildClient();
    }
}
