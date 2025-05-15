package main.helpers;

import java.time.LocalDate;
import java.util.Arrays;

public class CSVFileNameReader {
    public static String getSupermarketName (String csvFileName) {
        String[] csvFileNameArray = csvFileNameArray(csvFileName);
        for (String item : csvFileNameArray) {
            try {
                LocalDate csvFilePublishDate = LocalDate.parse(item);
            } catch (Exception e) {
                return item;
            }
        }
        return "N/A";
    }

    public static LocalDate getPublishDateName (String csvFileName) {
        String[] csvFileNameArray = csvFileNameArray(csvFileName);
        for (String item : csvFileNameArray) {
            try {
                return LocalDate.parse(item);
            } catch (Exception e) {
                System.out.print("Not a LocalDate");
            }
        }
        return LocalDate.now();
    }

    private static String[] csvFileNameArray (String csvFileName) {
        return Arrays.stream(csvFileName.replaceAll(".csv", "").split("_"))
                .filter(item -> !item.equalsIgnoreCase("discounts"))
                .filter(item -> !item.equalsIgnoreCase("discount"))
                .toArray(String[]::new);
    }
}
