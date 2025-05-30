package main.controller;

import lombok.RequiredArgsConstructor;
import main.service.CSVSupermarketAndProductParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/supermarket/csv")
public class CSVSupermarketAndProductParserController {

    private final CSVSupermarketAndProductParserService CSVSupermarketAndProductParserService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadCSVFiles (@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(CSVSupermarketAndProductParserService.uploadCSVFile(file));
    }
}
