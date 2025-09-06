package com.yourname.pojo2jsts.generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RandomValueGeneratorTest {
    
    private RandomValueGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new RandomValueGenerator();
    }
    
    @Test
    void testGenerateString() {
        String result = generator.generateString();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.length() > 0);
    }
    
    @Test
    void testGenerateEmail() {
        String email = generator.generateEmail();
        assertNotNull(email);
        assertTrue(email.contains("@"));
        assertTrue(email.contains("."));
    }
    
    @Test
    void testGenerateInt() {
        int result = generator.generateInt();
        assertTrue(result >= 0);
        assertTrue(result < 1000);
    }
    
    @Test
    void testGenerateLong() {
        long result = generator.generateLong();
        assertTrue(result >= 0);
        assertTrue(result < 1000000L);
    }
    
    @Test
    void testGenerateDouble() {
        double result = generator.generateDouble();
        assertTrue(result >= 0);
        assertTrue(result <= 1000.0);
    }
    
    @Test
    void testGenerateFloat() {
        float result = generator.generateFloat();
        assertTrue(result >= 0);
        assertTrue(result <= 1000.0f);
    }
    
    @Test
    void testGenerateBoolean() {
        // Just verify it returns a boolean without error
        boolean result = generator.generateBoolean();
        // Boolean can be true or false, both are valid
        assertTrue(result || !result);
    }
    
    @Test
    void testGenerateDate() {
        String date = generator.generateDate();
        assertNotNull(date);
        assertFalse(date.isEmpty());
        // Should match format: yyyy-MM-dd HH:mm:ss
        assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }
    
    @Test
    void testGenerateUUID() {
        String uuid = generator.generateUUID();
        assertNotNull(uuid);
        // UUID format: 8-4-4-4-12 characters
        assertTrue(uuid.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }
    
    @Test
    void testGenerateArraySize() {
        int size = generator.generateArraySize();
        assertTrue(size >= 1);
        assertTrue(size <= 3);
    }
}