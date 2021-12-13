package com.microsoft.azure.samples.jsf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.microsoft.azure.samples.service.Storage;

import org.apache.commons.io.IOUtils;

@Named
@RequestScoped
public class IndexBean {

    @Inject
    private Storage storage;

    private Part uploadedFile;

    public void download(String fileName) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();  
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        response.reset();
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=\""+ fileName + "\"");

        try (OutputStream outputStream = response.getOutputStream();
            InputStream inputStream = storage.read(fileName)) {
            IOUtils.copy(inputStream, outputStream);
        }

        context.responseComplete();
    }
    
    public void upload() throws IOException {
        try (InputStream fileInputStream = uploadedFile.getInputStream()) {
            String fileName = uploadedFile.getSubmittedFileName();
            long contentLength = uploadedFile.getSize();
            storage.save(fileName, fileInputStream, contentLength);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("File " + fileName + " has been uploaded!"));
        }
    }

    public List<String> listFileNames() {
        return storage.listNames();
    }

    public Part getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(Part uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
}
