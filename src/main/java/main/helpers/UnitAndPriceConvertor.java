package main.helpers;

import main.model.DTO.ProductValuePerUnitDTO;

public class UnitAndPriceConvertor {

    public static ProductValuePerUnitDTO convertPriceToNewUnit (ProductValuePerUnitDTO productValuePerUnit) {
        switch (productValuePerUnit.getOriginal_unit()) {
            case "kg" -> iWillThinkOfANameLater(productValuePerUnit, "kg");
            case "g" -> iWillThinkOfANameLater2(productValuePerUnit, "kg");
            case "l" -> iWillThinkOfANameLater(productValuePerUnit, "l");
            case "ml" -> iWillThinkOfANameLater2(productValuePerUnit, "l");
            case "role" -> iWillThinkOfANameLater3(productValuePerUnit, "o rolă");
            case "buc" -> iWillThinkOfANameLater3(productValuePerUnit, "o buc");
        }

        return productValuePerUnit;
    }

    private static void iWillThinkOfANameLater (ProductValuePerUnitDTO productValuePerUnit, String unit) {
        double subUnit = productValuePerUnit.getOriginal_quantity() * 1000;
        double pricePerSubUnit = productValuePerUnit.getOriginal_unit_price() / subUnit;
        double newPrice = pricePerSubUnit * 1000;

        productValuePerUnit.setConverted_unit_price(newPrice);
        productValuePerUnit.setConverted_quantity(1d);
        productValuePerUnit.setConverted_unit(unit);
    }

    private static void iWillThinkOfANameLater2 (ProductValuePerUnitDTO productValuePerUnit, String unit) {
        double pricePerSubUnit = productValuePerUnit.getOriginal_unit_price() / productValuePerUnit.getOriginal_quantity();
        double newPrice = pricePerSubUnit * 1000;

        productValuePerUnit.setConverted_unit_price(newPrice);
        productValuePerUnit.setConverted_quantity(1d);
        productValuePerUnit.setConverted_unit(unit);
    }

    private static void iWillThinkOfANameLater3 (ProductValuePerUnitDTO productValuePerUnit, String unit) {
        double newPrice = productValuePerUnit.getOriginal_unit_price() / productValuePerUnit.getOriginal_quantity();

        productValuePerUnit.setConverted_unit_price(newPrice);
        productValuePerUnit.setConverted_quantity(1d);
        productValuePerUnit.setConverted_unit(unit);
    }

}
