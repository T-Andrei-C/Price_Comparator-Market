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
public class ProductHistoryDTO {

    private Product product;
    private String supermarket_name;
    private String currency;
    private List<PriceHistoryDTO> price_history;

}
