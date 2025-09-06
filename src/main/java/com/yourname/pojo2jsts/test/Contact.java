package com.yourname.pojo2jsts.test;

import java.time.LocalDateTime;

/**
 * Contact entity for testing Map values with complex objects
 */
public class Contact {
    private String name;
    private String email;
    private String phone;
    private ContactType type;
    private boolean primary;
    private LocalDateTime lastContacted;
    
    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public ContactType getType() { return type; }
    public void setType(ContactType type) { this.type = type; }
    
    public boolean isPrimary() { return primary; }
    public void setPrimary(boolean primary) { this.primary = primary; }
    
    public LocalDateTime getLastContacted() { return lastContacted; }
    public void setLastContacted(LocalDateTime lastContacted) { this.lastContacted = lastContacted; }
}