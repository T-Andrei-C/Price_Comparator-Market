package main.controller;

import lombok.RequiredArgsConstructor;
import main.service.CSVDiscountParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/discount/csv")
public class CSVDiscountParserController {

    private final CSVDiscountParserService CSVdiscountParserService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCSVFiles (@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(CSVdiscountParserService.uploadCSVFile(file));
    }
}
