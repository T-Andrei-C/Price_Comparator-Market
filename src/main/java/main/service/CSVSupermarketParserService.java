package main.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import main.model.Product;
import main.model.Supermarket;
import main.model.representation.ProductCSVRepresentation;
import main.respository.DiscountRepository;
//import main.respository.ProductRepository;
import main.respository.SupermarketRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CSVParserService {

    private final DiscountRepository discountRepository;
    //    private final ProductRepository productRepository;
    private final SupermarketRepository supermarketRepository;

//    public String uploadCSVFile(MultipartFile csvFile) throws IOException {
//        try (Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
//
//            HeaderColumnNameMappingStrategy<ProductCSVRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
//            strategy.setType(ProductCSVRepresentation.class);
//
//            CsvToBean<ProductCSVRepresentation> csvToBean = new CsvToBeanBuilder<ProductCSVRepresentation>(reader)
//                    .withSeparator(';')
//                    .withMappingStrategy(strategy)
//                    .withIgnoreLeadingWhiteSpace(true)
//                    .withIgnoreEmptyLine(true)
//                    .build();
//
////            System.out.print(csvFile.getOriginalFilename());
////            System.out.print(csvToBean.parse().);
//
//            String[] test = csvFile.getOriginalFilename().split("_");
//
//
//            Set<ProductCSVRepresentation> productsSet = csvToBean.parse()
//                    .stream()
//                    .map(products -> ProductCSVRepresentation.builder()
//                            .name(products.getName())
//                            .brand(products.getBrand())
//                            .category(products.getCategory())
//                            .unit(products.getUnit())
//                            .quantity(products.getQuantity())
//                            .supermarket_name(test[0])
//                            .publish_date(LocalDate.of(2025, 5, 1))
//                            .price(products.getPrice())
//                            .currency(products.getCurrency())
//                            .build()
//                    ).collect(Collectors.toSet());
//
//            Set<Product> products = new HashSet<>();
//            Set<Supermarket> supermarkets = new HashSet<>();
//
//            for (ProductCSVRepresentation product : productsSet) {
//                products.add(Product.builder()
//                        .name(product.getName())
//                        .category(product.getCategory())
//                        .brand(product.getBrand())
//                        .quantity(product.getQuantity())
//                        .unit(product.getUnit())
//                        .build());
//            }
//
//            productRepository.saveAll(verifyIfProductsExistInTable(products));
//
//            for (ProductCSVRepresentation product : productsSet) {
//                Product supermarketProduct = findProductByFields(
//                        product.getName(),
//                        product.getCategory(),
//                        product.getUnit(),
//                        product.getBrand(),
//                        product.getQuantity()
//                );
//
//                supermarkets.add(Supermarket.builder()
//                        .name(product.getSupermarket_name())
//                        .publish_date(product.getPublish_date())
//                        .product_price(product.getPrice())
//                        .currency(product.getCurrency())
//                        .product(supermarketProduct)
//                        .build());
//            }
//
//
//////            System.out.print(products + "\n");
//////            System.out.print(supermarkets + "\n");
//////            System.out.print(verifyIfSupermarketsExistInTable(supermarkets) + "\n");
//            supermarketRepository.saveAll(verifyIfSupermarketsExistInTable(supermarkets));
//        }
//        return "";
//    }
//
//    private Set<Product> verifyIfProductsExistInTable(Set<Product> products) {
//        List<Product> productsTable = productRepository.findAll();
//        if (!productsTable.isEmpty()){
//            Set<Product> newProducts = new HashSet<>();
//
//            for (Product product : products){
//                boolean afterCheck = false;
//                for (Product tableProduct : productsTable){
//                    if (checkProductsFields(tableProduct, product)) {
//                        afterCheck = true;
//                        break;
//                    }
//                }
//
//                if (!afterCheck) {
//                    newProducts.add(product);
//                }
//            }
//            return newProducts;
//        }
//
//        return products;
//    }
//
////    private Set<Product> verifyIfProductsExistInTable(Set<Product> products) {
////        List<Product> productsTable = productRepository.findAll();
////        if (!productsTable.isEmpty()) {
////            Set<Product> newProducts = new HashSet<>();
////
////            for (Product product : products) {
////                boolean afterCheck = false;
////                for (Product tableProduct : productsTable) {
////                    if (checkProductsFields(tableProduct, product)) {
////                        afterCheck = true;
////                        break;
////                    }
////                }
////                if (!afterCheck) {
////                    newProducts.add(product);
////                }
////            }
////            return newProducts;
////        }
////        return products;
////    }
////
//    private boolean checkProductsFields(Product tableProduct, Product newProduct) {
//        return tableProduct.getName().equals(newProduct.getName()) &&
//                tableProduct.getBrand().equals(newProduct.getBrand()) &&
//                tableProduct.getCategory().equals(newProduct.getCategory()) &&
//                tableProduct.getQuantity().doubleValue() == newProduct.getQuantity().doubleValue() &&
//                tableProduct.getUnit().equals(newProduct.getUnit());
//    }
////
//    private Set<Supermarket> verifyIfSupermarketsExistInTable(Set<Supermarket> supermarkets) {
//        List<Supermarket> supermarketsTable = supermarketRepository.findAll();
//
////        if (!supermarketsTable.isEmpty()) {
////            Set<Supermarket> newSupermarkets = new HashSet<>();
//

    /// /            for (Supermarket supermarket : supermarkets) {
    /// /                boolean afterCheck = false;
    /// /                for (Supermarket tableSupermarket : supermarketsTable) {
    /// /                    if (tableSupermarket.getName().equals(supermarket.getName()) &&
    /// /                            checkIfProductIsTheSameProduct(tableSupermarket.getProduct(), supermarket.getProduct())
    /// /                    ) {
    /// /                        if (tableSupermarket.getPublish_date().isEqual(supermarket.getPublish_date()) ||
    /// /                                tableSupermarket.getPublish_date().isBefore(supermarket.getPublish_date())
    /// /                        ) {
    /// /                            Supermarket updatedSupermarket = supermarketRepository.findById(tableSupermarket.getId()).orElse(null);
    /// /                            if (updatedSupermarket != null) {
    /// /                                updatedSupermarket.setPublish_date(supermarket.getPublish_date());
    /// /                                updatedSupermarket.setProduct_price(supermarket.getProduct_price());
    /// /                                supermarketRepository.save(updatedSupermarket);
    /// /                            }
    /// /                        }
    /// /                        afterCheck = true;
    /// /                    }
    /// /                }
    /// /
    /// /                if (!afterCheck) {
    /// /                    newSupermarkets.add(supermarket);
    /// /                }
    /// /            }
    /// /            return newSupermarkets;
    /// /        }
//        return supermarkets;
//    }
//
//    private Product findProductByFields(String name, String category, String unit, String brand, Double quantity) {
//        for (Product product : productRepository.findAll()) {
//            if (
//                    product.getName().equals(name) &&
//                    product.getCategory().equals(category) &&
//                    product.getUnit().equals(unit) &&
//                    product.getBrand().equals(brand) &&
//                    product.getQuantity().equals(quantity)
//            ) {
//                return product;
//            }
//        }
//        return null;
//    }
//
//    private boolean checkIfProductIsTheSameProduct(Product tableProduct, Product test){
//        return tableProduct.getName().equals(test.getName()) &&
//                tableProduct.getCategory().equals(test.getCategory()) &&
//                tableProduct.getUnit().equals(test.getUnit()) &&
//                tableProduct.getBrand().equals(test.getBrand()) &&
//                tableProduct.getQuantity().equals(test.getQuantity());
//    }
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

            String[] test = csvFile.getOriginalFilename().split("_");


            Set<Supermarket> supermarkets = csvToBean.parse()
                    .stream()
                    .map(products -> Supermarket.builder()
                            .product_name(products.getName())
                            .supermarket_name(test[0])
                            .brand(products.getBrand())
                            .category(products.getCategory())
                            .unit(products.getUnit())
                            .quantity(products.getQuantity())
                            .publish_date(LocalDate.of(2025, 5, 1))
                            .product_price(products.getPrice())
                            .currency(products.getCurrency())
                            .build()
                    ).collect(Collectors.toSet());

            supermarketRepository.saveAll(checkIfSupermarketsExistsInTable(supermarkets));
        }
        return "";
    }

    private Set<Supermarket> checkIfSupermarketsExistsInTable (Set<Supermarket> supermarkets) {
        List<Supermarket> tableSupermarkets = supermarketRepository.findAll();

        if (!tableSupermarkets.isEmpty()){
            Set<Supermarket> newSupermarkets = new HashSet<>();
            for (Supermarket supermarket : supermarkets) {
                boolean checkAfter = false;
                for (Supermarket tableSupermarket : tableSupermarkets) {
                    if (supermarket.equals(tableSupermarket)){
                        if (supermarket.getPublish_date().isEqual(tableSupermarket.getPublish_date()) ||
                            supermarket.getPublish_date().isAfter(tableSupermarket.getPublish_date())
                        ) {
                            Supermarket updatedSupermarket = supermarketRepository.findById(tableSupermarket.getId()).orElse(null);
                            if (updatedSupermarket != null) {
                                updatedSupermarket.setProduct_price(supermarket.getProduct_price());
                                updatedSupermarket.setPublish_date(supermarket.getPublish_date());
                                supermarketRepository.save(updatedSupermarket);
                                checkAfter = true;
                                break;
                            }
                        }
                    }
                }
                if (!checkAfter){
                    newSupermarkets.add(supermarket);
                }
            }
            return newSupermarkets;
        }
        return supermarkets;
    }

//    private boolean verifyProductsFields (Supermarket tableSupermarket, Supermarket newSupermarket) {
////        return tableSupermarket.
//    }
}
