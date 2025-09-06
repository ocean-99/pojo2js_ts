package com.yourname.pojo2jsts.ui;

import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.yourname.pojo2jsts.config.PluginSettings;

import javax.swing.*;
import java.awt.*;

/**
 * General settings panel for basic plugin configuration
 */
public class GeneralSettingsPanel extends JPanel {
    
    private JBTextField maxRecursionDepthField;
    private JBTextField arrayMaxSizeField;
    private JBCheckBox enableRandomValuesCheckbox;
    private JBCheckBox formatOutputCheckbox;
    private JBCheckBox copyToClipboardCheckbox;
    
    public GeneralSettingsPanel() {
        initComponents();
        layoutComponents();
    }
    
    private void initComponents() {
        maxRecursionDepthField = new JBTextField(5);
        arrayMaxSizeField = new JBTextField(5);
        enableRandomValuesCheckbox = new JBCheckBox("Generate random values for primitive types");
        formatOutputCheckbox = new JBCheckBox("Format generated output (JSON/TypeScript)");
        copyToClipboardCheckbox = new JBCheckBox("Automatically copy results to clipboard");
        
        // Set tooltips
        maxRecursionDepthField.setToolTipText("Maximum depth for recursive object generation (1-20)");
        arrayMaxSizeField.setToolTipText("Maximum number of elements in generated arrays (1-10)");
        enableRandomValuesCheckbox.setToolTipText("When enabled, generates random values. When disabled, uses placeholder values.");
        formatOutputCheckbox.setToolTipText("Apply proper indentation and formatting to generated output");
        copyToClipboardCheckbox.setToolTipText("Automatically copy generated content to system clipboard");
    }
    
    private void layoutComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 15, 0);
        gbc.anchor = GridBagConstraints.WEST;
        JBLabel titleLabel = new JBLabel("General Settings");
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() + 2));
        add(titleLabel, gbc);
        
        // Reset for other components
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 0, 5, 10);
        
        // Max recursion depth
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JBLabel("Max Recursion Depth:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(maxRecursionDepthField, gbc);
        
        // Array max size
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        add(new JBLabel("Array Max Size:"), gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(arrayMaxSizeField, gbc);
        
        // Checkboxes
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(15, 0, 5, 0);
        add(enableRandomValuesCheckbox, gbc);
        
        gbc.gridy = 4;
        gbc.insets = new Insets(5, 0, 5, 0);
        add(formatOutputCheckbox, gbc);
        
        gbc.gridy = 5;
        add(copyToClipboardCheckbox, gbc);
        
        // Add description
        gbc.gridy = 6;
        gbc.insets = new Insets(20, 0, 0, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextArea description = new JTextArea(3, 50);
        description.setOpaque(false);
        description.setEditable(false);
        description.setWrapStyleWord(true);
        description.setLineWrap(true);
        description.setFont(description.getFont().deriveFont(Font.ITALIC));
        description.setText(
            "These settings control the basic behavior of JSON and TypeScript generation. " +
            "Recursion depth prevents infinite loops with circular references. " +
            "Array size limits the number of sample elements generated for collections."
        );
        add(description, gbc);
        
        // Add glue to push everything to top
        gbc.gridy = 7;
        gbc.weighty = 1.0;
        add(Box.createVerticalGlue(), gbc);
    }
    
    public boolean isModified(PluginSettings settings) {
        return !String.valueOf(settings.getMaxRecursionDepth()).equals(maxRecursionDepthField.getText()) ||
               !String.valueOf(settings.getArrayMaxSize()).equals(arrayMaxSizeField.getText()) ||
               settings.isEnableRandomValues() != enableRandomValuesCheckbox.isSelected() ||
               settings.isFormatOutput() != formatOutputCheckbox.isSelected() ||
               settings.isCopyToClipboard() != copyToClipboardCheckbox.isSelected();
    }
    
    public void apply(PluginSettings settings) {
        try {
            int maxDepth = Integer.parseInt(maxRecursionDepthField.getText());
            settings.setMaxRecursionDepth(maxDepth);
        } catch (NumberFormatException e) {
            settings.setMaxRecursionDepth(5); // fallback to default
        }
        
        try {
            int arraySize = Integer.parseInt(arrayMaxSizeField.getText());
            settings.setArrayMaxSize(arraySize);
        } catch (NumberFormatException e) {
            settings.setArrayMaxSize(3); // fallback to default
        }
        
        settings.setEnableRandomValues(enableRandomValuesCheckbox.isSelected());
        settings.setFormatOutput(formatOutputCheckbox.isSelected());
        settings.setCopyToClipboard(copyToClipboardCheckbox.isSelected());
    }
    
    public void reset(PluginSettings settings) {
        maxRecursionDepthField.setText(String.valueOf(settings.getMaxRecursionDepth()));
        arrayMaxSizeField.setText(String.valueOf(settings.getArrayMaxSize()));
        enableRandomValuesCheckbox.setSelected(settings.isEnableRandomValues());
        formatOutputCheckbox.setSelected(settings.isFormatOutput());
        copyToClipboardCheckbox.setSelected(settings.isCopyToClipboard());
    }
}