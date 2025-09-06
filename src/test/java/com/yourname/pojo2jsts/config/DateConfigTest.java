package com.yourname.pojo2jsts.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.format.DateTimeFormatter;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DateConfig class
 */
class DateConfigTest {
    
    private DateConfig dateConfig;
    
    @BeforeEach
    void setUp() {
        dateConfig = new DateConfig();
    }
    
    @Test
    void testDefaultValues() {
        assertEquals("yyyy-MM-dd HH:mm:ss", dateConfig.getFormat());
        assertEquals(365, dateConfig.getPastDays());
        assertEquals(0, dateConfig.getFutureDays());
        assertFalse(dateConfig.isUseCurrentTime());
        assertEquals("UTC", dateConfig.getTimeZone());
    }
    
    @Test
    void testParameterizedConstructor() {
        dateConfig = new DateConfig("dd/MM/yyyy", 30, 7);
        
        assertEquals("dd/MM/yyyy", dateConfig.getFormat());
        assertEquals(30, dateConfig.getPastDays());
        assertEquals(7, dateConfig.getFutureDays());
    }
    
    @Test
    void testValidDateFormat() {
        assertDoesNotThrow(() -> dateConfig.setFormat("yyyy-MM-dd"));
        assertDoesNotThrow(() -> dateConfig.setFormat("dd/MM/yyyy HH:mm"));
        assertDoesNotThrow(() -> dateConfig.setFormat("MMM dd, yyyy"));
    }
    
    @Test
    void testInvalidDateFormat() {
        assertThrows(IllegalArgumentException.class, 
            () -> dateConfig.setFormat("invalid-format-xyz"));
        assertThrows(IllegalArgumentException.class, 
            () -> dateConfig.setFormat("yyyy-MM-dd HH:mm:xx"));
    }
    
    @Test
    void testPastDaysValidation() {
        assertDoesNotThrow(() -> dateConfig.setPastDays(0));
        assertDoesNotThrow(() -> dateConfig.setPastDays(100));
        assertDoesNotThrow(() -> dateConfig.setPastDays(365));
        
        assertThrows(IllegalArgumentException.class, 
            () -> dateConfig.setPastDays(-1));
        assertThrows(IllegalArgumentException.class, 
            () -> dateConfig.setPastDays(-100));
    }
    
    @Test
    void testFutureDaysValidation() {
        assertDoesNotThrow(() -> dateConfig.setFutureDays(0));
        assertDoesNotThrow(() -> dateConfig.setFutureDays(30));
        assertDoesNotThrow(() -> dateConfig.setFutureDays(365));
        
        assertThrows(IllegalArgumentException.class, 
            () -> dateConfig.setFutureDays(-1));
        assertThrows(IllegalArgumentException.class, 
            () -> dateConfig.setFutureDays(-30));
    }
    
    @Test
    void testTotalRangeDays() {
        dateConfig.setPastDays(30);
        dateConfig.setFutureDays(7);
        
        assertEquals(37, dateConfig.getTotalRangeDays());
    }
    
    @Test
    void testIncludesFuture() {
        dateConfig.setFutureDays(0);
        assertFalse(dateConfig.includesFuture());
        
        dateConfig.setFutureDays(7);
        assertTrue(dateConfig.includesFuture());
    }
    
    @Test
    void testIncludesPast() {
        dateConfig.setPastDays(0);
        assertFalse(dateConfig.includesPast());
        
        dateConfig.setPastDays(30);
        assertTrue(dateConfig.includesPast());
    }
    
    @Test
    void testEqualsAndHashCode() {
        DateConfig config1 = new DateConfig("yyyy-MM-dd", 30, 7);
        DateConfig config2 = new DateConfig("yyyy-MM-dd", 30, 7);
        DateConfig config3 = new DateConfig("dd/MM/yyyy", 30, 7);
        
        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
        assertNotEquals(config1, config3);
        assertNotEquals(config1, null);
        assertNotEquals(config1, "string");
    }
    
    @Test
    void testToString() {
        dateConfig = new DateConfig("yyyy-MM-dd HH:mm:ss", 365, 0);
        String result = dateConfig.toString();
        
        assertTrue(result.contains("yyyy-MM-dd HH:mm:ss"));
        assertTrue(result.contains("365"));
        assertTrue(result.contains("DateConfig"));
    }
    
    @Test
    void testComplexDateFormats() {
        // Test various complex date formats to ensure they are valid
        String[] formats = {
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "EEE, dd MMM yyyy HH:mm:ss zzz",
            "yyyy.MM.dd G 'at' HH:mm:ss z",
            "yyyy-DDD",
            "yyyy-'W'ww-u"
        };
        
        for (String format : formats) {
            assertDoesNotThrow(() -> dateConfig.setFormat(format), 
                "Format should be valid: " + format);
            
            // Verify the format is actually usable
            assertDoesNotThrow(() -> DateTimeFormatter.ofPattern(format),
                "Format should create valid DateTimeFormatter: " + format);
        }
    }
}