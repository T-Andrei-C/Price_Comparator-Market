package main.model.representation;

import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SupermarketCSVRepresentation extends ProductRepresentation {

    @CsvBindByName(column = "price")
    private Double price;

    @CsvBindByName(column = "currency")
    private String currency;

    private String supermarket_name;

    private LocalDate publish_date;
}
