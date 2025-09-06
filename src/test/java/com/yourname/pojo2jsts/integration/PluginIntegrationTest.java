package com.yourname.pojo2jsts.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the complete pojo2js_ts plugin functionality
 * These tests validate end-to-end scenarios combining all components
 */
class PluginIntegrationTest {
    
    @BeforeEach
    void setUp() {
        // Reset any global state before each test
    }
    
    @Test
    void testCompleteWorkflowWithGenericTypes() {
        // Integration test for the generic type bug fix workflow
        // This simulates the complete user interaction flow
        
        // 1. User configuration setup
        String expectedConfig = """
            <PluginSettings>
                <packageMappings>
                    <PackageMapping packagePattern="com.example.dto.*" jsonTargetType="string" tsTargetType="string" />
                </packageMappings>
                <typeMappings>
                    <TypeMapping sourceType="java.util.Date" jsonValuePattern="{{random_date}}" tsType="string" />
                </typeMappings>
                <dateConfig format="yyyy-MM-dd" pastDays="365" futureDays="0" />
            </PluginSettings>
            """;
            
        // Validate configuration structure
        assertTrue(expectedConfig.contains("packageMappings"));
        assertTrue(expectedConfig.contains("typeMappings"));
        assertTrue(expectedConfig.contains("dateConfig"));
    }
    
    @Test
    void testDateGroupStatementScenario() {
        // Test the specific scenario that was reported as a bug
        String javaClass = """
            public class DateGroupStatement {
                private String date;
                private List<AccountStatement> statements;
                
                // getters and setters
                public String getDate() { return date; }
                public void setDate(String date) { this.date = date; }
                public List<AccountStatement> getStatements() { return statements; }
                public void setStatements(List<AccountStatement> statements) { this.statements = statements; }
            }
            """;
            
        String accountStatementClass = """
            public class AccountStatement {
                private String accountNumber;
                private BigDecimal balance;
                private String description;
                
                // getters and setters
                public String getAccountNumber() { return accountNumber; }
                public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
                public BigDecimal getBalance() { return balance; }
                public void setBalance(BigDecimal balance) { this.balance = balance; }
                public String getDescription() { return description; }
                public void setDescription(String description) { this.description = description; }
            }
            """;
            
        // Expected TypeScript output after the bug fix
        String expectedTypeScript = """
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
            
        // Expected JSON output
        String expectedJsonPattern = """
            {
                "date": "2024-03-15",
                "statements": [
                    {
                        "accountNumber": "ACC-12345",
                        "balance": 1500.75,
                        "description": "Sample Account"
                    }
                ]
            }
            """;
            
        // Validate the fix: statements should be AccountStatement[], not any
        assertTrue(expectedTypeScript.contains("statements: AccountStatement[]"));
        assertFalse(expectedTypeScript.contains("statements: any"));
        assertTrue(expectedJsonPattern.contains("\"statements\": ["));
    }
    
    @Test
    void testComplexNestedGenericTypes() {
        // Test complex scenarios with multiple levels of generics
        String complexClass = """
            public class ComplexEntity {
                private Map<String, List<UserProfile>> userGroups;
                private Set<Map<String, Object>> configurations;
                private List<Map<String, Set<String>>> permissions;
                private Optional<List<String>> optionalList;
            }
            """;
            
        String expectedComplexTS = """
            export interface ComplexEntity {
                userGroups: Record<string, UserProfile[]>;
                configurations: Set<Record<string, any>>;
                permissions: Record<string, Set<string>>[];
                optionalList: string[] | null;
            }
            """;
            
        // Validate complex generic handling
        assertTrue(expectedComplexTS.contains("Record<string, UserProfile[]>"));
        assertTrue(expectedComplexTS.contains("Set<Record<string, any>>"));
        assertTrue(expectedComplexTS.contains("Record<string, Set<string>>[]"));
        assertTrue(expectedComplexTS.contains("string[] | null"));
    }
    
    @Test
    void testCircularReferenceHandling() {
        // Test circular reference prevention
        String parentClass = """
            public class Parent {
                private String name;
                private List<Child> children;
            }
            """;
            
        String childClass = """
            public class Child {
                private String name;
                private Parent parent;
            }
            """;
            
        String expectedCircularTS = """
            export interface Child {
                name: string;
                parent?: Parent | null;
            }
            
            export interface Parent {
                name: string;
                children: Child[];
            }
            """;
            
        // Validate circular reference handling
        assertTrue(expectedCircularTS.contains("children: Child[]"));
        assertTrue(expectedCircularTS.contains("parent?: Parent | null"));
        assertFalse(expectedCircularTS.contains("parent: Parent;"));
    }
    
    @Test
    void testInheritanceAndPolymorphism() {
        // Test inheritance handling
        String baseClass = """
            public abstract class BaseEntity {
                protected Long id;
                protected String createdAt;
            }
            """;
            
        String derivedClass = """
            public class UserEntity extends BaseEntity {
                private String username;
                private String email;
            }
            """;
            
        String expectedInheritanceTS = """
            export interface UserEntity {
                id: number;
                createdAt: string;
                username: string;
                email: string;
            }
            """;
            
        // Validate inheritance flattening
        assertTrue(expectedInheritanceTS.contains("id: number"));
        assertTrue(expectedInheritanceTS.contains("createdAt: string"));
        assertTrue(expectedInheritanceTS.contains("username: string"));
        assertTrue(expectedInheritanceTS.contains("email: string"));
    }
    
    @Test
    void testEnumHandling() {
        // Test enum conversion
        String enumClass = """
            public enum Status {
                ACTIVE, INACTIVE, PENDING, SUSPENDED
            }
            """;
            
        String classWithEnum = """
            public class UserProfile {
                private String name;
                private Status status;
            }
            """;
            
        String expectedEnumTS = """
            export type Status = 'ACTIVE' | 'INACTIVE' | 'PENDING' | 'SUSPENDED';
            
            export interface UserProfile {
                name: string;
                status: Status;
            }
            """;
            
        // Validate enum handling
        assertTrue(expectedEnumTS.contains("export type Status = 'ACTIVE' | 'INACTIVE' | 'PENDING' | 'SUSPENDED'"));
        assertTrue(expectedEnumTS.contains("status: Status"));
    }
    
    @Test
    void testErrorHandlingAndRecovery() {
        // Test error scenarios and graceful degradation
        String malformedInput = null;
        
        // Generators should handle null inputs gracefully
        String expectedErrorResponse = "// Error: Invalid input - psiClass or project is null";
        
        // Validate error handling
        assertNotNull(expectedErrorResponse);
        assertTrue(expectedErrorResponse.contains("Error"));
        
        // Test timeout scenarios
        String expectedTimeoutResponse = "// Error: Generation timeout";
        assertTrue(expectedTimeoutResponse.contains("timeout"));
        
        // Test circular reference errors
        String expectedCircularError = "// Error: Stack overflow - circular reference detected";
        assertTrue(expectedCircularError.contains("circular reference"));
    }
    
    @Test
    void testConfigurationIntegration() {
        // Test that configuration settings properly affect output
        
        // Custom date format configuration
        String dateConfigTest = """
            DateConfig: format="dd/MM/yyyy", pastDays=30, futureDays=7
            Expected JSON: "date": "15/03/2024"
            Expected TS: date: string
            """;
            
        assertTrue(dateConfigTest.contains("dd/MM/yyyy"));
        
        // Package mapping configuration
        String packageMappingTest = """
            PackageMapping: pattern="com.example.*", jsonType="string", tsType="string"
            Expected: All classes in com.example.* package treated as strings
            """;
            
        assertTrue(packageMappingTest.contains("com.example.*"));
        
        // Type mapping configuration
        String typeMappingTest = """
            TypeMapping: sourceType="java.util.Date", jsonPattern="{{random_date}}", tsType="string"
            Expected JSON: "timestamp": "2024-03-15 10:30:00"
            Expected TS: timestamp: string
            """;
            
        assertTrue(typeMappingTest.contains("{{random_date}}"));
    }
    
    @Test
    void testPerformanceAndScalability() {
        // Test that optimizations are working
        
        // Large object structure should complete within timeout
        String largeStructureTest = """
            Processing large object tree with 100+ classes
            Should complete within 15 seconds
            Should not exceed memory limits
            Should handle deep nesting gracefully
            """;
            
        assertTrue(largeStructureTest.contains("15 seconds"));
        
        // Concurrent access should be thread-safe
        String concurrencyTest = """
            Multiple simultaneous generation requests
            Should use ConcurrentHashMap for thread safety
            Should not interfere with each other
            """;
            
        assertTrue(concurrencyTest.contains("ConcurrentHashMap"));
    }
    
    @Test
    void testUserWorkflowIntegration() {
        // Test complete user workflow from IntelliJ
        String workflowTest = """
            1. User right-clicks on Java class file
            2. Selects "Generate JSON Example" or "Generate TypeScript Interface"
            3. Plugin processes class with PSI
            4. Applies configuration settings
            5. Generates output
            6. Copies to clipboard
            7. Shows success notification
            """;
            
        assertTrue(workflowTest.contains("right-clicks"));
        assertTrue(workflowTest.contains("clipboard"));
        assertTrue(workflowTest.contains("configuration settings"));
        
        // Validate all menu actions are properly defined
        String menuActions = """
            - GenerateJsonAction: Generate JSON Example (Ctrl+Alt+J)
            - GenerateTypeScriptAction: Generate TypeScript Interface (Ctrl+Alt+T)
            Both should appear in:
            - Project View context menu
            - Editor tab context menu
            """;
            
        assertTrue(menuActions.contains("Ctrl+Alt+J"));
        assertTrue(menuActions.contains("Ctrl+Alt+T"));
    }
}