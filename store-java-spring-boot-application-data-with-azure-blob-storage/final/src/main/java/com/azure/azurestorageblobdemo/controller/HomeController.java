package com.azure.azurestorageblobdemo.controller;

import java.io.IOException;
import java.io.InputStream;

import com.azure.azurestorageblobdemo.service.Storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);

    @Qualifier("BlobStorage")
    @Autowired
    private Storage storage;
    
    @GetMapping("/")
    public String index(Model model) {
        try {
            model.addAttribute("files", storage.listFiles());
        } catch (IOException exception) {
            LOGGER.error("Unable to load file list", exception);
            model.addAttribute("message", "File list could not be loaded.");
        }
        return "index";
    }

    @PostMapping("/files")
    public String upload(@RequestParam("file") MultipartFile file, RedirectAttributes attributes) {
        // check if file is empty
        if (file.isEmpty()) {
            attributes.addFlashAttribute("message", "Please select a file to upload.");
            return "redirect:/";
        }

        // normalize the file path
        String fileName = file.getOriginalFilename();
        try (InputStream fileInputStream = file.getInputStream()) {
            storage.save(fileName, fileInputStream);
        } catch (IOException exception) {
            LOGGER.error("Upload failed", exception);
            attributes.addFlashAttribute("message", "Upload failed");
            return "redirect:/";
        }

        attributes.addFlashAttribute("message", "You successfully uploaded " + fileName + '!');

        return "redirect:/";
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<StreamingResponseBody> download(@PathVariable("fileName") String fileName) {
        StreamingResponseBody body = out -> {
            try (InputStream fileStream = storage.read(fileName)) {
                StreamUtils.copy(fileStream, out);
            }
        };
        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(body);
    }
}
