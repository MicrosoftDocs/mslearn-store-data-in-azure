package com.microsoft.azure.samples.service;

import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;

@Singleton
public class BlobStorage implements Storage {

    @PostConstruct
    private void init() {
        throw new UnsupportedOperationException();
    }

    public List<String> listNames() {
        throw new UnsupportedOperationException();
    }

    public void save(String name, InputStream inputStream, long contentLength) {
        throw new UnsupportedOperationException();
    }

    public InputStream read(String name) {
        throw new UnsupportedOperationException();
    }
    
}
