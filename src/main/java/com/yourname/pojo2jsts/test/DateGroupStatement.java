package com.yourname.pojo2jsts.test;

import java.util.List;

/**
 * Test class to reproduce the generic type issue
 */
public class DateGroupStatement {
    private String date;
    private List<AccountStatement> statements;
    
    // Getters and setters
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    
    public List<AccountStatement> getStatements() { return statements; }
    public void setStatements(List<AccountStatement> statements) { this.statements = statements; }
}