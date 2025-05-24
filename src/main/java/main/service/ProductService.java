package main.service;

import lombok.RequiredArgsConstructor;
import main.helpers.DiscountCalculator;
import main.helpers.SimulateDate;
import main.model.DTO.ProductWithDiscountDTO;
import main.model.Discount;
import main.model.Product;
import main.model.Supermarket;
import main.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Set<ProductWithDiscountDTO> getAllProductsWithTheirBiggestDiscount() {
        List<Product> products = productRepository.findAll();
        LocalDate simulateDate = SimulateDate.getDate();
        Set<ProductWithDiscountDTO> productsBiggestDiscount = new HashSet<>();

        for (Product product : products) {
            List<Discount> biggestDiscounts = new ArrayList<>();

            for (Supermarket supermarket : product.getSupermarkets()) {

                Discount discount = supermarket.getDiscount();
                if (discount != null && DiscountCalculator.checkIfDiscountApplies(simulateDate, discount)) {
                    //first check if the list with biggest discounts is empty and if it is we add the discount to the list
                    if (!biggestDiscounts.isEmpty()) {
                        Discount biggestDiscount = biggestDiscounts.getFirst();

                        //check if current discount is bigger than the one we added in the biggest discounts list
                        //if is true, we empty the list and add the current discount
                        if (discount.getPercentage_of_discount() > biggestDiscount.getPercentage_of_discount()) {
                            biggestDiscounts.clear();
                            biggestDiscounts.add(discount);
                        } else if (discount.getPercentage_of_discount().equals(biggestDiscount.getPercentage_of_discount())) {
                            //we add this one if the discount is equal to the one we have
                            biggestDiscounts.add(discount);
                        }
                    } else {
                        biggestDiscounts.add(discount);
                    }
                }
            }

            for (Discount biggestDiscount : biggestDiscounts) {
                createProductWithDiscount(productsBiggestDiscount, product, biggestDiscount);
            }
        }

        return productsBiggestDiscount;
    }

    public Set<ProductWithDiscountDTO> getProductsByBrand(String brand) {
        Set<ProductWithDiscountDTO> productsWithDiscount = new HashSet<>();
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getBrand().equals(brand))
                .toList();

        createProductWithDiscountFromProducts(tableProducts, productsWithDiscount);
        return productsWithDiscount;
    }

    public Set<ProductWithDiscountDTO> getProductsBySupermarketName(String supermarketName) {
        Set<ProductWithDiscountDTO> productsWithDiscount = new HashSet<>();
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getSupermarkets().stream()
                        //first we find the products that that exist in the supermarket
                        .anyMatch(supermarket -> supermarket.getName().equals(supermarketName))
                        //then we remove the supermarkets from the product that don't have the name
                ).peek(product -> product.getSupermarkets()
                        .removeIf(supermarket -> !supermarket.getName().equals(supermarketName))
                ).toList();

        createProductWithDiscountFromProducts(tableProducts, productsWithDiscount);
        return productsWithDiscount;
    }

    public Set<ProductWithDiscountDTO> getProductsByCategory(String category) {
        Set<ProductWithDiscountDTO> productsWithDiscount = new HashSet<>();
        List<Product> tableProducts = productRepository.findAll().stream()
                .filter(product -> product.getCategory().equals(category))
                .toList();

        createProductWithDiscountFromProducts(tableProducts, productsWithDiscount);
        return productsWithDiscount;
    }

    private void createProductWithDiscount(Set<ProductWithDiscountDTO> productsWithDiscount, Product product, Discount discount) {
        Supermarket supermarket = discount.getSupermarket();
        Double discountPrice = DiscountCalculator.applyDiscountToProductPrice(
                supermarket.getProduct_price(),
                discount.getPercentage_of_discount()
        );

        productsWithDiscount.add(ProductWithDiscountDTO.builder()
                .product(product)
                .supermarket_name(supermarket.getName())
                .product_price(supermarket.getProduct_price())
                .percentage_of_discount(discount.getPercentage_of_discount())
                .discount_price(discountPrice)
                .currency(supermarket.getCurrency())
                .build()
        );
    }

    private void createProductWithDiscountFromProducts(List<Product> products, Set<ProductWithDiscountDTO> productsWithDiscount) {
        LocalDate simulateDate = SimulateDate.getDate();

        for (Product product : products) {
            for (Supermarket supermarket : product.getSupermarkets()) {
                Discount discount = supermarket.getDiscount();

                if (discount != null && DiscountCalculator.checkIfDiscountApplies(simulateDate, discount)) {
                    createProductWithDiscount(productsWithDiscount, product, discount);
                } else {
                    productsWithDiscount.add(
                            ProductWithDiscountDTO.builder()
                                    .product(product)
                                    .supermarket_name(supermarket.getName())
                                    .product_price(supermarket.getProduct_price())
                                    .percentage_of_discount(0d)
                                    .discount_price(supermarket.getProduct_price())
                                    .currency(supermarket.getCurrency())
                                    .build()
                    );
                }
            }
        }
    }

    //Couldn't finish
//    public Set<ProductHistoryDTO> getProductsPriceHistoryBySupermarketName(String supermarketName, LocalDate
//            from, LocalDate to) {
//        List<Product> tableProducts = productRepository.findAll().stream()
//                .filter(product -> product.getSupermarkets().stream()
//                        .anyMatch(supermarket -> supermarket.getName().equals(supermarketName))
//                ).peek(product -> product.getSupermarkets()
//                        .removeIf(supermarket -> !supermarket.getName().equals(supermarketName))
//                ).toList();
//
//        return createProductHistoryForEachProduct(tableProducts, from, to);
//    }


//    private Set<ProductHistoryDTO> createProductHistoryForEachProduct(List<Product> tableProducts, LocalDate
//            from, LocalDate to) {
//
//        Set<ProductHistoryDTO> productsHistory = new HashSet<>();
//
//        for (Product product : tableProducts) {
//            for (Supermarket supermarket : product.getSupermarkets()) {
//                ProductHistoryDTO productHistory = ProductHistoryDTO.builder()
//                        .product(product)
//                        .supermarket_name(supermarket.getName())
//                        .currency(supermarket.getCurrency())
//                        .price_history(new ArrayList<>())
//                        .build();
//
//
//                if (supermarket.getPublish_date().isBefore(to) || supermarket.getPublish_date().isEqual(to)) {
//                    Discount discount = supermarket.getDiscount();
//
//                    if (discount != null) {
//                        if (discount.getFrom_date().isAfter(from) && discount.getTo_date().isBefore(to)) {
//                            if (supermarket.getPublish_date().isAfter(from)) {
//                                createPriceHistoryWithoutDiscount(
//                                        productHistory,
//                                        supermarket,
//                                        supermarket.getPublish_date(),
//                                        discount.getFrom_date()
//                                );
//                            } else {
//                                createPriceHistoryWithoutDiscount(
//                                        productHistory,
//                                        supermarket,
//                                        from,
//                                        discount.getFrom_date()
//                                );
//                            }
//
//                            createPriceHistoryWithDiscount(
//                                    productHistory,
//                                    supermarket,
//                                    discount,
//                                    discount.getFrom_date(),
//                                    discount.getTo_date()
//                            );
//
//                            createPriceHistoryWithoutDiscount(
//                                    productHistory,
//                                    supermarket,
//                                    discount.getTo_date(),
//                                    to
//                            );
//
//                        }
//
//                        if ((discount.getFrom_date().isBefore(from) || discount.getFrom_date().isEqual(from)) && discount.getTo_date().isBefore(to)) {
//                            if (discount.getFrom_date().isBefore(from)) {
//                                createPriceHistoryWithDiscount(
//                                        productHistory,
//                                        supermarket,
//                                        discount,
//                                        from,
//                                        discount.getTo_date()
//                                );
//                            } else {
//                                createPriceHistoryWithDiscount(
//                                        productHistory,
//                                        supermarket,
//                                        discount,
//                                        discount.getFrom_date(),
//                                        discount.getTo_date()
//                                );
//                            }
//
//                            createPriceHistoryWithoutDiscount(
//                                    productHistory,
//                                    supermarket,
//                                    discount.getTo_date(),
//                                    to
//                            );
//
//                        }
//
//                        if (discount.getFrom_date().isAfter(from) && (discount.getTo_date().isEqual(to) || discount.getTo_date().isAfter(to))) {
//
//                            if (supermarket.getPublish_date().isAfter(from)) {
//                                createPriceHistoryWithoutDiscount(
//                                        productHistory,
//                                        supermarket,
//                                        supermarket.getPublish_date(),
//                                        discount.getFrom_date()
//                                );
//                            } else {
//                                createPriceHistoryWithoutDiscount(
//                                        productHistory,
//                                        supermarket,
//                                        from,
//                                        discount.getFrom_date()
//                                );
//                            }
//
//                            createPriceHistoryWithDiscount(
//                                    productHistory,
//                                    supermarket,
//                                    discount,
//                                    discount.getFrom_date(),
//                                    discount.getTo_date()
//                            );
//
//                        }
//
//                    }
//                }
//                productsHistory.add(productHistory);
//            }
//        }
//
//        return productsHistory;
//    }

//    private void createPriceHistoryWithoutDiscount(ProductHistoryDTO productHistory, Supermarket
//            supermarket, LocalDate from_date, LocalDate to_date) {
//        productHistory.getPrice_history().add(
//                PriceHistoryDTO.builder()
//                        .from(from_date)
//                        .to(to_date)
//                        .has_discount(false)
//                        .product_price(supermarket.getProduct_price())
//                        .discounted_price(supermarket.getProduct_price())
//                        .percentage_of_discount(0d)
//                        .build()
//        );
//    }
//
//    private void createPriceHistoryWithDiscount(ProductHistoryDTO productHistory, Supermarket supermarket, Discount
//            discount, LocalDate from_date, LocalDate to_date) {
//
//        productHistory.getPrice_history().add(
//                PriceHistoryDTO.builder()
//                        .from(from_date)
//                        .to(to_date)
//                        .has_discount(true)
//                        .product_price(supermarket.getProduct_price())
//                        .discounted_price(DiscountCalculator.applyDiscountToProductPrice(supermarket.getProduct_price(), discount.getPercentage_of_discount()))
//                        .percentage_of_discount(discount.getPercentage_of_discount())
//                        .build()
//        );
//    }
}
