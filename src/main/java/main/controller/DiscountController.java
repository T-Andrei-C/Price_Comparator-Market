package main.controller;

import lombok.RequiredArgsConstructor;
import main.model.DTO.ProductHighestDiscountDTO;
import main.service.DiscountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/price_comparator/api/v1/discounts")
public class DiscountController {

    private final DiscountService discountService;

    @GetMapping("/newest")
    private Set<ProductHighestDiscountDTO> getNewestDiscounts () {
        return discountService.getNewestDiscounts();
    }

}
