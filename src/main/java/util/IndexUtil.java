package util;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The IndexUtil contains logic to configure the Lucene index (data store)
 * and perform operations, such as indexing and searching, on document
 * objects.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-25
 */

public class IndexUtil {
    private static final Logger LOGGER = Logger.getLogger(IndexUtil.class.getName());
    private static final String INDEXED_FILES_PATH = "indexedFiles";

    /**
     * This method configures Apache Lucene to index and search documents
     * by performing the following steps:
     *      1. Creating the index (data store) in the local filesystem.
     *      2. Creating a IndexWriter to build the documents.
     *      3. Indexing the documents to be searched.
     * @param fileList The list of files to be added to the index.
     * @return The directory with indexed documents to be searched.
     */
    public Directory configureLucene(List<File> fileList) {
        Directory indexDir = null;
        try {
            indexDir = FSDirectory.open(Paths.get(INDEXED_FILES_PATH));
            IndexWriter writer = createIndexWriter(indexDir);
            indexDocuments(writer, fileList);
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error using Lucene index operations: ", e);
        }
        return indexDir;
    }

    /**
     * Helper method
     * This method creates the IndexWriter object to be used to create
     * the index (data store) and add documents to the index.
     * @param dir The local directory that IndexWriter will write to.
     * @return An IndexWriter.
     * @throws IOException
     */
    private IndexWriter createIndexWriter(Directory dir) throws IOException {
        // NOTE: StandardAnalyzer removes common words by default.
        // Initialize Analyzer with EMPTY_SET to prevent words from being filtered out
        Analyzer analyzer = new StandardAnalyzer(CharArraySet.EMPTY_SET);
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter writer = new IndexWriter(dir, iwc);
        return writer;
    }

    /**
     * Helper Method
     * This method acquires content from the file resources to build
     * documents to be added to the index.
     * @param writer The IndexWriter that adds documents to the index.
     * @param files The list of files to build into document objects.
     * @throws IOException
     */
    private void indexDocuments(IndexWriter writer, List<File> files) throws IOException {
        for (File file : files) {
            // map file contents into Document objects
            Document doc = new Document();
            doc.add(new StringField("filepath", file.toString(), Field.Store.YES));
            doc.add(new TextField("contents", new String(Files.readAllBytes(file.toPath())), Field.Store.YES));

            // Writer adds each document to the "index"
            writer.updateDocument(new Term("filepath", file.toString()), doc);
        }
        writer.close();
    }
}
