package main.service;

import lombok.RequiredArgsConstructor;
import main.helpers.DiscountCalculator;
import main.model.DTO.PriceHistoryDTO;
import main.model.DTO.ProductHighestDiscountDTO;
import main.model.DTO.ProductHistoryDTO;
import main.model.Discount;
import main.model.Product;
import main.model.Supermarket;
import main.model.SupermarketHistory;
import main.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Set<ProductHighestDiscountDTO> displayHighestDiscountForProducts() {
        List<Product> products = productRepository.findAll();
        LocalDate simulateDate = LocalDate.parse("2025-05-02");
        Set<ProductHighestDiscountDTO> productsHighestDiscount = new HashSet<>();

        for (Product product : products) {
            List<Discount> highestDiscounts = new ArrayList<>();
            for (Supermarket supermarket : product.getSupermarkets()) {
                for (Discount discount : filterDiscountsByDate(supermarket.getDiscounts(), simulateDate)) {

                    if (!highestDiscounts.isEmpty()) {
                        if (discount.getPercentage_of_discount() > highestDiscounts.getFirst().getPercentage_of_discount()) {
                            highestDiscounts.clear();
                            highestDiscounts.add(discount);
                        } else if (discount.getPercentage_of_discount().equals(highestDiscounts.getFirst().getPercentage_of_discount())) {
                            highestDiscounts.add(discount);
                        }
                    } else {
                        highestDiscounts.add(discount);
                    }

                }
            }

            for (Discount highestDiscount : highestDiscounts) {
                productsHighestDiscount.add(
                        ProductHighestDiscountDTO.builder()
                                .product_name(product.getName())
                                .supermarket_name(highestDiscount.getSupermarket().getName())
                                .product_price(highestDiscount.getSupermarket().getProduct_price())
                                .percentage_of_discount(highestDiscount.getPercentage_of_discount())
                                .product_price_with_discount(DiscountCalculator.applyDiscountToProductPrice(highestDiscount.getSupermarket().getProduct_price(), highestDiscount.getPercentage_of_discount()))
                                .brand(product.getBrand())
                                .category(product.getCategory())
                                .currency(highestDiscount.getSupermarket().getCurrency())
                                .quantity(product.getQuantity())
                                .build()
                );
            }
        }

        return productsHighestDiscount;
    }

    public Set<ProductHistoryDTO> getProductsPriceHistoryByBrand(String brand) {
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getBrand().equals(brand))
                .toList();

        return createProductHistoryForEachProduct(tableProducts);
    }

    public Set<ProductHistoryDTO> getProductsPriceHistoryBySupermarketName (String supermarketName) {
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getSupermarkets().stream()
                        .anyMatch(supermarket -> supermarket.getName().equals(supermarketName))
                ).peek(product -> product.getSupermarkets()
                        .removeIf(supermarket -> !supermarket.getName().equals(supermarketName))
                ).toList();

        return createProductHistoryForEachProduct(tableProducts);
    }

    public Set<ProductHistoryDTO> getProductsPriceHistoryByCategory (String category) {
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getCategory().equals(category))
                .toList();

        return createProductHistoryForEachProduct(tableProducts);
    }

    private Set<ProductHistoryDTO> createProductHistoryForEachProduct(List<Product> tableProducts) {

        Set<ProductHistoryDTO> productsHistory = new HashSet<>();

        for (Product tableProduct : tableProducts) {
            for (Supermarket supermarket : tableProduct.getSupermarkets()) {

                ProductHistoryDTO productHistory = ProductHistoryDTO.builder()
                        .product(tableProduct)
                        .supermarket_name(supermarket.getName())
                        .currency(supermarket.getCurrency())
                        .price_history(new ArrayList<>())
                        .build();

                PriceHistoryDTO priceHistory =  PriceHistoryDTO.builder()
                        .publish_date(supermarket.getPublish_date())
                        .product_price(supermarket.getProduct_price())
                        .discounted_price(supermarket.getProduct_price())
                        .percentage_of_discount(0d)
                        .had_discount(false)
                        .is_current(true)
                        .build();

                for (Discount discount : filterDiscountsByDate(supermarket.getDiscounts(), supermarket.getPublish_date())) {

                    priceHistory.setDiscounted_price(DiscountCalculator.applyDiscountToProductPrice(
                            priceHistory.getDiscounted_price(), discount.getPercentage_of_discount()
                    ));
                    priceHistory.setHad_discount(true);
                    priceHistory.setPercentage_of_discount(discount.getPercentage_of_discount());

                }

                productHistory.getPrice_history().add(priceHistory);

                for (SupermarketHistory supermarketHistory : supermarket.getSupermarketHistories()) {
                    priceHistory =  PriceHistoryDTO.builder()
                            .publish_date(supermarketHistory.getPublish_date())
                            .product_price(supermarketHistory.getProduct_price())
                            .discounted_price(supermarketHistory.getProduct_price())
                            .had_discount(false)
                            .is_current(false)
                            .build();

                    for (Discount discount : filterDiscountsByDate(supermarket.getDiscounts(), supermarketHistory.getPublish_date())) {

                        priceHistory.setDiscounted_price(DiscountCalculator.applyDiscountToProductPrice(
                                priceHistory.getDiscounted_price(), discount.getPercentage_of_discount()
                        ));
                        priceHistory.setHad_discount(true);
                        priceHistory.setPercentage_of_discount(discount.getPercentage_of_discount());

                    }

                    productHistory.getPrice_history().add(priceHistory);

                }

                productsHistory.add(productHistory);

            }
        }
        return productsHistory;
    }

    private Set<Discount> filterDiscountsByDate(Set<Discount> discounts, LocalDate simulateDate) {
        return discounts.stream()
                .filter(discount ->
                        discount.getFrom_date().isEqual(simulateDate) ||
                                discount.getTo_date().isEqual(simulateDate) ||
                                simulateDate.isAfter(discount.getFrom_date()) ||
                                simulateDate.isBefore(discount.getTo_date())
                ).collect(Collectors.toSet());
    }
}
