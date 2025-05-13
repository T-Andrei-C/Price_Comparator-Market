package main.controller;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import main.service.CSVParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/csv")
public class CSVParserController {

    private final CSVParserService CSVParserService;

    @PostMapping(value = "/upload", consumes = "multipart/form_data")
    private ResponseEntity<String> uploadCSVFiles (@RequestParam("file") MultipartFile csvFile){
        return ResponseEntity.ok("d");
    }
}
