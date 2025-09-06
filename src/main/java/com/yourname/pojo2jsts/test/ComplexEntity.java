package com.yourname.pojo2jsts.test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.*;

/**
 * Complex test POJO with various data types for testing enhanced generators
 */
public class ComplexEntity {
    private String id;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private LocalDate dueDate;
    private Status status;
    private Optional<String> description;
    
    // Collections
    private List<String> tags;
    private Set<Category> categories;
    private Map<String, Object> metadata;
    private Map<Integer, String> indexMap;
    
    // Nested objects
    private List<Address> addresses;
    private Map<String, Contact> contacts;
    
    // Arrays
    private String[] permissions;
    private int[] scores;
    
    // Self-reference for circular reference testing
    private ComplexEntity parent;
    private List<ComplexEntity> children;
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public Optional<String> getDescription() { return description; }
    public void setDescription(Optional<String> description) { this.description = description; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }
    
    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
    
    public Map<Integer, String> getIndexMap() { return indexMap; }
    public void setIndexMap(Map<Integer, String> indexMap) { this.indexMap = indexMap; }
    
    public List<Address> getAddresses() { return addresses; }
    public void setAddresses(List<Address> addresses) { this.addresses = addresses; }
    
    public Map<String, Contact> getContacts() { return contacts; }
    public void setContacts(Map<String, Contact> contacts) { this.contacts = contacts; }
    
    public String[] getPermissions() { return permissions; }
    public void setPermissions(String[] permissions) { this.permissions = permissions; }
    
    public int[] getScores() { return scores; }
    public void setScores(int[] scores) { this.scores = scores; }
    
    public ComplexEntity getParent() { return parent; }
    public void setParent(ComplexEntity parent) { this.parent = parent; }
    
    public List<ComplexEntity> getChildren() { return children; }
    public void setChildren(List<ComplexEntity> children) { this.children = children; }
}