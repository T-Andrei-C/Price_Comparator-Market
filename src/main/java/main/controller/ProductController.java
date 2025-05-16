package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.DTO.ProductHighestDiscountDTO;
import main.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
