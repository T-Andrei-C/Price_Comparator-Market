package main.model.representation;

import com.opencsv.bean.CsvBindByName;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiscountCSVRepresentation {
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

    @CsvBindByName(column = "percentage_of_discount")
    private Double percentage_of_discount;

    @CsvBindByName(column = "from_date")
    private String from_date;

    @CsvBindByName(column = "to_date")
    private String to_date;
}
