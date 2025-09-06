package com.yourname.pojo2jsts.test;

import java.util.Date;
import java.util.List;

/**
 * Sample POJO class for testing the plugin
 */
public class User {
    private String name;
    private int age;
    private String email;
    private boolean active;
    private Date createdDate;
    private List<String> tags;
    private Address address;
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public Date getCreatedDate() { return createdDate; }
    public void setCreatedDate(Date createdDate) { this.createdDate = createdDate; }
    
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
}