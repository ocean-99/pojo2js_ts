package com.yourname.pojo2jsts.generators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for TypeScript generator generic type handling
 */
class TypeScriptGeneratorTest {
    
    private TypeScriptGenerator generator;
    
    @BeforeEach
    void setUp() {
        generator = new TypeScriptGenerator();
    }
    
    @Test
    void testGenerateWithNullClass() {
        String result = generator.generate(null, null);
        assertNotNull(result);
        assertEquals("any", result);
    }
    
    @Test
    void testIsCollectionType() {
        // Use reflection to test the private method
        try {
            java.lang.reflect.Method method = TypeScriptGenerator.class.getDeclaredMethod("isCollectionType", String.class);
            method.setAccessible(true);
            
            assertTrue((Boolean) method.invoke(generator, "java.util.List"));
            assertTrue((Boolean) method.invoke(generator, "java.util.ArrayList"));
            assertTrue((Boolean) method.invoke(generator, "java.util.Set"));
            assertTrue((Boolean) method.invoke(generator, "java.util.HashSet"));
            assertFalse((Boolean) method.invoke(generator, "java.lang.String"));
        } catch (Exception e) {
            fail("Failed to test isCollectionType method: " + e.getMessage());
        }
    }
    
    @Test
    void testIsMapType() {
        // Use reflection to test the private method
        try {
            java.lang.reflect.Method method = TypeScriptGenerator.class.getDeclaredMethod("isMapType", String.class);
            method.setAccessible(true);
            
            assertTrue((Boolean) method.invoke(generator, "java.util.Map"));
            assertTrue((Boolean) method.invoke(generator, "java.util.HashMap"));
            assertTrue((Boolean) method.invoke(generator, "java.util.LinkedHashMap"));
            assertFalse((Boolean) method.invoke(generator, "java.util.List"));
        } catch (Exception e) {
            fail("Failed to test isMapType method: " + e.getMessage());
        }
    }
    
    @Test
    void testIsSystemClass() {
        // Use reflection to test the private method
        try {
            java.lang.reflect.Method method = TypeScriptGenerator.class.getDeclaredMethod("isSystemClass", String.class);
            method.setAccessible(true);
            
            assertTrue((Boolean) method.invoke(generator, "java.lang.String"));
            assertTrue((Boolean) method.invoke(generator, "java.util.List"));
            assertTrue((Boolean) method.invoke(generator, "javax.annotation.Nullable"));
            assertFalse((Boolean) method.invoke(generator, "com.yourname.pojo2jsts.test.User"));
            assertFalse((Boolean) method.invoke(generator, "my.custom.Class"));
        } catch (Exception e) {
            fail("Failed to test isSystemClass method: " + e.getMessage());
        }
    }
}