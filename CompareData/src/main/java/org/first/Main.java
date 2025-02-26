package org.first;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Main {
    // Reads country names from a CSV file
    public static List<String> readCSVData(String filePath) {
        List<String> csvData = new ArrayList<>();
        File file = new File(filePath);

        // Check if file exists
        if (!file.exists()) {
            System.out.println("Error: The CSV file does not exist at " + filePath);
            return csvData;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim(); // Remove leading/trailing spaces
                if (!line.isEmpty()) { // Ignore empty lines
                    csvData.add(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return csvData;
    }

    // Scrapes country names from the IBAN website TABLE
    public static List<String> scrapeTableData(String url) {
        List<String> webData = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).get();

            // Find the table with country data
            Element table = doc.select("table.table-bordered").first();
            if (table != null) {
                Elements rows = table.select("tr");

                // Loop through each row, skipping the first (header)
                for (int i = 1; i < rows.size(); i++) {
                    Element row = rows.get(i);
                    Elements columns = row.select("td");

                    if (!columns.isEmpty()) {
                        String countryName = columns.get(0).text().trim(); // First column contains country names
                        webData.add(countryName);
                    }
                }
            } else {
                System.out.println("Error: Could not find the country list table on the website.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return webData;
    }

    public static void main(String[] args) {
        String csvPath = "C:\\Users\\Laptop\\Documents\\Multiverse\\Java\\CompareData\\ListOfCountries(Sheet1).csv"; // Update with actual file path
        String websiteUrl = "https://www.iban.com/country-codes"; // Correct website

        // Read CSV data
        List<String> csvData = readCSVData(csvPath);
        System.out.println("CSV Data (" + csvData.size() + " items): " + csvData);

        // Scrape website data (from the correct table)
        List<String> webData = scrapeTableData(websiteUrl);
        System.out.println("\nWebsite Data (" + webData.size() + " items): " + webData);

        // Compare CSV and Website data
        System.out.println("\nComparing Data...");

        // Find items that are in the CSV but missing from the website
        Set<String> missingOnWebsite = new HashSet<>(csvData);
        missingOnWebsite.removeAll(webData);

        // Find items that are on the website but missing from the CSV
        Set<String> missingInCSV = new HashSet<>(webData);
        missingInCSV.removeAll(csvData);

        // Print differences
        if (!missingOnWebsite.isEmpty() || !missingInCSV.isEmpty()) {
            System.out.println("Differences found:");

            if (!missingOnWebsite.isEmpty()) {
                System.out.println("❌ These are in the CSV but missing from the Website: " + missingOnWebsite);
            }

            if (!missingInCSV.isEmpty()) {
                System.out.println("❌ These are on the Website but missing from the CSV: " + missingInCSV);
            }
        } else {
            System.out.println("✅ Both lists match!");
        }
    }
}
