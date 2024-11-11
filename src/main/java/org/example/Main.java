package org.example;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static final String OUTPUT_FILEPATH = "output.txt";

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Ошибка: Не указан путь к входному файлу.");
            System.exit(1);
        }

        String inputFilePath = args[0];
        long startTime = System.currentTimeMillis();

        try {
            List<String> lines = readLinesFromFile(inputFilePath);
            List<List<String>> groups = groupLines(lines);
            writeGroupsToFile(groups, OUTPUT_FILEPATH, startTime);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> readLinesFromFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isValidLine(line)) {
                    lines.add(line);
                }
            }
        }

        return lines;
    }

    private static boolean isValidLine(String line) {
        return line.matches("^(\"[0-9]+\";)*(\"[0-9]+\")$");
    }

    public static List<List<String>> groupLines(List<String> lines) {
        Map<String, List<String>> groups = new HashMap<>();
        Map<String, String> lineToGroup = new HashMap<>();

        for (String line : lines) {
            String[] elements = line.split(";");
            String groupKey = null;

            for (int i = 0; i < elements.length; i++) {
                if (!elements[i].isEmpty()) {
                    String key = elements[i] + "@" + i;
                    if (groups.containsKey(key)) {
                        groupKey = groups.get(key).get(0);
                        break;
                    }
                }
            }

            if (groupKey == null) {
                groupKey = line;
            }

            for (int i = 0; i < elements.length; i++) {
                if (!elements[i].isEmpty()) {
                    String key = elements[i] + "@" + i;
                    groups.computeIfAbsent(key, k -> new ArrayList<>()).add(groupKey);
                }
            }

            lineToGroup.put(line, groupKey);
        }

        Map<String, List<String>> groupedLines = new HashMap<>();
        for (String line : lines) {
            String groupKey = lineToGroup.get(line);
            groupedLines.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(line);
        }

        return groupedLines.values().stream()
                .filter(group -> group.size() > 1)
                .sorted((group1, group2) -> Integer.compare(group2.size(), group1.size()))
                .collect(Collectors.toList());
    }

    public static void writeGroupsToFile(List<List<String>> groups, String path, long startTime) throws IOException {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("Время выполнения программы: " + executionTime + " мс");
            writer.newLine();
            writer.write("Количество групп с более чем одним элементом: " + groups.size());
            writer.newLine();

            for (int i = 0; i < groups.size(); i++) {
                writer.write("Группа " + (i + 1));
                writer.newLine();
                for (String line : groups.get(i)) {
                    writer.write(line);
                    writer.newLine();
                }
                writer.newLine();
            }
        }
    }
}