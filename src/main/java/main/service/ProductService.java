package main.service;

import lombok.RequiredArgsConstructor;
import main.helpers.DiscountCalculator;
import main.helpers.SimulateDate;
import main.model.DTO.PriceHistoryDTO;
import main.model.DTO.ProductHighestDiscountDTO;
import main.model.DTO.ProductHistoryDTO;
import main.model.Discount;
import main.model.Product;
import main.model.Supermarket;
import main.model.SupermarketHistory;
import main.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
        LocalDate simulateDate = SimulateDate.getDate();
        Set<ProductHighestDiscountDTO> productsHighestDiscount = new HashSet<>();

        for (Product product : products) {
            List<Discount> highestDiscounts = new ArrayList<>();
            for (Supermarket supermarket : product.getSupermarkets()) {
                if (supermarket.getDiscount() == null || !checkIfDiscountsIsActive(supermarket.getDiscount(), simulateDate)) {
                    break;
                }

                Discount discount = supermarket.getDiscount();

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

            for (Discount highestDiscount : highestDiscounts) {
                productsHighestDiscount.add(
                        ProductHighestDiscountDTO.builder()
                                .product(product)
                                .supermarket_name(highestDiscount.getSupermarket().getName())
                                .product_price(highestDiscount.getSupermarket().getProduct_price())
                                .percentage_of_discount(highestDiscount.getPercentage_of_discount())
                                .product_price_with_discount(DiscountCalculator.applyDiscountToProductPrice(highestDiscount.getSupermarket().getProduct_price(), highestDiscount.getPercentage_of_discount()))
                                .currency(highestDiscount.getSupermarket().getCurrency())
                                .build()
                );
            }
        }

        return productsHighestDiscount;
    }

    public Set<ProductHistoryDTO> getProductsPriceHistoryByBrand(String brand, LocalDate from, LocalDate to) {
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getId().equals(3L))
                .filter(product -> product.getSupermarkets().stream()
                        .anyMatch(supermarket -> supermarket.getSupermarketHistories().stream()
                                .anyMatch(supermarketHistory -> supermarketHistory.getPublish_date().isBefore(to))
                        )
                ).peek(product -> product.getSupermarkets()
                        .removeIf(supermarket -> supermarket.getSupermarketHistories().stream()
                                .noneMatch(supermarketHistory -> supermarketHistory.getPublish_date().isBefore(to)) &&
                                supermarket.getPublish_date().isAfter(to)
                        )
                )
                .toList();

        return createProductHistoryForEachProduct(tableProducts, from, to);
    }

    public Set<ProductHistoryDTO> getProductsPriceHistoryBySupermarketName(String supermarketName, LocalDate from, LocalDate to) {
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getSupermarkets().stream()
                        .anyMatch(supermarket -> supermarket.getName().equals(supermarketName))
                ).peek(product -> product.getSupermarkets()
                        .removeIf(supermarket -> !supermarket.getName().equals(supermarketName))
                ).toList();

        return createProductHistoryForEachProduct(tableProducts, from, to);
    }

    public Set<ProductHistoryDTO> getProductsPriceHistoryByCategory(String category, LocalDate from, LocalDate to) {
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getCategory().equals(category))
                .toList();

        return createProductHistoryForEachProduct(tableProducts, from, to);
    }

    private Set<ProductHistoryDTO> createProductHistoryForEachProduct(List<Product> tableProducts, LocalDate from, LocalDate to) {

        Set<ProductHistoryDTO> productsHistory = new HashSet<>();

        for (Product product : tableProducts) {
            for (Supermarket supermarket : product.getSupermarkets()) {
                ProductHistoryDTO productHistory = ProductHistoryDTO.builder()
                        .product(product)
                        .supermarket_name(supermarket.getName())
                        .currency(supermarket.getCurrency())
                        .price_history(new ArrayList<>())
                        .build();



                if (supermarket.getPublish_date().isBefore(to) || supermarket.getPublish_date().isEqual(to)) {
                    Discount discount = supermarket.getDiscount();

                    if (discount != null) {
                        if (discount.getFrom_date().isAfter(from) && discount.getTo_date().isBefore(to)) {
                            if (supermarket.getPublish_date().isAfter(from)) {
                                createPriceHistoryWithoutDiscount(
                                        productHistory,
                                        supermarket,
                                        supermarket.getPublish_date(),
                                        discount.getFrom_date()
                                );
                            } else {
                                createPriceHistoryWithoutDiscount(
                                        productHistory,
                                        supermarket,
                                        from,
                                        discount.getFrom_date()
                                );
                            }

                            createPriceHistoryWithDiscount(
                                    productHistory,
                                    supermarket,
                                    discount,
                                    discount.getFrom_date(),
                                    discount.getTo_date()
                            );

                            createPriceHistoryWithoutDiscount(
                                    productHistory,
                                    supermarket,
                                    discount.getTo_date(),
                                    to
                            );

                        }

                        if ((discount.getFrom_date().isBefore(from) || discount.getFrom_date().isEqual(from)) && discount.getTo_date().isBefore(to)) {
                            if (discount.getFrom_date().isBefore(from)) {
                                createPriceHistoryWithDiscount(
                                        productHistory,
                                        supermarket,
                                        discount,
                                        from,
                                        discount.getTo_date()
                                );
                            } else {
                                createPriceHistoryWithDiscount(
                                        productHistory,
                                        supermarket,
                                        discount,
                                        discount.getFrom_date(),
                                        discount.getTo_date()
                                );
                            }

                            createPriceHistoryWithoutDiscount(
                                    productHistory,
                                    supermarket,
                                    discount.getTo_date(),
                                    to
                            );

                        }

                        if (discount.getFrom_date().isAfter(from) && (discount.getTo_date().isEqual(to) || discount.getTo_date().isAfter(to))) {

                            if (supermarket.getPublish_date().isAfter(from)) {
                                createPriceHistoryWithoutDiscount(
                                        productHistory,
                                        supermarket,
                                        supermarket.getPublish_date(),
                                        discount.getFrom_date()
                                );
                            } else {
                                createPriceHistoryWithoutDiscount(
                                        productHistory,
                                        supermarket,
                                        from,
                                        discount.getFrom_date()
                                );
                            }

                            createPriceHistoryWithDiscount(
                                    productHistory,
                                    supermarket,
                                    discount,
                                    discount.getFrom_date(),
                                    discount.getTo_date()
                            );

                        }

                    }
                }
                productsHistory.add(productHistory);
            }
        }

        return productsHistory;
    }

    private void createPriceHistoryWithoutDiscount (ProductHistoryDTO productHistory, Supermarket supermarket, LocalDate from_date, LocalDate to_date) {
        productHistory.getPrice_history().add(
                PriceHistoryDTO.builder()
                        .from(from_date)
                        .to(to_date)
                        .has_discount(false)
                        .product_price(supermarket.getProduct_price())
                        .discounted_price(supermarket.getProduct_price())
                        .percentage_of_discount(0d)
                        .build()
        );
    }

    private void createPriceHistoryWithDiscount (ProductHistoryDTO productHistory, Supermarket supermarket, Discount discount, LocalDate from_date, LocalDate to_date) {
        productHistory.getPrice_history().add(
                PriceHistoryDTO.builder()
                        .from(from_date)
                        .to(to_date)
                        .has_discount(true)
                        .product_price(supermarket.getProduct_price())
                        .discounted_price(DiscountCalculator.applyDiscountToProductPrice(supermarket.getProduct_price(), discount.getPercentage_of_discount()))
                        .percentage_of_discount(discount.getPercentage_of_discount())
                        .build()
        );
    }

    private boolean checkIfDiscountsIsActive(Discount discount, LocalDate simulateDate) {
        return discount.getFrom_date().isEqual(simulateDate) ||
                discount.getTo_date().isEqual(simulateDate) ||
                simulateDate.isAfter(discount.getFrom_date()) ||
                simulateDate.isBefore(discount.getTo_date());
    }
}
