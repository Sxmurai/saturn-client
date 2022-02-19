/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.internal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {
    public static final Path ROOT = Paths.get("");
    public static final Path CONFIG_FOLDER = ROOT.resolve("Saturn");

    /**
     * Reads file contents
     * @param path The path
     * @return the contents or null if an exception occurred
     */
    public static String read(Path path) {
        try {
            return Files.readString(path);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Writes to a file
     * @param path The path to write to
     * @param text The data to input into the file
     */
    public static void write(Path path, String text) {
        OutputStream stream = null;

        try {
            stream = new FileOutputStream(path.toFile());
            stream.write(text.getBytes(StandardCharsets.UTF_8), 0, text.length());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Creates a directory
     * @param path the path
     */
    public static void mkDir(Path path) {
        try {
            Files.createDirectory(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if a path exists
     * @param path The path
     * @return if it exists
     */
    public static boolean exists(Path path) {
        return Files.exists(path);
    }
}
