package main.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Product;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingListDTO {

    private String shopping_list_name;
    private String supermarket_name;
    private String currency;
    private List<ProductWithPriceDTO> products;

}
