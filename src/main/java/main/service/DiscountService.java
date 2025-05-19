package main.service;

import lombok.RequiredArgsConstructor;
import main.helpers.DiscountCalculator;
import main.helpers.SimulateDate;
import main.model.DTO.ProductHighestDiscountDTO;
import main.model.Discount;
import main.model.Product;
import main.model.Supermarket;
import main.repository.DiscountRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountRepository discountRepository;

    public Set<ProductHighestDiscountDTO> getNewestDiscounts () {
        Set<ProductHighestDiscountDTO> newestDiscounts = new HashSet<>();
        LocalDate simulateDate = SimulateDate.getDate();

        Set<Discount> discounts = discountRepository.findAll().stream()
                .filter(discount -> discount.getFrom_date().isEqual(simulateDate))
                .collect(Collectors.toSet());

        for (Discount discount : discounts) {
            Supermarket discountSupermarket = discount.getSupermarket();

            newestDiscounts.add(
                    ProductHighestDiscountDTO.builder()
                            .product(discountSupermarket.getProduct())
                            .supermarket_name(discountSupermarket.getName())
                            .product_price(discountSupermarket.getProduct_price())
                            .percentage_of_discount(discount.getPercentage_of_discount())
                            .product_price_with_discount(DiscountCalculator.applyDiscountToProductPrice(discountSupermarket.getProduct_price(), discount.getPercentage_of_discount()))
                            .currency(discountSupermarket.getCurrency())
                            .build()
            );
        }

        return newestDiscounts;
    }
}
