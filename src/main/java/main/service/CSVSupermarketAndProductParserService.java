package main.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import main.helpers.CSVFileNameReader;
import main.model.Product;
import main.model.Supermarket;
import main.model.SupermarketHistory;
import main.model.representation.ProductCSVRepresentation;
import main.repository.ProductRepository;
import main.repository.SupermarketHistoryRepository;
import main.repository.SupermarketRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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

    public String uploadCSVFile(MultipartFile csvFile) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {

            HeaderColumnNameMappingStrategy<ProductCSVRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ProductCSVRepresentation.class);

            CsvToBean<ProductCSVRepresentation> csvToBean = new CsvToBeanBuilder<ProductCSVRepresentation>(reader)
                    .withSeparator(';')
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            String csvFileName = csvFile.getOriginalFilename();

            Set<ProductCSVRepresentation> productsRepresentation = csvToBean.parse()
                    .stream()
                    .map(products -> ProductCSVRepresentation.builder()
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
        }
        return "";
    }

    private Set<Product> testProductsUpload (Set<ProductCSVRepresentation> productCSVRepresentations) {
        Set<Product> products = new HashSet<>();

        for (ProductCSVRepresentation productCSVRepresentation : productCSVRepresentations) {
            products.add(Product.builder()
                    .name(productCSVRepresentation.getName())
                    .unit(productCSVRepresentation.getUnit())
                    .quantity(productCSVRepresentation.getQuantity())
                    .brand(productCSVRepresentation.getBrand())
                    .category(productCSVRepresentation.getCategory())
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

    private Set<Supermarket> testSupermarketUpload (Set<ProductCSVRepresentation> productCSVRepresentations) {
        Set<Supermarket> supermarkets = new HashSet<>();

        for (ProductCSVRepresentation productCSVRepresentation : productCSVRepresentations) {
            Product product = getProduct(
                    productCSVRepresentation.getName(),
                    productCSVRepresentation.getUnit(),
                    productCSVRepresentation.getCategory(),
                    productCSVRepresentation.getBrand(),
                    productCSVRepresentation.getQuantity()
            );

            supermarkets.add(Supermarket.builder()
                    .product_price(productCSVRepresentation.getPrice())
                    .name(productCSVRepresentation.getSupermarket_name())
                    .publish_date(productCSVRepresentation.getPublish_date())
                    .currency(productCSVRepresentation.getCurrency())
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
                        if (supermarket.getPublish_date().isEqual(tableSupermarket.getPublish_date())) {
                            if (!supermarket.getProduct_price().equals(tableSupermarket.getProduct_price())) {

                                Supermarket currentSupermarket = supermarketRepository.findSupermarketByFields(supermarket.getName(), supermarket.getCurrency(), supermarket.getProduct());
                                supermarketHistoryRepository.save(
                                        SupermarketHistory.builder()
                                                .publish_date(currentSupermarket.getPublish_date())
                                                .product(currentSupermarket.getProduct())
                                                .supermarket_name(currentSupermarket.getName())
                                                .currency(currentSupermarket.getCurrency())
                                                .product_price(currentSupermarket.getProduct_price())
                                                .build()
                                );

                                currentSupermarket.setProduct_price(supermarket.getProduct_price());
                                supermarketRepository.save(currentSupermarket);
                            }
                        }

                        if (supermarket.getPublish_date().isAfter(tableSupermarket.getPublish_date())) {

                            Supermarket currentSupermarket = supermarketRepository.findSupermarketByFields(supermarket.getName(), supermarket.getCurrency(), supermarket.getProduct());
                            supermarketHistoryRepository.save(
                                    SupermarketHistory.builder()
                                            .publish_date(currentSupermarket.getPublish_date())
                                            .product(currentSupermarket.getProduct())
                                            .supermarket_name(currentSupermarket.getName())
                                            .currency(currentSupermarket.getCurrency())
                                            .product_price(currentSupermarket.getProduct_price())
                                            .build()
                            );
                            currentSupermarket.setPublish_date(supermarket.getPublish_date());

                            if (!supermarket.getProduct_price().equals(tableSupermarket.getProduct_price())) {
                                currentSupermarket.setProduct_price(supermarket.getProduct_price());
                            }

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

    private Product getProduct (String name, String unit, String category, String brand, Double quantity) {
        return productRepository.findProductByFields(name, unit, category, brand, quantity).orElse(null);
    }

}
