package main.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductHighestDiscountDTO {

    private String product_name;
    private String supermarket_name;
    private Double percentage_of_discount;
    private Double product_price;
    private Double product_price_with_discount;
    private String currency;
    private String category;
    private String brand;
    private Double quantity;

}
