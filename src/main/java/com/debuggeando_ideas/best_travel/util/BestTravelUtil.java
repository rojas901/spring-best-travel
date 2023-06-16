package com.debuggeando_ideas.best_travel.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

public class BestTravelUtil {

    private static final Random random = new Random();

    public static LocalDateTime getRandomSoon() {
        var randomHours = random.nextInt(5 - 2) + 2;
        var now = LocalDateTime.now();
        return now.plusHours(randomHours);
    }

    public static LocalDateTime getRandomLatter() {
        var randomHours = random.nextInt(12 - 6) + 6;
        var now = LocalDateTime.now();
        return now.plusHours(randomHours);
    }

    public static LocalDate getRandomSoonDays() {
        var randomDays = random.nextInt(5 - 2) + 2;
        var now = LocalDate.now();
        return now.plusDays(randomDays);
    }

    public static LocalDate getRandomLatterDays() {
        var randomDays = random.nextInt(12 - 6) + 6;
        var now = LocalDate.now();
        return now.plusDays(randomDays);
    }

    public static void writeNotification(String text, String path) throws IOException {
        var fileWriter = new FileWriter(path, true);
        var bufferedWriter = new BufferedWriter(fileWriter);
        try (fileWriter; bufferedWriter) {
            bufferedWriter.write(text);
            bufferedWriter.newLine();
        }
    }

}
