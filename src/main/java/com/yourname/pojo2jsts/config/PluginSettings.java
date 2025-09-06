package com.yourname.pojo2jsts.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Persistent plugin settings with XML serialization support
 */
@Service
@State(
    name = "PluginSettings",
    storages = @Storage("pojo2js_ts.xml")
)
public final class PluginSettings implements PersistentStateComponent<PluginSettings> {
    
    // General settings
    private int maxRecursionDepth = 5;
    private int arrayMaxSize = 3;
    private boolean enableRandomValues = true;
    private boolean formatOutput = true;
    private boolean copyToClipboard = true;
    
    // Date configuration
    private DateConfig dateConfig = new DateConfig();
    
    // Package mappings
    private List<PackageMapping> packageMappings = new ArrayList<>();
    
    // Type mappings
    private List<TypeMapping> typeMappings = new ArrayList<>();
    
    public PluginSettings() {
        initializeDefaultMappings();
    }
    
    public static PluginSettings getInstance() {
        return ApplicationManager.getApplication().getService(PluginSettings.class);
    }
    
    @Override
    public @Nullable PluginSettings getState() {
        return this;
    }
    
    @Override
    public void loadState(@NotNull PluginSettings state) {
        XmlSerializerUtil.copyBean(state, this);
        
        // Ensure we have default mappings if none were loaded
        if (packageMappings.isEmpty() && typeMappings.isEmpty()) {
            initializeDefaultMappings();
        }
    }
    
    private void initializeDefaultMappings() {
        // Clear existing mappings
        packageMappings.clear();
        typeMappings.clear();
        
        // Initialize default type mappings
        typeMappings.addAll(Arrays.asList(
            new TypeMapping("java.util.Date", "\"{{random_date}}\"", "string", "Standard Java Date"),
            new TypeMapping("java.time.LocalDateTime", "\"{{random_datetime}}\"", "string", "Java 8 LocalDateTime"),
            new TypeMapping("java.time.LocalDate", "\"{{random_date_only}}\"", "string", "Java 8 LocalDate"),
            new TypeMapping("java.time.LocalTime", "\"{{random_time_only}}\"", "string", "Java 8 LocalTime"),
            new TypeMapping("java.math.BigDecimal", "{{random_decimal}}", "number", "BigDecimal for precision"),
            new TypeMapping("java.math.BigInteger", "{{random_integer}}", "number", "BigInteger for large numbers"),
            new TypeMapping("java.util.UUID", "\"{{random_uuid}}\"", "string", "Universally Unique Identifier")
        ));
        
        // Initialize default package mappings (examples)
        packageMappings.addAll(Arrays.asList(
            new PackageMapping("com.example.dto.*", "string", "string"),
            new PackageMapping("org.springframework.data.domain.*", "any", "any")
        ));
        
        // Set them as disabled by default (user can enable as needed)
        packageMappings.forEach(mapping -> mapping.setEnabled(false));
    }
    
    // General settings getters/setters
    public int getMaxRecursionDepth() { return maxRecursionDepth; }
    public void setMaxRecursionDepth(int maxRecursionDepth) { 
        this.maxRecursionDepth = Math.max(1, Math.min(maxRecursionDepth, 20));
    }
    
    public int getArrayMaxSize() { return arrayMaxSize; }
    public void setArrayMaxSize(int arrayMaxSize) { 
        this.arrayMaxSize = Math.max(1, Math.min(arrayMaxSize, 10));
    }
    
    public boolean isEnableRandomValues() { return enableRandomValues; }
    public void setEnableRandomValues(boolean enableRandomValues) { this.enableRandomValues = enableRandomValues; }
    
    public boolean isFormatOutput() { return formatOutput; }
    public void setFormatOutput(boolean formatOutput) { this.formatOutput = formatOutput; }
    
    public boolean isCopyToClipboard() { return copyToClipboard; }
    public void setCopyToClipboard(boolean copyToClipboard) { this.copyToClipboard = copyToClipboard; }
    
    // Date config
    public DateConfig getDateConfig() { return dateConfig; }
    public void setDateConfig(DateConfig dateConfig) { this.dateConfig = dateConfig; }
    
    // Package mappings
    public List<PackageMapping> getPackageMappings() { return packageMappings; }
    public void setPackageMappings(List<PackageMapping> packageMappings) { 
        this.packageMappings = packageMappings != null ? packageMappings : new ArrayList<>();
    }
    
    public void addPackageMapping(PackageMapping mapping) {
        if (mapping != null) {
            packageMappings.add(mapping);
        }
    }
    
    public void removePackageMapping(PackageMapping mapping) {
        packageMappings.remove(mapping);
    }
    
    // Type mappings
    public List<TypeMapping> getTypeMappings() { return typeMappings; }
    public void setTypeMappings(List<TypeMapping> typeMappings) { 
        this.typeMappings = typeMappings != null ? typeMappings : new ArrayList<>();
    }
    
    public void addTypeMapping(TypeMapping mapping) {
        if (mapping != null) {
            typeMappings.add(mapping);
        }
    }
    
    public void removeTypeMapping(TypeMapping mapping) {
        typeMappings.remove(mapping);
    }
    
    // Utility methods
    public Optional<PackageMapping> findPackageMapping(String className) {
        return packageMappings.stream()
                .filter(PackageMapping::isEnabled)
                .filter(mapping -> mapping.matches(className))
                .findFirst();
    }
    
    public Optional<TypeMapping> findTypeMapping(String typeName) {
        return typeMappings.stream()
                .filter(TypeMapping::isEnabled)
                .filter(mapping -> mapping.matches(typeName))
                .findFirst();
    }
    
    /**
     * Reset all settings to defaults
     */
    public void resetToDefaults() {
        maxRecursionDepth = 5;
        arrayMaxSize = 3;
        enableRandomValues = true;
        formatOutput = true;
        copyToClipboard = true;
        dateConfig = new DateConfig();
        initializeDefaultMappings();
    }
    
    /**
     * Validate current settings
     */
    public List<String> validateSettings() {
        List<String> errors = new ArrayList<>();
        
        if (maxRecursionDepth < 1 || maxRecursionDepth > 20) {
            errors.add("Max recursion depth must be between 1 and 20");
        }
        
        if (arrayMaxSize < 1 || arrayMaxSize > 10) {
            errors.add("Array max size must be between 1 and 10");
        }
        
        // Validate date config
        try {
            if (dateConfig.getFormat() == null || dateConfig.getFormat().trim().isEmpty()) {
                errors.add("Date format cannot be empty");
            }
        } catch (IllegalArgumentException e) {
            errors.add("Invalid date format: " + e.getMessage());
        }
        
        // Validate package mappings
        for (int i = 0; i < packageMappings.size(); i++) {
            PackageMapping mapping = packageMappings.get(i);
            if (mapping.getPackagePattern() == null || mapping.getPackagePattern().trim().isEmpty()) {
                errors.add("Package mapping #" + (i + 1) + " has empty pattern");
            }
        }
        
        // Validate type mappings
        for (int i = 0; i < typeMappings.size(); i++) {
            TypeMapping mapping = typeMappings.get(i);
            if (mapping.getSourceType() == null || mapping.getSourceType().trim().isEmpty()) {
                errors.add("Type mapping #" + (i + 1) + " has empty source type");
            }
        }
        
        return errors;
    }
}