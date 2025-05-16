package main.helpers;

public class DiscountCalculator {

    public static Double applyDiscountToProductPrice (Double price, Double percentageOfDiscount) {
        return price * (1 - (percentageOfDiscount / 100));
    }

}
