package com.moac.android.amilate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestFileUtils {

    public static String readTestDataFile(String _filename) {
        return readTestDataFile(ClassLoader.getSystemClassLoader(), _filename);
    }

    /**
     * Use for instrumentation tests
     */
    public static String readTestDataFile(ClassLoader _classloader, String _filename) {
        InputStream inputStream = _classloader.getResourceAsStream(_filename);
        if(inputStream == null)
            throw new IllegalArgumentException("Test data resource could not be found: " + _filename);

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while((line = reader.readLine()) != null) {
                sb.append(line);
            }
            reader.close(); // closes inputStream internally.
            return sb.toString();
        } catch(IOException ex) {
            // Catch the IOException and throw RuntimeException to neaten signatures
            throw new RuntimeException("Test data resource could not be read: " + _filename, ex);
        }
    }

}
