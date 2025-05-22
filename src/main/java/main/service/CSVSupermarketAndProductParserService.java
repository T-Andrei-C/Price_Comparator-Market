package main.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import main.helpers.CSVFileNameReader;
import main.helpers.DiscountCalculator;
import main.helpers.SimulateDate;
import main.model.Discount;
import main.model.Product;
import main.model.Supermarket;
import main.model.SupermarketHistory;
import main.model.representation.SupermarketCSVRepresentation;
import main.model.user.Notification;
import main.model.user.TargetProduct;
import main.model.user.User;
import main.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CSVSupermarketAndProductParserService {

    private final ProductRepository productRepository;
    private final SupermarketRepository supermarketRepository;
    private final SupermarketHistoryRepository supermarketHistoryRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final TargetProductRepository targetProductRepository;

    public String uploadCSVFile(MultipartFile csvFile) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {

            HeaderColumnNameMappingStrategy<SupermarketCSVRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(SupermarketCSVRepresentation.class);

            CsvToBean<SupermarketCSVRepresentation> csvToBean = new CsvToBeanBuilder<SupermarketCSVRepresentation>(reader)
                    .withSeparator(';')
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            String csvFileName = csvFile.getOriginalFilename();

            Set<SupermarketCSVRepresentation> productsRepresentation = csvToBean.parse()
                    .stream()
                    .map(products -> SupermarketCSVRepresentation.builder()
                            .price(products.getPrice())
                            .publish_date(CSVFileNameReader.getPublishDateName(csvFileName))

                            .name(products.getName())
                            .supermarket_name(CSVFileNameReader.getSupermarketName(csvFileName))
                            .unit(products.getUnit())
                            .brand(products.getBrand())
                            .currency(products.getCurrency())
                            .quantity(products.getQuantity())
                            .category(products.getCategory())
                            .build()
                    ).collect(Collectors.toSet());

            productRepository.saveAll(testProductsUpload(productsRepresentation));
            supermarketRepository.saveAll(testSupermarketUpload(productsRepresentation));
            createNotificationIfPriceDroppedForTargetProduct();

        }
        return "";
    }

    private Set<Product> testProductsUpload(Set<SupermarketCSVRepresentation> supermarketCSVRepresentations) {
        Set<Product> products = new HashSet<>();

        for (SupermarketCSVRepresentation supermarketCSVRepresentation : supermarketCSVRepresentations) {
            products.add(Product.builder()
                    .name(supermarketCSVRepresentation.getName())
                    .unit(supermarketCSVRepresentation.getUnit())
                    .quantity(supermarketCSVRepresentation.getQuantity())
                    .brand(supermarketCSVRepresentation.getBrand())
                    .category(supermarketCSVRepresentation.getCategory())
                    .build());
        }

        List<Product> tableProducts = productRepository.findAll();

        if (!tableProducts.isEmpty()) {
            Set<Product> newProducts = new HashSet<>();
            for (Product product : products) {
                boolean afterCheck = false;
                for (Product tableProduct : tableProducts) {
                    if (product.equals(tableProduct)) {
                        afterCheck = true;
                        break;
                    }
                }

                if (!afterCheck) {
                    newProducts.add(product);
                }
            }
            return newProducts;
        }

        return products;
    }

    private Set<Supermarket> testSupermarketUpload(Set<SupermarketCSVRepresentation> supermarketCSVRepresentations) {
        Set<Supermarket> supermarkets = new HashSet<>();

        for (SupermarketCSVRepresentation supermarketCSVRepresentation : supermarketCSVRepresentations) {
            Product product = getProduct(
                    supermarketCSVRepresentation.getName(),
                    supermarketCSVRepresentation.getUnit(),
                    supermarketCSVRepresentation.getCategory(),
                    supermarketCSVRepresentation.getBrand(),
                    supermarketCSVRepresentation.getQuantity()
            );

            supermarkets.add(Supermarket.builder()
                    .product_price(supermarketCSVRepresentation.getPrice())
                    .name(supermarketCSVRepresentation.getSupermarket_name())
                    .publish_date(supermarketCSVRepresentation.getPublish_date())
                    .currency(supermarketCSVRepresentation.getCurrency())
                    .product(product)
                    .build());

        }

        List<Supermarket> tableSupermarkets = supermarketRepository.findAll();

        if (!supermarkets.isEmpty()) {
            Set<Supermarket> newSupermarkets = new HashSet<>();

            for (Supermarket supermarket : supermarkets) {
                boolean afterCheck = false;
                for (Supermarket tableSupermarket : tableSupermarkets) {
                    if (supermarket.equals(tableSupermarket)) {

                        if (supermarket.getPublish_date().isAfter(tableSupermarket.getPublish_date())) {
                            Supermarket currentSupermarket = supermarketRepository.findSupermarketByFields(supermarket.getName(), supermarket.getProduct());
                            supermarketHistoryRepository.save(
                                    SupermarketHistory.builder()
                                            .publish_date(currentSupermarket.getPublish_date())
                                            .supermarket(currentSupermarket)
                                            .currency(currentSupermarket.getCurrency())
                                            .product_price(currentSupermarket.getProduct_price())
                                            .build()
                            );

                            currentSupermarket.setProduct_price(supermarket.getProduct_price());
                            currentSupermarket.setPublish_date(supermarket.getPublish_date());
                            supermarketRepository.save(currentSupermarket);
                        }

                        if (supermarket.getPublish_date().isEqual(tableSupermarket.getPublish_date())) {
                            Supermarket currentSupermarket = supermarketRepository.findSupermarketByFields(supermarket.getName(), supermarket.getProduct());
                            currentSupermarket.setProduct_price(supermarket.getProduct_price());
                            supermarketRepository.save(currentSupermarket);
                        }

                        afterCheck = true;
                        break;
                    }
                }

                if (!afterCheck) {
                    newSupermarkets.add(supermarket);
                }

            }

            return newSupermarkets;

        }

        return supermarkets;
    }

    private Product getProduct(String name, String unit, String category, String brand, Double quantity) {
        return productRepository.findProductByFields(name, unit, category, brand, quantity).orElse(null);
    }

    private void createNotificationIfPriceDroppedForTargetProduct() {
        List<Supermarket> supermarkets = supermarketRepository.findAll();
        List<User> users = userRepository.findAll();
        List<TargetProduct> targetProductsToRemove = new ArrayList<>();
        LocalDate simulateDate = SimulateDate.getDate();

        for (User user : users) {
            if (user.getTargetProduct() != null) {

                TargetProduct targetProduct = targetProductRepository.findById(user.getTargetProduct().getId()).orElse(null);

                for (Supermarket supermarket : supermarkets) {
                    if (user.getTargetProduct().getProduct().equals(supermarket.getProduct())) {
                        Discount discount = supermarket.getDiscount();

                        if ((simulateDate.isEqual(discount.getFrom_date()) || simulateDate.isAfter(discount.getFrom_date())) &&
                                (simulateDate.isEqual(discount.getTo_date()) || simulateDate.isBefore(discount.getTo_date()))
                        ) {
                            double discountPrice = DiscountCalculator.applyDiscountToProductPrice(supermarket.getProduct_price(), discount.getPercentage_of_discount());
                            if (user.getTargetProduct().getExpectedPrice() >= discountPrice) {
                                Notification notification = Notification.builder()
                                        .message(
                                                "Product " +
                                                        user.getTargetProduct().getProduct().getName() +
                                                        " from the target product is below or equal to the target price in the supermarket " +
                                                        supermarket.getName() +
                                                        " with a price of " +
                                                        discountPrice
                                        )
                                        .user(user)
                                        .seen(false)
                                        .build();

                                notificationRepository.save(notification);
                                targetProductRepository.deleteById(targetProduct.getId());
                                break;
                            }
                        } else {
                            if (user.getTargetProduct().getExpectedPrice() >= supermarket.getProduct_price()) {
                                Notification notification = Notification.builder()
                                        .message(
                                                "Product " +
                                                        user.getTargetProduct().getProduct().getName() +
                                                        " from the target product is below or equal to the target price in the supermarket " +
                                                        supermarket.getName() +
                                                        " with a price of " +
                                                        supermarket.getProduct_price()
                                        )
                                        .user(user)
                                        .seen(false)
                                        .build();

                                notificationRepository.save(notification);
                                targetProductRepository.removeById(targetProduct.getId());
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
