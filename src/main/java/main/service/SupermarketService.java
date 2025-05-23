package main.service;

import lombok.RequiredArgsConstructor;
import main.helpers.DiscountCalculator;
import main.helpers.SimulateDate;
import main.helpers.UnitAndPriceConvertor;
import main.model.DTO.ProductValuePerUnitDTO;
import main.model.Discount;
import main.model.Product;
import main.model.Supermarket;
import main.repository.ProductRepository;
import main.repository.SupermarketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class SupermarketService {

    private final SupermarketRepository supermarketRepository;
    private final ProductRepository productRepository;

    public ProductValuePerUnitDTO getSupermarketProductUnitSubstitution(String supermarketName, Long productId) {

        LocalDate simulateDate = SimulateDate.getDate();
        Product product = productRepository.findById(productId).orElse(null);
        Supermarket supermarket = supermarketRepository.findSupermarketByFields(supermarketName, product);
        Discount discount = supermarket.getDiscount();

        if (discount != null && DiscountCalculator.checkIfDiscountApplies(simulateDate, discount)) {
            Double discountPrice = DiscountCalculator.applyDiscountToProductPrice(
                    supermarket.getProduct_price(),
                    discount.getPercentage_of_discount()
            );

            return UnitAndPriceConvertor.convertPriceToNewUnit(createProductValuePerUnit(product, discountPrice));
        }

        return UnitAndPriceConvertor.convertPriceToNewUnit(createProductValuePerUnit(product, supermarket.getProduct_price()));

    }

    private ProductValuePerUnitDTO createProductValuePerUnit(Product product, Double currentPrice) {
        return ProductValuePerUnitDTO.builder()
                .original_unit(product.getUnit())
                .original_quantity(product.getQuantity())
                .original_unit_price(currentPrice)
                .converted_unit("")
                .converted_quantity(1d)
                .converted_unit_price(1d)
                .build();
    }

}
