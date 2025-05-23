package main.helpers;

import main.model.Discount;

import java.time.LocalDate;

public class DiscountCalculator {

    public static Double applyDiscountToProductPrice (Double price, Double percentageOfDiscount) {
        return price * (1 - (percentageOfDiscount / 100));
    }

    public static boolean checkIfDiscountApplies (LocalDate date, Discount discount) {
        return (date.isEqual(discount.getFrom_date()) || date.isAfter(discount.getFrom_date())) &&
                (date.isEqual(discount.getTo_date()) || date.isBefore(discount.getTo_date()));
    }

}
