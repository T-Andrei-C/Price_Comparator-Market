package main.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryDTO {

    private Double product_price;
    private boolean has_discount;
    private Double discounted_price;
    private Double percentage_of_discount;
    private LocalDate from;
    private LocalDate to;

}
