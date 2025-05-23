package main.helpers;

import main.model.DTO.ProductValuePerUnitDTO;

public class UnitAndPriceConvertor {

    public static ProductValuePerUnitDTO convertPriceToNewUnit (ProductValuePerUnitDTO productValuePerUnit) {
        switch (productValuePerUnit.getOriginal_unit()) {
            case "kg" -> convertToSingleUnit(productValuePerUnit, "kg");
            case "g" -> convertToSingleSupraUnit(productValuePerUnit, "kg");
            case "l" -> convertToSingleUnit(productValuePerUnit, "l");
            case "ml" -> convertToSingleSupraUnit(productValuePerUnit, "l");
            case "role" -> dividePriceWithQuantity(productValuePerUnit, "o rolă");
            case "buc" -> dividePriceWithQuantity(productValuePerUnit, "o buc");
        }

        
        return productValuePerUnit;
    }

    private static void convertToSingleUnit (ProductValuePerUnitDTO productValuePerUnit, String unit) {
        //convert the unit to its sub unit ex. 0.4 kg to 400 g
        double subUnit = productValuePerUnit.getOriginal_quantity() * 1000;
        //find out the price per singular unit ex. original price = 9, subUnit = 400 g, 1 g = 0.0225
        double pricePerSubUnit = productValuePerUnit.getOriginal_unit_price() / subUnit;
        //convert the price to match one singular unit ex. 1 g = 0.0225, 1 kg = 22.5
        double newPrice = pricePerSubUnit * 1000;

        productValuePerUnit.setConverted_unit_price(newPrice);
        productValuePerUnit.setConverted_quantity(1d);
        productValuePerUnit.setConverted_unit(unit);
    }

    private static void convertToSingleSupraUnit(ProductValuePerUnitDTO productValuePerUnit, String unit) {
        //find out the price per singular unit ex. original price = 9, unit = 400 g, 1 g = 0.0225
        double pricePerSubUnit = productValuePerUnit.getOriginal_unit_price() / productValuePerUnit.getOriginal_quantity();
        //convert the price to match one singular unit ex. 1 g = 0.0225, 1 kg = 22.5
        double newPrice = pricePerSubUnit * 1000;

        productValuePerUnit.setConverted_unit_price(newPrice);
        productValuePerUnit.setConverted_quantity(1d);
        productValuePerUnit.setConverted_unit(unit);
    }

    private static void dividePriceWithQuantity(ProductValuePerUnitDTO productValuePerUnit, String unit) {
        double newPrice = productValuePerUnit.getOriginal_unit_price() / productValuePerUnit.getOriginal_quantity();

        productValuePerUnit.setConverted_unit_price(newPrice);
        productValuePerUnit.setConverted_quantity(1d);
        productValuePerUnit.setConverted_unit(unit);
    }

}
