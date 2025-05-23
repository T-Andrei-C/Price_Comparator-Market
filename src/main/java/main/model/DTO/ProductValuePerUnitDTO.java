package main.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductValuePerUnitDTO {

    private String original_unit;
    private String converted_unit;
    private Double original_unit_price;
    private Double converted_unit_price;
    private Double original_quantity;
    private Double converted_quantity;

}
