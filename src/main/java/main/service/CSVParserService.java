package main.service;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import main.model.Product;
import main.model.representation.ProductCSVRepresentation;
import main.respository.DiscountRepository;
import main.respository.ProductRepository;
import main.respository.SupermarketRepository;
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
public class CSVParserService {

    private final DiscountRepository discountRepository;
    private final ProductRepository productRepository;
    private final SupermarketRepository supermarketRepository;

    public String uploadCSVFile (MultipartFile csvFile) throws IOException {
        try (Reader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))){

            HeaderColumnNameMappingStrategy<ProductCSVRepresentation> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(ProductCSVRepresentation.class);

            CsvToBean<ProductCSVRepresentation> csvToBean = new CsvToBeanBuilder<ProductCSVRepresentation>(reader)
                            .withSeparator(';')
                            .withMappingStrategy(strategy)
                            .withIgnoreLeadingWhiteSpace(true)
                            .withIgnoreEmptyLine(true)
                            .build();

//            System.out.print(csvFile.getOriginalFilename());
//            System.out.print(csvToBean.parse().);

            Set<Product> productsSet = csvToBean.parse()
                    .stream()
                    .map(products -> Product.builder()
                            .name(products.getName())
                            .brand(products.getBrand())
                            .category(products.getCategory())
                            .unit(products.getUnit())
                            .quantity(products.getQuantity())
                            .build()
                    ).collect(Collectors.toSet());

            productRepository.saveAll(verifyIfProductsExistInTable(productsSet));
        }
        return "";
    }

    private Set<Product> verifyIfProductsExistInTable (Set<Product> products){
        List<Product> productsTable = productRepository.findAll();
        if (!productsTable.isEmpty()){
            Set<Product> newProducts = new HashSet<>();

            for (Product product : products){
                boolean afterCheck = false;
                for (Product tableProduct : productsTable){
                    if (checkProductsFields(tableProduct, product)){
                        afterCheck = true;
                        break;
                    }
                }
                if (!afterCheck){
                    newProducts.add(product);
                }
            }
            return newProducts;
        }
        return products;
    }

    private boolean checkProductsFields (Product tableProduct, Product newProduct){
        return tableProduct.getName().equals(newProduct.getName()) &&
                tableProduct.getBrand().equals(newProduct.getBrand()) &&
                tableProduct.getCategory().equals(newProduct.getCategory()) &&
                tableProduct.getQuantity().doubleValue() == newProduct.getQuantity().doubleValue() &&
                tableProduct.getUnit().equals(newProduct.getUnit());
    }
}
