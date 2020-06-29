package service;

import models.SearchResult;
import util.FileUtil;
import util.IndexUtil;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;


import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The SearchServiceImpl contains the implementation of the interface
 * to perform the different search operations.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-25
 */
public class SearchServiceImpl implements SearchServiceInterface {
    private static final Logger LOGGER = Logger.getLogger(SearchServiceImpl.class.getName());
    private static final int MAX_DOCUMENTS_HIT = 3;
    private static final String[] charsToEscape = {"?", ".", "[", "]", "(", ")",  "&", "\"[", "\""};

    private FileUtil fileUtil = new FileUtil();
    private List<File> fileList = fileUtil.getFileFromResources();
    private IndexUtil indexUtil = new IndexUtil();
    private Instant endTime, startTime;

    /**
     * This method launches the simple string match operation.
     * @param searchTerm The word to be searched for.
     * @return The SearchResult with files, count, and elapsed time.
     */
    public SearchResult performStringMatch(String searchTerm) {
        String[] searchToken = searchTerm.split(" ");
        SearchResult result = stringMatch(searchToken, new SearchResult());
        return result;
    }

    /**
     * Helper Method
     * Contains the logic associated with performing the simple string
     * matching search operation.
     * @param searchToken The word or phrase (token) to be searched for.
     * @param result An empty result to be updated.
     * @return A SearchResult with the total number of matches per file.
     */
    private SearchResult stringMatch(String[] searchToken, SearchResult result) {
        startTime = Instant.now();
        Scanner scanner = null;
        for (File file: fileList) {
            int count = 0;
            try {
                scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    if (scanner.next().equalsIgnoreCase(searchToken[0])) {
                        int tokenCount = 1;
                        if (searchToken.length != 1) {
                              for (int i=1; i < searchToken.length; i++) {
                                  if (scanner.next().equalsIgnoreCase(searchToken[i])) {
                                      tokenCount++;
                                  }
                              }
                          }

                        if (tokenCount == searchToken.length) {
                            count++;
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                LOGGER.log(Level.WARNING, "File is not found: ", e);
            } finally {
                scanner.close();
            }
            result.addSearchResult(file.toString(), count);
        }
        endTime = Instant.now();
        result.setElapsedTime(Duration.between(startTime, endTime).toMillis());
        return result;
    }

    /**
     * This method launches the regular expression match operation.
     * @param searchTerm The word to be searched for.
     * @return The SearchResult with files, count, and elapsed time.
     */
    public SearchResult performRegexMatch(String searchTerm) {
        SearchResult result = regexMatch(searchTerm, new SearchResult());
        return result;
    }

    /**
     * Helper Method
     * Contains the logic associated with performing the regex expression
     * matching search operation.
     * @param searchTerm The word to be searched for.
     * @param result An empty result to be updated.
     * @return A SearchResult with the total number of matches per file.
     */
    private SearchResult regexMatch(String searchTerm, SearchResult result) {
        // Instantiate regex util objects
        String escaped = escapeCharacters(searchTerm);
        Pattern pattern = Pattern.compile("\\b" + escaped + "\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher;

        startTime = Instant.now();
        Scanner scanner = null;
        for (File file: fileList) {
            int count = 0;
            try {
                scanner = new Scanner(file);
                while (scanner.hasNext()) {
                    String scan = scanner.nextLine();
                    matcher = pattern.matcher(scan);
                    while (matcher.find()) {
                        count++;
                    }
                }
            } catch(FileNotFoundException e) {
                LOGGER.log(Level.WARNING, "File is not found: ",e );
            } finally {
                scanner.close();
            }
            result.addSearchResult(file.toString(), count);
        }
        endTime = Instant.now();
        result.setElapsedTime(Duration.between(startTime, endTime).toMillis());
        return result;
    }

    /**
     * Helper Method
     * This method removes special characters in the search term in order for
     * the simple string match to be equal. Otherwise words starting with or
     * nested in between special characters are not counted.
     * @param word The searchTerm to be processed.
     * @param escapeChar The searchTerm trimmed of special characters.
     * @return
     */
    private String replaceCharacters(String word, String escapeChar) {
        for (int i = 0 ; i < charsToEscape.length ; i++){
            if(word.contains(charsToEscape[i])){
                word = word.replace(charsToEscape[i], escapeChar);
            }
        }
        return word;
    }

    /**
     * Helper Method
     * This method appends backslashes to special characters in order to escape
     * them for the regular expression matching.
     * @param word The searchTerm to be processed, if necessary.
     * @return The searchTerm with escaped characters.
     */
    private String escapeCharacters(String word) {
        for (int i = 0 ; i < charsToEscape.length ; i++){
            if(word.contains(charsToEscape[i])){
                word = word.replace(charsToEscape[i], "\\" + charsToEscape);
            }
        }
        return word;
    }

    /**
     * This method launches the indexed match operation.
     * @param searchTerm The word to be searched for.
     * @return The SearchResult with files, count, and elapsed time.
     */
    public SearchResult performIndexedMatch(String searchTerm) {
        // Lucene Setup - Create data store and index documents
        Directory indexDir = indexUtil.configureLucene(fileList);
        SearchResult result = indexedMatch(searchTerm, indexDir, new SearchResult());
        return result;
    }

    /**
     * Helper Method
     * Contains the logic associated with performing the regex expression
     * matching search operation.
     * @param searchTerm
     * @param dir
     * @param result
     * @return
     */
    private SearchResult indexedMatch(String searchTerm, Directory dir, SearchResult result) {
        try {
            // NOTE: QueryParser initializes the same Analyzer as IndexWriter so the
            // indexed search is tokenized the same way
            QueryParser qp = new QueryParser("contents", new StandardAnalyzer(CharArraySet.EMPTY_SET));
            Query searchTermQuery = qp.parse(qp.escape(searchTerm));
            Query matchAllQuery = new BooleanQuery.Builder()
                    .add(new BooleanClause(new MatchAllDocsQuery(), BooleanClause.Occur.MUST))
                    .add(new BooleanClause(searchTermQuery, BooleanClause.Occur.SHOULD))
                    .build();

            // Lucene Setup - Create objects to search indexed documents
            IndexReader reader = DirectoryReader.open(dir);
            IndexSearcher searcher = new IndexSearcher(reader);
            startTime = Instant.now();
            TopDocs hits = searcher.search(matchAllQuery, MAX_DOCUMENTS_HIT);
            endTime = Instant.now();
            result.setElapsedTime(Duration.between(startTime, endTime).toMillis());

            // NOTE - Lucene does not support count per document
            System.out.println("\n--- Document Search Results ---");
            for (ScoreDoc sd: hits.scoreDocs) {
                Document d = searcher.doc(sd.doc);
                System.out.println(d.get("filepath") + ": Score: " + sd.score);
                result.addSearchResult(d.get("filepath"), ((int) sd.score));
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error using Lucene index operations: ", e);
        } catch (ParseException e) {
            LOGGER.log(Level.WARNING, "Error parsing query: ", e);
        }
        return result;
    }
}
