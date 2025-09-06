package com.yourname.pojo2jsts.test;

import java.util.UUID;

/**
 * Category entity for testing nested objects in collections
 */
public class Category {
    private UUID id;
    private String name;
    private String description;
    private int priority;
    private boolean active;
    private Category parent; // Self-reference
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public Category getParent() { return parent; }
    public void setParent(Category parent) { this.parent = parent; }
}