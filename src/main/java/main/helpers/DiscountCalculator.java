package main.helpers;

import main.model.Discount;

import java.time.LocalDate;

public class DiscountCalculator {

    public static Double applyDiscountToProductPrice (Double price, Double percentageOfDiscount) {
        //ex. percentage = 20%, percentage to number = 0.2, then we find out the amount we want to keep
        //of the price, 1 minus 0.2 = 0.8, which means we will be keeping 80% of the price
        return price * (1 - (percentageOfDiscount / 100));
    }

    public static boolean checkIfDiscountApplies (LocalDate date, Discount discount) {
        return (date.isEqual(discount.getFrom_date()) || date.isAfter(discount.getFrom_date())) &&
                (date.isEqual(discount.getTo_date()) || date.isBefore(discount.getTo_date()));
    }

}
