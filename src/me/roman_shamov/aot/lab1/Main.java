package me.roman_shamov.aot.lab1;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        File file = new File("files/text.txt");
        Files.createDirectories(Path.of("files"));
        if (file.createNewFile())
            return;
        String text = Files.readString(file.toPath())
                .replaceAll("\\p{IsPunctuation}", "")
                .replaceAll("[<>]", "")
                .replaceAll(" ", "");
        ArrayList<String> words = new ArrayList<>(Arrays.stream(text.split("\\s")).map(a -> a.replace(" ",""))
                .filter(s -> !s.isEmpty()).toList());
        Collections.sort(words);
        StringBuilder resultBuilder = new StringBuilder();
        StringBuilder wordsCounter = new StringBuilder();
        Map<String, Integer> wordsMap = new HashMap<>();
        for (String word : words) {
            word.replace(" ", "");
            if(word.matches(".*[\\da-zA-Z]+.*"))
                continue;
            String simplifiedWord = PSMethods.trimWord(word).toLowerCase();
            resultBuilder.append(word).append(" --> ").append(simplifiedWord).append("\n");
            wordsMap.put(simplifiedWord, wordsMap.containsKey(simplifiedWord) ? wordsMap.get(simplifiedWord) + 1: 1);
        }
        wordsMap = wordsMap.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<String, Integer> entry : wordsMap.entrySet()) {
            wordsCounter.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        Files.writeString(Path.of("files/result.txt"), resultBuilder.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.writeString(Path.of("files/count.txt"), wordsCounter.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
