package com.yourname.pojo2jsts.generators;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TypeScriptGenerator {
    
    private static final Logger LOGGER = Logger.getLogger(TypeScriptGenerator.class.getName());
    private final Map<String, Integer> processedInterfaces = new ConcurrentHashMap<>();
    private final StringBuilder interfaceDefinitions = new StringBuilder();
    private final Set<String> currentPath = new HashSet<>();
    private final Set<String> processingClasses = new HashSet<>();
    private int recursionDepth = 0;
    private static final int MAX_RECURSION_DEPTH = 12;
    private static final int MAX_SAME_CLASS_VISITS = 3;
    private static final int MAX_PROCESSING_TIME_MS = 15000; // 15 seconds timeout
    private long startTime;
    
    public String generate(PsiClass psiClass, Project project) {
        if (psiClass == null || project == null) {
            LOGGER.warning("Null input provided to TypeScriptGenerator");
            return "// Error: Invalid input - psiClass or project is null";
        }
        
        // Reset state for each generation
        processedInterfaces.clear();
        interfaceDefinitions.setLength(0);
        currentPath.clear();
        processingClasses.clear();
        recursionDepth = 0;
        startTime = System.currentTimeMillis();
        
        try {
            LOGGER.fine("Starting TypeScript generation for class: " + psiClass.getQualifiedName());
            
            // First pass: collect all interface names to avoid forward reference issues
            collectAllInterfaces(psiClass, project);
            
            // Second pass: generate the main interface
            processedInterfaces.clear();
            currentPath.clear();
            processingClasses.clear();
            recursionDepth = 0;
            
            String mainInterface = generateInterface(psiClass, project);
            
            // Combine all interface definitions
            StringBuilder result = new StringBuilder();
            String definitions = interfaceDefinitions.toString().trim();
            if (!definitions.isEmpty()) {
                result.append(definitions);
                if (!definitions.endsWith("\n")) {
                    result.append("\n");
                }
                result.append("\n");
            }
            result.append(mainInterface);
            
            return result.toString();
        } catch (StackOverflowError e) {
            LOGGER.severe("Stack overflow during TypeScript generation: " + e.getMessage());
            return "// Error: Stack overflow - circular reference detected";
        } catch (OutOfMemoryError e) {
            LOGGER.severe("Out of memory during TypeScript generation: " + e.getMessage());
            return "// Error: Out of memory - structure too complex";
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during TypeScript generation", e);
            return "// Error generating TypeScript interface: " + sanitizeComment(e.getMessage());
        } finally {
            // Cleanup
            processedInterfaces.clear();
            interfaceDefinitions.setLength(0);
            currentPath.clear();
            processingClasses.clear();
        }
    }
    
    private String generateInterface(PsiClass psiClass, Project project) {
        if (psiClass == null) return "any";
        
        String className = psiClass.getName();
        String qualifiedName = psiClass.getQualifiedName();
        
        if (className == null || qualifiedName == null) return "any";
        
        // Check if already processed to avoid infinite recursion
        int visitCount = processedInterfaces.getOrDefault(qualifiedName, 0);
        if (visitCount >= MAX_SAME_CLASS_VISITS || recursionDepth > MAX_RECURSION_DEPTH ||
            currentPath.contains(qualifiedName)) {
            return className;
        }
        
        processedInterfaces.put(qualifiedName, visitCount + 1);
        currentPath.add(qualifiedName);
        recursionDepth++;
        
        StringBuilder interfaceBody = new StringBuilder();
        interfaceBody.append("export interface ").append(className).append(" {\n");
        
        // Get all fields including inherited ones
        List<PsiField> fields = getAllFields(psiClass);
        
        for (PsiField field : fields) {
            // Skip static and final fields
            if (field.hasModifierProperty(PsiModifier.STATIC) || 
                field.hasModifierProperty(PsiModifier.FINAL)) {
                continue;
            }
            
            String fieldName = field.getName();
            String fieldType = convertJavaTypeToTypeScript(field.getType(), project);
            
            // Handle optional fields (can be enhanced with annotations)
            boolean isOptional = isFieldOptional(field);
            String optionalMarker = isOptional ? "?" : "";
            
            interfaceBody.append("  ").append(fieldName).append(optionalMarker)
                         .append(": ").append(fieldType).append(";\n");
        }
        
        interfaceBody.append("}");
        
        currentPath.remove(qualifiedName);
        recursionDepth--;
        
        return interfaceBody.toString();
    }
    
    private List<PsiField> getAllFields(PsiClass psiClass) {
        List<PsiField> allFields = new ArrayList<>();
        
        // Get fields from current class
        allFields.addAll(Arrays.asList(psiClass.getFields()));
        
        // Get fields from superclass (except Object)
        PsiClass superClass = psiClass.getSuperClass();
        if (superClass != null && !"java.lang.Object".equals(superClass.getQualifiedName())) {
            allFields.addAll(getAllFields(superClass));
        }
        
        return allFields;
    }
    
    private String convertJavaTypeToTypeScript(PsiType type, Project project) {
        if (type == null) return "any";
        
        String typeName = type.getCanonicalText();
        
        // Handle primitive types
        if (PsiType.INT.equals(type) || "int".equals(typeName) ||
            PsiType.LONG.equals(type) || "long".equals(typeName) ||
            PsiType.DOUBLE.equals(type) || "double".equals(typeName) ||
            PsiType.FLOAT.equals(type) || "float".equals(typeName)) {
            return "number";
        } else if (PsiType.BOOLEAN.equals(type) || "boolean".equals(typeName)) {
            return "boolean";
        }
        
        // Handle wrapper types and common classes
        if ("java.lang.String".equals(typeName) || "String".equals(typeName)) {
            return "string";
        } else if ("java.lang.Integer".equals(typeName) || "Integer".equals(typeName) ||
                   "java.lang.Long".equals(typeName) || "Long".equals(typeName) ||
                   "java.lang.Double".equals(typeName) || "Double".equals(typeName) ||
                   "java.lang.Float".equals(typeName) || "Float".equals(typeName)) {
            return "number";
        } else if ("java.lang.Boolean".equals(typeName) || "Boolean".equals(typeName)) {
            return "boolean";
        } else if ("java.util.Date".equals(typeName) || "Date".equals(typeName) ||
                   "java.time.LocalDateTime".equals(typeName) || "LocalDateTime".equals(typeName) ||
                   "java.time.LocalDate".equals(typeName) || "LocalDate".equals(typeName) ||
                   "java.time.LocalTime".equals(typeName) || "LocalTime".equals(typeName) ||
                   "java.time.Instant".equals(typeName) || "Instant".equals(typeName)) {
            return "string"; // Dates are typically represented as ISO strings in JSON
        } else if ("java.util.UUID".equals(typeName) || "UUID".equals(typeName)) {
            return "string";
        } else if ("java.math.BigDecimal".equals(typeName) || "BigDecimal".equals(typeName) ||
                   "java.math.BigInteger".equals(typeName) || "BigInteger".equals(typeName)) {
            return "number";
        } else if ("java.lang.Character".equals(typeName) || "Character".equals(typeName) ||
                   PsiType.CHAR.equals(type) || "char".equals(typeName)) {
            return "string";
        } else if ("java.lang.Byte".equals(typeName) || "Byte".equals(typeName) ||
                   PsiType.BYTE.equals(type) || "byte".equals(typeName) ||
                   "java.lang.Short".equals(typeName) || "Short".equals(typeName) ||
                   PsiType.SHORT.equals(type) || "short".equals(typeName)) {
            return "number";
        }
        
        // Handle arrays
        if (type instanceof PsiArrayType) {
            PsiType componentType = ((PsiArrayType) type).getComponentType();
            return convertJavaTypeToTypeScript(componentType, project) + "[]";
        }
        
        // Handle generic types and collections
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            PsiClass psiClass = classType.resolve();
            
            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                
                // Handle collections (List, Set, Collection, etc.)
                if (isCollectionType(qualifiedName)) {
                    PsiType[] parameters = classType.getParameters();
                    if (parameters.length > 0) {
                        String elementType = convertJavaTypeToTypeScript(parameters[0], project);
                        return elementType + "[]";
                    }
                    return "any[]";
                }
                
                // Handle Map types
                if (isMapType(qualifiedName)) {
                    PsiType[] parameters = classType.getParameters();
                    if (parameters.length >= 2) {
                        String keyType = convertJavaTypeToTypeScript(parameters[0], project);
                        String valueType = convertJavaTypeToTypeScript(parameters[1], project);
                        
                        // TypeScript Record type for string keys, otherwise generic object
                        if ("string".equals(keyType)) {
                            return "Record<string, " + valueType + ">";
                        } else if ("number".equals(keyType)) {
                            return "Record<number, " + valueType + ">";
                        } else {
                            return "{ [key: " + keyType + "]: " + valueType + " }";
                        }
                    }
                    return "{ [key: string]: any }";
                }
                
                // Handle Optional
                if ("java.util.Optional".equals(qualifiedName)) {
                    PsiType[] parameters = classType.getParameters();
                    if (parameters.length > 0) {
                        return convertJavaTypeToTypeScript(parameters[0], project) + " | null";
                    }
                    return "any | null";
                }
                
                // Handle enums
                if (psiClass.isEnum()) {
                    return generateEnumType(psiClass);
                }
                
                // Handle custom classes - generate interface for them
                if (!isSystemClass(qualifiedName)) {
                    String interfaceName = psiClass.getName();
                    if (interfaceName != null) {
                        // Check if we need to generate the interface definition
                        if (!currentPath.contains(qualifiedName) && 
                            processedInterfaces.getOrDefault(qualifiedName, 0) == 0) {
                            // Generate the interface definition
                            String nestedInterface = generateInterface(psiClass, project);
                            if (nestedInterface != null && !nestedInterface.equals(interfaceName)) {
                                // Check if interface definition is not already added
                                String interfaceSignature = "export interface " + interfaceName + " {";
                                if (!interfaceDefinitions.toString().contains(interfaceSignature)) {
                                    interfaceDefinitions.append(nestedInterface).append("\n\n");
                                }
                            }
                        }
                        return interfaceName;
                    }
                    return "any";
                }
            }
        }
        
        return "any";
    }
    
    private boolean isFieldOptional(PsiField field) {
        PsiType type = field.getType();
        String typeName = type.getCanonicalText();
        
        // Check for @Nullable annotations
        PsiAnnotation[] annotations = field.getAnnotations();
        for (PsiAnnotation annotation : annotations) {
            String annotationName = annotation.getQualifiedName();
            if (annotationName != null && 
                (annotationName.contains("Nullable") || annotationName.contains("CheckForNull"))) {
                return true;
            }
        }
        
        // Wrapper types are potentially nullable
        if (typeName.startsWith("java.lang.") && !typeName.equals("java.lang.String")) {
            return true;
        }
        
        // Optional types are nullable
        if (typeName.startsWith("java.util.Optional")) {
            return true;
        }
        
        return false;
    }
    
    private boolean isSystemClass(String qualifiedName) {
        if (qualifiedName == null) return true;
        
        return qualifiedName.startsWith("java.") ||
               qualifiedName.startsWith("javax.") ||
               qualifiedName.startsWith("sun.") ||
               qualifiedName.startsWith("com.sun.") ||
               qualifiedName.startsWith("org.springframework.") ||
               qualifiedName.startsWith("com.fasterxml.jackson.");
    }
    
    private boolean isCollectionType(String qualifiedName) {
        return "java.util.List".equals(qualifiedName) ||
               "java.util.Set".equals(qualifiedName) ||
               "java.util.Collection".equals(qualifiedName) ||
               "java.util.ArrayList".equals(qualifiedName) ||
               "java.util.LinkedList".equals(qualifiedName) ||
               "java.util.Vector".equals(qualifiedName) ||
               "java.util.HashSet".equals(qualifiedName) ||
               "java.util.LinkedHashSet".equals(qualifiedName) ||
               "java.util.TreeSet".equals(qualifiedName) ||
               "java.util.Queue".equals(qualifiedName) ||
               "java.util.Deque".equals(qualifiedName);
    }
    
    private boolean isMapType(String qualifiedName) {
        return "java.util.Map".equals(qualifiedName) ||
               "java.util.HashMap".equals(qualifiedName) ||
               "java.util.LinkedHashMap".equals(qualifiedName) ||
               "java.util.TreeMap".equals(qualifiedName) ||
               "java.util.ConcurrentHashMap".equals(qualifiedName);
    }
    
    private String generateEnumType(PsiClass enumClass) {
        PsiField[] enumConstants = enumClass.getFields();
        StringBuilder enumType = new StringBuilder();
        
        List<String> constantNames = new ArrayList<>();
        for (PsiField field : enumConstants) {
            if (field.hasModifierProperty(PsiModifier.STATIC) && 
                field.hasModifierProperty(PsiModifier.FINAL) &&
                field.hasModifierProperty(PsiModifier.PUBLIC)) {
                constantNames.add('"' + field.getName() + '"');
            }
        }
        
        if (constantNames.isEmpty()) {
            return "string"; // Fallback for enums without constants
        }
        
        return String.join(" | ", constantNames);
    }
    
    private void collectAllInterfaces(PsiClass psiClass, Project project) {
        if (psiClass == null) return;
        
        String qualifiedName = psiClass.getQualifiedName();
        if (qualifiedName == null || isSystemClass(qualifiedName) || 
            currentPath.contains(qualifiedName) || recursionDepth > MAX_RECURSION_DEPTH) {
            return;
        }
        
        currentPath.add(qualifiedName);
        recursionDepth++;
        
        try {
            // Process all fields to discover dependent interfaces
            List<PsiField> fields = getAllFields(psiClass);
            for (PsiField field : fields) {
                if (field.hasModifierProperty(PsiModifier.STATIC) || 
                    field.hasModifierProperty(PsiModifier.FINAL)) {
                    continue;
                }
                collectInterfacesFromType(field.getType(), project);
            }
        } finally {
            currentPath.remove(qualifiedName);
            recursionDepth--;
        }
    }
    
    private void collectInterfacesFromType(PsiType type, Project project) {
        if (type == null) return;
        
        // Handle arrays
        if (type instanceof PsiArrayType) {
            collectInterfacesFromType(((PsiArrayType) type).getComponentType(), project);
            return;
        }
        
        // Handle generic types
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            PsiClass psiClass = classType.resolve();
            
            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                
                // Handle collections and maps - process their type parameters
                if (isCollectionType(qualifiedName) || isMapType(qualifiedName)) {
                    PsiType[] parameters = classType.getParameters();
                    for (PsiType parameter : parameters) {
                        collectInterfacesFromType(parameter, project);
                    }
                    return;
                }
                
                // Handle custom classes
                if (!isSystemClass(qualifiedName)) {
                    collectAllInterfaces(psiClass, project);
                }
            }
        }
    }
    
    /**
     * Sanitizes error messages for use in TypeScript comments
     */
    private String sanitizeComment(String message) {
        if (message == null) return "Unknown error";
        return message.replaceAll("\\*/", "* /")
                     .replaceAll("[\n\r\t]", " ")
                     .trim();
    }
    
    /**
     * Enhanced timeout and circular reference checking
     */
    private boolean shouldStopProcessing(String qualifiedName) {
        // Check timeout
        if (System.currentTimeMillis() - startTime > MAX_PROCESSING_TIME_MS) {
            LOGGER.warning("TypeScript generation timeout exceeded");
            return true;
        }
        
        // Check circular reference
        if (processingClasses.contains(qualifiedName)) {
            LOGGER.fine("Circular reference detected for class: " + qualifiedName);
            return true;
        }
        
        // Check recursion limits
        if (recursionDepth > MAX_RECURSION_DEPTH) {
            LOGGER.fine("Maximum recursion depth exceeded");
            return true;
        }
        
        return false;
    }
}