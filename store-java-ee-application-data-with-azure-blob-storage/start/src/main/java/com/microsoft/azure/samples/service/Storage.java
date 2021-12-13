package com.microsoft.azure.samples.service;

import java.io.InputStream;
import java.util.List;

public interface Storage {

    List<String> listFiles();

    void save(String fileName, InputStream fileInputStream);

    InputStream read(String fileName);
}
