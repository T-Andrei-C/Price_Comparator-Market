package main.model.representation;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCSVRepresentation {

    @CsvBindByName(column = "product_name")
    private String name;

    @CsvBindByName(column = "product_category")
    private String category;

    @CsvBindByName(column = "brand")
    private String brand;

    @CsvBindByName(column = "package_quantity")
    private Double quantity;

    @CsvBindByName(column = "package_unit")
    private String unit;

    @CsvBindByName(column = "price")
    private Double price;

    @CsvBindByName(column = "currency")
    private String currency;

}
