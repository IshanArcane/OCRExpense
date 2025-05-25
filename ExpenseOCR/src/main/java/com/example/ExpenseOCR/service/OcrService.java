package com.example.ExpenseOCR.service;

import com.example.ExpenseOCR.model.DTO.OcrResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class OcrService {

    @Autowired
    private RestTemplate restTemplate;

    public OcrResponseDTO extractFieldsFromReceipt(MultipartFile file) throws IOException {
        // Prepare request
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new MultipartInputStreamFileResource(file.getInputStream(), file.getOriginalFilename()));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Send to Python OCR
        String pythonUrl = "http://localhost:5001/ocr";
        ResponseEntity<Map> response = restTemplate.postForEntity(pythonUrl, requestEntity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("Failed to retrieve OCR data.");
        }

        String ocrText = (String) response.getBody().get("text");
        return parseOcrText(ocrText);
    }

    private OcrResponseDTO parseOcrText(String ocrText) {
        OcrResponseDTO dto = new OcrResponseDTO();
        dto.setCategory("General"); // default for now

        String[] lines = ocrText.split("\\n");

        // Parse amount
        String amount = Arrays.stream(lines)
                .filter(line -> line.toLowerCase().contains("total") || line.contains("₹"))
                .reduce((first, second) -> second) // take last match
                .orElse("Not Found");
        dto.setAmount(extractAmount(amount));

        // Parse date
        String date = Arrays.stream(lines)
                .filter(line -> line.toLowerCase().contains("date"))
                .findFirst()
                .orElse("Not Found");
        dto.setDate(extractDate(date));

        // Extract simple product keywords
        List<String> products = new ArrayList<>();
        for (String line : lines) {
            if (line.toLowerCase().contains("laptop") || line.toLowerCase().contains("bag") || line.toLowerCase().contains("usb")) {
                products.add(line.split(" ")[0]); // basic term
            }
        }
        dto.setProductNames(products.isEmpty() ? List.of("Editable by user") : products);
        dto.setQuantity(1); // default quantity

        return dto;
    }

    private String extractAmount(String text) {
        return text.replaceAll("[^0-9.₹]", "").replace("₹", "₹ ");
    }

    private String extractDate(String text) {
        return text.replaceAll("[^0-9\\-/]", "");
    }
}
