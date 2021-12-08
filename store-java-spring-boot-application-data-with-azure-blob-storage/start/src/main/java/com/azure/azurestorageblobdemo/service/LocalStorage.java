package com.azure.azurestorageblobdemo.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

@Service
public class LocalStorage implements Storage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalStorage.class);

    private Path baseDir;

    @PostConstruct
    private void init() {
        try {
            baseDir = Files.createTempDirectory("tmpStorage");
        } catch (IOException exception) {
            throw new IllegalStateException("Can't work without local directory", exception);
        }
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
    public List<String> listFiles() {
        try {
            return Files.list(baseDir)
                .filter(Files::isRegularFile)
                .map(path -> path.toFile().getName())
                .collect(Collectors.toList());
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public void save(String fileName, InputStream fileInputStream) {
        Path localPath = baseDir.resolve(fileName);
        try {
            Files.copy(fileInputStream, localPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    public InputStream read(String fileName) {
        Path localPath = baseDir.resolve(fileName);
        try {
            return Files.newInputStream(localPath);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }
    
}
