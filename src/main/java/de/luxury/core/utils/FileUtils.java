package de.luxury.core.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileUtils {

    public static void createFile(File file) {
        if (!file.getAbsoluteFile().getParentFile().exists()) {
            file.getAbsoluteFile().getParentFile().mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFileAsString(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        assert reader != null;
        reader.lines().forEachOrdered(stringBuilder::append);

        return stringBuilder.toString();
    }

    public static String readFileAsString(File file, boolean formatted) {
        if (formatted) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(
                        new FileReader(file, StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder stringBuilder = new StringBuilder();
            assert reader != null;
            reader.lines().forEachOrdered(line -> {
                stringBuilder.append(line).append("\n");
            });
            return stringBuilder.toString();
        }
        return readFileAsString(file);
    }

    public static BufferedReader getFileReader(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new FileReader(file, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

    public static void writeFile(File file, String content) {
        writeFile(file, content.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeFile(File file, byte[] content) {
        createFile(file);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(content);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
