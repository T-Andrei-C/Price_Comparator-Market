package main.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Product;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithPriceDTO {

    private Product product;
    private Double price;
    private Boolean has_discount;
    private Double percentage_of_discount;
    private Double discount_price;

}
