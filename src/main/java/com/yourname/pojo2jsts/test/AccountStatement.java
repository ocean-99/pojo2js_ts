package com.yourname.pojo2jsts.test;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * AccountStatement class for testing generic types
 */
public class AccountStatement {
    private String id;
    private LocalDate date;
    private BigDecimal amount;
    private String description;
    private String category;
    
    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}