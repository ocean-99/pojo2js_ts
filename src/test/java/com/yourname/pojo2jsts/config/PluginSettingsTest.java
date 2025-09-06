package com.yourname.pojo2jsts.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Unit tests for PluginSettings class
 */
class PluginSettingsTest {
    
    private PluginSettings settings;
    
    @BeforeEach
    void setUp() {
        settings = new PluginSettings();
    }
    
    @Test
    void testDefaultValues() {
        assertNotNull(settings.getPackageMappings());
        assertTrue(settings.getPackageMappings().isEmpty());
        
        assertNotNull(settings.getTypeMappings());
        assertTrue(settings.getTypeMappings().isEmpty());
        
        assertNotNull(settings.getDateConfig());
        assertEquals("yyyy-MM-dd HH:mm:ss", settings.getDateConfig().getFormat());
        assertEquals(365, settings.getDateConfig().getPastDays());
        assertEquals(0, settings.getDateConfig().getFutureDays());
        assertFalse(settings.getDateConfig().isUseCurrentTime());
        assertEquals("UTC", settings.getDateConfig().getTimeZone());
    }
    
    @Test
    void testPackageMappingsManagement() {
        PackageMapping mapping1 = new PackageMapping("com.example.*", "string", "string");
        PackageMapping mapping2 = new PackageMapping("com.test.*", "number", "number");
        
        List<PackageMapping> mappings = new ArrayList<>();
        mappings.add(mapping1);
        mappings.add(mapping2);
        
        settings.setPackageMappings(mappings);
        
        assertEquals(2, settings.getPackageMappings().size());
        assertEquals("com.example.*", settings.getPackageMappings().get(0).getPackagePattern());
        assertEquals("com.test.*", settings.getPackageMappings().get(1).getPackagePattern());
    }
    
    @Test
    void testTypeMappingsManagement() {
        TypeMapping mapping1 = new TypeMapping("java.util.Date", "\"{{random_date}}\"", "string");
        TypeMapping mapping2 = new TypeMapping("java.math.BigDecimal", "{{random_number}}", "number");
        
        List<TypeMapping> mappings = new ArrayList<>();
        mappings.add(mapping1);
        mappings.add(mapping2);
        
        settings.setTypeMappings(mappings);
        
        assertEquals(2, settings.getTypeMappings().size());
        assertEquals("java.util.Date", settings.getTypeMappings().get(0).getSourceType());
        assertEquals("java.math.BigDecimal", settings.getTypeMappings().get(1).getSourceType());
    }
    
    @Test
    void testDateConfigManagement() {
        DateConfig dateConfig = new DateConfig("dd/MM/yyyy", 30, 7);
        dateConfig.setUseCurrentTime(true);
        dateConfig.setTimeZone("Asia/Shanghai");
        
        settings.setDateConfig(dateConfig);
        
        assertEquals("dd/MM/yyyy", settings.getDateConfig().getFormat());
        assertEquals(30, settings.getDateConfig().getPastDays());
        assertEquals(7, settings.getDateConfig().getFutureDays());
        assertTrue(settings.getDateConfig().isUseCurrentTime());
        assertEquals("Asia/Shanghai", settings.getDateConfig().getTimeZone());
    }
    
    @Test
    void testStateManagement() {
        // Prepare test data
        PackageMapping packageMapping = new PackageMapping("com.example.*", "string", "string");
        TypeMapping typeMapping = new TypeMapping("java.util.Date", "\"{{random_date}}\"", "string");
        DateConfig dateConfig = new DateConfig("yyyy-MM-dd", 100, 10);
        
        List<PackageMapping> packageMappings = new ArrayList<>();
        packageMappings.add(packageMapping);
        
        List<TypeMapping> typeMappings = new ArrayList<>();
        typeMappings.add(typeMapping);
        
        // Set state
        settings.setPackageMappings(packageMappings);
        settings.setTypeMappings(typeMappings);
        settings.setDateConfig(dateConfig);
        
        // Get state
        PluginSettings retrievedState = settings.getState();
        
        assertNotNull(retrievedState);
        assertEquals(1, retrievedState.getPackageMappings().size());
        assertEquals(1, retrievedState.getTypeMappings().size());
        assertNotNull(retrievedState.getDateConfig());
        
        assertEquals("com.example.*", retrievedState.getPackageMappings().get(0).getPackagePattern());
        assertEquals("java.util.Date", retrievedState.getTypeMappings().get(0).getSourceType());
        assertEquals("yyyy-MM-dd", retrievedState.getDateConfig().getFormat());
    }
    
    @Test
    void testLoadState() {
        // Create a source settings object
        PluginSettings sourceSettings = new PluginSettings();
        
        PackageMapping packageMapping = new PackageMapping("com.test.*", "string", "string");
        TypeMapping typeMapping = new TypeMapping("java.time.LocalDate", "\"{{random_date_only}}\"", "string");
        DateConfig dateConfig = new DateConfig("MM/dd/yyyy", 180, 30);
        
        List<PackageMapping> packageMappings = new ArrayList<>();
        packageMappings.add(packageMapping);
        
        List<TypeMapping> typeMappings = new ArrayList<>();
        typeMappings.add(typeMapping);
        
        sourceSettings.setPackageMappings(packageMappings);
        sourceSettings.setTypeMappings(typeMappings);
        sourceSettings.setDateConfig(dateConfig);
        
        // Load state into target settings
        settings.loadState(sourceSettings);
        
        // Verify loaded state
        assertEquals(1, settings.getPackageMappings().size());
        assertEquals(1, settings.getTypeMappings().size());
        
        assertEquals("com.test.*", settings.getPackageMappings().get(0).getPackagePattern());
        assertEquals("java.time.LocalDate", settings.getTypeMappings().get(0).getSourceType());
        assertEquals("MM/dd/yyyy", settings.getDateConfig().getFormat());
        assertEquals(180, settings.getDateConfig().getPastDays());
        assertEquals(30, settings.getDateConfig().getFutureDays());
    }
    
    @Test
    void testNullSafetyInLoadState() {
        PluginSettings sourceSettings = new PluginSettings();
        sourceSettings.setPackageMappings(null);
        sourceSettings.setTypeMappings(null);
        sourceSettings.setDateConfig(null);
        
        settings.loadState(sourceSettings);
        
        // Should fallback to empty lists and default DateConfig
        assertNotNull(settings.getPackageMappings());
        assertTrue(settings.getPackageMappings().isEmpty());
        
        assertNotNull(settings.getTypeMappings());
        assertTrue(settings.getTypeMappings().isEmpty());
        
        assertNotNull(settings.getDateConfig());
    }
    
    @Test
    void testLoadStateWithNullParameter() {
        // Load initial data
        PackageMapping mapping = new PackageMapping("com.example.*", "string", "string");
        List<PackageMapping> mappings = new ArrayList<>();
        mappings.add(mapping);
        settings.setPackageMappings(mappings);
        
        // Load null state - should preserve existing data
        settings.loadState(null);
        
        assertEquals(1, settings.getPackageMappings().size());
        assertEquals("com.example.*", settings.getPackageMappings().get(0).getPackagePattern());
    }
    
    @Test
    void testCopyConstructorBehavior() {
        // Test that collections are properly copied, not referenced
        PackageMapping mapping = new PackageMapping("com.example.*", "string", "string");
        List<PackageMapping> originalMappings = new ArrayList<>();
        originalMappings.add(mapping);
        
        settings.setPackageMappings(originalMappings);
        
        // Modify original list
        originalMappings.add(new PackageMapping("com.test.*", "number", "number"));
        
        // Settings should not be affected by external list changes
        assertEquals(1, settings.getPackageMappings().size());
        assertEquals("com.example.*", settings.getPackageMappings().get(0).getPackagePattern());
    }
    
    @Test
    void testIndependentInstanceState() {
        PluginSettings settings1 = new PluginSettings();
        PluginSettings settings2 = new PluginSettings();
        
        PackageMapping mapping1 = new PackageMapping("com.example.*", "string", "string");
        List<PackageMapping> mappings1 = new ArrayList<>();
        mappings1.add(mapping1);
        settings1.setPackageMappings(mappings1);
        
        PackageMapping mapping2 = new PackageMapping("com.test.*", "number", "number");
        List<PackageMapping> mappings2 = new ArrayList<>();
        mappings2.add(mapping2);
        settings2.setPackageMappings(mappings2);
        
        // Instances should be independent
        assertEquals(1, settings1.getPackageMappings().size());
        assertEquals(1, settings2.getPackageMappings().size());
        assertEquals("com.example.*", settings1.getPackageMappings().get(0).getPackagePattern());
        assertEquals("com.test.*", settings2.getPackageMappings().get(0).getPackagePattern());
    }
}