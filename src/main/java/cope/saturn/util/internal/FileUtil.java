/*
 * Copyright (c) 2022. Saturn Client (https://github.com/Sxmurai/saturn-client)
 * All rights reserved.
 */

package cope.saturn.util.internal;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

public class FileUtil {
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
}
