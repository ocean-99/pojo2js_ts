package com.yourname.pojo2jsts.generators;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomValueGenerator {
    
    private static final Random RANDOM = new Random();
    private static final String[] SAMPLE_STRINGS = {
        "Lorem", "ipsum", "dolor", "sit", "amet", "consectetur", "adipiscing", "elit",
        "sample", "example", "test", "demo", "placeholder", "text"
    };
    
    private static final String[] SAMPLE_EMAILS = {
        "user@example.com", "test@demo.org", "sample@test.net", "admin@company.com"
    };
    
    public String generateString() {
        return SAMPLE_STRINGS[RANDOM.nextInt(SAMPLE_STRINGS.length)] + RANDOM.nextInt(1000);
    }
    
    public String generateEmail() {
        return SAMPLE_EMAILS[RANDOM.nextInt(SAMPLE_EMAILS.length)];
    }
    
    public int generateInt() {
        return RANDOM.nextInt(1000);
    }
    
    public long generateLong() {
        return RANDOM.nextLong(1000000L);
    }
    
    public double generateDouble() {
        return Math.round(RANDOM.nextDouble() * 1000.0 * 100.0) / 100.0;
    }
    
    public float generateFloat() {
        return Math.round(RANDOM.nextFloat() * 1000.0f * 100.0f) / 100.0f;
    }
    
    public boolean generateBoolean() {
        return RANDOM.nextBoolean();
    }
    
    public String generateDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime randomDate = now.minusDays(ThreadLocalRandom.current().nextInt(0, 365))
            .minusHours(ThreadLocalRandom.current().nextInt(0, 24))
            .minusMinutes(ThreadLocalRandom.current().nextInt(0, 60));
        
        return randomDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    public String generateDateTime() {
        return generateDate();
    }
    
    public String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }
    
    public int generateArraySize() {
        return RANDOM.nextInt(3) + 1; // 1-3 elements
    }
}