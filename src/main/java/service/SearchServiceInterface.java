package service;

import models.SearchResult;

/**
 * The SearchServiceInterface defines the different search operations
 * that can be performed in this application.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-25
 */

public interface SearchServiceInterface {

    SearchResult performStringMatch(String searchTerm);

    SearchResult performRegexMatch(String searchTerm);

    SearchResult performIndexedMatch(String searchTerm);
}
