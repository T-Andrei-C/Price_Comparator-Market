package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.DTO.ProductWithDiscountDTO;
import main.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/price_comparator/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/biggest_discount")
    private Set<ProductWithDiscountDTO> getAllProductsWithTheirBiggestDiscount () {
        return productService.getAllProductsWithTheirBiggestDiscount();
    }

    @GetMapping("/filter/brand/{brand}")
    private Set<ProductWithDiscountDTO> getAllProductsByBrand (@PathVariable String brand){
        return productService.getProductsByBrand(brand);
    }

    @GetMapping("/filter/supermarket_name/{supermarketName}")
    private Set<ProductWithDiscountDTO> getAllProductsBySupermarketName (@PathVariable String supermarketName){
        return productService.getProductsBySupermarketName(supermarketName);
    }

    @GetMapping("/filter/category/{category}")
    private Set<ProductWithDiscountDTO> getAllProductsByCategory (@PathVariable String category){
        return productService.getProductsByCategory(category);
    }

}
