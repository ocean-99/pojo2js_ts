package com.yourname.pojo2jsts.config;

import java.util.Objects;

/**
 * Type mapping configuration for converting specific Java types
 * to custom JSON values and TypeScript types
 */
public class TypeMapping {
    private String sourceType;       // 源Java类型 (e.g., "java.util.Date", "java.math.BigDecimal")
    private String jsonValuePattern; // JSON值模式 (e.g., "\"{{random_date}}\"", "{{random_number}}")
    private String tsType;           // TypeScript类型 (e.g., "string", "number")
    private String customGenerator;  // 自定义生成器类名 (可选)
    private boolean enabled;         // 是否启用此映射
    private String description;      // 描述说明
    
    public TypeMapping() {
        this.enabled = true;
    }
    
    public TypeMapping(String sourceType, String jsonValuePattern, String tsType) {
        this.sourceType = sourceType;
        this.jsonValuePattern = jsonValuePattern;
        this.tsType = tsType;
        this.enabled = true;
    }
    
    public TypeMapping(String sourceType, String jsonValuePattern, String tsType, String description) {
        this(sourceType, jsonValuePattern, tsType);
        this.description = description;
    }
    
    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }
    
    public String getJsonValuePattern() { return jsonValuePattern; }
    public void setJsonValuePattern(String jsonValuePattern) { this.jsonValuePattern = jsonValuePattern; }
    
    public String getTsType() { return tsType; }
    public void setTsType(String tsType) { this.tsType = tsType; }
    
    public String getCustomGenerator() { return customGenerator; }
    public void setCustomGenerator(String customGenerator) { this.customGenerator = customGenerator; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    /**
     * Check if this mapping applies to the given type name
     */
    public boolean matches(String typeName) {
        if (!enabled || sourceType == null || typeName == null) {
            return false;
        }
        
        return sourceType.equals(typeName) || 
               (typeName.contains(".") && typeName.endsWith("." + sourceType)) ||
               (sourceType.contains(".") && sourceType.endsWith("." + typeName));
    }
    
    /**
     * Check if this mapping has a custom generator
     */
    public boolean hasCustomGenerator() {
        return customGenerator != null && !customGenerator.trim().isEmpty();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeMapping that = (TypeMapping) o;
        return enabled == that.enabled &&
                Objects.equals(sourceType, that.sourceType) &&
                Objects.equals(jsonValuePattern, that.jsonValuePattern) &&
                Objects.equals(tsType, that.tsType) &&
                Objects.equals(customGenerator, that.customGenerator);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(sourceType, jsonValuePattern, tsType, customGenerator, enabled);
    }
    
    @Override
    public String toString() {
        return "TypeMapping{" +
                "sourceType='" + sourceType + '\'' +
                ", jsonPattern='" + jsonValuePattern + '\'' +
                ", tsType='" + tsType + '\'' +
                ", enabled=" + enabled +
                (description != null ? ", desc='" + description + '\'' : "") +
                '}';
    }
}