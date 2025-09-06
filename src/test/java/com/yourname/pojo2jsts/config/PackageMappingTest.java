package com.yourname.pojo2jsts.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PackageMapping class
 */
class PackageMappingTest {
    
    private PackageMapping mapping;
    
    @BeforeEach
    void setUp() {
        mapping = new PackageMapping();
    }
    
    @Test
    void testDefaultConstructor() {
        assertNull(mapping.getPackagePattern());
        assertNull(mapping.getJsonTargetType());
        assertNull(mapping.getTsTargetType());
        assertTrue(mapping.isRecursive());
        assertTrue(mapping.isEnabled());
    }
    
    @Test
    void testParameterizedConstructor() {
        mapping = new PackageMapping("com.example.*", "string", "string");
        
        assertEquals("com.example.*", mapping.getPackagePattern());
        assertEquals("string", mapping.getJsonTargetType());
        assertEquals("string", mapping.getTsTargetType());
        assertTrue(mapping.isRecursive());
        assertTrue(mapping.isEnabled());
    }
    
    @Test
    void testWildcardMatchingRecursive() {
        mapping.setPackagePattern("com.example.*");
        mapping.setRecursive(true);
        mapping.setEnabled(true);
        
        assertTrue(mapping.matches("com.example.User"));
        assertTrue(mapping.matches("com.example.dto.UserDTO"));
        assertTrue(mapping.matches("com.example.model.deep.nested.Class"));
        assertFalse(mapping.matches("com.other.User"));
        assertFalse(mapping.matches("com.exampl.User"));
        assertFalse(mapping.matches(null));
    }
    
    @Test
    void testWildcardMatchingNonRecursive() {
        mapping.setPackagePattern("com.example.*");
        mapping.setRecursive(false);
        mapping.setEnabled(true);
        
        assertTrue(mapping.matches("com.example.User"));
        assertFalse(mapping.matches("com.example.dto.UserDTO")); // subpackage should not match
        assertFalse(mapping.matches("com.other.User"));
    }
    
    @Test
    void testExactPackageMatching() {
        mapping.setPackagePattern("com.example.dto");
        mapping.setEnabled(true);
        
        assertTrue(mapping.matches("com.example.dto.UserDTO"));
        assertFalse(mapping.matches("com.example.dto.nested.UserDTO"));
        assertFalse(mapping.matches("com.example.User"));
    }
    
    @Test
    void testDisabledMapping() {
        mapping.setPackagePattern("com.example.*");
        mapping.setEnabled(false);
        
        assertFalse(mapping.matches("com.example.User"));
    }
    
    @Test
    void testNullValues() {
        mapping.setPackagePattern(null);
        mapping.setEnabled(true);
        
        assertFalse(mapping.matches("com.example.User"));
    }
    
    @Test
    void testEqualsAndHashCode() {
        PackageMapping mapping1 = new PackageMapping("com.example.*", "string", "string");
        PackageMapping mapping2 = new PackageMapping("com.example.*", "string", "string");
        PackageMapping mapping3 = new PackageMapping("com.other.*", "string", "string");
        
        assertEquals(mapping1, mapping2);
        assertEquals(mapping1.hashCode(), mapping2.hashCode());
        assertNotEquals(mapping1, mapping3);
        assertNotEquals(mapping1, null);
        assertNotEquals(mapping1, "string");
    }
    
    @Test
    void testToString() {
        mapping = new PackageMapping("com.example.*", "string", "string");
        String result = mapping.toString();
        
        assertTrue(result.contains("com.example.*"));
        assertTrue(result.contains("string"));
        assertTrue(result.contains("PackageMapping"));
    }
}