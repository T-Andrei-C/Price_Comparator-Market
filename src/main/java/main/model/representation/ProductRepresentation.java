package main.model.representation;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRepresentation {

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

}
