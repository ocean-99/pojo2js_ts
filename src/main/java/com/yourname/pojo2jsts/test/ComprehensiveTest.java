package com.yourname.pojo2jsts.test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Comprehensive test class to demonstrate all supported types and generic handling
 */
public class ComprehensiveTest {
    
    // Basic types
    private String name;
    private int age;
    private boolean active;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    
    // Generic collections with custom types
    private List<AccountStatement> statements;
    private Set<Category> categories;
    private Map<String, Contact> contacts;
    private Map<Integer, String> indexMap;
    
    // Nested custom objects
    private Address primaryAddress;
    private List<Address> allAddresses;
    
    // Complex nested generics
    private Map<String, List<AccountStatement>> groupedStatements;
    private List<Map<String, Object>> dynamicData;
    
    // Arrays
    private String[] tags;
    private AccountStatement[] statementArray;
    
    // Optional and enums
    private Status status;
    private java.util.Optional<String> description;
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public List<AccountStatement> getStatements() { return statements; }
    public void setStatements(List<AccountStatement> statements) { this.statements = statements; }
    
    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }
    
    public Map<String, Contact> getContacts() { return contacts; }
    public void setContacts(Map<String, Contact> contacts) { this.contacts = contacts; }
    
    public Map<Integer, String> getIndexMap() { return indexMap; }
    public void setIndexMap(Map<Integer, String> indexMap) { this.indexMap = indexMap; }
    
    public Address getPrimaryAddress() { return primaryAddress; }
    public void setPrimaryAddress(Address primaryAddress) { this.primaryAddress = primaryAddress; }
    
    public List<Address> getAllAddresses() { return allAddresses; }
    public void setAllAddresses(List<Address> allAddresses) { this.allAddresses = allAddresses; }
    
    public Map<String, List<AccountStatement>> getGroupedStatements() { return groupedStatements; }
    public void setGroupedStatements(Map<String, List<AccountStatement>> groupedStatements) { this.groupedStatements = groupedStatements; }
    
    public List<Map<String, Object>> getDynamicData() { return dynamicData; }
    public void setDynamicData(List<Map<String, Object>> dynamicData) { this.dynamicData = dynamicData; }
    
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
    
    public AccountStatement[] getStatementArray() { return statementArray; }
    public void setStatementArray(AccountStatement[] statementArray) { this.statementArray = statementArray; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public java.util.Optional<String> getDescription() { return description; }
    public void setDescription(java.util.Optional<String> description) { this.description = description; }
}