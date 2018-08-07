package com.uk.settlement.config.reader;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import static java.lang.Thread.currentThread;
import static java.util.stream.Collectors.joining;

public class JsonConfigReader {
    public static <T> T readConfig(String fileName, Class<T> clazz) {
        ClassLoader classloader = currentThread().getContextClassLoader();
        InputStream inputStream = classloader.getResourceAsStream(fileName);
        String result = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(joining("\n"));
        return new Gson().fromJson(result, clazz);
    }
}