package main.service;

import lombok.RequiredArgsConstructor;
import main.helpers.DiscountCalculator;
import main.helpers.SimulateDate;
import main.helpers.UnitAndPriceConvertor;
import main.model.DTO.ProductValuePerUnitDTO;
import main.model.Discount;
import main.model.Product;
import main.model.Supermarket;
import main.repository.DiscountRepository;
import main.repository.ProductRepository;
import main.repository.SupermarketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SupermarketService {

    private final SupermarketRepository supermarketRepository;
    private final ProductRepository productRepository;
    private final DiscountRepository discountRepository;

    public ProductValuePerUnitDTO getSupermarketProductUnitSubstitution(String supermarketName, Long productId) {
        Product product = productRepository.findById(productId).orElse(null);
        Supermarket supermarket = supermarketRepository.findSupermarketByFields(supermarketName, product);
        System.out.print(supermarket.getId());
        LocalDate simulateDate = SimulateDate.getDate();
        Discount discount = discountRepository.getDiscountByFields(supermarket, simulateDate);
        Double priceWithDiscount = DiscountCalculator.applyDiscountToProductPrice(supermarket.getProduct_price(), discount.getPercentage_of_discount());

        return UnitAndPriceConvertor.convertPriceToNewUnit(
                ProductValuePerUnitDTO.builder()
                        .original_unit(product.getUnit())
                        .original_quantity(product.getQuantity())
                        .original_unit_price(priceWithDiscount)
                        .converted_unit("")
                        .converted_quantity(1d)
                        .converted_unit_price(1d)
                        .build()
        );
    }

}
