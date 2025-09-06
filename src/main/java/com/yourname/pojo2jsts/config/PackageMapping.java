package com.yourname.pojo2jsts.config;

import java.util.Objects;

/**
 * Package mapping configuration for converting classes in specific packages
 * to specific types in JSON/TypeScript generation
 */
public class PackageMapping {
    private String packagePattern;  // 包名模式，支持通配符 (e.g., "com.example.*")
    private String jsonTargetType;  // JSON目标类型 (e.g., "string", "number")
    private String tsTargetType;    // TypeScript目标类型 (e.g., "string", "number")
    private boolean recursive;      // 是否应用到子包
    private boolean enabled;        // 是否启用此映射
    
    public PackageMapping() {
        this.recursive = true;
        this.enabled = true;
    }
    
    public PackageMapping(String packagePattern, String jsonTargetType, String tsTargetType) {
        this.packagePattern = packagePattern;
        this.jsonTargetType = jsonTargetType;
        this.tsTargetType = tsTargetType;
        this.recursive = true;
        this.enabled = true;
    }
    
    public String getPackagePattern() { return packagePattern; }
    public void setPackagePattern(String packagePattern) { this.packagePattern = packagePattern; }
    
    public String getJsonTargetType() { return jsonTargetType; }
    public void setJsonTargetType(String jsonTargetType) { this.jsonTargetType = jsonTargetType; }
    
    public String getTsTargetType() { return tsTargetType; }
    public void setTsTargetType(String tsTargetType) { this.tsTargetType = tsTargetType; }
    
    public boolean isRecursive() { return recursive; }
    public void setRecursive(boolean recursive) { this.recursive = recursive; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    /**
     * Check if this mapping applies to the given class name
     */
    public boolean matches(String className) {
        if (!enabled || packagePattern == null || className == null) {
            return false;
        }
        
        if (packagePattern.endsWith("*")) {
            String basePackage = packagePattern.substring(0, packagePattern.length() - 1);
            if (recursive) {
                return className.startsWith(basePackage);
            } else {
                // Only match direct package, not subpackages
                String remaining = className.substring(basePackage.length());
                return !remaining.contains(".");
            }
        } else {
            // Exact package match
            int lastDot = className.lastIndexOf('.');
            if (lastDot == -1) return false;
            String packageName = className.substring(0, lastDot);
            return packageName.equals(packagePattern);
        }
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PackageMapping that = (PackageMapping) o;
        return recursive == that.recursive &&
                enabled == that.enabled &&
                Objects.equals(packagePattern, that.packagePattern) &&
                Objects.equals(jsonTargetType, that.jsonTargetType) &&
                Objects.equals(tsTargetType, that.tsTargetType);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(packagePattern, jsonTargetType, tsTargetType, recursive, enabled);
    }
    
    @Override
    public String toString() {
        return "PackageMapping{" +
                "pattern='" + packagePattern + '\'' +
                ", jsonType='" + jsonTargetType + '\'' +
                ", tsType='" + tsTargetType + '\'' +
                ", recursive=" + recursive +
                ", enabled=" + enabled +
                '}';
    }
}