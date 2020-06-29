package util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The FileUtil contains operations that handle file utilities,
 * such as retrieving from resources, reading, and processing.
 *
 * @author Tanya Wanwatanakool
 * @since 2020-06-25
 */

public class FileUtil {
    // Provided sample files located in resources
    private static final String[] FILES = new String[] {
            "french_armed_forces.txt",
            "hitchhikers.txt",
            "warp_drive.txt"
    };

    /**
     * Retrieves the files from IntelliJ's resources and turns them
     * into a list to be read from.
     * @return The list of files to perform document searches on.
     */
    public List<File> getFileFromResources() {
        List<File> fileList = new ArrayList<>();
        for (String fileName: FILES) {
            ClassLoader classLoader = getClass().getClassLoader();
            URL resource = classLoader.getResource(fileName);
            if (resource == null) {
                throw new IllegalArgumentException("File is not found.");
            } else {
                fileList.add(new File(resource.getFile()));
            }
        }
        return fileList;
    }
}
