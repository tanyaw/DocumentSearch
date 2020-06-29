package models;

import java.util.*;

/**
 * The SearchResults data model holds a List of search result data,
 * a RelevancyComparator, and the total time for a search operation to
 * complete.
 *
 * @author Tanya Wanwatanakool
 * @version 1.0
 * @since 2020-06-25
 */

public class SearchResult {
    private long elapsedTime;
    private List<FileResult> searchResultsList ;

    /**
     * Creates a new SearchResult with initial values.
     */
    public SearchResult() {
        elapsedTime = -1;
        searchResultsList = new ArrayList<>();
    }

    /**
     * Returns searchResultsList that is sorted in descending order
     * using the RelevancyComparator.
     * @return The List of all and associated matches.
     */
    public List<FileResult> getSearchResultsList() {
        // Sort by descending
        Collections.sort(searchResultsList, new RelevancyComp());
        return searchResultsList;
    }

    /**
     * Add a result for the document searched.
     * @param file The file that was searched.
     * @param count The number of matches found in the file.
     */
    public void addSearchResult(String file, int count) {
        searchResultsList.add(new FileResult(file, count));
    }

    /**
     * Returns elapsedTime.
     * @return The duration of the search operation to complete.
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * Set the elapsedTime of the search.
     * @param duration The total time for search operation to complete.
     */
    public void setElapsedTime(long duration) {
        elapsedTime = duration;
    }

}

/**
 * The RelevancyComparator contains the sorting logic to order
 * incoming FileResult to the List to be added in descending
 * order based on the matches found in a document.
 */
class RelevancyComp implements Comparator<FileResult> {
    @Override
    public int compare(FileResult f1, FileResult f2) {
        if(f1.getCount() < f2.getCount()) {
            return 1;
        } else {
            return -1;
        }
    }
}
