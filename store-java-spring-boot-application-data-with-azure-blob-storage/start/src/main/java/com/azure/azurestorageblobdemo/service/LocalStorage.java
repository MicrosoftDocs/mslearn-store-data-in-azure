package com.azure.azurestorageblobdemo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service("LocalStorage")
public class LocalStorage implements Storage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalStorage.class);

    private Path baseDir;

    @PostConstruct
    private void init() throws IOException {
        baseDir = Files.createTempDirectory("tmpStorage");
    }

    @PreDestroy
    private void shutdown() {
        try {
            FileSystemUtils.deleteRecursively(baseDir);
        }  catch (IOException exception) {
            LOGGER.error("Unable to delete temporary directory", exception);
        }
    }

    @Override
    public List<String> listFiles() throws IOException {
        return Files.list(baseDir)
            .filter(Files::isRegularFile)
            .map(path -> path.toFile().getName())
            .collect(Collectors.toList());
    }

    @Override
    public void save(String fileName, InputStream fileInputStream) throws IOException {
        Path localPath = baseDir.resolve(fileName);
        Files.copy(fileInputStream, localPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public InputStream read(String fileName) throws IOException {
        Path localPath = baseDir.resolve(fileName);
        return Files.newInputStream(localPath);
    }
    
}
