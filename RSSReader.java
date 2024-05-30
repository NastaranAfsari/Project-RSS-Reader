import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;


//منابع استفاده شده برای این پروژه
//faradars
//geeksforgeeks
//javatpoint
//w3schools
//https://codereview.stackexchange.com/questions/131661/simple-java-rss-reader
//https://www.vogella.com/tutorials/RSSFeed/article.html
//https://www.baeldung.com/rome-rss


public class RSSReader {

    private static final int MAX_ITEMS = 5;
    private static final String FILENAME = "data.txt";
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        List<String> addresses = new ArrayList<>();
        List<String> rssAddresses = new ArrayList<>();

        loadWebsites(names, addresses, rssAddresses);

        while (true) {
            System.out.println("1. Show Updates\n2. Add URL\n3. Remove URL\n4. Exit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    showUpdates(rssAddresses);
                    break;
                case 2:
                    addUrl(names ,addresses, rssAddresses);
                    break;
                case 3:
                    removeUrl(names, addresses, rssAddresses);
                    break;
                case 4:
                    saveWebsites(names, addresses, rssAddresses);
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 4.");
            }
        }
    }

    private static void addUrl(List<String> names,List<String> addresses, List<String> rssAddresses) {
        System.out.print("Enter website name: ");
        String name = scanner.nextLine();
        System.out.print("Enter website address: ");
        String address = scanner.nextLine();

        if (addresses.contains(address)) {
            System.out.println("This website is already added.");
            return;
        }

        String rssAddress;
        try {
            rssAddress = extractRssUrl(address);
        } catch (IOException e) {
            System.out.println("Error extracting RSS address: " + e.getMessage());
            return;
        }
        names.add(name);
        addresses.add(address);
        rssAddresses.add(rssAddress);
        System.out.println("Website added successfully.");
    }

    private static void loadWebsites(List<String> names, List<String> addresses, List<String> rssAddresses) {
        try (Scanner fileScanner = new Scanner(new File(FILENAME))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(";");
                names.add(parts[0]);
                addresses.add(parts[1]);
                rssAddresses.add(parts[2]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Welcome to RSS reader!\nType a valid number for your desired action:\nFile not found. Creating new file...");
        }
    }

    private static void saveWebsites(List<String> names, List<String> addresses, List<String> rssAddresses) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILENAME))) {
            for (int i = 0; i < names.size(); i++) {
                writer.println(names.get(i) + ";" + addresses.get(i) + ";" + rssAddresses.get(i));
            }
        } catch (IOException e) {
            System.out.println("Error saving websites to file: " + e.getMessage());
        }
    }

    private static void showUpdates(List<String> rssAddresses) {
        displayWebsites(rssAddresses);
        System.out.print("Enter the number of the website you want to view updates for: ");
        int index = scanner.nextInt();
        scanner.nextLine();
        if (index >= 1 && index <= rssAddresses.size()) {
            retrieveRssContent(rssAddresses.get(index - 1));//یک تابع برای دریافت محتوای RSS وبسایت مدنظر
        } else {
            System.out.println("Invalid website number.");
        }
    }

    private static void removeUrl(List<String> names, List<String> addresses, List<String> rssAddresses) {
        displayWebsites(addresses);
        System.out.print("Enter the number of the website you want to delete: ");
        int index = scanner.nextInt();

        if (index >= 1 && index <= names.size()) {
            names.remove(index - 1);
            addresses.remove(index - 1);
            rssAddresses.remove(index - 1);
            System.out.println("Website deleted successfully.");
        } else {
            System.out.println("Invalid website number.");
        }
    }


    private static void displayWebsites(List<String> rssAddresses) {
        System.out.println("Websites:");
        for (int i = 0; i < rssAddresses.size(); i++) {
            System.out.println((i + 1) + ". " + rssAddresses.get(i));
        }
    }

    public static void retrieveRssContent(String rssUrl) {
        try {
            Document doc = Jsoup.connect(rssUrl).get();
            Elements items = doc.select("item");

            for (int i = 0; i < MAX_ITEMS && i < items.size(); ++i) {
                Element item = items.get(i);
                System.out.println("Title: " + item.select("title").text());
                System.out.println("Link: " + item.select("link").text());
                System.out.println("Description: " + item.select("description").text());
            }
        } catch (IOException e) {
            System.out.println("Error in retrieving RSS content for " + rssUrl + ": " + e.getMessage());
        }
    }

    public static String extractRssUrl(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        return doc.select("[type='application/rss+xml']").attr("abs:href");
    }
}
