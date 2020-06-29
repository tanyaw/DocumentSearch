import models.SearchRequest;
import models.SearchResult;
import service.SearchServiceImpl;
import service.SearchServiceInterface;

/**
 * The DocumentSearchDriver launches the search application
 * that performs String, Regex, or Indexed search on files
 * based on user input.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-25
 */

public class DocumentSearchDriver {
    public static SearchServiceUI searchServiceUI = new SearchServiceUI();
    public static SearchServiceInterface searchService = new SearchServiceImpl();

    public static void main(String[] args) {
        // Initial prompt displayed to user
        SearchRequest search = searchServiceUI.printSearchServiceMenu();

        // Perform search method based on user input then print results
        SearchResult result;
        switch (search.getSearchType()) {
            case STRING:
                result = searchService.performStringMatch(search.getSearchTerm());
                searchServiceUI.printSearchResults(result);
                break;
            case REGEX:
                result =  searchService.performRegexMatch(search.getSearchTerm());
                searchServiceUI.printSearchResults(result);
                break;
            case INDEXED:
                searchService.performIndexedMatch(search.getSearchTerm());
                break;
        }
    }
}