package com.yourname.pojo2jsts.generators;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import com.intellij.psi.util.PsiUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.logging.Level;

public class JsonGenerator {
    
    private static final Logger LOGGER = Logger.getLogger(JsonGenerator.class.getName());
    private final RandomValueGenerator randomValueGenerator = new RandomValueGenerator();
    private final Map<String, Integer> visitedClasses = new ConcurrentHashMap<>();
    private final Set<String> processingClasses = new HashSet<>();
    private int recursionDepth = 0;
    private static final int MAX_RECURSION_DEPTH = 8;
    private static final int MAX_SAME_CLASS_VISITS = 3;
    private static final int MAX_PROCESSING_TIME_MS = 10000; // 10 seconds timeout
    private long startTime;
    
    public String generate(PsiClass psiClass, Project project) {
        if (psiClass == null || project == null) {
            LOGGER.warning("Null input provided to JsonGenerator");
            return "{\n  \"error\": \"Invalid input: psiClass or project is null\"\n}";
        }
        
        // Reset state for each generation
        visitedClasses.clear();
        processingClasses.clear();
        recursionDepth = 0;
        startTime = System.currentTimeMillis();
        
        try {
            LOGGER.fine("Starting JSON generation for class: " + psiClass.getQualifiedName());
            String json = generateJsonObject(psiClass, project);
            return formatJson(json);
        } catch (StackOverflowError e) {
            LOGGER.severe("Stack overflow during JSON generation: " + e.getMessage());
            return "{\n  \"error\": \"Stack overflow - circular reference detected\"\n}";
        } catch (OutOfMemoryError e) {
            LOGGER.severe("Out of memory during JSON generation: " + e.getMessage());
            return "{\n  \"error\": \"Out of memory - structure too complex\"\n}";
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error during JSON generation", e);
            return "{\n  \"error\": \"Failed to generate JSON: " + sanitizeErrorMessage(e.getMessage()) + "\"\n}";
        } finally {
            // Cleanup
            visitedClasses.clear();
            processingClasses.clear();
        }
    }
    
    private String generateJsonObject(PsiClass psiClass, Project project) {
        if (psiClass == null) return "null";
        
        // Check timeout
        if (System.currentTimeMillis() - startTime > MAX_PROCESSING_TIME_MS) {
            LOGGER.warning("JSON generation timeout exceeded");
            return "{\"error\": \"Generation timeout\"}";
        }
        
        String className = psiClass.getQualifiedName();
        if (className == null) {
            LOGGER.fine("Class has no qualified name, using simple name");
            className = psiClass.getName();
            if (className == null) return "null";
        }
        
        // Enhanced circular reference detection
        if (processingClasses.contains(className)) {
            LOGGER.fine("Circular reference detected for class: " + className);
            return "null";
        }
        
        int visitCount = visitedClasses.getOrDefault(className, 0);
        if (visitCount >= MAX_SAME_CLASS_VISITS || recursionDepth > MAX_RECURSION_DEPTH) {
            LOGGER.fine("Recursion/visit limits reached for class: " + className);
            return "null";
        }
        
        visitedClasses.put(className, visitCount + 1);
        processingClasses.add(className);
        recursionDepth++;
        
        StringBuilder json = new StringBuilder("{\n");
        
        // Get all fields including inherited ones
        List<PsiField> fields = getAllFields(psiClass);
        
        boolean first = true;
        for (PsiField field : fields) {
            // Skip static and final fields
            if (field.hasModifierProperty(PsiModifier.STATIC) || 
                field.hasModifierProperty(PsiModifier.FINAL)) {
                continue;
            }
            
            if (!first) {
                json.append(",\n");
            }
            first = false;
            
            String fieldName = field.getName();
            String value = generateValueForType(field.getType(), project);
            
            json.append("  \"").append(fieldName).append("\": ").append(value);
        }
        
        json.append("\n}");
        
        // Cleanup visit tracking
        processingClasses.remove(className);
        visitedClasses.put(className, visitedClasses.get(className) - 1);
        if (visitedClasses.get(className) <= 0) {
            visitedClasses.remove(className);
        }
        recursionDepth--;
        
        return json.toString();
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
    
    private String generateValueForType(PsiType type, Project project) {
        if (type == null) return "null";
        
        String typeName = type.getCanonicalText();
        
        // Handle primitive types
        if (PsiType.INT.equals(type) || "int".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateInt());
        } else if (PsiType.LONG.equals(type) || "long".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateLong());
        } else if (PsiType.DOUBLE.equals(type) || "double".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateDouble());
        } else if (PsiType.FLOAT.equals(type) || "float".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateFloat());
        } else if (PsiType.BOOLEAN.equals(type) || "boolean".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateBoolean());
        }
        
        // Handle wrapper types and common classes
        if ("java.lang.String".equals(typeName) || "String".equals(typeName)) {
            return "\"" + randomValueGenerator.generateString() + "\"";
        } else if ("java.lang.Integer".equals(typeName) || "Integer".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateInt());
        } else if ("java.lang.Long".equals(typeName) || "Long".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateLong());
        } else if ("java.lang.Double".equals(typeName) || "Double".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateDouble());
        } else if ("java.lang.Float".equals(typeName) || "Float".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateFloat());
        } else if ("java.lang.Boolean".equals(typeName) || "Boolean".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateBoolean());
        } else if ("java.util.Date".equals(typeName) || "Date".equals(typeName)) {
            return "\"" + randomValueGenerator.generateDate() + "\"";
        } else if ("java.time.LocalDateTime".equals(typeName) || "LocalDateTime".equals(typeName)) {
            return "\"" + randomValueGenerator.generateDateTime() + "\"";
        } else if ("java.time.LocalDate".equals(typeName) || "LocalDate".equals(typeName)) {
            return "\"" + randomValueGenerator.generateDate().split(" ")[0] + "\"";
        } else if ("java.time.LocalTime".equals(typeName) || "LocalTime".equals(typeName)) {
            return "\"" + randomValueGenerator.generateDate().split(" ")[1] + "\"";
        } else if ("java.util.UUID".equals(typeName) || "UUID".equals(typeName)) {
            return "\"" + randomValueGenerator.generateUUID() + "\"";
        } else if ("java.math.BigDecimal".equals(typeName) || "BigDecimal".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateDouble());
        } else if ("java.math.BigInteger".equals(typeName) || "BigInteger".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateLong());
        }
        
        // Handle arrays
        if (type instanceof PsiArrayType) {
            PsiType componentType = ((PsiArrayType) type).getComponentType();
            return generateArrayValue(componentType, project);
        }
        
        // Handle collections
        if (type instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) type;
            PsiClass psiClass = classType.resolve();
            
            if (psiClass != null) {
                String qualifiedName = psiClass.getQualifiedName();
                
                // Handle List, Set, Collection, ArrayList, LinkedList, etc.
                if (isCollectionType(qualifiedName)) {
                    PsiType[] parameters = classType.getParameters();
                    if (parameters.length > 0) {
                        return generateArrayValue(parameters[0], project);
                    }
                    return "[]";
                }
                
                // Handle Map, HashMap, LinkedHashMap, etc.
                if (isMapType(qualifiedName)) {
                    PsiType[] parameters = classType.getParameters();
                    if (parameters.length >= 2) {
                        return generateMapValue(parameters[0], parameters[1], project);
                    }
                    return "{}";
                }
                
                // Handle Optional
                if ("java.util.Optional".equals(qualifiedName)) {
                    PsiType[] parameters = classType.getParameters();
                    if (parameters.length > 0) {
                        // Sometimes generate null, sometimes the value
                        return randomValueGenerator.generateBoolean() ? 
                            generateValueForType(parameters[0], project) : "null";
                    }
                    return "null";
                }
                
                // Handle custom classes
                return generateJsonObject(psiClass, project);
            }
        }
        
        return "null";
    }
    
    private String generateArrayValue(PsiType elementType, Project project) {
        StringBuilder array = new StringBuilder("[");
        int size = randomValueGenerator.generateArraySize();
        
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                array.append(", ");
            }
            array.append(generateValueForType(elementType, project));
        }
        
        array.append("]");
        return array.toString();
    }
    
    private String generateMapValue(PsiType keyType, PsiType valueType, Project project) {
        StringBuilder map = new StringBuilder("{");
        int size = Math.min(randomValueGenerator.generateArraySize(), 3); // Limit map size
        
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                map.append(", ");
            }
            
            // Generate key - must be a string in JSON
            String key = generateKeyForType(keyType);
            String value = generateValueForType(valueType, project);
            
            map.append("\"").append(key).append("\": ").append(value);
        }
        
        map.append("}");
        return map.toString();
    }
    
    private String generateKeyForType(PsiType keyType) {
        // In JSON, keys must be strings
        if (keyType == null) {
            return "key" + randomValueGenerator.generateInt();
        }
        
        String typeName = keyType.getCanonicalText();
        if ("java.lang.String".equals(typeName) || "String".equals(typeName)) {
            return randomValueGenerator.generateString();
        } else if (PsiType.INT.equals(keyType) || "int".equals(typeName) ||
                   "java.lang.Integer".equals(typeName) || "Integer".equals(typeName)) {
            return String.valueOf(randomValueGenerator.generateInt());
        } else {
            return "key" + randomValueGenerator.generateInt();
        }
    }
    
    private boolean isCollectionType(String qualifiedName) {
        return COLLECTION_TYPES.contains(qualifiedName);
    }
    
    private boolean isMapType(String qualifiedName) {
        return MAP_TYPES.contains(qualifiedName);
    }
    
    private String formatJson(String json) {
        // Simple JSON formatting - can be improved
        StringBuilder formatted = new StringBuilder();
        int indentLevel = 0;
        boolean inString = false;
        boolean escaped = false;
        
        for (char c : json.toCharArray()) {
            if (!inString) {
                switch (c) {
                    case '{':
                    case '[':
                        formatted.append(c).append('\n');
                        indentLevel++;
                        appendIndent(formatted, indentLevel);
                        break;
                    case '}':
                    case ']':
                        formatted.append('\n');
                        indentLevel--;
                        appendIndent(formatted, indentLevel);
                        formatted.append(c);
                        break;
                    case ',':
                        formatted.append(c).append('\n');
                        appendIndent(formatted, indentLevel);
                        break;
                    case ':':
                        formatted.append(c).append(' ');
                        break;
                    case ' ':
                    case '\n':
                    case '\t':
                        // Skip whitespace when not in string
                        break;
                    default:
                        formatted.append(c);
                }
            } else {
                formatted.append(c);
            }
            
            if (c == '"' && !escaped) {
                inString = !inString;
            }
            escaped = (c == '\\' && !escaped);
        }
        
        return formatted.toString();
    }
    
    private void appendIndent(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
    }
    
    /**
     * Sanitizes error messages to prevent JSON injection
     */
    private String sanitizeErrorMessage(String message) {
        if (message == null) return "Unknown error";
        return message.replaceAll("[\"\\\\]", "\\\\$0")
                     .replaceAll("[\n\r\t]", " ")
                     .trim();
    }
    
    /**
     * Enhanced collection type detection with caching
     */
    private static final Set<String> COLLECTION_TYPES = Set.of(
        "java.util.List", "java.util.Set", "java.util.Collection",
        "java.util.ArrayList", "java.util.LinkedList", "java.util.Vector",
        "java.util.HashSet", "java.util.LinkedHashSet", "java.util.TreeSet",
        "java.util.Queue", "java.util.Deque", "java.util.ArrayDeque"
    );
    
    /**
     * Enhanced map type detection with caching
     */
    private static final Set<String> MAP_TYPES = Set.of(
        "java.util.Map", "java.util.HashMap", "java.util.LinkedHashMap",
        "java.util.TreeMap", "java.util.ConcurrentHashMap", "java.util.WeakHashMap",
        "java.util.IdentityHashMap", "java.util.EnumMap"
    );
}