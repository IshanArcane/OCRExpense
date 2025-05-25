package com.example.ExpenseOCR.service;

import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wrapper around InputStreamResource to provide a filename for multipart uploads.
 */
public class MultipartInputStreamFileResource extends InputStreamResource {

    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    /**
     * Return -1 if content length is unknown.
     */
    @Override
    public long contentLength() throws IOException {
        return -1; // or return super.contentLength() if you want to delegate
    }
}
