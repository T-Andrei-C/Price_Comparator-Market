package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.DTO.ProductValuePerUnitDTO;
import main.service.SupermarketService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/supermarkets")
public class SupermarketController {

    private final SupermarketService supermarketService;

    @GetMapping("/supermarket/{supermarketName}/product/{productId}")
    private ProductValuePerUnitDTO getSupermarketProductUnitSubstitution (@PathVariable String supermarketName, @PathVariable Long productId) {
        return supermarketService.getSupermarketProductUnitSubstitution(supermarketName, productId);
    }
}
