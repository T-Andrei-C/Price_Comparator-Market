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
public class ProductWithDiscountDTO {
    
    private Product product;
    private String supermarket_name;
    private Double percentage_of_discount;
    private Double product_price;
    private Double discount_price;
    private String currency;

}
