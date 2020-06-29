import enums.SearchType;
import models.FileResult;
import models.SearchRequest;
import models.SearchResult;

import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * The SearchServiceUI prints the instructions and results
 * to display on to the user on the console.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-25
 */

public class SearchServiceUI {
    private Scanner scanner = new Scanner(new InputStreamReader(System.in));
    private SearchRequest request = new SearchRequest();

    /**
     * This method prompts the user with the document search
     * menu options and reads in the input to used to drive
     * the search methods.
     * @return The SearchRequest contains the search term and search method
     *         to be used in the Search Service.
     */
    public SearchRequest printSearchServiceMenu() {
        System.out.println("Welcome to Document Search application!\n");
        retrieveSearchTerm();
        retrieveSearchType();
        return request;
    }

    /**
     * Helper Method
     * Prompts the user to enter the term to be searched.
     */
    private void retrieveSearchTerm() {
        while (request.getSearchTerm() == null || request.getSearchTerm().isEmpty()) {
            System.out.print("Enter the search term: ");
            request.setSearchTerm(scanner.nextLine().trim());
        }
    }

    /**
     * Helper Method
     * Prompts the user to enter the search type to be performed.
     */
    private void retrieveSearchType() {
        int input = -1;
        do {
            System.out.println("\nSearch Method: 1) String Match 2) Regular Expression 3) Indexed");
            System.out.print("Please input one of the search methods by entering a number (1-3): ");

            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                request.setSearchType(SearchType.values()[input - 1]);
            } else {
                scanner.nextLine();
            }
        }
        while (input > 3 || input < 1);
    }

    /**
     * This method prints out the filepath, number of matches where
     * the search term is found, and the elapsed time to complete
     * the search operation.
     * @param result The result contains the values to print to console.
     */
    public void printSearchResults(SearchResult result) {
        System.out.println("\n--- Document Search Results ---");
        for(FileResult f: result.getSearchResultsList()) {
            System.out.println(f);
        }
        System.out.println("Elapsed time: " + result.getElapsedTime() + " ms");
    }
}