package com.student;
import java.util.Scanner;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
    
        public class App {
        
            static Scanner input = new Scanner(System.in);
        
            // Books catalog: fixed-size array
            static Book[] books = new Book[100];
            static int bookCount = 0;
        
            // Personal reading list: fixed-size array
            static Book[] readingList = new Book[20];
            static int readingCount = 0;
        
            // Book class
            static class Book {
                String title;
                String author;
                String url;
        
                Book(String title, String author, String url) {
                    this.title = title;
                    this.author = author;
                    this.url = url;
                }
            }
        
            public static void main(String[] args) {
        
                loadBooks();
        
                int choice;
        
                do {
                    System.out.println("\n===== GUTENBERG BOOK EXPLORER =====");
                    System.out.println("1. Explore Books");
                    System.out.println("2. Search by Title");
                    System.out.println("3. Search by Author");
                    System.out.println("4. Add Book to Reading List");
                    System.out.println("5. View Reading List");
                    System.out.println("6. Remove a Book");
                    System.out.println("7. Display Book Details");
                    System.out.println("8. Exit");
                    System.out.print("Enter your choice: ");
        
                    if (!input.hasNextInt()) {
                        System.out.println("Please enter a valid number.");
                        input.nextLine();
                        choice = 0;
                        continue;
                    }
        
                    choice = input.nextInt();
                    input.nextLine();
        
                    switch (choice) {
                        case 1:
                            exploreBooks();
                            break;
        
                        case 2:
                            System.out.print("Enter book title: ");
                            searchBook(input.nextLine());
                            break;
        
                        case 3:
                            System.out.print("Enter author name: ");
                            searchBook(input.nextLine(), true);
                            break;
        
                        case 4:
                            addBook();
                            break;
        
                        case 5:
                            viewReadingList();
                            break;
        
                        case 6:
                            removeBook();
                            break;
        
                        case 7:
                            displayBookDetails();
                            break;
        
                        case 8:
                            System.out.println("Thank you for using Gutenberg Book Explorer!");
                            break;
        
                        default:
                            System.out.println("Invalid choice. Try again.");
                    }
        
                } while (choice != 8);
            }
        
            // Connect to Project Gutenberg and load books
            static void loadBooks() {
        
                try {
                    System.out.println("Connecting to Project Gutenberg...");
        
                    Document doc = Jsoup.connect(
                        "https://www.gutenberg.org/browse/scores/top"
                    ).timeout(15000).get();
        
                    for (Element link : doc.select("a[href^=/ebooks/]")) {
        
                        if (bookCount >= books.length) {
                            break;
                        }
        
                        String title = link.text().trim();
                        String href = link.attr("href");
        
                        if (title.isEmpty()) {
                            continue;
                        }
        
                        String url = "https://www.gutenberg.org" + href;
        
                        // Avoid duplicate books
                        boolean exists = false;
        
                        for (int i = 0; i < bookCount; i++) {
                            if (books[i].url.equals(url)) {
                                exists = true;
                                break;
                            }
                        }
        
                        if (!exists) {
                            String author = "Unknown";
                            String parentText = link.parent().text();
        
                            int position = parentText.toLowerCase().indexOf(" by ");
        
                            if (position != -1) {
                                author = parentText.substring(position + 4).trim();
                            }
        
                            books[bookCount] = new Book(title, author, url);
                            bookCount++;
                        }
                    }
        
                    System.out.println("Books loaded successfully: " + bookCount);
        
                } catch (IOException e) {
                    System.out.println("Connection error: " + e.getMessage());
                }
            }
        
            // Display at least 10 books
            static void exploreBooks() {
        
                if (bookCount == 0) {
                    loadBooks();
                }
        
                if (bookCount == 0) {
                    System.out.println("No books available.");
                    return;
                }
        
                System.out.println("\n===== AVAILABLE BOOKS =====");
        
                int limit = Math.min(10, bookCount);
        
                for (int i = 0; i < limit; i++) {
                    System.out.println((i + 1) + ". " + books[i].title);
                    System.out.println("   Author: " + books[i].author);
                }
            }
        
            // Method overloading: search by title
            static void searchBook(String title) {
        
                boolean found = false;
        
                for (int i = 0; i < bookCount; i++) {
        
                    if (books[i].title.toLowerCase()
                            .contains(title.toLowerCase())) {
        
                        System.out.println("Found: " + books[i].title);
                        System.out.println("Author: " + books[i].author);
                        System.out.println("URL: " + books[i].url);
        
                        found = true;
                    }
                }
        
                if (!found) {
                    System.out.println("No book found with this title.");
                }
            }
        
            // Method overloading: search by author or title
            static void searchBook(String keyword, boolean searchByAuthor) {
        
                boolean found = false;
        
                for (int i = 0; i < bookCount; i++) {
        
                    String value;
        
                    if (searchByAuthor) {
                        value = books[i].author;
                    } else {
                        value = books[i].title;
                    }
        
                    if (value.toLowerCase().contains(keyword.toLowerCase())) {
        
                        System.out.println("Title: " + books[i].title);
                        System.out.println("Author: " + books[i].author);
                        System.out.println("URL: " + books[i].url);
        
                        found = true;
                    }
                }
        
                if (!found) {
                    System.out.println("No matching book found.");
                }
            }
        
            // Add a book to the reading list
            static void addBook() {
        
                if (readingCount >= readingList.length) {
                    System.out.println("Reading list is full.");
                    return;
                }
        
                if (bookCount == 0) {
                    System.out.println("No books available.");
                    return;
                }
        
                exploreBooks();
        
                System.out.print("Enter book number to add (1-10): ");
                int number = input.nextInt();
                input.nextLine();
        
                if (number < 1 || number > Math.min(10, bookCount)) {
                    System.out.println("Invalid book number.");
                    return;
                }
        
                Book selected = books[number - 1];
        
                for (int i = 0; i < readingCount; i++) {
                    if (readingList[i].url.equals(selected.url)) {
                        System.out.println("This book is already in your reading list.");
                        return;
                    }
                }
        
                readingList[readingCount] = selected;
                readingCount++;
        
                System.out.println("Book added successfully!");
            }
        
            // View personal reading list
            static void viewReadingList() {
        
                if (readingCount == 0) {
                    System.out.println("Your reading list is empty.");
                    return;
                }
        
                System.out.println("\n===== MY READING LIST =====");
        
                for (int i = 0; i < readingCount; i++) {
                    System.out.println((i + 1) + ". " + readingList[i].title);
                    System.out.println("   Author: " + readingList[i].author);
                }
            }
        
            // Remove a book from the reading list
            static void removeBook() {
        
                if (readingCount == 0) {
                    System.out.println("Your reading list is empty.");
                    return;
                }
        
                viewReadingList();
        
                System.out.print("Enter book number to remove: ");
                int number = input.nextInt();
                input.nextLine();
        
                if (number < 1 || number > readingCount) {
                    System.out.println("Invalid book number.");
                    return;
                }
        
                for (int i = number - 1; i < readingCount - 1; i++) {
                    readingList[i] = readingList[i + 1];
                }
        
                readingList[readingCount - 1] = null;
                readingCount--;
        
                System.out.println("Book removed successfully!");
            }
        
            // Display details of a selected book
            static void displayBookDetails() {
        
                if (bookCount == 0) {
                    System.out.println("No books available.");
                    return;
                }
        
                exploreBooks();
        
                System.out.print("Enter book number to view details (1-10): ");
                int number = input.nextInt();
                input.nextLine();
        
                if (number < 1 || number > Math.min(10, bookCount)) {
                    System.out.println("Invalid book number.");
                    return;
                }
        
                Book selected = books[number - 1];
        
                System.out.println("\n===== BOOK DETAILS =====");
                System.out.println("Title: " + selected.title);
                System.out.println("Author: " + selected.author);
                System.out.println("URL: " + selected.url);
        
                try {
                    Document doc = Jsoup.connect(selected.url)
                            .timeout(15000).get();
        
                    Element authorLink = doc.select("a[href^=/authors/]").first();
        
                    if (authorLink != null) {
                        System.out.println("Author details: " + authorLink.text());
                    }
        
                    System.out.println("Page title: " + doc.title());
        
                } catch (IOException e) {
                    System.out.println("Could not load extra details.");
                }
            }
        }
    



