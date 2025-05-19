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
public class DiscountCSVRepresentation extends ProductRepresentation{

    @CsvBindByName(column = "percentage_of_discount")
    private Double percentage_of_discount;

    @CsvBindByName(column = "from_date")
    private String from_date;

    @CsvBindByName(column = "to_date")
    private String to_date;
}
