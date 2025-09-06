package com.yourname.pojo2jsts.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for TypeMapping class
 */
class TypeMappingTest {
    
    private TypeMapping mapping;
    
    @BeforeEach
    void setUp() {
        mapping = new TypeMapping();
    }
    
    @Test
    void testDefaultConstructor() {
        assertNull(mapping.getSourceType());
        assertNull(mapping.getJsonValuePattern());
        assertNull(mapping.getTsType());
        assertNull(mapping.getCustomGenerator());
        assertTrue(mapping.isEnabled());
        assertNull(mapping.getDescription());
    }
    
    @Test
    void testParameterizedConstructor() {
        mapping = new TypeMapping("java.util.Date", "\"{{random_date}}\"", "string");
        
        assertEquals("java.util.Date", mapping.getSourceType());
        assertEquals("\"{{random_date}}\"", mapping.getJsonValuePattern());
        assertEquals("string", mapping.getTsType());
        assertTrue(mapping.isEnabled());
    }
    
    @Test
    void testConstructorWithDescription() {
        mapping = new TypeMapping("java.util.Date", "\"{{random_date}}\"", "string", "Date mapping");
        
        assertEquals("java.util.Date", mapping.getSourceType());
        assertEquals("\"{{random_date}}\"", mapping.getJsonValuePattern());
        assertEquals("string", mapping.getTsType());
        assertEquals("Date mapping", mapping.getDescription());
        assertTrue(mapping.isEnabled());
    }
    
    @Test
    void testExactTypeMatching() {
        mapping.setSourceType("java.util.Date");
        mapping.setEnabled(true);
        
        assertTrue(mapping.matches("java.util.Date"));
        assertFalse(mapping.matches("java.util.Calendar"));
        assertFalse(mapping.matches(null));
    }
    
    @Test
    void testShortTypeMatching() {
        mapping.setSourceType("Date");
        mapping.setEnabled(true);
        
        assertTrue(mapping.matches("java.util.Date"));
        assertTrue(mapping.matches("Date"));
        assertFalse(mapping.matches("java.sql.Date")); // Different Date class
    }
    
    @Test
    void testFullyQualifiedMatching() {
        mapping.setSourceType("java.util.Date");
        mapping.setEnabled(true);
        
        assertTrue(mapping.matches("Date"));  // Should match short name
        assertTrue(mapping.matches("java.util.Date"));  // Should match full name
        assertFalse(mapping.matches("java.sql.Date"));
    }
    
    @Test
    void testDisabledMapping() {
        mapping.setSourceType("java.util.Date");
        mapping.setEnabled(false);
        
        assertFalse(mapping.matches("java.util.Date"));
    }
    
    @Test
    void testCustomGenerator() {
        assertFalse(mapping.hasCustomGenerator());
        
        mapping.setCustomGenerator("com.example.CustomGenerator");
        assertTrue(mapping.hasCustomGenerator());
        
        mapping.setCustomGenerator("");
        assertFalse(mapping.hasCustomGenerator());
        
        mapping.setCustomGenerator("   ");
        assertFalse(mapping.hasCustomGenerator());
        
        mapping.setCustomGenerator(null);
        assertFalse(mapping.hasCustomGenerator());
    }
    
    @Test
    void testEqualsAndHashCode() {
        TypeMapping mapping1 = new TypeMapping("java.util.Date", "\"{{random_date}}\"", "string");
        TypeMapping mapping2 = new TypeMapping("java.util.Date", "\"{{random_date}}\"", "string");
        TypeMapping mapping3 = new TypeMapping("java.util.Calendar", "\"{{random_date}}\"", "string");
        
        assertEquals(mapping1, mapping2);
        assertEquals(mapping1.hashCode(), mapping2.hashCode());
        assertNotEquals(mapping1, mapping3);
        assertNotEquals(mapping1, null);
        assertNotEquals(mapping1, "string");
    }
    
    @Test
    void testToString() {
        mapping = new TypeMapping("java.util.Date", "\"{{random_date}}\"", "string", "Test mapping");
        String result = mapping.toString();
        
        assertTrue(result.contains("java.util.Date"));
        assertTrue(result.contains("{{random_date}}"));
        assertTrue(result.contains("string"));
        assertTrue(result.contains("TypeMapping"));
    }
}