package com.yourname.pojo2jsts.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.yourname.pojo2jsts.config.DateConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;

/**
 * Date configuration panel for customizing date/time generation
 */
public class DateConfigPanel extends JPanel {
    
    private JBTextField formatField;
    private JBTextField pastDaysField;
    private JBTextField futureDaysField;
    private JBCheckBox useCurrentTimeCheckbox;
    private JBTextField timeZoneField;
    private JBLabel previewLabel;
    
    public DateConfigPanel() {
        initComponents();
        layoutComponents();
        setupListeners();
    }
    
    private void initComponents() {
        formatField = new JBTextField(20);
        pastDaysField = new JBTextField(5);
        futureDaysField = new JBTextField(5);
        useCurrentTimeCheckbox = new JBCheckBox("Use current time as base");
        timeZoneField = new JBTextField(10);
        previewLabel = new JBLabel("Preview will appear here");
        
        // Set tooltips
        formatField.setToolTipText("Java DateTimeFormatter pattern (e.g., yyyy-MM-dd HH:mm:ss)");
        pastDaysField.setToolTipText("Number of days in the past for random date generation");
        futureDaysField.setToolTipText("Number of days in the future for random date generation");
        useCurrentTimeCheckbox.setToolTipText("Use current time as reference point instead of random dates");
        timeZoneField.setToolTipText("Time zone for date generation (e.g., UTC, America/New_York)");
    }
    
    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        JBLabel titleLabel = new JBLabel("Date and Time Configuration");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() + 2));
        add(titleLabel, gbc);
        
        // Reset for other components
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        
        // Date format
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JBLabel("Date Format Pattern:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(formatField, gbc);
        
        // Past days
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JBLabel("Past Days Range:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(pastDaysField, gbc);
        
        // Future days
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JBLabel("Future Days Range:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(futureDaysField, gbc);
        
        // Time zone
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JBLabel("Time Zone:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(timeZoneField, gbc);
        
        // Use current time checkbox
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 0, 5, 0);
        add(useCurrentTimeCheckbox, gbc);
        
        // Preview section
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 5, 0);
        JBLabel previewTitle = new JBLabel("Preview:");
        previewTitle.setFont(previewTitle.getFont().deriveFont(Font.BOLD));
        add(previewTitle, gbc);
        
        gbc.gridy = 7;
        gbc.insets = new Insets(5, 0, 5, 0);
        previewLabel.setFont(previewLabel.getFont().deriveFont(Font.PLAIN));
        previewLabel.setBorder(BorderFactory.createEtchedBorder());
        add(previewLabel, gbc);
        
        // Common format examples
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 5, 0);
        JBLabel examplesTitle = new JBLabel("Common Format Examples:");
        examplesTitle.setFont(examplesTitle.getFont().deriveFont(Font.BOLD));
        add(examplesTitle, gbc);
        
        gbc.gridy = 9;
        gbc.insets = new Insets(5, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextArea examples = new JTextArea(6, 50);
        examples.setOpaque(false);
        examples.setEditable(false);
        examples.setFont(examples.getFont().deriveFont(Font.PLAIN, examples.getFont().getSize() - 1));
        examples.setText(
            "yyyy-MM-dd HH:mm:ss     → 2023-12-25 14:30:45\\n" +
            "yyyy-MM-dd              → 2023-12-25\\n" +
            "dd/MM/yyyy HH:mm        → 25/12/2023 14:30\\n" +
            "MMM dd, yyyy            → Dec 25, 2023\\n" +
            "HH:mm:ss                → 14:30:45\\n" +
            "yyyy-MM-dd'T'HH:mm:ss'Z' → 2023-12-25T14:30:45Z (ISO 8601)"
        );
        add(examples, gbc);
        
        // Add glue to push everything to top
        gbc.gridy = 10;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }
    
    private void setupListeners() {
        // Update preview when format changes
        formatField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
        });
        
        updatePreview(); // Initial preview
    }
    
    private void updatePreview() {
        try {
            String pattern = formatField.getText();
            if (pattern.isEmpty()) {
                previewLabel.setText("Enter a format pattern to see preview");
                previewLabel.setForeground(Color.GRAY);
                return;
            }
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            String preview = java.time.LocalDateTime.now().format(formatter);
            previewLabel.setText(preview);
            previewLabel.setForeground(Color.BLACK);
        } catch (Exception e) {
            previewLabel.setText("Invalid format pattern: " + e.getMessage());
            previewLabel.setForeground(Color.RED);
        }
    }
    
    public boolean isModified(DateConfig dateConfig) {
        return !dateConfig.getFormat().equals(formatField.getText()) ||
               dateConfig.getPastDays() != parseInt(pastDaysField.getText(), dateConfig.getPastDays()) ||
               dateConfig.getFutureDays() != parseInt(futureDaysField.getText(), dateConfig.getFutureDays()) ||
               dateConfig.isUseCurrentTime() != useCurrentTimeCheckbox.isSelected() ||
               !dateConfig.getTimeZone().equals(timeZoneField.getText());
    }
    
    public void apply(DateConfig dateConfig) {
        dateConfig.setFormat(formatField.getText());
        dateConfig.setPastDays(parseInt(pastDaysField.getText(), dateConfig.getPastDays()));
        dateConfig.setFutureDays(parseInt(futureDaysField.getText(), dateConfig.getFutureDays()));
        dateConfig.setUseCurrentTime(useCurrentTimeCheckbox.isSelected());
        dateConfig.setTimeZone(timeZoneField.getText());
    }
    
    public void reset(DateConfig dateConfig) {
        formatField.setText(dateConfig.getFormat());
        pastDaysField.setText(String.valueOf(dateConfig.getPastDays()));
        futureDaysField.setText(String.valueOf(dateConfig.getFutureDays()));
        useCurrentTimeCheckbox.setSelected(dateConfig.isUseCurrentTime());
        timeZoneField.setText(dateConfig.getTimeZone());
        updatePreview();
    }
    
    private int parseInt(String text, int defaultValue) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}