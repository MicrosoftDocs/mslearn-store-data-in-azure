package com.microsoft.azure.samples.service;

import java.io.InputStream;
import java.util.List;

public interface Storage {

    List<String> listNames();

    void save(String name, InputStream inputStream, long contentLength);

    InputStream read(String name);
}
