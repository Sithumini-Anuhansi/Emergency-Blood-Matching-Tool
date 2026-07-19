package util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for reading CSV files.
 */
public class CSVReader {

    /**
     * Reads a CSV file and returns all data rows.
     * The first row (header) is skipped.
     *
     * @param filePath path to the CSV file
     * @return list of rows, where each row is a String[]
     */
    public static List<String[]> readCSV(String filePath) {

        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {

                // Skip header
                if (firstLine) {
                    firstLine = false;
                    continue;
                }

                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] values = line.split(",");

                // Trim spaces
                for (int i = 0; i < values.length; i++) {
                    values[i] = values[i].trim();
                }

                rows.add(values);
            }

        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        }

        return rows;
    }

    /**
     * Checks whether a file exists and can be read.
     *
     * @param filePath path to file
     * @return true if readable
     */
    public static boolean fileExists(String filePath) {

        try (BufferedReader ignored = new BufferedReader(new FileReader(filePath))) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}