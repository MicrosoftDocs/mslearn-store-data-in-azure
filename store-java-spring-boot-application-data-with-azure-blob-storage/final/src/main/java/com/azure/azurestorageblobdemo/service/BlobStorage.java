package com.azure.azurestorageblobdemo.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

@Service("BlobStorage")
public class BlobStorage implements Storage {

    @Override
    public List<String> listFiles() {
        throw new UnsupportedOperationException();
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
