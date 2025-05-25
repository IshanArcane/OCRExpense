package com.example.ExpenseOCR.controller;

import com.example.ExpenseOCR.model.DTO.OcrResponseDTO;
import com.example.ExpenseOCR.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class OcrController {

    @Autowired
    private OcrService ocrService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> handleReceiptUpload(@RequestParam("file") MultipartFile file) {
        try {
            OcrResponseDTO dto = ocrService.extractFieldsFromReceipt(file);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process the image. Please try again."));
        }
    }

}
