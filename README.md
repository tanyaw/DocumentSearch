# DocumentSearch
The DocumentSearch application is a Maven project written in Java and developed using IntelliJ. It uses Apache Lucene for full-featured text search capabilities and both Junit and Mockito for testing.

### Running DocumentSearch application
1. Open the project in IntelliJ
2. Launch the DocumentSearch program by running the `DocumentSearchDriver.java` class in `src/main/java`
    1. Use the IntelliJ console to interact with the program to enter the serachTerm and search method
3. Run the 2M performance test by running the `DocumentSearchTest.java` class in `src/test/java`


### Issues
This program is not 100% production ready. Due to the time constraint, I was unable to deliver a fully-functioning solution for the DocumentSearch application. The search string and regex methods are able to accurately produce results for search terms without special characters; however, a majority of unhandled edge cases have to do with search terms that have special characters. The indexed method has limitations in the results that are displayed.
1. String Search
     - COMPLETE:
          1. Can handle single and phrase searchTerms
          2. Can handle most searchTerms with special characters
     - EDGECASE:
          1. If one of the searchTerms is a stand-alone special character, it will fail to match
     
2. Regex Search
      - Despite escaping the special characters within the searchTerm, I had difficulty with creating a regular expression that would respect word boundaries and also be able to match specical characters. 
      - COMPLETE:
          1. Can handle single and phrase searchTerms
      - EDGECASE:
          1. It cannot handle searchTerms with special characters at all
      
3. Indexed Search
    - Unfortunately, when I completed implementing Apache Lucene for the indexed search I did not realize that the search query would only return the total number of hits, associated document the term was found in, and the relevancy score of the document. I tried to find and was unsuccessful in implementing a way to retrieve the count per document from the total count.
    - COMPLETE:
        1. Can handle single and phrase searchTerms
        2. Can handle searchTerms with special characters
    - INCOMPLETE:
        1. You will notice that the indexed search results return a score instead of the number of matches. 
        2. The results will typically return in the correct order of relevancy; however, due to the way Lucene scores the document objects sometimes this is not always the case.


## Design
1. This is the initial checklist I came up with to organize a high-level solution for Document Search. I used this to determine what packages and classes I would need to create and how to organize the project. 
```
Program:
1. Retrieve/Process file resources
2. Prompt user with menu
    1. Handle invalid user input
3. Implement 3 search operations 
    - Simple string matching
    - Text search using regex
    - Index Search
        - Research NoSQL DB to store text files then index/search
        - Look into Elastic Search, Apache Solr, MongoDB
4. Determine relevancy of files 
    - Sort by descending
```

2. Research Indexed Search
    - Since we are performing text search operations, it was obvious to me that I would want to consider using a non-relational database and/or text-based search engine.
    - Initially, I looked into MongoDB, Elastic Search (ES), and Apache Solr as possible candidates to do indexed search. From this research, I learned that both Solr and ES were built on top of Apache Lucene.
    - I decided to move forward with Apache Lucene over Solr and ES because the program requirements are simple indexing/searching and do not need complex full-text features. Also, with the given time constraints, implementing a Java library would be much easier learning how to use Apache Solr or installing and setting up an ES cluster. There are a lot more resources and examples of working with Lucene and good documentation on setup/how to use versus the other two indexed search solutions.
    

## Testing
- I wrote a very basic unit test to verify the search results relevancy order of files and number of matches of each search method for a specific search term. 
- I manually tested the program by entering different search terms that were single words, phrases, and contained special characters. 
- I wrote a performance test to determine the average runtime of each search method using randomly generated search terms.


## TODO
In order to meet the requirements, Elastic Search should be integrated in order to report the count per document. Elastic Search provides an API that returns this information in addition to being able to perform indexing and searching capabilities.

## Questions:
**1. Run a performance test that does 2M searches with random search terms, and measures execution time. Which approach is fastest? Why?**
```
--- Document Search Performance Test Result ---
String Match average runtime: 1244 ms
Regex Match average runtime: 968 ms
Indexed Match average runtime: 28 ms
```

The indexed search is the fastest, because we are preprocessing the file resources and then searching for the search term that has already been indexed. Apache Lucene is a text search engine library that uses an inverted index data structure to map the terms to documents. The index stores statistics about the terms to make term-based search more efficient. Due to the indexing, it is faster to perform seraches and return the documents in order of relevancy based on the documents' scoring.

Regex matching is faster than string matching, because it defines a regular expression to match the entire token pattern. Instead of iterating through every single word it can find the entire pattern match within a block of text.

String matching takes the longest because it must iterate through every single word and perform an equals operation to check if there is a match. It takes more time if the serach term is a phrase, because then it must go into another iteration to see if the next words match the phrase (token).

---
**2. Provide some thoughts on what you would do on the software or hardware side to make this program scale to handle massive content and/or very large request volume (5000 requests/second or more).**

In order to make this program scale to handle more content and/or a large requests volume there are multiple solutions we can consider:
    
    1. HARDWARE SOLUTIONS:
          1. Horizontal Scaling of Hardware Resources — Adding more servers and/or instances of the program to handle a larger request volume.
                - This will decreases the load on one program since there are more servers/instances of the program to handle requests and increase/maintain performance.
                - The tradeoff is that this  will increase architecture complexity with more machines.
          
          2. Vertical Scaling of Hardware Resources — Increasing the number of RAM or CPU to existing infrastructure to handle processing more content.
                - This will increases the load a server and/or instance of the program can handle in order to increase/maintain performance.
                - The tradeoff is that it is an expensive solution to purchase RAM/CPU and there are hardware limitations.
          
          3. Using in-memory cache — Writing to cache to store result data from recent requests. 
                - Incoming requests should try to retrieve data from cache before performing the request that will have to access external resources. This will increases data retrieval speed and performance by reducing number of times external data storage must be accessed.

    1. SOFTWARE SOLUTIONS:
          1. MultiThreading — Using asynchronous calls to one IndexWriter to add documents to the index concurrently.
                - This will decrease the amount of time it takes to preprocess the data resources.
          
          2. Retry Mechanism — If the search program goes down and/or becomes unavailable, there should be logic that will handle retrying the request without failure on user’s end.


## Results 
1. Match searchTerm "in the"
![inThe-string](/results/inThe-string.png)
![inThe-regex](/results/inThe-regex.png)
![inThe-indexed](/results/inThe-indexed.png)

2. Match searchTerm "the"
![the-string](/results/the-string.png)
![the-regex](/results/the-regex.png)
![the-indexed](/results/the-indexed.png)

3. Match searchTerm "[4]"
![the-string](/results/[4]-string.png)
![the-regex](/results/[4]-regex.png)
![the-indexed](/results/[4]-indexed.png)
