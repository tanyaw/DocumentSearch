package service;

import static org.junit.jupiter.api.Assertions.*;

import models.FileResult;
import models.SearchResult;

import org.junit.jupiter.api.Test;

import java.util.List;

class SearchServiceImplTest {
    private SearchServiceInterface searchService = new SearchServiceImpl();

    private static final String FRENCH_FILE = "/Users/twanwatanakool/IdeaProjects/DocumentSearch/target/classes/french_armed_forces.txt";
    private static final String HITCH_FILE = "/Users/twanwatanakool/IdeaProjects/DocumentSearch/target/classes/hitchhikers.txt";
    private static final String WARP_FILE = "/Users/twanwatanakool/IdeaProjects/DocumentSearch/target/classes/warp_drive.txt";

    private static final String IN_THE_STR = "in the";
    private static final String IS_STR = "is";

    @Test
    void testPerformStringMatch() {
        SearchResult result = searchService.performStringMatch(IN_THE_STR);
        List<FileResult> resultData = result.getSearchResultsList();
        assertInTheResults(resultData);

        result = searchService.performStringMatch(IS_STR);
        resultData = result.getSearchResultsList();
        assertIsResults(resultData);
    }

    @Test
    void testPerformRegexMatch() {
        SearchResult result = searchService.performRegexMatch(IN_THE_STR);
        List<FileResult> resultData = result.getSearchResultsList();
        assertInTheResults(resultData);

        result = searchService.performRegexMatch(IS_STR);
        resultData = result.getSearchResultsList();
        assertIsResults(resultData);
    }

    private void assertInTheResults(List<FileResult> result) {
        assertEquals(FRENCH_FILE, result.get(0).getFile());
        assertEquals(15, result.get(0).getCount());

        assertEquals(HITCH_FILE, result.get(1).getFile());
        assertEquals(2, result.get(1).getCount());

        assertEquals(WARP_FILE, result.get(2).getFile());
        assertEquals(1, result.get(2).getCount());
    }

    private void assertIsResults(List<FileResult> result) {
        assertEquals(HITCH_FILE, result.get(0).getFile());
        assertEquals(4, result.get(0).getCount());

        assertEquals(WARP_FILE, result.get(1).getFile());
        assertEquals(3, result.get(1).getCount());

        assertEquals(FRENCH_FILE, result.get(2).getFile());
        assertEquals(1, result.get(2).getCount());
    }
}