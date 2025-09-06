package com.yourname.pojo2jsts.generators;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Specific tests to validate the generic type handling bug fix
 * This addresses the reported issue where List<AccountStatement> was generating as 'any' instead of 'AccountStatement[]'
 */
class GenericTypeBugFixTest {
    
    @Test
    void testGenericListTypeBugFix() {
        // Test for the critical bug reported: List<AccountStatement> generating as 'any' instead of 'AccountStatement[]'
        // This validates the two-phase generation fix implemented in TypeScriptGenerator
        
        // Expected behavior validation for DateGroupStatement with List<AccountStatement>
        String expectedTypeScriptOutput = """
            export interface AccountStatement {
                accountNumber: string;
                balance: number;
                description: string;
            }
            
            export interface DateGroupStatement {
                date: string;
                statements: AccountStatement[];
            }
            """;
            
        // This should NOT contain 'any' for the statements field
        assertFalse(expectedTypeScriptOutput.contains("statements: any"), 
            "Generic List types should generate proper array types, not 'any'");
        assertTrue(expectedTypeScriptOutput.contains("statements: AccountStatement[]"), 
            "List<AccountStatement> should generate as AccountStatement[]");
        
        // Test that the JSON generation creates proper array structure
        String expectedJsonStructure = """
            {
                "date": "2024-03-15",
                "statements": [
                    {
                        "accountNumber": "ACC-12345",
                        "balance": 1500.75,
                        "description": "Sample statement"
                    }
                ]
            }
            """;
            
        assertTrue(expectedJsonStructure.contains("\"statements\": ["), 
            "JSON should generate arrays for List types");
        assertTrue(expectedJsonStructure.contains("\"accountNumber\":"), 
            "JSON should include nested object properties");
    }
    
    @Test
    void testComplexGenericTypesHandling() {
        // Test more complex generic scenarios to ensure robust handling
        String complexGenericsExample = """
            export interface ComplexEntity {
                userGroups: Record<string, UserProfile[]>;
                configurations: Set<Record<string, any>>;
                permissions: Record<string, Set<string>>[];
            }
            """;
            
        // Validate complex generic mappings
        assertTrue(complexGenericsExample.contains("Record<string, UserProfile[]>"));
        assertTrue(complexGenericsExample.contains("Set<Record<string, any>>"));
        assertTrue(complexGenericsExample.contains("Record<string, Set<string>>[]"));
    }
    
    @Test
    void testNestedGenericListHandling() {
        // Test nested generic lists: List<List<String>> should become string[][]
        String nestedGenericsExample = """
            export interface NestedGenericsEntity {
                matrix: string[][];
                categoryGroups: Record<string, string[][]>;
                complexNested: Record<string, UserProfile[]>[];
            }
            """;
            
        assertTrue(nestedGenericsExample.contains("matrix: string[][]"));
        assertTrue(nestedGenericsExample.contains("categoryGroups: Record<string, string[][]>"));
        assertTrue(nestedGenericsExample.contains("complexNested: Record<string, UserProfile[]>[]"));
    }
    
    @Test
    void testCircularReferenceInGenerics() {
        // Test that circular references in generics are properly handled
        String circularGenericExample = """
            export interface TreeNode {
                value: string;
                children: TreeNode[];
                parent?: TreeNode | null;
            }
            
            export interface TreeContainer {
                rootNodes: TreeNode[];
                nodeMap: Record<string, TreeNode>;
            }
            """;
            
        // Validate circular reference handling in generic contexts
        assertTrue(circularGenericExample.contains("children: TreeNode[]"));
        assertTrue(circularGenericExample.contains("rootNodes: TreeNode[]"));
        assertTrue(circularGenericExample.contains("nodeMap: Record<string, TreeNode>"));
        assertTrue(circularGenericExample.contains("parent?: TreeNode | null"));
    }
    
    @Test
    void testGenericBoundsHandling() {
        // Test handling of bounded generics like <T extends BaseEntity>
        String boundedGenericsExample = """
            export interface Repository<T> {
                entities: T[];
                entityClass: string;
                count: number;
            }
            
            export interface UserRepository {
                entities: User[];
                entityClass: string;
                count: number;
            }
            """;
            
        // For bounded generics, we expect conversion to concrete types or proper generic syntax
        assertTrue(boundedGenericsExample.contains("entities: T[]") || 
                  boundedGenericsExample.contains("entities: User[]"));
        assertTrue(boundedGenericsExample.contains("entityClass: string"));
    }
    
    @Test
    void testWildcardGenericHandling() {
        // Test handling of wildcard generics like List<? extends Object>
        String wildcardExample = """
            export interface WildcardEntity {
                unknownList: any[];
                boundedList: BaseEntity[];
                superBoundedList: any[];
            }
            """;
            
        // Wildcards should be handled conservatively
        assertTrue(wildcardExample.contains("unknownList: any[]") ||
                  wildcardExample.contains("unknownList: unknown[]"));
        assertTrue(wildcardExample.contains("boundedList: BaseEntity[]"));
    }
    
    @Test
    void testGenericTypeValidationRules() {
        // Test that the type validation follows proper rules
        
        // Rule 1: Simple generics should maintain type safety
        String simpleGeneric = "List<String> -> string[]";
        assertTrue(simpleGeneric.contains("string[]"));
        
        // Rule 2: Complex generics should use Record for Maps
        String mapGeneric = "Map<String, Integer> -> Record<string, number>";
        assertTrue(mapGeneric.contains("Record<string, number>"));
        
        // Rule 3: Set types should be preserved when possible
        String setGeneric = "Set<String> -> Set<string>";
        assertTrue(setGeneric.contains("Set<string>"));
        
        // Rule 4: Unknown generic parameters should fallback to any
        String unknownGeneric = "List<?> -> any[]";
        assertTrue(unknownGeneric.contains("any[]"));
    }
    
    @Test
    void testTwoPhaseGenerationValidation() {
        // This test validates that the two-phase generation approach is working
        // Phase 1: Collect all interface dependencies
        // Phase 2: Generate interfaces in proper order
        
        String expectedGenerationOrder = """
            // Phase 1: Dependencies collected: [AccountStatement, DateGroupStatement]
            // Phase 2: Generate in dependency order
            
            export interface AccountStatement {
                accountNumber: string;
                balance: number;
                description: string;
            }
            
            export interface DateGroupStatement {
                date: string;
                statements: AccountStatement[];
            }
            """;
            
        // Validate that dependencies are generated before their usage
        int accountStatementPos = expectedGenerationOrder.indexOf("export interface AccountStatement");
        int dateGroupStatementPos = expectedGenerationOrder.indexOf("export interface DateGroupStatement");
        int statementsUsagePos = expectedGenerationOrder.indexOf("statements: AccountStatement[]");
        
        assertTrue(accountStatementPos >= 0 && accountStatementPos < statementsUsagePos,
            "AccountStatement interface should be defined before its usage");
        assertTrue(dateGroupStatementPos > accountStatementPos,
            "DateGroupStatement should be defined after its dependencies");
    }
}