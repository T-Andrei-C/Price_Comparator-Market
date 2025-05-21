package main.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import main.helpers.CSVFileNameReader;
import main.model.Discount;
import main.model.DiscountHistory;
import main.model.Product;
import main.model.Supermarket;
import main.model.representation.DiscountCSVRepresentation;
import main.repository.DiscountHistoryRepository;
import main.repository.DiscountRepository;
import main.repository.ProductRepository;
import main.repository.SupermarketRepository;
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
public class CSVDiscountParserService {

    private final DiscountRepository discountRepository;
    private final SupermarketRepository supermarketRepository;
    private final ProductRepository productRepository;
    private final DiscountHistoryRepository discountHistoryRepository;

    public String uploadCSVFile(MultipartFile csvFile) throws IOException {

        try (Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {

            HeaderColumnNameMappingStrategy<DiscountCSVRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(DiscountCSVRepresentation.class);

            CsvToBean<DiscountCSVRepresentation> csvToBean = new CsvToBeanBuilder<DiscountCSVRepresentation>(reader)
                    .withSeparator(';')
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .build();

            String csvFileName = csvFile.getOriginalFilename();

            Set<Discount> discountSet = csvToBean.parse()
                    .stream()
                    .filter(discounts -> verifyIfSupermarketExistsForDiscount(discounts, CSVFileNameReader.getSupermarketName(csvFileName)))
                    .map(discounts -> Discount.builder()
                            .percentage_of_discount(discounts.getPercentage_of_discount())
                            .from_date(LocalDate.parse(discounts.getFrom_date()))
                            .to_date(LocalDate.parse(discounts.getTo_date()))
                            .supermarket(getSupermarketForDiscount(discounts, CSVFileNameReader.getSupermarketName(csvFileName)))
                            .build()
                    ).collect(Collectors.toSet());

            discountRepository.saveAll(checkIfDiscountsInTable(discountSet));
        }

        return "";
    }

    private Set<Discount> checkIfDiscountsInTable(Set<Discount> discounts) {
        List<Discount> tableDiscounts = discountRepository.findAll();

        if (!tableDiscounts.isEmpty()) {
            Set<Discount> newDiscounts = new HashSet<>();

            for (Discount discount : discounts) {
                if (discount.getSupermarket().getDiscount() != null) {
                    Discount tableDiscount = discountRepository.findById(discount.getSupermarket().getDiscount().getId()).orElse(null);

                    if (tableDiscount != null) {

                        if (tableDiscount.getFrom_date().isBefore(discount.getFrom_date()) &&
                                (tableDiscount.getTo_date().isBefore(discount.getFrom_date()) ||
                                tableDiscount.getTo_date().isEqual(discount.getFrom_date()))
                        ) {
                            DiscountHistory discountHistory = DiscountHistory.builder()
                                    .supermarket(tableDiscount.getSupermarket())
                                    .from_date(tableDiscount.getFrom_date())
                                    .to_date(tableDiscount.getTo_date())
                                    .percentage_of_discount(tableDiscount.getPercentage_of_discount())
                                    .build();

                            tableDiscount.setPercentage_of_discount(discount.getPercentage_of_discount());
                            tableDiscount.setFrom_date(discount.getFrom_date());
                            tableDiscount.setTo_date(discount.getTo_date());

                            discountRepository.save(tableDiscount);
                            discountHistoryRepository.save(discountHistory);
                        } else if (tableDiscount.getFrom_date().isBefore(discount.getFrom_date()) &&
                                tableDiscount.getTo_date().isAfter(discount.getFrom_date()) &&
                                (tableDiscount.getTo_date().isBefore(discount.getTo_date()) ||
                                tableDiscount.getTo_date().isAfter(discount.getTo_date()) ||
                                tableDiscount.getTo_date().isEqual(discount.getTo_date()))
                        ) {
                            DiscountHistory discountHistory = DiscountHistory.builder()
                                    .supermarket(tableDiscount.getSupermarket())
                                    .from_date(tableDiscount.getFrom_date())
                                    .to_date(discount.getFrom_date())
                                    .percentage_of_discount(tableDiscount.getPercentage_of_discount())
                                    .build();

                            tableDiscount.setPercentage_of_discount(discount.getPercentage_of_discount());
                            tableDiscount.setFrom_date(discount.getFrom_date());
                            tableDiscount.setTo_date(discount.getTo_date());

                            discountRepository.save(tableDiscount);
                            discountHistoryRepository.save(discountHistory);
                        } else {
                            tableDiscount.setPercentage_of_discount(discount.getPercentage_of_discount());
                            tableDiscount.setFrom_date(discount.getFrom_date());
                            tableDiscount.setTo_date(discount.getTo_date());

                            discountRepository.save(tableDiscount);
                        }

                    } else {
                        newDiscounts.add(discount);
                    }
                } else {
                    newDiscounts.add(discount);
                }
            }

            return newDiscounts;
        }
        return discounts;
    }

    private boolean verifyIfSupermarketExistsForDiscount(DiscountCSVRepresentation discountCSVRepresentation, String supermarketName) {
        List<Supermarket> supermarkets = supermarketRepository.findAll();

        if (!supermarkets.isEmpty()) {
            for (Supermarket supermarket : supermarkets) {
                if (checkSupermarketFieldsWithDiscountFields(discountCSVRepresentation, supermarket, supermarketName)) {
                    return true;
                }
            }
        }

        return false;
    }

    private Supermarket getSupermarketForDiscount(DiscountCSVRepresentation discountCSVRepresentation, String supermarketName) {
        Product product = productRepository.findProductByFields(
                discountCSVRepresentation.getName(),
                discountCSVRepresentation.getUnit(),
                discountCSVRepresentation.getCategory(),
                discountCSVRepresentation.getBrand(),
                discountCSVRepresentation.getQuantity()
        ).orElse(null);

        return supermarketRepository.findSupermarketByFields(supermarketName, product);
    }

    private boolean checkSupermarketFieldsWithDiscountFields(DiscountCSVRepresentation discountCSVRepresentation, Supermarket supermarket, String supermarketName) {
        return supermarket.getName().equals(supermarketName) &&
                supermarket.getProduct().getName().equals(discountCSVRepresentation.getName()) &&
                supermarket.getProduct().getBrand().equals(discountCSVRepresentation.getBrand()) &&
                supermarket.getProduct().getCategory().equals(discountCSVRepresentation.getCategory()) &&
                supermarket.getProduct().getQuantity().equals(discountCSVRepresentation.getQuantity()) &&
                supermarket.getProduct().getUnit().equals(discountCSVRepresentation.getUnit());
    }

}
