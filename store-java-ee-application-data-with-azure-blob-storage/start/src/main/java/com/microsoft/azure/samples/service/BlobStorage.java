package com.microsoft.azure.samples.service;

import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class BlobStorage {

    @PostConstruct
    private void init() {
        throw new UnsupportedOperationException();
    }

    public List<String> listFiles() {
        throw new UnsupportedOperationException();
    }

    public void save(String fileName, InputStream fileInputStream) {
        throw new UnsupportedOperationException();
    }

    public InputStream read(String fileName) {
        throw new UnsupportedOperationException();
    }
    
}
