package models;

import enums.SearchType;

/**
 * The SearchRequest data model holds the user input for the
 * term to search and the search type used in the Search service.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-25
 */

public class SearchRequest {
    private String searchTerm;
    private SearchType searchType;

    /**
     * Returns the searchTerm.
     * @return The term that will be searched for in documents.
     */
    public String getSearchTerm() {
        return searchTerm;
    }

    /**
     * Sets the searchTerm.
     * @param term The term to be searched for in documents.
     */
    public void setSearchTerm(String term) {
        this.searchTerm = term;
    }

    /**
     * Returns the searchType to be performed.
     * @return The searchTypeType is an enum value of STRING, REGEX, or INDEXED.
     */
    public SearchType getSearchType() {
        return this.searchType;
    }

    /**
     * Sets the searchType.
     * @param type The type is a searchType enum value that indicates
     *               the search operation to be performed.
     */
    public void setSearchType(SearchType type) {
        this.searchType = type;
    }

}
