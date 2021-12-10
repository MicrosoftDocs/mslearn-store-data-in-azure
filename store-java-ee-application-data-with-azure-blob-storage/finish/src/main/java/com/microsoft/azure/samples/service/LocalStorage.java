package com.microsoft.azure.samples.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Named;
import javax.inject.Singleton;

import org.apache.commons.io.file.PathUtils;
import org.jboss.logging.Logger;

@Singleton
@Named("LocalStorage")
public class LocalStorage implements Storage {

    private static final Logger LOGGER = Logger.getLogger(LocalStorage.class);

    private Path baseDir;

    @PostConstruct
    private void init() {
        try {
            baseDir = Files.createTempDirectory("tmpStorage");
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    @PreDestroy
    private void shutdown() {
        try {
            PathUtils.deleteDirectory(baseDir);
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
