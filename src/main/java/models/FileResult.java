package models;

/**
 * The FileResult data model holds the total number of matches for a given file.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-25
 */

public class FileResult {
    private String file;
    private int count;
    private float score;

    /**
     * Creates a new FileResult to associate a file with its count.
     * @param file The document that is searched.
     * @param count The number of matches found in the document.
     */
    public FileResult(String file, int count) {
        this.file = file;
        this.count = count;
    }

    /**
     * Returns the file.
     * @return The filepath of the document.
     */
    public String getFile() {
        return file;
    }

    /**
     * Returns the count.
     * @return The total number of matches found in the document.
     */
    public int getCount() {
        return count;
    }

    /**
     * Prints the file and count.
     * @return Text to display the filepath and number of matches.
     */
    public String toString() {
        return this.file + ": " + this.count + " matches";
    }
}
