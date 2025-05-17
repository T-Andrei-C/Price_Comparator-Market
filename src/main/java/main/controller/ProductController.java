package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.DTO.ProductHighestDiscountDTO;
import main.model.DTO.ProductHistoryDTO;
import main.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/biggest_discount")
    private Set<ProductHighestDiscountDTO> getAllProductsWithTheirHighestDiscount () {
        return productService.displayHighestDiscountForProducts();
    }

    @GetMapping("/history/filter/brand/{brand}")
    private Set<ProductHistoryDTO> getAllProductsPriceHistoryByBrand (@PathVariable String brand){
        return productService.getProductsPriceHistoryByBrand(brand);
    }

    @GetMapping("/history/filter/supermarket_name/{supermarketName}")
    private Set<ProductHistoryDTO> getAllProductsPriceHistoryBySupermarketName (@PathVariable String supermarketName){
        return productService.getProductsPriceHistoryBySupermarketName(supermarketName);
    }

    @GetMapping("/history/filter/category/{category}")
    private Set<ProductHistoryDTO> getAllProductsPriceHistoryByCategory (@PathVariable String category){
        return productService.getProductsPriceHistoryByCategory(category);
    }

}
