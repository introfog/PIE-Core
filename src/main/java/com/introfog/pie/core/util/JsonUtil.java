package com.introfog.pie.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Arrays;
import java.util.List;

public class JsonUtil {
    public static <T> T deserializeJsonFromFileToObject(String path, Class<T> clazz) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(path)));
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(json, clazz);
    }

    public static <T> List<T> deserializeJsonFromFileToArray(String path, Class<T[]> clazz) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(path)));
        ObjectMapper mapper = new ObjectMapper();

        return Arrays.asList(mapper.readValue(json, clazz));
    }

    public static <T> void serializeObjectToJsonFile(T object, String path) throws IOException {
        StringWriter writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, object);
        Files.write(Paths.get(path), writer.toString().getBytes());
    }

    public static <T> void serializeArrayToJsonFile(List<T> list, String path, T[] arrayToSerialize) throws IOException {
        StringWriter writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        mapper.writerWithDefaultPrettyPrinter().writeValue(writer, list.toArray(arrayToSerialize));
        Files.write(Paths.get(path), writer.toString().getBytes());
    }
}
