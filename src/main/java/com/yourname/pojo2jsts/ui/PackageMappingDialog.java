package com.yourname.pojo2jsts.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.yourname.pojo2jsts.config.PackageMapping;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for editing package mapping configuration
 */
public class PackageMappingDialog extends DialogWrapper {
    
    private JBTextField packagePatternField;
    private JBTextField jsonTypeField;
    private JBTextField tsTypeField;
    private JBCheckBox recursiveCheckbox;
    private JBCheckBox enabledCheckbox;
    
    private PackageMapping originalMapping;
    
    public PackageMappingDialog(Component parent, String title, @Nullable PackageMapping mapping) {
        super(parent, true);
        this.originalMapping = mapping;
        setTitle(title);
        init();
        
        if (mapping != null) {
            loadFromMapping(mapping);
        } else {
            setDefaults();
        }
    }
    
    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        initComponents();
        
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Package pattern
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JBLabel("Package Pattern:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(packagePatternField, gbc);
        
        // JSON target type
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JBLabel("JSON Target Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(jsonTypeField, gbc);
        
        // TypeScript target type
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JBLabel("TypeScript Target Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(tsTypeField, gbc);
        
        // Checkboxes
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(recursiveCheckbox, gbc);
        
        gbc.gridy = 4;
        panel.add(enabledCheckbox, gbc);
        
        // Help text
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 5, 5);
        JTextArea helpText = new JTextArea(4, 30);
        helpText.setOpaque(false);
        helpText.setEditable(false);
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);
        helpText.setFont(helpText.getFont().deriveFont(Font.ITALIC, helpText.getFont().getSize() - 1));
        helpText.setText(
            "Package Pattern: Use * for wildcards (e.g., 'com.example.*' for all subpackages)\\n" +
            "Target Types: 'string', 'number', 'boolean', 'any', or custom types\\n" +
            "Recursive: When enabled, pattern applies to subpackages\\n" +
            "Enabled: Uncheck to temporarily disable this mapping"
        );
        panel.add(helpText, gbc);
        
        return panel;
    }
    
    private void initComponents() {
        packagePatternField = new JBTextField(30);
        jsonTypeField = new JBTextField(15);
        tsTypeField = new JBTextField(15);
        recursiveCheckbox = new JBCheckBox("Apply to subpackages (recursive)");
        enabledCheckbox = new JBCheckBox("Enabled");
        
        // Set tooltips
        packagePatternField.setToolTipText("Java package pattern with optional wildcards");
        jsonTypeField.setToolTipText("Target type for JSON generation (e.g., string, number, any)");
        tsTypeField.setToolTipText("Target type for TypeScript generation (e.g., string, number, any)");
        recursiveCheckbox.setToolTipText("Whether this mapping applies to subpackages");
        enabledCheckbox.setToolTipText("Enable or disable this mapping");
    }
    
    private void setDefaults() {
        packagePatternField.setText("com.example.*");
        jsonTypeField.setText("string");
        tsTypeField.setText("string");
        recursiveCheckbox.setSelected(true);
        enabledCheckbox.setSelected(true);
    }
    
    private void loadFromMapping(PackageMapping mapping) {
        packagePatternField.setText(mapping.getPackagePattern());
        jsonTypeField.setText(mapping.getJsonTargetType());
        tsTypeField.setText(mapping.getTsTargetType());
        recursiveCheckbox.setSelected(mapping.isRecursive());
        enabledCheckbox.setSelected(mapping.isEnabled());
    }
    
    public PackageMapping getPackageMapping() {
        PackageMapping mapping = new PackageMapping();
        mapping.setPackagePattern(packagePatternField.getText().trim());
        mapping.setJsonTargetType(jsonTypeField.getText().trim());
        mapping.setTsTargetType(tsTypeField.getText().trim());
        mapping.setRecursive(recursiveCheckbox.isSelected());
        mapping.setEnabled(enabledCheckbox.isSelected());
        return mapping;
    }
    
    @Override
    protected ValidationInfo doValidate() {
        String pattern = packagePatternField.getText().trim();
        if (pattern.isEmpty()) {
            return new ValidationInfo("Package pattern cannot be empty", packagePatternField);
        }
        
        String jsonType = jsonTypeField.getText().trim();
        if (jsonType.isEmpty()) {
            return new ValidationInfo("JSON target type cannot be empty", jsonTypeField);
        }
        
        String tsType = tsTypeField.getText().trim();
        if (tsType.isEmpty()) {
            return new ValidationInfo("TypeScript target type cannot be empty", tsTypeField);
        }
        
        return null;
    }
}