package main.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import main.helpers.CSVFileNameReader;
import main.model.Supermarket;
import main.model.SupermarketHistory;
import main.model.representation.ProductCSVRepresentation;
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
public class CSVSupermarketParserService {

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

            Set<Supermarket> supermarkets = csvToBean.parse()
                    .stream()
                    .map(products -> Supermarket.builder()
                            .product_name(products.getName())
                            .supermarket_name(CSVFileNameReader.getSupermarketName(csvFileName))
                            .brand(products.getBrand())
                            .category(products.getCategory())
                            .unit(products.getUnit())
                            .quantity(products.getQuantity())
                            .publish_date(CSVFileNameReader.getPublishDateName(csvFileName))
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
                                SupermarketHistory supermarketHistory = SupermarketHistory.builder()
                                        .supermarket_name(updatedSupermarket.getSupermarket_name())
                                        .product_name(updatedSupermarket.getProduct_name())
                                        .brand(updatedSupermarket.getBrand())
                                        .unit(updatedSupermarket.getUnit())
                                        .category(updatedSupermarket.getCategory())
                                        .quantity(updatedSupermarket.getQuantity())
                                        .currency(updatedSupermarket.getCurrency())
                                        .product_price(updatedSupermarket.getProduct_price())
                                        .publish_date(updatedSupermarket.getPublish_date())
                                        .build();

                                supermarketHistoryRepository.save(supermarketHistory);

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
}
