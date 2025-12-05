package org.vniizht.suburbtransform.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Resources {

    public static boolean exists(String resource) {
        try (InputStream is = getResourceAsStream(resource)) {
            return is != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static String load(String resource) throws IOException {
        try (InputStream is = getResourceAsStream(resource)) {
            if (is == null) throw new IOException("Resource not found: " + resource);

            // Чтение файла для Java 8
            byte[] buffer = new byte[1024];
            int bytesRead;
            StringBuilder content = new StringBuilder();

            while ((bytesRead = is.read(buffer)) != -1) {
                content.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
            }

            return content.toString();
        }
    }

    private static InputStream getResourceAsStream(String resource) throws IOException {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }
}
