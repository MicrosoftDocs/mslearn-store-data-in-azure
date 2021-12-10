package com.microsoft.azure.samples.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface Storage {

    List<String> listFiles() throws IOException;

    void save(String fileName, InputStream fileInputStream) throws IOException;

    InputStream read(String fileName) throws IOException;
}
