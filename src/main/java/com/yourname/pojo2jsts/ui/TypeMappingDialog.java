package com.yourname.pojo2jsts.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.yourname.pojo2jsts.config.TypeMapping;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for editing type mapping configuration
 */
public class TypeMappingDialog extends DialogWrapper {
    
    private JBTextField sourceTypeField;
    private JBTextField jsonPatternField;
    private JBTextField tsTypeField;
    private JBTextField customGeneratorField;
    private JBTextArea descriptionArea;
    private JBCheckBox enabledCheckbox;
    
    private TypeMapping originalMapping;
    
    public TypeMappingDialog(Component parent, String title, @Nullable TypeMapping mapping) {
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
        
        // Source type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JBLabel("Source Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(sourceTypeField, gbc);
        
        // JSON pattern
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JBLabel("JSON Value Pattern:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(jsonPatternField, gbc);
        
        // TypeScript type
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JBLabel("TypeScript Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(tsTypeField, gbc);
        
        // Custom generator (optional)
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JBLabel("Custom Generator (optional):"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(customGeneratorField, gbc);
        
        // Description
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        panel.add(new JBLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(300, 60));
        panel.add(descScrollPane, gbc);
        
        // Enabled checkbox
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.weighty = 0;
        panel.add(enabledCheckbox, gbc);
        
        // Help section
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(15, 5, 5, 5);
        
        JPanel helpPanel = new JPanel(new BorderLayout());
        JBLabel helpTitle = new JBLabel("Examples:");
        helpTitle.setFont(helpTitle.getFont().deriveFont(Font.BOLD));
        helpPanel.add(helpTitle, BorderLayout.NORTH);
        
        JTextArea examples = new JTextArea(6, 30);
        examples.setOpaque(false);
        examples.setEditable(false);
        examples.setFont(examples.getFont().deriveFont(Font.PLAIN, examples.getFont().getSize() - 1));
        examples.setText(
            "Source Type: java.util.Date\\n" +
            "JSON Pattern: \\\"{{random_date}}\\\"\\n" +
            "TS Type: string\\n\\n" +
            "Source Type: java.math.BigDecimal\\n" +
            "JSON Pattern: {{random_number}}\\n" +
            "TS Type: number"
        );
        examples.setBorder(BorderFactory.createEtchedBorder());
        helpPanel.add(examples, BorderLayout.CENTER);
        
        panel.add(helpPanel, gbc);
        
        return panel;
    }
    
    private void initComponents() {
        sourceTypeField = new JBTextField(30);
        jsonPatternField = new JBTextField(25);
        tsTypeField = new JBTextField(15);
        customGeneratorField = new JBTextField(30);
        descriptionArea = new JBTextArea(3, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        enabledCheckbox = new JBCheckBox("Enabled");
        
        // Set tooltips
        sourceTypeField.setToolTipText("Fully qualified Java class name (e.g., java.util.Date)");
        jsonPatternField.setToolTipText("JSON value pattern with templates or fixed values");
        tsTypeField.setToolTipText("Target TypeScript type (string, number, boolean, etc.)");
        customGeneratorField.setToolTipText("Optional: Custom generator class name for complex logic");
        descriptionArea.setToolTipText("Brief description of this type mapping");
        enabledCheckbox.setToolTipText("Enable or disable this mapping");
    }
    
    private void setDefaults() {
        sourceTypeField.setText("java.util.Date");
        jsonPatternField.setText("\\\"{{random_date}}\\\"");
        tsTypeField.setText("string");
        customGeneratorField.setText("");
        descriptionArea.setText("Maps Java Date to random date string");
        enabledCheckbox.setSelected(true);
    }
    
    private void loadFromMapping(TypeMapping mapping) {
        sourceTypeField.setText(mapping.getSourceType());
        jsonPatternField.setText(mapping.getJsonValuePattern());
        tsTypeField.setText(mapping.getTsType());
        customGeneratorField.setText(mapping.getCustomGenerator() != null ? mapping.getCustomGenerator() : "");
        descriptionArea.setText(mapping.getDescription() != null ? mapping.getDescription() : "");
        enabledCheckbox.setSelected(mapping.isEnabled());
    }
    
    public TypeMapping getTypeMapping() {
        TypeMapping mapping = new TypeMapping();
        mapping.setSourceType(sourceTypeField.getText().trim());
        mapping.setJsonValuePattern(jsonPatternField.getText().trim());
        mapping.setTsType(tsTypeField.getText().trim());
        
        String customGen = customGeneratorField.getText().trim();
        mapping.setCustomGenerator(customGen.isEmpty() ? null : customGen);
        
        String desc = descriptionArea.getText().trim();
        mapping.setDescription(desc.isEmpty() ? null : desc);
        
        mapping.setEnabled(enabledCheckbox.isSelected());
        return mapping;
    }
    
    @Override
    protected ValidationInfo doValidate() {
        String sourceType = sourceTypeField.getText().trim();
        if (sourceType.isEmpty()) {
            return new ValidationInfo("Source type cannot be empty", sourceTypeField);
        }
        
        String jsonPattern = jsonPatternField.getText().trim();
        if (jsonPattern.isEmpty()) {
            return new ValidationInfo("JSON value pattern cannot be empty", jsonPatternField);
        }
        
        String tsType = tsTypeField.getText().trim();
        if (tsType.isEmpty()) {
            return new ValidationInfo("TypeScript type cannot be empty", tsTypeField);
        }
        
        return null;
    }
}