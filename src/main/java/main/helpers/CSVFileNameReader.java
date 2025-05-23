package main.helpers;

import java.time.LocalDate;
import java.util.Arrays;

public class CSVFileNameReader {
    public static String getSupermarketName(String csvFileName) {
        String[] csvFileNameArray = csvFileNameArray(csvFileName);
        //if the item can't be parsed by the LocalDate.parse, it means the item is the name of the supermarket
        for (String item : csvFileNameArray) {
            try {
                LocalDate csvFilePublishDate = LocalDate.parse(item);
            } catch (Exception e) {
                return item;
            }
        }
        return "N/A";
    }


    public static LocalDate getPublishDate(String csvFileName) {
        String[] csvFileNameArray = csvFileNameArray(csvFileName);
        //if it finds a local date it will return it
        for (String item : csvFileNameArray) {
            try {
                return LocalDate.parse(item);
            } catch (Exception e) {
                System.out.print("Not a LocalDate");
            }
        }
        return LocalDate.now();
    }

    //remove the .csv from the name and get the name of the file and date
    private static String[] csvFileNameArray(String csvFileName) {
        return Arrays.stream(csvFileName.replaceAll(".csv", "").split("_"))
                .filter(item -> !item.equalsIgnoreCase("discounts"))
                .filter(item -> !item.equalsIgnoreCase("discount"))
                .toArray(String[]::new);
    }
}
