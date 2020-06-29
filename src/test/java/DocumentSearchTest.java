import service.SearchServiceImpl;
import service.SearchServiceInterface;
import util.FileUtil;
import util.IndexUtil;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * The DocumentSearchTest is a performance test that does 2M searches
 * with random search terms, and measures the average execution time.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-27
 */

public class DocumentSearchTest {
    public static void main(String[] args) {
        new TestCases();
    }

    /**
     * The TestCases class contains logic to run the performance test.
     */
    static class TestCases {
        private static final int TEST_CASES = 2000;
        private static final int LIST_SIZE = 1000;
        private List<String> searchTermList = new ArrayList<>();
        private Instant endTime, startTime;
        private long totalTime;

        private SearchServiceInterface searchService = new SearchServiceImpl();
        private FileUtil fileUtil = new FileUtil();
        private IndexUtil indexUtil = new IndexUtil();

        /**
         * The TestCases object will configure and run the performance tests.
         */
        public TestCases() {
            System.out.println("--- Document Search Performance Test Results ---");
            generateSearchTerms();
            runTestCases();
        }

        /**
         * This method reuses given resource files to create terms and phrases
         * from in order to ensure that matches will be found instead of randomly
         * generating words that may not match.
         */
        private void generateSearchTerms() {
            List<File> fileList = fileUtil.getFileFromResources();
            Scanner scanner;

            try {
                // Generate random single words from resource files
                for (File file : fileList) {
                    scanner = new Scanner(file);
                    while(scanner.hasNext()) {
                        for (String word: scanner.nextLine().split(" ")) {
                            word = word.replaceAll("[^a-zA-Z0-9]", "");
                            if (!word.isEmpty()) {
                                searchTermList.add(word);
                            }
                        }
                        if (searchTermList.size() == 500) { break; }
                    }
                }

                // Generate phrases (token) from single words
                for (int i=0; i < searchTermList.size() / 2; i = i +2) {
                    searchTermList.add(searchTermList.get(i) + " " + searchTermList.get(i+1));
                    if (searchTermList.size() == LIST_SIZE) { break; }
                }

            } catch (Exception e) { }

            Collections.shuffle(searchTermList);
        }

        /**
         * This method performs each search 2M times with different randomly
         * generate searchTerms then calculates the average run time of each
         * search operation.
         */
        private void runTestCases() {
            // TEST 1: String match
            for (int i=0; i < TEST_CASES; i++) {
                for (String term : searchTermList) {
                    startTime = Instant.now();
                    searchService.performStringMatch(term);
                    endTime = Instant.now();

                    totalTime += Duration.between(startTime, endTime).toMillis();
                }
            }
            System.out.println("String Match average runtime: " + (totalTime / (long) TEST_CASES) + " ms");
            cleanUp();

            // TEST 2: Regex match
            for (int i=0; i < TEST_CASES; i++) {
                for (String term : searchTermList) {
                    startTime = Instant.now();
                    searchService.performRegexMatch(term);
                    endTime = Instant.now();

                    totalTime += Duration.between(startTime, endTime).toMillis();
                }
            }
            System.out.println("Regex Match average runtime: " + (totalTime / (long) TEST_CASES) + " ms");
            cleanUp();

            // TEST 3: Indexed match
            try {
                // NOTE: Setup Lucene IndexSearcher so it doesn't affect runtime
                Directory indexDir = indexUtil.configureLucene(fileUtil.getFileFromResources());
                IndexSearcher searcher;
                IndexReader reader = DirectoryReader.open(indexDir);
                searcher = new IndexSearcher(reader);

                for (int i=0; i < TEST_CASES; i++) {
                    for (String term : searchTermList) {
                        startTime = Instant.now();
                        QueryParser qp = new QueryParser("contents", new StandardAnalyzer(CharArraySet.EMPTY_SET));
                        Query searchTermQuery = qp.parse(qp.escape(term));
                        searcher.search(searchTermQuery, 3);
                        endTime = Instant.now();

                        totalTime += Duration.between(startTime, endTime).toMillis();
                    }
                }
                System.out.println("Indexed Match average runtime: " + (totalTime / (long) TEST_CASES) + " ms");
                cleanUp();
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }

        /**
         * Helper Method
         * This method resets the totalTime to calculate the next search
         * operation runtime.
         */
        private void cleanUp() {
            totalTime = 0L;
        }
    }
}
