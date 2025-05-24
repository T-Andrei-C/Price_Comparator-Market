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
import main.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CSVSupermarketAndProductParserService {

    private final ProductRepository productRepository;
    private final SupermarketRepository supermarketRepository;
    private final SupermarketHistoryRepository supermarketHistoryRepository;
    private final NotificationRepository notificationRepository;
    private final TargetProductRepository targetProductRepository;

    public String uploadCSVFile(MultipartFile csvFile) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {

            if (!Objects.requireNonNull(csvFile.getOriginalFilename()).contains("discount")) {
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
                                .publish_date(CSVFileNameReader.getPublishDate(csvFileName))
                                .name(products.getName())
                                .supermarket_name(CSVFileNameReader.getSupermarketName(csvFileName))
                                .unit(products.getUnit())
                                .brand(products.getBrand())
                                .currency(products.getCurrency())
                                .quantity(products.getQuantity())
                                .category(products.getCategory())
                                .build()
                        ).collect(Collectors.toSet());

                productRepository.saveAll(uploadProducts(productsRepresentation));
                supermarketRepository.saveAll(uploadSupermarkets(productsRepresentation));
                createNotificationIfPriceDroppedForTargetProduct();

                return "Products and supermarkets added successfully";
            }

        }
        return "Something went wrong";
    }

    private Set<Product> uploadProducts(Set<SupermarketCSVRepresentation> supermarketCSVRepresentations) {
        Set<Product> products = transformSupermarketCsvRepresentationIntoProducts(supermarketCSVRepresentations);
        List<Product> tableProducts = productRepository.findAll();

        if (!tableProducts.isEmpty()) {
            Set<Product> newProducts = new HashSet<>();

            for (Product product : products) {
                boolean checkIfProductExist = tableProducts.stream()
                        .anyMatch(tableProduct -> product.equals(tableProduct));

                if (!checkIfProductExist) {
                    newProducts.add(product);
                }
            }
            return newProducts;
        }

        return products;
    }

    private Set<Supermarket> uploadSupermarkets(Set<SupermarketCSVRepresentation> supermarketCSVRepresentations) {
        Set<Supermarket> supermarkets = transformSupermarketCsvRepresentationIntoSupermarkets(supermarketCSVRepresentations);
        List<Supermarket> tableSupermarkets = supermarketRepository.findAll();

        if (!supermarkets.isEmpty()) {
            Set<Supermarket> newSupermarkets = new HashSet<>();

            for (Supermarket supermarket : supermarkets) {
                boolean checkIfSupermarketExist = false;
                for (Supermarket tableSupermarket : tableSupermarkets) {
                    if (supermarket.equals(tableSupermarket)) {
                        Supermarket currentSupermarket = supermarketRepository.findSupermarketByFields(supermarket.getName(), supermarket.getProduct());

                        if (supermarket.getPublish_date().isAfter(tableSupermarket.getPublish_date())) {
                            createSupermarketHistory(currentSupermarket);
                            updateSupermarket(currentSupermarket, supermarket);
                        }

                        if (supermarket.getPublish_date().isEqual(tableSupermarket.getPublish_date())) {
                            updateSupermarket(currentSupermarket, supermarket);
                        }

                        checkIfSupermarketExist = true;
                        break;
                    }
                }

                if (!checkIfSupermarketExist) {
                    newSupermarkets.add(supermarket);
                }

            }

            return newSupermarkets;

        }

        return supermarkets;
    }


    private void createNotificationIfPriceDroppedForTargetProduct() {
        List<Supermarket> supermarkets = supermarketRepository.findAll();
        List<TargetProduct> targetProducts = targetProductRepository.findAll();
        LocalDate simulateDate = SimulateDate.getDate();

        for (TargetProduct targetProduct : targetProducts) {
            for (Supermarket supermarket : supermarkets) {
                if (targetProduct.getProduct().equals(supermarket.getProduct())) {
                    Discount discount = supermarket.getDiscount();

                    if (discount != null && DiscountCalculator.checkIfDiscountApplies(simulateDate, discount)) {
                        double discountPrice = DiscountCalculator.applyDiscountToProductPrice(
                                supermarket.getProduct_price(),
                                discount.getPercentage_of_discount()
                        );

                        if (targetProduct.getExpectedPrice() >= discountPrice) {

                            createNotification(targetProduct, supermarket, discountPrice);

                            targetProductRepository.delete(targetProduct);
                            break;
                        }
                    } else {
                        if (targetProduct.getExpectedPrice() >= supermarket.getProduct_price()) {

                            createNotification(targetProduct, supermarket, supermarket.getProduct_price());

                            targetProductRepository.delete(targetProduct);
                            break;
                        }
                    }
                }
            }
        }
    }

    private Set<Product> transformSupermarketCsvRepresentationIntoProducts(Set<SupermarketCSVRepresentation> supermarketCSVRepresentations) {
        return supermarketCSVRepresentations.stream()
                .map(supermarketCSVRepresentation -> Product.builder()
                        .name(supermarketCSVRepresentation.getName())
                        .unit(supermarketCSVRepresentation.getUnit())
                        .quantity(supermarketCSVRepresentation.getQuantity())
                        .brand(supermarketCSVRepresentation.getBrand())
                        .category(supermarketCSVRepresentation.getCategory())
                        .build()
                ).collect(Collectors.toSet());
    }

    private Product getProductForSupermarket(SupermarketCSVRepresentation supermarketCSVRepresentation) {
        return productRepository.findProductByFields(
                supermarketCSVRepresentation.getName(),
                supermarketCSVRepresentation.getUnit(),
                supermarketCSVRepresentation.getCategory(),
                supermarketCSVRepresentation.getBrand(),
                supermarketCSVRepresentation.getQuantity()
        ).orElse(null);
    }

    private Set<Supermarket> transformSupermarketCsvRepresentationIntoSupermarkets(Set<SupermarketCSVRepresentation> supermarketCSVRepresentations) {
        return supermarketCSVRepresentations.stream()
                .map(supermarketCSVRepresentation -> Supermarket.builder()
                        .product_price(supermarketCSVRepresentation.getPrice())
                        .name(supermarketCSVRepresentation.getSupermarket_name())
                        .publish_date(supermarketCSVRepresentation.getPublish_date())
                        .currency(supermarketCSVRepresentation.getCurrency())
                        .product(getProductForSupermarket(supermarketCSVRepresentation))
                        .build()
                ).collect(Collectors.toSet());
    }

    private void updateSupermarket(Supermarket currentSupermarket, Supermarket newSupermarket) {
        currentSupermarket.setProduct_price(newSupermarket.getProduct_price());
        currentSupermarket.setPublish_date(newSupermarket.getPublish_date());
        supermarketRepository.save(currentSupermarket);
    }

    private void createSupermarketHistory(Supermarket supermarket) {
        supermarketHistoryRepository.save(
                SupermarketHistory.builder()
                        .publish_date(supermarket.getPublish_date())
                        .supermarket(supermarket)
                        .currency(supermarket.getCurrency())
                        .product_price(supermarket.getProduct_price())
                        .build()
        );
    }

    private void createNotification(TargetProduct targetProduct, Supermarket supermarket, Double price) {
        Notification notification = Notification.builder()
                .message(
                        "Product " +
                                targetProduct.getProduct().getName() +
                                " from the target product is below or equal to the target price in the supermarket " +
                                supermarket.getName() +
                                " with a price of " +
                                price
                )
                .user(targetProduct.getUser())
                .seen(false)
                .build();

        notificationRepository.save(notification);
    }
}
